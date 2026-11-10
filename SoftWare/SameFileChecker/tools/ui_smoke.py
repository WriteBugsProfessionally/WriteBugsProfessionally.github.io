"""界面烟雾测试：扫描、复选框点击、最后一副本保护、删除后的模型更新、设置对话框。"""

from __future__ import annotations

import os
import shutil
import sys
import tempfile
import time

os.environ.setdefault("QT_QPA_PLATFORM", "offscreen")
os.environ.setdefault("QT_QPA_FONTDIR", r"C:\Windows\Fonts")

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, os.path.join(ROOT, "src"))

from PySide6.QtCore import QSettings, Qt  # noqa: E402
from PySide6.QtTest import QTest  # noqa: E402
from PySide6.QtWidgets import QApplication  # noqa: E402

settings_dir = tempfile.mkdtemp(prefix="sfc_settings_")
settings = QSettings(os.path.join(settings_dir, "test.ini"), QSettings.Format.IniFormat)

from sfc.scanner import ScanOptions  # noqa: E402
from sfc.ui.empty_folder_dialog import EmptyFolderDialog  # noqa: E402
from sfc.ui.main_window import ConfirmDeleteDialog, MainWindow, ScanThread  # noqa: E402
from sfc.ui.models import CHECK_ROLE  # noqa: E402
from sfc.ui.settings_dialog import SettingsDialog  # noqa: E402
from sfc.ui.theme import apply_theme  # noqa: E402

SOURCE_DEMO = os.path.join(ROOT, "demo_data")


def wait(app: QApplication, seconds: float) -> None:
    end = time.time() + seconds
    while time.time() < end:
        app.processEvents()
        time.sleep(0.01)


def main() -> int:
    if not os.path.isdir(SOURCE_DEMO):
        print("缺少 demo_data，先运行 tools/make_demo_data.py")
        return 2
    temp_root = tempfile.mkdtemp(prefix="sfc_ui_")
    demo = os.path.join(temp_root, "素材")
    shutil.copytree(SOURCE_DEMO, demo)

    app = QApplication([])
    apply_theme(app)

    # 回归：字节数超过 2 GB 时，Qt 信号不能再用 32 位 int（会 OverflowError）
    probe = ScanThread([], ScanOptions())
    seen: list[tuple] = []
    probe.phaseChanged.connect(lambda *args: seen.append(("phase", args)))
    probe.progressed.connect(lambda *args: seen.append(("progress", args)))
    probe.phaseChanged.emit("full", "第三轮", 67_862_375_349, 12)
    probe.progressed.emit(67_862_375_349, 67_862_375_349, 12, 12, 24.1e6)
    app.processEvents()
    assert len(seen) == 2, seen
    assert seen[0][1][2] == 67_862_375_349
    assert seen[1][1][0] == 67_862_375_349

    window = MainWindow(settings)
    window.resize(1460, 920)
    window.show()
    window.add_roots([demo])

    state = {"result": None}
    window.scanCompleted.connect(lambda result: state.__setitem__("result", result))
    window.start_scan()
    deadline = time.time() + 120
    while state["result"] is None and time.time() < deadline:
        app.processEvents()
        time.sleep(0.02)
    if state["result"] is None:
        print("扫描超时")
        return 3
    wait(app, 1.0)

    # 选中 3 个文件的图片组
    group_index = 0
    for index, group in enumerate(window.group_model.groups):
        if group.kind == "图片" and group.count >= 3:
            group_index = index
            break
    window.select_group(group_index)
    wait(app, 0.8)

    model = window.file_model
    checks = list(model.checks)
    keep_row = next(i for i, checked in enumerate(checks) if not checked)
    checked_rows = [i for i, checked in enumerate(checks) if checked]
    warnings: list[int] = []
    model.lastCopyWarning.connect(lambda: warnings.append(1))

    # 点击唯一未勾选的“保留项”：应被拒绝
    rect = window.file_table.visualRect(model.index(keep_row, 0))
    QTest.mouseClick(
        window.file_table.viewport(),
        Qt.MouseButton.LeftButton,
        Qt.KeyboardModifier.NoModifier,
        rect.center(),
    )
    wait(app, 0.2)
    assert warnings, "点击最后一个未勾选项应触发警告"
    assert model.checks == checks, "最后一副本保护失效"

    # 取消一个已勾选项，再把保留项勾上：应允许
    rect_checked = window.file_table.visualRect(model.index(checked_rows[0], 0))
    QTest.mouseClick(
        window.file_table.viewport(),
        Qt.MouseButton.LeftButton,
        Qt.KeyboardModifier.NoModifier,
        rect_checked.center(),
    )
    wait(app, 0.2)
    assert model.checks[checked_rows[0]] is False, "取消勾选失败"
    QTest.mouseClick(
        window.file_table.viewport(),
        Qt.MouseButton.LeftButton,
        Qt.KeyboardModifier.NoModifier,
        rect.center(),
    )
    wait(app, 0.2)
    assert model.checks[keep_row] is True, "勾选保留项失败"

    count_before = len(window.group_model.groups)
    files_before = window.group_model.groups[group_index].count
    target = window.group_model.groups[group_index].files[0].path
    window._apply_deletions([target])
    wait(app, 0.3)
    assert len(window.group_model.groups) in (count_before, count_before - 1)
    if len(window.group_model.groups) == count_before:
        assert window.group_model.groups[group_index].count == files_before - 1

    # 设置对话框
    dialog = SettingsDialog(window.settings, window)
    dialog.resize(640, 760)
    dialog.show()
    wait(app, 0.5)
    dialog.grab().save(os.path.join(ROOT, "screenshot_settings.png"), "PNG")
    dialog.close()

    # 删除确认对话框
    paths = [entry.path for group in window.result.groups[:2] for entry in group.files[:2]]
    confirm = ConfirmDeleteDialog(len(paths), 12345678, paths, "recycle", window)
    confirm.show()
    wait(app, 0.4)
    confirm.grab().save(os.path.join(ROOT, "screenshot_delete.png"), "PNG")
    confirm.close()

    # 空文件夹清理窗口
    empty_root = os.path.join(temp_root, "空文件夹测试")
    for index in range(5):
        os.makedirs(os.path.join(empty_root, f"空目录-{index}"))
    empty_dialog = EmptyFolderDialog([empty_root], window)
    empty_dialog.show()
    empty_dialog.start_scan()
    deadline = time.time() + 30
    while empty_dialog._scanning and time.time() < deadline:
        app.processEvents()
        time.sleep(0.02)
    wait(app, 0.2)
    assert empty_dialog.model.rowCount() == 5, empty_dialog.model.rowCount()
    empty_dialog.model.setData(empty_dialog.model.index(0, 0), True, CHECK_ROLE)
    wait(app, 0.1)
    assert empty_dialog.delete_button.isEnabled()
    empty_dialog.close()
    wait(app, 0.1)

    print("UI 烟雾测试通过：组数", len(window.group_model.groups))
    window.close()
    shutil.rmtree(temp_root, ignore_errors=True)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
