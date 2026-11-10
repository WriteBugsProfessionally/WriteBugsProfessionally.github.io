"""文件操作：移入回收站（可撤销）、移入隔离区、在资源管理器中定位。"""

from __future__ import annotations

import ctypes
import os
import shutil
import subprocess
import time
from dataclasses import dataclass
from datetime import datetime

from .util import to_extended

if os.name == "nt":
    from ctypes import wintypes

    class _SHFILEOPSTRUCTW(ctypes.Structure):
        _fields_ = [
            ("hwnd", wintypes.HWND),
            ("wFunc", wintypes.UINT),
            ("pFrom", wintypes.LPCWSTR),
            ("pTo", wintypes.LPCWSTR),
            ("fFlags", ctypes.c_ushort),
            ("fAnyOperationsAborted", wintypes.BOOL),
            ("hNameMappings", ctypes.c_void_p),
            ("lpszProgressTitle", wintypes.LPCWSTR),
        ]

    _FO_DELETE = 3
    _FOF_SILENT = 0x0004
    _FOF_NOCONFIRMATION = 0x0010
    _FOF_ALLOWUNDO = 0x0040
    _FOF_NOERRORUI = 0x0400

    _shell32 = ctypes.windll.shell32
    _SHFileOperationW = _shell32.SHFileOperationW
    _SHFileOperationW.argtypes = [ctypes.POINTER(_SHFILEOPSTRUCTW)]
    _SHFileOperationW.restype = ctypes.c_int


@dataclass(slots=True)
class DeleteOutcome:
    removed: list[str]
    failures: list[tuple[str, str]]


@dataclass(slots=True)
class MoveOutcome:
    moved: list[tuple[str, str]]
    failures: list[tuple[str, str]]
    folder: str


def _normal(path: str) -> str:
    return os.path.normpath(os.path.abspath(path))


def recycle_files(paths: list[str]) -> DeleteOutcome:
    """移入 Windows 回收站；失败的文件保持原样。"""
    if not paths:
        return DeleteOutcome([], [])
    if os.name != "nt":
        return DeleteOutcome([], [(p, "当前系统不支持回收站") for p in paths])

    candidates = [_normal(p) for p in paths]
    # SHFileOperation 不支持 \\?\ 前缀，也不支持超长路径
    normal = [p for p in candidates if not p.startswith("\\\\?\\") and len(p) < 260]
    skipped = [(p, "路径过长，无法移入回收站，请改用隔离区") for p in candidates if p not in normal]

    removed: list[str] = []
    failures: list[tuple[str, str]] = list(skipped)
    if normal:
        buf = "\0".join(normal) + "\0\0"
        op = _SHFILEOPSTRUCTW()
        op.hwnd = None
        op.wFunc = _FO_DELETE
        op.pFrom = buf
        op.pTo = None
        op.fFlags = _FOF_ALLOWUNDO | _FOF_NOCONFIRMATION | _FOF_SILENT | _FOF_NOERRORUI
        op.fAnyOperationsAborted = False
        op.hNameMappings = None
        op.lpszProgressTitle = None
        code = _SHFileOperationW(ctypes.byref(op))
        aborted = bool(op.fAnyOperationsAborted)
        for path in normal:
            exists = os.path.exists(to_extended(path))
            if exists:
                reason = "操作被取消" if aborted else "无法移入回收站（文件被占用、只读或权限不足）"
                failures.append((path, reason))
            else:
                removed.append(path)
        if code != 0 and not removed and not failures:
            failures.extend((p, f"回收站操作失败（错误码 0x{code:04X}）") for p in normal)
    return DeleteOutcome(removed, failures)


def _unique_destination(folder: str, name: str) -> str:
    dest = os.path.join(folder, name)
    if not os.path.exists(to_extended(dest)):
        return dest
    stem, ext = os.path.splitext(name)
    for index in range(2, 10000):
        candidate = os.path.join(folder, f"{stem} ({index}){ext}")
        if not os.path.exists(to_extended(candidate)):
            return candidate
    return os.path.join(folder, f"{stem}_{int(time.time() * 1000)}{ext}")


def move_to_quarantine(paths: list[str], quarantine_root: str) -> MoveOutcome:
    """把文件移动到隔离区（同一盘符为瞬时改名，跨盘为复制后删除）。"""
    folder = os.path.join(
        os.path.abspath(quarantine_root), datetime.now().strftime("%Y%m%d_%H%M%S")
    )
    os.makedirs(folder, exist_ok=True)
    moved: list[tuple[str, str]] = []
    failures: list[tuple[str, str]] = []
    for path in paths:
        src = _normal(path)
        try:
            dest = _unique_destination(folder, os.path.basename(src))
            shutil.move(to_extended(src), to_extended(dest))
            moved.append((src, dest))
        except (OSError, shutil.Error) as exc:
            failures.append((src, str(exc)))
    return MoveOutcome(moved, failures, folder)


def open_in_explorer(path: str) -> bool:
    """在资源管理器中选中文件（或打开文件夹）。"""
    text = _normal(path)
    try:
        if os.path.isdir(text):
            os.startfile(text)  # type: ignore[attr-defined]
        else:
            subprocess.Popen(f'explorer /select,"{text}"')
        return True
    except OSError:
        return False


def open_file(path: str) -> bool:
    try:
        os.startfile(path)  # type: ignore[attr-defined]
        return True
    except OSError:
        return False
