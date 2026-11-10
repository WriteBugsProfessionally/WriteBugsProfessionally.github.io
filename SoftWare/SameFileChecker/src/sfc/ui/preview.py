"""缩略图加载：图片用 Qt 解码，视频用 FFmpeg 抽首帧（找不到则优雅降级）。"""

from __future__ import annotations

import os
import shutil
import subprocess
import threading
from collections import OrderedDict
from concurrent.futures import ThreadPoolExecutor

from PySide6.QtCore import QObject, QSize, Qt, Signal
from PySide6.QtGui import QImage, QImageReader

from ..models import FileEntry
from ..util import IMAGE_EXTS, VIDEO_EXTS, app_dir, to_extended

_CREATE_NO_WINDOW = 0x08000000 if os.name == "nt" else 0


def find_ffmpeg() -> str | None:
    candidates = [
        shutil.which("ffmpeg"),
        os.path.join(app_dir(), "bin", "ffmpeg.exe"),
        os.path.join(app_dir(), "ffmpeg.exe"),
    ]
    for candidate in candidates:
        if candidate and os.path.isfile(candidate):
            return candidate
    return None


def image_dimensions(path: str) -> QSize:
    reader = QImageReader(path)
    reader.setAutoTransform(True)
    size = reader.size()
    if size.isValid():
        return size
    if "\\\\?\\" not in path:
        reader = QImageReader(to_extended(path))
        reader.setAutoTransform(True)
        size = reader.size()
    return size


class PreviewLoader(QObject):
    loaded = Signal(str, QImage, str)   # 路径, 图片, 说明
    failed = Signal(str, str)           # 路径, 原因
    started = Signal(str)               # 路径

    def __init__(self, parent=None) -> None:
        super().__init__(parent)
        self._pool = ThreadPoolExecutor(max_workers=2, thread_name_prefix="sfc-preview")
        self._cache: OrderedDict[tuple[str, int, int], tuple[QImage, str]] = OrderedDict()
        self._lock = threading.Lock()
        self._ffmpeg = find_ffmpeg()

    @property
    def ffmpeg_available(self) -> bool:
        return bool(self._ffmpeg)

    def request(self, entry: FileEntry) -> None:
        key = (entry.path, entry.size, entry.mtime_ns)
        with self._lock:
            cached = self._cache.get(key)
            if cached is not None:
                self._cache.move_to_end(key)
        if cached is not None:
            self.loaded.emit(entry.path, cached[0], cached[1])
            return
        self.started.emit(entry.path)
        self._pool.submit(self._work, entry, key)

    def _work(self, entry: FileEntry, key: tuple[str, int, int]) -> None:
        try:
            if entry.ext in VIDEO_EXTS:
                image = self._video_frame(entry)
                note = "视频首帧"
            elif entry.ext in IMAGE_EXTS:
                image = self._image_thumb(entry)
                note = "图片"
            else:
                image = None
                note = ""
            if image is None or image.isNull():
                self.failed.emit(entry.path, "无法生成预览")
                return
            with self._lock:
                self._cache[key] = (image, note)
                while len(self._cache) > 60:
                    self._cache.popitem(last=False)
            self.loaded.emit(entry.path, image, note)
        except Exception as exc:  # 预览失败不应影响主流程
            self.failed.emit(entry.path, str(exc) or "预览失败")

    def _image_thumb(self, entry: FileEntry) -> QImage | None:
        for path in (entry.path, to_extended(entry.path)):
            reader = QImageReader(path)
            reader.setAutoTransform(True)
            size = reader.size()
            if size.isValid():
                target = size.scaled(QSize(1000, 1000), Qt.AspectRatioMode.KeepAspectRatio)
                reader.setScaledSize(target)
            image = reader.read()
            if not image.isNull():
                return image
        return None

    def _video_frame(self, entry: FileEntry) -> QImage | None:
        if not self._ffmpeg:
            return None
        for seek in ("1", "0"):
            command = [
                self._ffmpeg,
                "-v", "error",
                "-ss", seek,
                "-i", entry.path,
                "-frames:v", "1",
                "-vf", "scale=960:540:force_original_aspect_ratio=decrease",
                "-f", "image2pipe",
                "-vcodec", "png",
                "-",
            ]
            try:
                completed = subprocess.run(
                    command,
                    capture_output=True,
                    timeout=25,
                    creationflags=_CREATE_NO_WINDOW,
                )
            except (OSError, subprocess.TimeoutExpired):
                continue
            if completed.returncode == 0 and completed.stdout:
                image = QImage.fromData(completed.stdout, "PNG")
                if not image.isNull():
                    return image
        return None

    def shutdown(self) -> None:
        self._pool.shutdown(wait=False, cancel_futures=True)
