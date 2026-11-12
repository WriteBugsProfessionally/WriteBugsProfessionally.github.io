"""空文件夹扫描与删除（独立于 Qt，方便测试）。

规则：
- 一个目录“空” = 里面没有任何文件，且所有子目录也都是空的；
- 只列出“最外层”的空目录（如果 A/B 都空，只列 A）；
- 不列出扫描根目录本身，避免误删用户选择的整个文件夹；
- 隐藏/系统目录、排除目录、符号链接、无权限目录都会让父目录不算空，避免误删；
- 删除前会再次检查，只有确实没有文件的目录才会被删。
"""

from __future__ import annotations

import os
import threading
import time
from dataclasses import dataclass, field
from typing import Callable

from .fileops import recycle_files
from .models import ScanError
from .scanner import DEFAULT_EXCLUDE_DIRS, prepare_roots
from .util import from_extended, to_extended

FILE_ATTRIBUTE_HIDDEN = 0x2
FILE_ATTRIBUTE_SYSTEM = 0x4
FILE_ATTRIBUTE_REPARSE_POINT = 0x400
MAX_ERRORS = 500


@dataclass(slots=True)
class EmptyScanOptions:
    exclude_dirs: frozenset[str] = frozenset(DEFAULT_EXCLUDE_DIRS)
    include_hidden: bool = False

    def normalized(self) -> "EmptyScanOptions":
        return EmptyScanOptions(
            exclude_dirs=frozenset(name.casefold() for name in self.exclude_dirs if name),
            include_hidden=bool(self.include_hidden),
        )


@dataclass(slots=True)
class EmptyScanResult:
    folders: list[str] = field(default_factory=list)
    dirs_scanned: int = 0
    errors: list[ScanError] = field(default_factory=list)
    elapsed: float = 0.0
    cancelled: bool = False
    roots: list[str] = field(default_factory=list)


@dataclass(slots=True)
class EmptyDeleteOutcome:
    removed: list[str] = field(default_factory=list)
    failures: list[tuple[str, str]] = field(default_factory=list)


@dataclass(slots=True)
class _Node:
    path: str
    parent: str | None
    files: int = 0
    children: list[str] = field(default_factory=list)
    blocked: bool = False


ProgressCallback = Callable[[int, str], None]
ErrorCallback = Callable[[str, str], None]


def _describe_error(exc: BaseException) -> str:
    if isinstance(exc, PermissionError):
        return "没有读取权限"
    if isinstance(exc, FileNotFoundError):
        return "文件夹不存在或已被移动"
    if isinstance(exc, OSError):
        return exc.strerror or str(exc)
    return str(exc)


def scan_empty_folders(
    roots: list[str],
    options: EmptyScanOptions | None = None,
    *,
    on_progress: ProgressCallback | None = None,
    on_error: ErrorCallback | None = None,
    cancel: threading.Event | None = None,
) -> EmptyScanResult:
    opts = (options or EmptyScanOptions()).normalized()
    cancel_event = cancel or threading.Event()
    started = time.monotonic()
    result = EmptyScanResult()

    valid_roots, root_errors = prepare_roots(roots)
    result.errors.extend(root_errors)
    result.roots = valid_roots
    if not valid_roots:
        result.elapsed = time.monotonic() - started
        return result

    nodes: dict[str, _Node] = {}
    order: list[str] = []
    stack: list[tuple[str, str | None]] = [(root, None) for root in reversed(valid_roots)]
    last_emit = 0.0
    dirs_scanned = 0

    def report(current: str, force: bool = False) -> None:
        nonlocal last_emit
        if on_progress is None:
            return
        now = time.monotonic()
        if force or now - last_emit >= 0.1:
            last_emit = now
            on_progress(dirs_scanned, current)

    while stack:
        if cancel_event.is_set():
            result.cancelled = True
            result.elapsed = time.monotonic() - started
            return result
        path, parent = stack.pop()
        node = _Node(path=path, parent=parent)
        nodes[path] = node
        order.append(path)
        dirs_scanned += 1
        report(path)

        try:
            with os.scandir(to_extended(path)) as iterator:
                for entry in iterator:
                    try:
                        st = entry.stat(follow_symlinks=False)
                    except OSError as exc:
                        node.blocked = True
                        _add_error(result.errors, on_error, entry.path, _describe_error(exc))
                        continue
                    attrs = getattr(st, "st_file_attributes", 0)
                    try:
                        is_dir = entry.is_dir(follow_symlinks=False)
                    except OSError:
                        is_dir = False

                    if attrs & FILE_ATTRIBUTE_REPARSE_POINT:
                        node.blocked = True
                        continue

                    name = entry.name
                    if is_dir:
                        hidden = attrs & (FILE_ATTRIBUTE_HIDDEN | FILE_ATTRIBUTE_SYSTEM)
                        if (not opts.include_hidden and hidden) or name.casefold() in opts.exclude_dirs:
                            node.blocked = True
                            continue
                        child = from_extended(entry.path)
                        node.children.append(child)
                        stack.append((child, path))
                    else:
                        # 任何文件（包括隐藏文件）都会让目录不再为空
                        node.files += 1
        except OSError as exc:
            node.blocked = True
            _add_error(result.errors, on_error, path, _describe_error(exc))

    empty: dict[str, bool] = {}
    for path in reversed(order):
        node = nodes[path]
        if node.blocked or node.files:
            empty[path] = False
            continue
        empty[path] = all(empty.get(child, False) for child in node.children)

    roots_set = set(valid_roots)
    folders: list[str] = []
    for path in order:
        if path in roots_set or not empty.get(path):
            continue
        node = nodes[path]
        parent = node.parent
        # 如果父目录也是空的，且父目录不是扫描根，则只保留最外层那个
        if parent is not None and parent not in roots_set and empty.get(parent, False):
            continue
        folders.append(path)

    folders.sort(key=lambda item: item.casefold())
    result.folders = folders
    result.dirs_scanned = dirs_scanned
    result.elapsed = time.monotonic() - started
    report("", force=True)
    return result


def _add_error(errors: list[ScanError], on_error: ErrorCallback | None, path: str, message: str) -> None:
    if len(errors) < MAX_ERRORS:
        errors.append(ScanError(path, message))
    if on_error is not None:
        on_error(path, message)


def _collect_empty_tree(path: str) -> list[str]:
    """收集目录树并确认其中没有任何文件/链接；返回先序目录列表。"""
    if os.path.islink(path):
        raise OSError("这是一个符号链接，已跳过")
    if not os.path.isdir(to_extended(path)):
        raise OSError("文件夹不存在")
    dirs: list[str] = []
    stack = [path]
    while stack:
        current = stack.pop()
        dirs.append(current)
        try:
            with os.scandir(to_extended(current)) as iterator:
                for entry in iterator:
                    try:
                        st = entry.stat(follow_symlinks=False)
                        is_dir = entry.is_dir(follow_symlinks=False)
                    except OSError as exc:
                        raise OSError(f"无法检查：{_describe_error(exc)}") from exc
                    if getattr(st, "st_file_attributes", 0) & FILE_ATTRIBUTE_REPARSE_POINT:
                        raise OSError("包含符号链接/联接点，已跳过")
                    if is_dir:
                        stack.append(from_extended(entry.path))
                    else:
                        raise OSError("文件夹中出现了文件，已跳过")
        except OSError as exc:
            if str(exc).startswith(("文件夹中出现了文件", "包含符号链接", "无法检查")):
                raise
            raise OSError(f"无法读取：{_describe_error(exc)}") from exc
    return dirs


def delete_empty_dirs(paths: list[str], mode: str = "recycle") -> EmptyDeleteOutcome:
    """删除空目录。mode: recycle=回收站, direct=直接删除（仅目录）。"""
    outcome = EmptyDeleteOutcome()

    if mode == "recycle":
        # 先逐个复查，再合并成一次 SHFileOperation 调用，避免几百个目录时特别慢
        safe: list[str] = []
        for path in dict.fromkeys(paths):
            try:
                _collect_empty_tree(path)
                safe.append(path)
            except OSError as exc:
                outcome.failures.append((path, _describe_error(exc)))
        if not safe:
            return outcome
        recycled = recycle_files(safe)
        removed_keys = {os.path.normcase(os.path.normpath(item)) for item in recycled.removed}
        failure_map = {
            os.path.normcase(os.path.normpath(item)): reason for item, reason in recycled.failures
        }
        for path in safe:
            key = os.path.normcase(os.path.normpath(path))
            if key in removed_keys:
                outcome.removed.append(path)
            else:
                outcome.failures.append((path, failure_map.get(key, "无法移入回收站")))
        return outcome

    for path in dict.fromkeys(paths):
        try:
            dirs = _collect_empty_tree(path)
            for current in reversed(dirs):
                os.rmdir(to_extended(current))
            outcome.removed.append(path)
        except OSError as exc:
            outcome.failures.append((path, _describe_error(exc)))
    return outcome
