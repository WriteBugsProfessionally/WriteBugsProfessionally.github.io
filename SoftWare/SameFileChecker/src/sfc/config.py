"""应用设置（不依赖 Qt，方便测试）。"""

from __future__ import annotations

import os
from dataclasses import dataclass, field

from .scanner import DEFAULT_EXCLUDE_DIRS, ScanOptions
from .util import IMAGE_EXTS, MEDIA_EXTS, VIDEO_EXTS

# 下拉框里的顺序
EXT_PRESETS: dict[str, tuple[str, frozenset[str] | None]] = {
    "media": ("图片 + 视频（推荐）", IMAGE_EXTS | VIDEO_EXTS),
    "images": ("仅图片", IMAGE_EXTS),
    "videos": ("仅视频", VIDEO_EXTS),
    "av": ("图片 + 视频 + 音频", MEDIA_EXTS),
    "all": ("所有文件", None),
    "custom": ("自定义扩展名…", frozenset()),
}

KEEP_POLICIES: list[tuple[str, str]] = [
    ("oldest", "保留修改时间最早的"),
    ("newest", "保留修改时间最新的"),
    ("shortest_path", "保留路径最短的"),
    ("shortest_name", "保留文件名最短的"),
]

DELETE_MODES: list[tuple[str, str]] = [
    ("recycle", "移入回收站（可从回收站还原）"),
    ("quarantine", "移动到隔离文件夹"),
]


def default_quarantine_dir() -> str:
    return os.path.join(os.path.expanduser("~"), "SameFileChecker_Quarantine")


@dataclass(slots=True)
class AppSettings:
    min_size_kb: int = 1
    ext_preset: str = "media"
    custom_exts: str = "jpg,png,mp4"
    workers: int = 4
    include_hidden: bool = False
    use_cache: bool = True
    auto_select: bool = True
    policy: str = "oldest"
    delete_mode: str = "recycle"
    quarantine_dir: str = field(default_factory=default_quarantine_dir)
    exclude_dirs: str = field(default="; ".join(DEFAULT_EXCLUDE_DIRS))

    def extensions(self) -> frozenset[str] | None:
        if self.ext_preset == "custom":
            items = [
                item.strip().lower().lstrip(".")
                for item in self.custom_exts.replace(";", ",").split(",")
                if item.strip()
            ]
            return frozenset(items) or None
        return EXT_PRESETS.get(self.ext_preset, EXT_PRESETS["media"])[1]

    def build_options(self) -> ScanOptions:
        excludes = [
            item.strip().casefold()
            for item in self.exclude_dirs.replace("\n", ";").split(";")
            if item.strip()
        ]
        return ScanOptions(
            min_size=max(0, int(self.min_size_kb)) * 1024,
            exts=self.extensions(),
            exclude_dirs=frozenset(excludes),
            include_hidden=bool(self.include_hidden),
            workers=max(1, min(16, int(self.workers))),
            use_cache=bool(self.use_cache),
        )

    def policy_label(self) -> str:
        return dict(KEEP_POLICIES).get(self.policy, KEEP_POLICIES[0][1])
