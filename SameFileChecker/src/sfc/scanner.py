"""查重引擎：枚举 → 按大小分组 → 文件头 → 中段/尾部 → 全文。

设计目标：在机械硬盘上尽量少读数据。只有大小相同的文件才会被读取；
先用 128 KB 文件头筛掉绝大多数不同文件，再对仍然撞车的文件做全文校验。
"""

from __future__ import annotations

import os
import threading
import time
from collections import defaultdict
from concurrent.futures import Future, ThreadPoolExecutor, as_completed
from dataclasses import dataclass
from typing import Callable

from .cache import HashCache
from .hashing import (
    FULL_DIRECT,
    FileChanged,
    hash_full,
    hash_head,
    hash_strong,
    quick_cost,
    strong_cost,
)
from .models import (
    DuplicateGroup,
    FileEntry,
    ScanCancelled,
    ScanError,
    ScanResult,
)
from .util import from_extended, split_ext, to_extended

FILE_ATTRIBUTE_HIDDEN = 0x2
FILE_ATTRIBUTE_SYSTEM = 0x4
FILE_ATTRIBUTE_REPARSE_POINT = 0x400

MAX_ERRORS = 2000

PHASE_ENUMERATING = "enumerating"
PHASE_QUICK = "quick"
PHASE_STRONG = "strong"
PHASE_FULL = "full"
PHASE_FINALIZING = "finalizing"

PHASE_LABELS = {
    PHASE_ENUMERATING: "正在枚举文件",
    PHASE_QUICK: "第一轮 · 读取文件头",
    PHASE_STRONG: "第二轮 · 校验中段与尾部",
    PHASE_FULL: "第三轮 · 完整内容校验",
    PHASE_FINALIZING: "正在整理结果",
}

DEFAULT_EXCLUDE_DIRS: tuple[str, ...] = (
    "$recycle.bin",
    "system volume information",
    "windows",
    "windows.old",
    "program files",
    "program files (x86)",
    "programdata",
    "appdata",
    "$windows.~bt",
    "$windows.~ws",
    "recovery",
    "perflogs",
    ".git",
    ".svn",
    ".hg",
    "node_modules",
    "__pycache__",
    ".venv",
    "venv",
    ".idea",
    ".vs",
    ".cache",
)

PhaseCallback = Callable[[str, str, int, int], None]
ProgressCallback = Callable[[int, int, int, int, float], None]
ErrorCallback = Callable[[str, str], None]


@dataclass(slots=True)
class ScanOptions:
    min_size: int = 1024
    exts: frozenset[str] | None = None      # None = 所有文件
    exclude_dirs: frozenset[str] = frozenset(DEFAULT_EXCLUDE_DIRS)
    include_hidden: bool = False
    workers: int = 4
    use_cache: bool = True

    def normalized(self) -> "ScanOptions":
        exts = self.exts
        if exts is not None:
            exts = frozenset(e.lower().lstrip(".") for e in exts if e)
        return ScanOptions(
            min_size=max(0, int(self.min_size)),
            exts=exts,
            exclude_dirs=frozenset(d.casefold() for d in self.exclude_dirs if d),
            include_hidden=bool(self.include_hidden),
            workers=max(1, min(16, int(self.workers))),
            use_cache=bool(self.use_cache),
        )


@dataclass(slots=True)
class _Work:
    entry: FileEntry
    head: bytes | None = None
    strong: bytes | None = None
    full: bytes | None = None
    computed: bool = False


class _Reporter:
    """线程安全的进度聚合器，限制 UI 刷新频率。"""

    def __init__(
        self,
        on_phase: PhaseCallback | None,
        on_progress: ProgressCallback | None,
        on_error: ErrorCallback | None,
        cancel: threading.Event | None,
    ) -> None:
        self.on_phase = on_phase
        self.on_progress = on_progress
        self.on_error = on_error
        self.cancel = cancel or threading.Event()
        self._lock = threading.Lock()
        self._phase_start = time.monotonic()
        self._last_emit = 0.0
        self._total_bytes = 0
        self._total_files = 0
        self._done_bytes = 0
        self._done_files = 0
        self._grand_bytes = 0

    @property
    def grand_bytes(self) -> int:
        with self._lock:
            return self._grand_bytes

    def phase(self, key: str, label: str, total_bytes: int = 0, total_files: int = 0) -> None:
        now = time.monotonic()
        with self._lock:
            self._total_bytes = max(0, total_bytes)
            self._total_files = max(0, total_files)
            self._done_bytes = 0
            self._done_files = 0
            self._phase_start = now
            self._last_emit = 0.0
        if self.on_phase is not None:
            self.on_phase(key, label, total_bytes, total_files)
        self._emit(force=True)

    def add(self, nbytes: int = 0, nfiles: int = 0) -> None:
        emit = False
        snapshot = None
        with self._lock:
            self._done_bytes += nbytes
            self._grand_bytes += nbytes
            self._done_files += nfiles
            now = time.monotonic()
            if now - self._last_emit >= 0.1:
                self._last_emit = now
                elapsed = max(1e-6, now - self._phase_start)
                snapshot = (
                    self._done_bytes,
                    self._total_bytes,
                    self._done_files,
                    self._total_files,
                    self._done_bytes / elapsed,
                )
                emit = True
        if emit and self.on_progress is not None and snapshot is not None:
            self.on_progress(*snapshot)

    def _emit(self, force: bool = False) -> None:
        if self.on_progress is None:
            return
        with self._lock:
            now = time.monotonic()
            elapsed = max(1e-6, now - self._phase_start)
            snapshot = (
                self._done_bytes,
                self._total_bytes,
                self._done_files,
                self._total_files,
                self._done_bytes / elapsed,
            )
            self._last_emit = now
        if force or snapshot[0] or snapshot[2]:
            self.on_progress(*snapshot)

    def error(self, path: str, message: str) -> None:
        if self.on_error is not None:
            self.on_error(path, message)


def _is_inside(path: str, parent: str) -> bool:
    try:
        common = os.path.commonpath([os.path.normcase(path), os.path.normcase(parent)])
    except ValueError:
        return False
    return common == os.path.normcase(parent)


def prepare_roots(roots: list[str]) -> tuple[list[str], list[ScanError]]:
    """规范化扫描根目录，去掉不存在和互相嵌套的目录，避免重复枚举。"""
    errors: list[ScanError] = []
    cleaned: list[str] = []
    for root in roots:
        text = (root or "").strip().strip('"')
        if not text:
            continue
        path = os.path.normpath(os.path.abspath(os.path.expanduser(text)))
        try:
            ok = os.path.isdir(to_extended(path))
        except OSError:
            ok = False
        if not ok:
            errors.append(ScanError(path, "文件夹不存在或无法访问"))
            continue
        cleaned.append(path)

    cleaned.sort(key=lambda p: (len(p), p.casefold()))
    kept: list[str] = []
    for path in cleaned:
        if any(_is_inside(path, parent) for parent in kept):
            continue
        kept.append(path)
    return kept, errors


def _describe_error(exc: BaseException) -> str:
    if isinstance(exc, FileChanged):
        return str(exc)
    if isinstance(exc, PermissionError):
        return "没有读取权限"
    if isinstance(exc, FileNotFoundError):
        return "文件已被移动或删除"
    if isinstance(exc, OSError):
        return exc.strerror or str(exc)
    return str(exc) or exc.__class__.__name__


def scan(
    roots: list[str],
    options: ScanOptions | None = None,
    *,
    cache: HashCache | None = None,
    on_phase: PhaseCallback | None = None,
    on_progress: ProgressCallback | None = None,
    on_error: ErrorCallback | None = None,
    cancel: threading.Event | None = None,
) -> ScanResult:
    opts = (options or ScanOptions()).normalized()
    cancel_event = cancel or threading.Event()
    reporter = _Reporter(on_phase, on_progress, on_error, cancel_event)
    result = ScanResult(roots=[])
    started = time.monotonic()

    valid_roots, root_errors = prepare_roots(roots)
    result.errors.extend(root_errors)
    result.roots = valid_roots

    if not valid_roots:
        result.stats.elapsed = time.monotonic() - started
        return result

    reporter.phase(PHASE_ENUMERATING, PHASE_LABELS[PHASE_ENUMERATING], 0, 0)
    try:
        files, dirs_seen = _enumerate(valid_roots, opts, reporter, result.errors, cancel_event)
    except ScanCancelled:
        result.cancelled = True
        result.stats.elapsed = time.monotonic() - started
        return result
    if cancel_event.is_set():
        result.cancelled = True
        result.stats.elapsed = time.monotonic() - started
        return result

    result.stats.files_seen = len(files)
    result.stats.dirs_seen = dirs_seen

    by_size: dict[int, list[FileEntry]] = defaultdict(list)
    for entry in files:
        by_size[entry.size].append(entry)
    candidates = [e for group in by_size.values() if len(group) >= 2 for e in group]
    result.stats.candidates = len(candidates)

    works: list[_Work] = [_Work(entry) for entry in candidates]
    if works:
        _load_cache(works, cache if opts.use_cache else None)
        try:
            _run_hashing(works, opts, reporter, result, cache if opts.use_cache else None, cancel_event)
        except ScanCancelled:
            result.cancelled = True
            result.stats.elapsed = time.monotonic() - started
            return result
        if cancel_event.is_set():
            result.cancelled = True
            result.stats.elapsed = time.monotonic() - started
            return result

    reporter.phase(PHASE_FINALIZING, PHASE_LABELS[PHASE_FINALIZING], 0, 0)
    groups = _build_groups(works)
    groups.sort(key=lambda g: (-g.wasted, -g.count, g.files[0].name.casefold()))

    stats = result.stats
    stats.groups = len(groups)
    stats.duplicate_files = sum(g.count for g in groups)
    stats.wasted = sum(g.wasted for g in groups)
    stats.hashed_files = sum(1 for w in works if w.computed)
    stats.bytes_read = reporter.grand_bytes
    stats.elapsed = time.monotonic() - started
    result.groups = groups
    return result


def _enumerate(
    roots: list[str],
    opts: ScanOptions,
    reporter: _Reporter,
    errors: list[ScanError],
    cancel: threading.Event,
) -> tuple[list[FileEntry], int]:
    files: list[FileEntry] = []
    dirs_seen = 0
    stack = list(reversed(roots))
    exts = opts.exts

    while stack:
        if cancel.is_set():
            raise ScanCancelled()
        current = stack.pop()
        try:
            iterator = os.scandir(to_extended(current))
        except OSError as exc:
            _add_error(errors, reporter, current, _describe_error(exc))
            continue
        with iterator:
            try:
                for entry in iterator:
                    if cancel.is_set():
                        raise ScanCancelled()
                    try:
                        st = entry.stat(follow_symlinks=False)
                    except OSError:
                        continue
                    attrs = getattr(st, "st_file_attributes", 0)
                    if attrs & FILE_ATTRIBUTE_REPARSE_POINT:
                        continue
                    name = entry.name
                    try:
                        is_dir = entry.is_dir(follow_symlinks=False)
                    except OSError:
                        is_dir = False

                    if is_dir:
                        dirs_seen += 1
                        if not opts.include_hidden and attrs & (
                            FILE_ATTRIBUTE_HIDDEN | FILE_ATTRIBUTE_SYSTEM
                        ):
                            continue
                        if name.casefold() in opts.exclude_dirs:
                            continue
                        stack.append(from_extended(entry.path))
                        continue

                    if not opts.include_hidden and attrs & (
                        FILE_ATTRIBUTE_HIDDEN | FILE_ATTRIBUTE_SYSTEM
                    ):
                        continue
                    size = st.st_size
                    if size < opts.min_size:
                        continue
                    ext = split_ext(name)
                    if exts is not None and ext not in exts:
                        continue
                    path = from_extended(entry.path)
                    files.append(
                        FileEntry(
                            path=path,
                            size=size,
                            mtime_ns=st.st_mtime_ns,
                            dev=getattr(st, "st_dev", 0),
                            ino=getattr(st, "st_ino", 0),
                            ext=ext,
                        )
                    )
                    reporter.add(0, 1)
            except OSError as exc:
                _add_error(errors, reporter, current, "读取文件夹时出错：" + _describe_error(exc))
    return files, dirs_seen


def _add_error(
    errors: list[ScanError], reporter: _Reporter, path: str, message: str
) -> None:
    if len(errors) < MAX_ERRORS:
        errors.append(ScanError(path, message))
    reporter.error(path, message)


def _load_cache(works: list[_Work], cache: HashCache | None) -> None:
    if cache is None or not cache.ok:
        return
    cached = cache.get_many([w.entry for w in works])
    if not cached:
        return
    for work in works:
        item = cached.get(work.entry.path)
        if item is None:
            continue
        work.head = item.head
        work.strong = item.strong
        work.full = item.full


def _flush_cache(items: list[_Work], cache: HashCache | None) -> None:
    if cache is None or not cache.ok or not items:
        return
    rows = [(w.entry, w.head, w.strong, w.full) for w in items if w.head is not None]
    cache.put_many(rows)


def _run_tasks(
    tasks: list[tuple[_Work, str, Callable[[_Work], object]]],
    workers: int,
    reporter: _Reporter,
    errors: list[ScanError],
    cancel: threading.Event,
) -> list[tuple[_Work, str, object]]:
    if not tasks:
        return []
    results: list[tuple[_Work, str, object]] = []
    with ThreadPoolExecutor(max_workers=workers, thread_name_prefix="sfc-hash") as pool:
        futures: dict[Future, tuple[_Work, str]] = {}
        for work, kind, fn in tasks:
            futures[pool.submit(fn, work)] = (work, kind)
        for fut in as_completed(futures):
            if cancel.is_set():
                for pending in futures:
                    pending.cancel()
                raise ScanCancelled()
            work, kind = futures[fut]
            reporter.add(0, 1)
            try:
                value = fut.result()
            except ScanCancelled:
                for pending in futures:
                    pending.cancel()
                raise
            except (FileChanged, OSError, ValueError) as exc:
                _add_error(errors, reporter, work.entry.path, _describe_error(exc))
            except Exception as exc:  # 防御：单个文件出错不能拖垮整个扫描
                _add_error(errors, reporter, work.entry.path, _describe_error(exc))
            else:
                results.append((work, kind, value))
    return results


def _run_hashing(
    works: list[_Work],
    opts: ScanOptions,
    reporter: _Reporter,
    result: ScanResult,
    cache: HashCache | None,
    cancel: threading.Event,
) -> None:
    # ---------- 第一轮：文件头 ----------
    small_pending = [
        w for w in works
        if w.full is None and w.head is None and w.entry.size <= FULL_DIRECT
    ]
    head_pending = [
        w for w in works
        if w.full is None and w.head is None and w.entry.size > FULL_DIRECT
    ]
    quick_total = sum(quick_cost(w.entry.size) for w in small_pending + head_pending)
    reporter.phase(
        PHASE_QUICK,
        PHASE_LABELS[PHASE_QUICK],
        quick_total,
        len(small_pending) + len(head_pending),
    )

    def on_bytes(n: int) -> None:
        reporter.add(n)

    def task_full(work: _Work) -> object:
        return hash_full(work.entry, cancel, on_bytes, capture_head=True)

    def task_head(work: _Work) -> object:
        return hash_head(work.entry, cancel, on_bytes)

    tasks: list[tuple[_Work, str, Callable[[_Work], object]]] = []
    tasks.extend((w, "full", task_full) for w in small_pending)
    tasks.extend((w, "head", task_head) for w in head_pending)

    dirty: list[_Work] = []
    for work, kind, value in _run_tasks(tasks, opts.workers, reporter, result.errors, cancel):
        if kind == "full":
            full, head = value  # type: ignore[misc]
            work.full = full
            work.head = head
        else:
            work.head = value  # type: ignore[assignment]
        work.computed = True
        dirty.append(work)
    _flush_cache(dirty, cache)

    # ---------- 第二轮：中段 + 尾部 ----------
    head_groups: dict[bytes, list[_Work]] = defaultdict(list)
    for work in works:
        if work.head is not None:
            head_groups[work.head].append(work)

    strong_need: list[_Work] = []
    forced_full: list[_Work] = []
    for group in head_groups.values():
        if len(group) < 2:
            continue
        if any(w.full is not None for w in group):
            forced_full.extend(w for w in group if w.full is None)
        else:
            strong_need.extend(w for w in group if w.strong is None)

    strong_total = sum(strong_cost(w.entry.size) for w in strong_need)
    reporter.phase(
        PHASE_STRONG,
        PHASE_LABELS[PHASE_STRONG],
        strong_total,
        len(strong_need),
    )

    def task_strong(work: _Work) -> object:
        assert work.head is not None
        return hash_strong(work.entry, work.head, cancel, on_bytes)

    dirty = []
    for work, _kind, value in _run_tasks(
        [(w, "strong", task_strong) for w in strong_need],
        opts.workers,
        reporter,
        result.errors,
        cancel,
    ):
        work.strong = value  # type: ignore[assignment]
        work.computed = True
        dirty.append(work)
    _flush_cache(dirty, cache)

    # ---------- 第三轮：全文 ----------
    full_need: dict[str, _Work] = {w.entry.path: w for w in forced_full}
    strong_groups: dict[bytes, list[_Work]] = defaultdict(list)
    for work in works:
        if work.strong is not None:
            strong_groups[work.strong].append(work)
    for group in strong_groups.values():
        if len(group) < 2:
            continue
        for work in group:
            if work.full is None:
                full_need[work.entry.path] = work

    items = list(full_need.values())
    full_total = sum(w.entry.size for w in items)
    reporter.phase(PHASE_FULL, PHASE_LABELS[PHASE_FULL], full_total, len(items))

    def task_full_any(work: _Work) -> object:
        need_head = work.head is None
        full, head = hash_full(work.entry, cancel, on_bytes, capture_head=need_head)
        return full, head

    dirty = []
    for work, _kind, value in _run_tasks(
        [(w, "full", task_full_any) for w in items],
        opts.workers,
        reporter,
        result.errors,
        cancel,
    ):
        full, head = value  # type: ignore[misc]
        work.full = full
        if work.head is None and head is not None:
            work.head = head
        work.computed = True
        dirty.append(work)
    _flush_cache(dirty, cache)


def _file_inode(entry: FileEntry) -> tuple[int, int]:
    """仅在确认重复后才查询 inode（scandir 在 Windows 上不提供 inode）。"""
    try:
        st = os.stat(to_extended(entry.path), follow_symlinks=False)
    except OSError:
        return (0, 0)
    return (getattr(st, "st_dev", 0), getattr(st, "st_ino", 0))


def _build_groups(works: list[_Work]) -> list[DuplicateGroup]:
    by_full: dict[bytes, list[_Work]] = defaultdict(list)
    for work in works:
        if work.full is not None:
            by_full[work.full].append(work)

    groups: list[DuplicateGroup] = []
    for digest, members in by_full.items():
        if len(members) < 2:
            continue
        unique: list[FileEntry] = []
        seen_inodes: set[tuple[int, int]] = set()
        for work in sorted(members, key=lambda w: w.entry.path.casefold()):
            entry = work.entry
            inode = _file_inode(entry)
            if inode[1]:
                if inode in seen_inodes:
                    # 同一文件的硬链接：删掉一个链接并不会释放空间，不算重复
                    continue
                seen_inodes.add(inode)
            unique.append(entry)
        if len(unique) >= 2:
            groups.append(DuplicateGroup(digest=digest, files=unique))
    return groups
