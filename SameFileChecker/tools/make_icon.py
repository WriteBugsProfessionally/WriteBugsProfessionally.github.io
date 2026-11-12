"""生成 Windows 多尺寸 ICO（PNG 载荷），供 PyInstaller 与窗口图标使用。"""

from __future__ import annotations

import os
import struct
import sys

os.environ.setdefault("QT_QPA_PLATFORM", "offscreen")
os.environ.setdefault("QT_QPA_FONTDIR", r"C:\Windows\Fonts")

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, os.path.join(ROOT, "src"))

from PySide6.QtCore import QBuffer, QByteArray, QIODevice  # noqa: E402
from PySide6.QtGui import QGuiApplication  # noqa: E402

from sfc.ui.icons import logo_pixmap  # noqa: E402


def main() -> int:
    app = QGuiApplication([])
    sizes = [16, 24, 32, 48, 64, 128, 256]
    pngs: list[bytes] = []
    for size in sizes:
        pixmap = logo_pixmap(size, dpr=1.0)
        data = QByteArray()
        buffer = QBuffer(data)
        buffer.open(QIODevice.OpenModeFlag.WriteOnly)
        pixmap.save(buffer, "PNG")
        buffer.close()
        pngs.append(bytes(data))
    app.quit()

    assets = os.path.join(ROOT, "assets")
    os.makedirs(assets, exist_ok=True)
    header = struct.pack("<HHH", 0, 1, len(sizes))
    offset = 6 + 16 * len(sizes)
    entries = bytearray()
    payload = bytearray()
    for size, png in zip(sizes, pngs):
        dimension = 0 if size >= 256 else size
        entries += struct.pack(
            "<BBBBHHII", dimension, dimension, 0, 0, 1, 32, len(png), offset + len(payload)
        )
        payload += png
    ico_path = os.path.join(assets, "app.ico")
    with open(ico_path, "wb") as handle:
        handle.write(header + entries + payload)
    with open(os.path.join(assets, "app.png"), "wb") as handle:
        handle.write(pngs[-1])
    print("已生成：", ico_path)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
