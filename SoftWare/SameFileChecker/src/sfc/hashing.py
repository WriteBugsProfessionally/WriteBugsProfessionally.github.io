"""分阶段哈希：文件头 → 中段+尾部 → 全文，尽量少读机械硬盘。

所有摘要都混入文件大小，避免不同大小文件的摘要被放在一起比较。
"""

from __future__ import annotations

import os
import struct
import threading
from hashlib import blake2b
from typing import BinaryIO, Callable

from .models import FileEntry, ScanCancelled
from .util import to_extended

HEAD_BYTES = 128 * 1024          # 第一阶段只读文件头 128 KB
MID_BYTES = 128 * 1024           # 第二阶段读中段 + 尾部各 128 KB
CHUNK = 4 * 1024 * 1024          # 全量哈希时的顺序读块
FULL_DIRECT = 512 * 1024         # ≤ 512 KB 的文件直接全文哈希，省一次往返

_SIZE = struct.Struct("<Q")

ProgressFn = Callable[[int], None]


class FileChanged(Exception):
    """扫描期间文件被修改/替换。"""


def _open(entry: FileEntry) -> BinaryIO:
    return open(to_extended(entry.path), "rb", buffering=0)


def _check_stat(f: BinaryIO, entry: FileEntry) -> None:
    st = os.fstat(f.fileno())
    if st.st_size != entry.size or st.st_mtime_ns != entry.mtime_ns:
        raise FileChanged("文件在扫描期间发生变化")


def _check_cancel(cancel: threading.Event | None) -> None:
    if cancel is not None and cancel.is_set():
        raise ScanCancelled()


def _read_exact(
    f: BinaryIO,
    length: int,
    cancel: threading.Event | None,
    on_bytes: ProgressFn | None,
) -> bytes:
    buf = bytearray(length)
    view = memoryview(buf)
    got = 0
    while got < length:
        _check_cancel(cancel)
        n = f.readinto(view[got:])
        if not n:
            raise FileChanged("文件长度在读取期间变短")
        got += n
        if on_bytes is not None:
            on_bytes(n)
    return bytes(buf)


def hash_head(
    entry: FileEntry,
    cancel: threading.Event | None = None,
    on_bytes: ProgressFn | None = None,
) -> bytes:
    """第一级指纹：大小 + 前 128 KB。"""
    h = blake2b(digest_size=16)
    h.update(_SIZE.pack(entry.size))
    length = min(HEAD_BYTES, entry.size)
    with _open(entry) as f:
        _check_stat(f, entry)
        data = _read_exact(f, length, cancel, on_bytes)
        h.update(data)
        _check_stat(f, entry)
    return h.digest()


def hash_strong(
    entry: FileEntry,
    head: bytes,
    cancel: threading.Event | None = None,
    on_bytes: ProgressFn | None = None,
) -> bytes:
    """第二级指纹：大小 + 头指纹 + 中段 128 KB + 尾部 128 KB。"""
    h = blake2b(digest_size=16)
    h.update(_SIZE.pack(entry.size))
    h.update(head)
    mid_len = min(MID_BYTES, entry.size)
    mid_start = max(0, entry.size // 2 - mid_len // 2)
    tail_len = min(MID_BYTES, entry.size)
    tail_start = max(0, entry.size - tail_len)
    with _open(entry) as f:
        _check_stat(f, entry)
        f.seek(mid_start)
        h.update(_read_exact(f, mid_len, cancel, on_bytes))
        if tail_start > mid_start + mid_len:
            f.seek(tail_start)
            h.update(_read_exact(f, tail_len, cancel, on_bytes))
        _check_stat(f, entry)
    return h.digest()


def hash_full(
    entry: FileEntry,
    cancel: threading.Event | None = None,
    on_bytes: ProgressFn | None = None,
    capture_head: bool = False,
) -> tuple[bytes, bytes | None]:
    """全文 BLAKE2b-256。返回 (全文摘要, 头摘要或 None)。"""
    h = blake2b(digest_size=32)
    h.update(_SIZE.pack(entry.size))
    hh = blake2b(digest_size=16) if capture_head else None
    if hh is not None:
        hh.update(_SIZE.pack(entry.size))
    head_left = min(HEAD_BYTES, entry.size) if capture_head else 0
    remaining = entry.size
    with _open(entry) as f:
        _check_stat(f, entry)
        while remaining > 0:
            _check_cancel(cancel)
            data = f.read(min(CHUNK, remaining))
            if not data:
                raise FileChanged("文件长度在读取期间变短")
            h.update(data)
            if hh is not None and head_left > 0:
                take = min(head_left, len(data))
                hh.update(data[:take])
                head_left -= take
            remaining -= len(data)
            if on_bytes is not None:
                on_bytes(len(data))
        _check_stat(f, entry)
    return h.digest(), (hh.digest() if hh is not None else None)


def quick_cost(size: int) -> int:
    """第一阶段预计读取的字节数（进度用）。"""
    return size if size <= FULL_DIRECT else min(HEAD_BYTES, size)


def strong_cost(size: int) -> int:
    """第二阶段预计读取的字节数（进度用）。"""
    if size <= FULL_DIRECT:
        return 0
    return min(MID_BYTES, size) * 2
