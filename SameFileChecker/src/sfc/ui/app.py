"""Qt 应用启动。"""

from __future__ import annotations

import ctypes
import os
import sys
import threading
import traceback
from datetime import datetime

from PySide6.QtCore import Qt
from PySide6.QtWidgets import QApplication, QMessageBox

from .. import APP_NAME, ORG_NAME, __version__
from .icons import app_icon
from .main_window import MainWindow
from .theme import apply_theme


def _set_app_user_model_id() -> None:
    if os.name != "nt":
        return
    try:
        ctypes.windll.shell32.SetCurrentProcessExplicitAppUserModelID(
            f"{ORG_NAME}.{APP_NAME}.{__version__}"
        )
    except (AttributeError, OSError):
        pass


def _crash_log_path() -> str:
    base = os.environ.get("LOCALAPPDATA") or os.path.expanduser("~")
    return os.path.join(base, "SameFileChecker", "error.log")


def _install_excepthook() -> None:
    state = {"busy": False}

    def hook(exc_type, exc, tb) -> None:
        if state["busy"]:
            return
        state["busy"] = True
        try:
            if tb is not None:
                text = "".join(traceback.format_exception(exc_type, exc, tb))
            else:
                text = (
                    f"{exc_type.__name__}: {exc}\n"
                    "(没有 traceback：异常可能来自 Qt 信号/槽)"
                )
            try:
                path = _crash_log_path()
                os.makedirs(os.path.dirname(path), exist_ok=True)
                with open(path, "a", encoding="utf-8") as handle:
                    handle.write(
                        f"\n===== {datetime.now()} | {threading.current_thread().name} =====\n{text}\n"
                    )
            except OSError:
                pass

            # 绝对不能在非 GUI 线程创建 QMessageBox：那会把工作线程卡住，
            # 主窗口随之“未响应”。工作线程里的异常只写日志。
            if (
                threading.current_thread() is not threading.main_thread()
                or QApplication.instance() is None
            ):
                return
            box = QMessageBox()
            box.setIcon(QMessageBox.Icon.Critical)
            box.setWindowTitle("程序遇到错误")
            box.setText("程序遇到未处理的错误，已写入日志：\n" + _crash_log_path())
            box.setDetailedText(text[-4000:])
            box.exec()
        finally:
            state["busy"] = False

    sys.excepthook = hook


def run(argv: list[str] | None = None) -> int:
    QApplication.setHighDpiScaleFactorRoundingPolicy(
        Qt.HighDpiScaleFactorRoundingPolicy.PassThrough
    )
    app = QApplication(sys.argv[:1])
    app.setApplicationName(APP_NAME)
    app.setOrganizationName(ORG_NAME)
    app.setWindowIcon(app_icon())
    apply_theme(app)
    _set_app_user_model_id()
    _install_excepthook()

    window = MainWindow()
    window.show()
    return app.exec()
