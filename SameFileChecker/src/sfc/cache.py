"""哈希缓存（SQLite）：重复扫描时跳过未修改文件的读取。

只做性能优化，缓存失效（大小或修改时间变化）时自动重算，不影响正确性。
"""

from __future__ import annotations

import os
import sqlite3
import threading
import time
from dataclasses import dataclass

from .models import FileEntry
from .util import norm_key

_SCHEMA = """
CREATE TABLE IF NOT EXISTS hashes (
    path      TEXT PRIMARY KEY,
    size      INTEGER NOT NULL,
    mtime_ns  INTEGER NOT NULL,
    head      BLOB,
    strong    BLOB,
    full      BLOB,
    updated   REAL NOT NULL
)
"""


@dataclass(slots=True)
class CachedHashes:
    head: bytes | None
    strong: bytes | None
    full: bytes | None


def default_cache_path() -> str:
    base = os.environ.get("LOCALAPPDATA") or os.path.expanduser("~")
    return os.path.join(base, "SameFileChecker", "hash_cache.db")


class HashCache:
    def __init__(self, path: str | None = None) -> None:
        self.path = path or default_cache_path()
        self.error: str | None = None
        self._lock = threading.Lock()
        self._conn: sqlite3.Connection | None = None
        try:
            parent = os.path.dirname(self.path)
            if parent:
                os.makedirs(parent, exist_ok=True)
            conn = sqlite3.connect(self.path, timeout=5.0, check_same_thread=False)
            conn.execute("PRAGMA journal_mode=WAL")
            conn.execute("PRAGMA synchronous=NORMAL")
            conn.execute(_SCHEMA)
            conn.commit()
            self._conn = conn
        except (sqlite3.Error, OSError) as exc:
            self.error = str(exc)
            self._conn = None

    @property
    def ok(self) -> bool:
        return self._conn is not None

    def get_many(self, entries: list[FileEntry]) -> dict[str, CachedHashes]:
        """按路径批量取缓存；大小或修改时间不一致的条目直接丢弃。"""
        conn = self._conn
        if conn is None or not entries:
            return {}
        wanted = {norm_key(e.path): e for e in entries}
        rows: dict[str, tuple] = {}
        keys = list(wanted)
        try:
            with self._lock:
                for start in range(0, len(keys), 400):
                    chunk = keys[start:start + 400]
                    marks = ",".join("?" * len(chunk))
                    cur = conn.execute(
                        f"SELECT path, size, mtime_ns, head, strong, full "
                        f"FROM hashes WHERE path IN ({marks})",
                        chunk,
                    )
                    for row in cur:
                        rows[row[0]] = row
        except sqlite3.Error as exc:
            self.error = str(exc)
            return {}

        result: dict[str, CachedHashes] = {}
        for key, entry in wanted.items():
            row = rows.get(key)
            if row is None:
                continue
            _, size, mtime_ns, head, strong, full = row
            if size != entry.size or mtime_ns != entry.mtime_ns:
                continue
            if head is None:
                # 缓存行必须至少有头指纹才能参与分组
                continue
            result[entry.path] = CachedHashes(
                head=bytes(head),
                strong=bytes(strong) if strong is not None else None,
                full=bytes(full) if full is not None else None,
            )
        return result

    def put_many(self, rows: list[tuple[FileEntry, bytes | None, bytes | None, bytes | None]]) -> None:
        conn = self._conn
        if conn is None or not rows:
            return
        now = time.time()
        payload = [
            (
                norm_key(entry.path),
                entry.size,
                entry.mtime_ns,
                head,
                strong,
                full,
                now,
            )
            for entry, head, strong, full in rows
            if head is not None or full is not None
        ]
        if not payload:
            return
        try:
            with self._lock:
                conn.executemany(
                    "INSERT INTO hashes(path, size, mtime_ns, head, strong, full, updated) "
                    "VALUES(?,?,?,?,?,?,?) "
                    "ON CONFLICT(path) DO UPDATE SET "
                    "size=excluded.size, mtime_ns=excluded.mtime_ns, "
                    "head=excluded.head, strong=excluded.strong, full=excluded.full, "
                    "updated=excluded.updated",
                    payload,
                )
                conn.commit()
        except sqlite3.Error as exc:
            self.error = str(exc)

    def count(self) -> int:
        conn = self._conn
        if conn is None:
            return 0
        try:
            with self._lock:
                row = conn.execute("SELECT COUNT(*) FROM hashes").fetchone()
                return int(row[0]) if row else 0
        except sqlite3.Error:
            return 0

    def clear(self) -> None:
        conn = self._conn
        if conn is None:
            return
        try:
            with self._lock:
                conn.execute("DELETE FROM hashes")
                conn.commit()
        except sqlite3.Error as exc:
            self.error = str(exc)

    def close(self) -> None:
        conn = self._conn
        self._conn = None
        if conn is not None:
            try:
                conn.close()
            except sqlite3.Error:
                pass
