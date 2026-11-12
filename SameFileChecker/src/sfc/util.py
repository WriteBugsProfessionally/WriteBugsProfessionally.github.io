"""通用小工具：Windows 长路径、文件大小/时间格式化、资源路径。"""

from __future__ import annotations

import os
import sys
from datetime import datetime

_EXT_PREFIX = "\\\\?\\"
_EXT_UNC_PREFIX = "\\\\?\\UNC\\"

IMAGE_EXTS: frozenset[str] = frozenset(
    {
        "jpg", "jpeg", "jpe", "jfif", "png", "gif", "bmp", "webp", "tif", "tiff",
        "heic", "heif", "avif", "ico", "svg", "psd",
        "raw", "cr2", "cr3", "nef", "arw", "dng", "orf", "rw2", "srw", "raf", "pef",
    }
)

VIDEO_EXTS: frozenset[str] = frozenset(
    {
        "mp4", "m4v", "mkv", "avi", "mov", "wmv", "flv", "webm", "mpg", "mpeg",
        "ts", "m2ts", "mts", "3gp", "3g2", "vob", "rm", "rmvb", "asf", "ogv",
        "divx", "f4v", "mxf",
    }
)

AUDIO_EXTS: frozenset[str] = frozenset(
    {"mp3", "flac", "wav", "aac", "m4a", "ogg", "wma", "ape", "opus", "aiff", "dsf"}
)

MEDIA_EXTS: frozenset[str] = IMAGE_EXTS | VIDEO_EXTS | AUDIO_EXTS


def to_extended(path: str) -> str:
    """给超长绝对路径加 Windows 扩展前缀，避免 MAX_PATH 限制。"""
    if path.startswith(_EXT_PREFIX):
        return path
    p = os.path.abspath(path)
    if len(p) < 240:
        return p
    if p.startswith("\\\\"):
        return _EXT_UNC_PREFIX + p[2:]
    return _EXT_PREFIX + p


def from_extended(path: str) -> str:
    if path.startswith(_EXT_UNC_PREFIX):
        return "\\\\" + path[len(_EXT_UNC_PREFIX):]
    if path.startswith(_EXT_PREFIX):
        return path[len(_EXT_PREFIX):]
    return path


def norm_key(path: str) -> str:
    """用于去重/缓存键的规范化路径（大小写不敏感，保留原始展示路径另行保存）。"""
    return os.path.normcase(os.path.normpath(from_extended(path)))


def split_ext(name: str) -> str:
    """返回小写扩展名（不带点）；没有扩展名返回空串。"""
    base = name.rsplit("\\", 1)[-1]
    if "." not in base:
        return ""
    ext = base.rsplit(".", 1)[-1].lower()
    return ext if ext and len(ext) <= 12 else ""


def human_size(num: int | float, decimals: int | None = None) -> str:
    """1536 -> '1.5 KB'。二进制单位，符合资源管理器习惯。"""
    n = float(num)
    units = ("B", "KB", "MB", "GB", "TB", "PB")
    idx = 0
    while n >= 1024 and idx < len(units) - 1:
        n /= 1024.0
        idx += 1
    if idx == 0:
        return f"{int(n)} B"
    if decimals is None:
        decimals = 2 if n < 10 else 1
    text = f"{n:.{decimals}f}".rstrip("0").rstrip(".")
    return f"{text} {units[idx]}"


def human_count(num: int) -> str:
    return f"{num:,}"


def format_elapsed(seconds: float) -> str:
    if seconds < 1:
        return "不到 1 秒"
    seconds = int(round(seconds))
    if seconds < 60:
        return f"{seconds} 秒"
    minutes, sec = divmod(seconds, 60)
    if minutes < 60:
        return f"{minutes} 分 {sec:02d} 秒"
    hours, minutes = divmod(minutes, 60)
    return f"{hours} 小时 {minutes:02d} 分"


def format_mtime(mtime_ns: int) -> str:
    try:
        return datetime.fromtimestamp(mtime_ns / 1_000_000_000).strftime("%Y-%m-%d %H:%M")
    except (OSError, OverflowError, ValueError):
        return "—"


def shorten_path(path: str, limit: int = 72) -> str:
    if len(path) <= limit:
        return path
    keep = max(8, (limit - 3) // 2)
    return path[:keep] + "…" + path[-keep:]


def resource_path(relative: str) -> str:
    """兼容源码运行与 PyInstaller 打包后的资源定位。"""
    base = getattr(sys, "_MEIPASS", None)
    if base:
        return os.path.join(base, relative)
    here = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))  # src/
    return os.path.join(here, relative)


def app_dir() -> str:
    if getattr(sys, "frozen", False):
        return os.path.dirname(os.path.abspath(sys.executable))
    return os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


def is_windows() -> bool:
    return os.name == "nt"
