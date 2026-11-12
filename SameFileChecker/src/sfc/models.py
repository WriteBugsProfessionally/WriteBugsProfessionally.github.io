"""数据模型：文件条目、重复组、扫描统计与结果。"""

from __future__ import annotations

import os
from dataclasses import dataclass, field

from .util import IMAGE_EXTS, VIDEO_EXTS


@dataclass(slots=True)
class FileEntry:
    path: str          # 规范化绝对路径（展示用，不带 \\?\ 前缀）
    size: int
    mtime_ns: int
    dev: int = 0
    ino: int = 0
    ext: str = ""

    @property
    def name(self) -> str:
        return os.path.basename(self.path)

    @property
    def directory(self) -> str:
        return os.path.dirname(self.path)

    @property
    def kind(self) -> str:
        if self.ext in IMAGE_EXTS:
            return "图片"
        if self.ext in VIDEO_EXTS:
            return "视频"
        return "其他"


@dataclass(slots=True)
class DuplicateGroup:
    digest: bytes
    files: list[FileEntry]

    @property
    def count(self) -> int:
        return len(self.files)

    @property
    def file_size(self) -> int:
        return self.files[0].size if self.files else 0

    @property
    def total_size(self) -> int:
        return sum(f.size for f in self.files)

    @property
    def wasted(self) -> int:
        return self.file_size * max(0, len(self.files) - 1)

    @property
    def kind(self) -> str:
        kinds = {f.kind for f in self.files}
        if len(kinds) == 1:
            return kinds.pop()
        return "混合"


@dataclass(slots=True)
class ScanError:
    path: str
    message: str


@dataclass(slots=True)
class ScanStats:
    files_seen: int = 0
    dirs_seen: int = 0
    candidates: int = 0
    hashed_files: int = 0
    bytes_read: int = 0
    elapsed: float = 0.0
    groups: int = 0
    duplicate_files: int = 0
    wasted: int = 0


@dataclass(slots=True)
class ScanResult:
    groups: list[DuplicateGroup] = field(default_factory=list)
    stats: ScanStats = field(default_factory=ScanStats)
    errors: list[ScanError] = field(default_factory=list)
    cancelled: bool = False
    roots: list[str] = field(default_factory=list)

    @property
    def wasted(self) -> int:
        return self.stats.wasted


class ScanCancelled(Exception):
    """用户取消扫描。"""
