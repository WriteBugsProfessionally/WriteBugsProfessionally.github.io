"""离屏渲染“空文件夹清理”对话框截图。"""

from __future__ import annotations

import os
import sys
import time

os.environ.setdefault("QT_QPA_PLATFORM", "offscreen")
os.environ.setdefault("QT_QPA_FONTDIR", r"C:\Windows\Fonts")

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, os.path.join(ROOT, "src"))

from PySide6.QtWidgets import QApplication  # noqa: E402

from sfc.ui.empty_folder_dialog import EmptyFolderDialog  # noqa: E402
from sfc.ui.models import CHECK_ROLE  # noqa: E402
from sfc.ui.theme import apply_theme  # noqa: E402


def main() -> int:
    demo = os.path.join(ROOT, "demo_data")
    if not os.path.isdir(demo):
        print("找不到演示目录，先运行 tools/make_demo_data.py")
        return 2

    app = QApplication([])
    apply_theme(app)
    dialog = EmptyFolderDialog([demo])
    dialog.resize(1000, 720)
    dialog.show()
    for _ in range(10):
        app.processEvents()
        time.sleep(0.01)

    dialog.start_scan()
    deadline = time.time() + 120
    while dialog._scanning and time.time() < deadline:
        app.processEvents()
        time.sleep(0.02)
    for _ in range(20):
        app.processEvents()
        time.sleep(0.01)

    if dialog.model.rowCount() == 0:
        print("没有找到空文件夹")
        return 3
    for row in (0, 2):
        if row < dialog.model.rowCount():
            dialog.model.setData(dialog.model.index(row, 0), True, CHECK_ROLE)
    if dialog.model.rowCount():
        dialog.table.selectRow(0)
    for _ in range(20):
        app.processEvents()
        time.sleep(0.01)

    output = os.path.join(ROOT, "docs", "empty_folders.png")
    dialog.grab().save(output, "PNG")
    print(f"截图已保存：{output}（找到 {dialog.model.rowCount()} 个空文件夹）")
    dialog.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
