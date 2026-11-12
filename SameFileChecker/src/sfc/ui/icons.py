"""内联 SVG 图标 + 程序 Logo（不依赖外部资源，打包更省心）。"""

from __future__ import annotations

import math

from PySide6.QtCore import QByteArray, QRect, QRectF, Qt
from PySide6.QtGui import QColor, QIcon, QImage, QPainter, QPen, QPixmap
from PySide6.QtSvg import QSvgRenderer

_ICONS: dict[str, str] = {
    "folder-plus": """
        <path d="M3 7.5A2.5 2.5 0 0 1 5.5 5H9l2 2.5h7.5A2.5 2.5 0 0 1 21 10v7.5A2.5 2.5 0 0 1 18.5 20h-13A2.5 2.5 0 0 1 3 17.5z"/>
        <path d="M12 11v6M9 14h6"/>""",
    "folder": """
        <path d="M3 7.5A2.5 2.5 0 0 1 5.5 5H9l2 2.5h7.5A2.5 2.5 0 0 1 21 10v7.5A2.5 2.5 0 0 1 18.5 20h-13A2.5 2.5 0 0 1 3 17.5z"/>""",
    "folder-minus": """
        <path d="M3 7.5A2.5 2.5 0 0 1 5.5 5H9l2 2.5h7.5A2.5 2.5 0 0 1 21 10v7.5A2.5 2.5 0 0 1 18.5 20h-13A2.5 2.5 0 0 1 3 17.5z"/>
        <path d="M9 14h6"/>""",
    "play": """<path d="M8 5.4v13.2a1 1 0 0 0 1.52.85l10.2-6.6a1 1 0 0 0 0-1.7L9.52 4.55A1 1 0 0 0 8 5.4z" fill="currentColor" stroke="none"/>""",
    "stop": """<rect x="6" y="6" width="12" height="12" rx="2.6" fill="currentColor" stroke="none"/>""",
    "trash": """
        <path d="M4 7h16M9.5 7V5.6A1.6 1.6 0 0 1 11.1 4h1.8a1.6 1.6 0 0 1 1.6 1.6V7"/>
        <path d="M6.6 7l.74 11.9A2 2 0 0 0 9.34 21h5.32a2 2 0 0 0 2-1.9L17.4 7"/>
        <path d="M10 11v6M14 11v6"/>""",
    "export": """
        <path d="M12 3v12M7.5 10.5 12 15l4.5-4.5"/>
        <path d="M5 17v2.5A1.5 1.5 0 0 0 6.5 21h11a1.5 1.5 0 0 0 1.5-1.5V17"/>""",
    "settings": """
        <path d="M4 7h9M17 7h3M4 17h3M11 17h9"/>
        <circle cx="15" cy="7" r="2.1"/><circle cx="7" cy="17" r="2.1"/>""",
    "open": """
        <path d="M4 8V6.4A1.4 1.4 0 0 1 5.4 5H9l2 2.5h5.6A1.4 1.4 0 0 1 18 8.9V10"/>
        <path d="M4.2 10.2h15.1a1 1 0 0 1 .96 1.28l-1.72 6.9A2 2 0 0 1 16.6 20H5.9a2 2 0 0 1-1.95-1.55L2.4 11.4a1 1 0 0 1 .98-1.2z"/>""",
    "copy": """
        <rect x="9" y="9" width="11" height="11" rx="2"/>
        <path d="M6 15H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v1"/>""",
    "check": """<path d="M5 12.5 10 17.5 19 7"/>""",
    "close": """<path d="M6 6l12 12M18 6 6 18"/>""",
    "image": """
        <rect x="3" y="4" width="18" height="16" rx="2.2"/>
        <circle cx="8.4" cy="9.4" r="1.5"/>
        <path d="M21 15.6 16 10.6l-6.6 7.2"/>""",
    "video": """
        <rect x="3" y="5" width="18" height="14" rx="2.2"/>
        <path d="M10.4 9.4 14.8 12l-4.4 2.6z"/>""",
    "shield": """
        <path d="M12 3l7 2.8v5.6c0 4.6-3 7.7-7 9.6-4-1.9-7-5-7-9.6V5.8z"/>
        <path d="M8.8 12.2l2.2 2.2 4.2-4.3"/>""",
    "search": """
        <circle cx="10.8" cy="10.8" r="7"/>
        <path d="M20 20l-3.7-3.7"/>""",
    "sparkles": """
        <path d="M12 3.2l1.7 5 5 1.7-5 1.7-1.7 5-1.7-5-5-1.7 5-1.7z"/>
        <path d="M18.8 15.5l.8 2.2 2.2.8-2.2.8-.8 2.2-.8-2.2-2.2-.8 2.2-.8z"/>""",
    "refresh": """
        <path d="M20 11a8 8 0 1 0-2.35 6.35"/>
        <path d="M20 4.5V11h-6.5"/>""",
    "info": """
        <circle cx="12" cy="12" r="8.6"/>
        <path d="M12 11v5.2"/><circle cx="12" cy="7.9" r="0.9" fill="currentColor" stroke="none"/>""",
    "file": """
        <path d="M6 3h8l4 4v14H6z"/><path d="M14 3v4h4"/>""",
    "chevron": """<path d="M9 6l6 6-6 6"/>""",
    "minus": """<path d="M6 12h12"/>""",
}


def _cache_key(name: str, color: str, size: int) -> tuple[str, str, int]:
    return (name, color, size)


_icon_cache: dict[tuple[str, str, int], QIcon] = {}


def icon(name: str, color: str = "#c9d2e0", size: int = 20) -> QIcon:
    """按颜色/尺寸渲染 SVG 图标，带缓存。"""
    key = _cache_key(name, color, size)
    cached = _icon_cache.get(key)
    if cached is not None:
        return cached

    body = _ICONS.get(name, _ICONS["file"])
    svg = (
        '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" '
        f'fill="none" stroke="{color}" stroke-width="1.7" '
        'stroke-linecap="round" stroke-linejoin="round">'
        f"{body}</svg>"
    )
    renderer = QSvgRenderer(QByteArray(svg.encode("utf-8")))
    dpr = 2.0
    pixmap = QPixmap(int(size * dpr), int(size * dpr))
    pixmap.setDevicePixelRatio(dpr)
    pixmap.fill(Qt.GlobalColor.transparent)
    painter = QPainter(pixmap)
    painter.setRenderHint(QPainter.RenderHint.Antialiasing, True)
    renderer.render(painter, QRectF(0, 0, size, size))
    painter.end()
    result = QIcon(pixmap)
    _icon_cache[key] = result
    return result


_LOGO_WHITE = QColor("#F2EFE6")
_LOGO_BLACK = QColor("#0B0B0B")
_LOGO_BORDER = QColor("#7A7A7A")
_logo_base_cache: QImage | None = None


def _logo_base() -> QImage:
    """32x32 像素底图：左右黑白分割 + 像素放大镜/等号，呼应头像的像素风格。"""
    global _logo_base_cache
    if _logo_base_cache is not None:
        return _logo_base_cache

    image = QImage(32, 32, QImage.Format.Format_ARGB32)
    for y in range(32):
        for x in range(32):
            image.setPixelColor(x, y, _LOGO_WHITE if x < 16 else _LOGO_BLACK)

    cells: set[tuple[int, int]] = set()
    center_x, center_y = 15.5, 14.5
    for y in range(32):
        for x in range(32):
            distance = math.hypot(x + 0.5 - center_x, y + 0.5 - center_y)
            if 8.4 <= distance <= 11.4:
                cells.add((x, y))

    # 镜片内的等号：表示“相同内容”
    for y in (12, 13, 17, 18):
        for x in range(11, 21):
            cells.add((x, y))

    # 像素手柄
    for y in range(20, 30):
        for x in range(21, 30):
            dx, dy = x - 21, y - 21
            if dx >= 0 and abs(dx - dy) <= 1.2 and dx <= 8:
                cells.add((x, y))

    for x, y in cells:
        image.setPixelColor(x, y, _LOGO_BLACK if x < 16 else _LOGO_WHITE)
    _logo_base_cache = image
    return image


def logo_pixmap(size: int = 40, dpr: float = 2.0) -> QPixmap:
    """黑白像素 Logo：头像式左右分割 + 放大镜 + 等号。"""
    physical = max(1, int(round(size * dpr)))
    scaled = _logo_base().scaled(
        physical,
        physical,
        Qt.AspectRatioMode.IgnoreAspectRatio,
        Qt.TransformationMode.FastTransformation,
    )
    painter = QPainter(scaled)
    painter.setRenderHint(QPainter.RenderHint.Antialiasing, False)
    border = max(1, physical // 128)
    painter.setPen(QPen(_LOGO_BORDER, border))
    painter.setBrush(Qt.BrushStyle.NoBrush)
    painter.drawRect(QRect(0, 0, physical - 1, physical - 1))
    painter.end()

    pixmap = QPixmap.fromImage(scaled)
    pixmap.setDevicePixelRatio(dpr)
    return pixmap


def app_icon() -> QIcon:
    result = QIcon()
    for size in (16, 24, 32, 48, 64, 128, 256):
        result.addPixmap(logo_pixmap(size, dpr=1.0))
    return result
