"""离屏渲染主窗口截图，用于检查界面效果（不影响用户真实设置）。"""

from __future__ import annotations

import os
import sys
import tempfile
import time

os.environ.setdefault("QT_QPA_PLATFORM", "offscreen")
os.environ.setdefault("QT_QPA_FONTDIR", r"C:\Windows\Fonts")

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, os.path.join(ROOT, "src"))

from PySide6.QtCore import QSettings  # noqa: E402
from PySide6.QtWidgets import QApplication  # noqa: E402

settings_dir = tempfile.mkdtemp(prefix="sfc_settings_")
settings = QSettings(os.path.join(settings_dir, "test.ini"), QSettings.Format.IniFormat)

from sfc.ui.main_window import MainWindow  # noqa: E402
from sfc.ui.theme import apply_theme  # noqa: E402


def main() -> int:
    demo = sys.argv[1] if len(sys.argv) > 1 else os.path.join(ROOT, "demo_data")
    output = sys.argv[2] if len(sys.argv) > 2 else os.path.join(ROOT, "screenshot.png")
    if not os.path.isdir(demo):
        print("找不到演示目录：", demo)
        return 2

    app = QApplication([])
    apply_theme(app)
    window = MainWindow(settings)
    window.resize(1460, 920)
    window.show()
    window.add_roots([demo])

    state = {"result": None}
    window.scanCompleted.connect(lambda result: state.__setitem__("result", result))
    window.start_scan()

    deadline = time.time() + 180
    while state["result"] is None and time.time() < deadline:
        app.processEvents()
        time.sleep(0.02)
    if state["result"] is None:
        print("扫描超时")
        return 3

    result = state["result"]
    print(f"扫描完成：{result.stats.groups} 组，可释放 {result.stats.wasted} 字节")
    for index, group in enumerate(window.group_model.groups):
        if group.kind == "图片":
            window.select_group(index)
            break

    # 预览“保留”的那一份（未勾选），更符合实际操作场景
    for row in range(window.file_model.rowCount()):
        if not window.file_model.is_checked(row):
            window.file_table.selectRow(row)
            break

    end = time.time() + 4
    while time.time() < end:
        app.processEvents()
        time.sleep(0.02)

    window.activateWindow()
    app.processEvents()
    pixmap = window.grab()
    if not pixmap.save(output, "PNG"):
        print("截图保存失败")
        return 4
    print("截图已保存：", output, pixmap.width(), "x", pixmap.height())
    window.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
