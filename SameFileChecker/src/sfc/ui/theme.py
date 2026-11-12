"""深色主题：调色板 + QSS。"""

from __future__ import annotations

import ctypes
import os

from PySide6.QtCore import QRectF, Qt
from PySide6.QtGui import QColor, QFont, QPainter, QPainterPath, QPalette, QPen
from PySide6.QtWidgets import QApplication, QProxyStyle, QStyle


def enable_dark_titlebar(widget) -> None:
    """Windows 10/11：让系统标题栏跟随深色主题。"""
    if os.name != "nt":
        return
    try:
        hwnd = int(widget.winId())
        if not hwnd:
            return
        enabled = ctypes.c_int(1)
        for attribute in (20, 19):  # DWMWA_USE_IMMERSIVE_DARK_MODE（旧系统为 19）
            result = ctypes.windll.dwmapi.DwmSetWindowAttribute(
                hwnd, attribute, ctypes.byref(enabled), ctypes.sizeof(enabled)
            )
            if result == 0:
                return
    except (AttributeError, OSError, ValueError):
        pass

BG = "#0e1116"
BG_DEEP = "#0a0d12"
PANEL = "#151a22"
PANEL_2 = "#1a2029"
FIELD = "#10151d"
BORDER = "#232b37"
BORDER_LIGHT = "#2c3646"
TEXT = "#e8edf5"
TEXT_DIM = "#c3ccda"
MUTED = "#8b95a7"
MUTED_2 = "#5f6b7d"
ACCENT = "#4d8dff"
ACCENT_HOVER = "#69a0ff"
GREEN = "#3ddc97"
RED = "#ff6b78"
YELLOW = "#f5c451"

QSS = """
* {
    outline: none;
}
QWidget {
    color: #e8edf5;
    font-family: "Microsoft YaHei UI", "Segoe UI", sans-serif;
    font-size: 13px;
}
QMainWindow, QDialog {
    background: #0e1116;
}
QFrame#card, QFrame#panel {
    background: #151a22;
    border: 1px solid #232b37;
    border-radius: 12px;
}
QFrame#statTile {
    background: #131821;
    border: 1px solid #202838;
    border-radius: 10px;
}
QFrame#actionBar {
    background: #121720;
    border: none;
    border-top: 1px solid #20283a;
}
QFrame#hsep {
    background: #222a38;
    border: none;
    max-height: 1px;
    min-height: 1px;
}
QLabel {
    background: transparent;
}
QLabel#appTitle {
    font-size: 21px;
    font-weight: 700;
    color: #f2f6fd;
    letter-spacing: 0.3px;
}
QLabel#appSubtitle {
    color: #8b95a7;
    font-size: 12px;
}
QLabel#sectionTitle {
    color: #aeb8c8;
    font-size: 12px;
    font-weight: 600;
    letter-spacing: 0.3px;
}
QLabel#muted {
    color: #8b95a7;
}
QLabel#hint {
    color: #68748a;
    font-size: 12px;
}
QLabel#statValue {
    color: #eef3fb;
    font-size: 20px;
    font-weight: 700;
}
QLabel#statValueAccent {
    color: #6aa0ff;
    font-size: 20px;
    font-weight: 700;
}
QLabel#statValueGreen {
    color: #3ddc97;
    font-size: 20px;
    font-weight: 700;
}
QLabel#statCaption {
    color: #7d889b;
    font-size: 11px;
}
QLabel#emptyTitle {
    color: #b9c4d4;
    font-size: 15px;
    font-weight: 600;
}
QLabel#emptyHint {
    color: #68748a;
    font-size: 12px;
}
QLabel#badge {
    background: #1b2a44;
    color: #7fa9fb;
    border-radius: 9px;
    padding: 1px 8px;
    font-size: 11px;
    font-weight: 600;
}
QLabel#previewName {
    color: #eef3fb;
    font-size: 14px;
    font-weight: 600;
}
QLabel#previewPath {
    color: #7d889b;
    font-size: 11px;
}
QLabel#groupTitle {
    color: #eef3fb;
    font-size: 14px;
    font-weight: 600;
}
QLabel#selectionSummary {
    color: #7fa9fb;
    font-size: 12px;
    font-weight: 600;
}
QLabel#legendDelete {
    color: #ff9aa3;
    font-size: 12px;
    font-weight: 600;
}
QLabel#legendKeep {
    color: #3ddc97;
    font-size: 12px;
    font-weight: 600;
}
QPushButton {
    background: #1b2230;
    border: 1px solid #2a3444;
    border-radius: 8px;
    padding: 7px 14px;
    color: #dbe3ef;
    font-weight: 500;
}
QPushButton:hover {
    background: #232d3d;
    border-color: #3a4860;
}
QPushButton:pressed {
    background: #171e29;
}
QPushButton:focus {
    border-color: #4d8dff;
}
QPushButton:disabled {
    color: #596478;
    background: #151b24;
    border-color: #222a36;
}
QPushButton#primary {
    background: #3f7bf0;
    border: 1px solid #5a8ef5;
    color: #ffffff;
    font-weight: 600;
    padding: 8px 18px;
}
QPushButton#primary:hover {
    background: #4f8af7;
    border-color: #6da0ff;
}
QPushButton#primary:pressed {
    background: #3569d4;
}
QPushButton#primary:disabled {
    background: #24334d;
    border-color: #2b3b58;
    color: #7086a8;
}
QPushButton#danger {
    background: rgba(255, 107, 120, 0.12);
    border: 1px solid rgba(255, 107, 120, 0.45);
    color: #ff8f99;
    font-weight: 600;
}
QPushButton#danger:hover {
    background: rgba(255, 107, 120, 0.2);
    border-color: rgba(255, 107, 120, 0.7);
}
QPushButton#danger:pressed {
    background: rgba(255, 107, 120, 0.28);
}
QPushButton#danger:disabled {
    color: #6b4a50;
    background: #191d24;
    border-color: #2a252c;
}
QPushButton#ghost {
    background: transparent;
    border: 1px solid #2a3444;
    color: #b9c4d4;
}
QPushButton#ghost:hover {
    background: #1b2330;
    border-color: #3a4860;
}
QPushButton#subtle {
    background: transparent;
    border: none;
    color: #8b95a7;
    padding: 4px 6px;
}
QPushButton#subtle:hover {
    color: #dbe3ef;
    background: #1b2330;
}
QLineEdit, QSpinBox, QComboBox {
    background: #10151d;
    border: 1px solid #2a3444;
    border-radius: 8px;
    padding: 6px 10px;
    color: #e8edf5;
    selection-background-color: #3f7bf0;
    selection-color: #ffffff;
}
QLineEdit:hover, QSpinBox:hover, QComboBox:hover {
    border-color: #37455c;
}
QLineEdit:focus, QSpinBox:focus, QComboBox:focus {
    border-color: #4d8dff;
}
QLineEdit:disabled, QSpinBox:disabled, QComboBox:disabled {
    color: #5b6678;
    background: #141a23;
    border-color: #222a36;
}
QComboBox::drop-down {
    border: none;
    width: 26px;
}
QComboBox QAbstractItemView {
    background: #161c26;
    border: 1px solid #2c3646;
    border-radius: 8px;
    padding: 4px;
    selection-background-color: #22304a;
    selection-color: #eef4ff;
    outline: none;
}
QComboBox QAbstractItemView::item {
    padding: 6px 10px;
    border-radius: 5px;
    min-height: 22px;
}
QListWidget#rootList {
    background: #10151d;
    border: 1px solid #232b37;
    border-radius: 8px;
    padding: 4px;
}
QListWidget#rootList::item {
    padding: 3px 6px;
    border-radius: 6px;
    color: #c6d0de;
}
QListWidget#rootList::item:hover {
    background: #1a2230;
}
QListWidget#rootList::item:selected {
    background: #1d2941;
    color: #dce8ff;
}
QTableView {
    background: transparent;
    border: none;
    alternate-background-color: #161c25;
    selection-background-color: #223451;
    selection-color: #f2f6fd;
    gridline-color: transparent;
}
QTableView::item {
    padding: 3px 8px;
    border: none;
}
QTableView::item:selected {
    background: #223451;
}
QHeaderView::section {
    background: transparent;
    color: #7f8b9d;
    border: none;
    border-bottom: 1px solid #232b37;
    padding: 7px 8px;
    font-size: 11px;
    font-weight: 600;
}
QTableCornerButton::section {
    background: transparent;
    border: none;
}
QScrollBar:vertical {
    width: 10px;
    background: transparent;
    margin: 2px 2px 2px 0;
}
QScrollBar::handle:vertical {
    background: #2b3546;
    border-radius: 3px;
    min-height: 28px;
}
QScrollBar::handle:vertical:hover {
    background: #3a4760;
}
QScrollBar::add-line:vertical, QScrollBar::sub-line:vertical {
    height: 0;
    background: transparent;
}
QScrollBar::add-page:vertical, QScrollBar::sub-page:vertical {
    background: transparent;
}
QScrollBar:horizontal {
    height: 10px;
    background: transparent;
    margin: 0 2px 2px 2px;
}
QScrollBar::handle:horizontal {
    background: #2b3546;
    border-radius: 3px;
    min-width: 28px;
}
QScrollBar::handle:horizontal:hover {
    background: #3a4760;
}
QScrollBar::add-line:horizontal, QScrollBar::sub-line:horizontal {
    width: 0;
    background: transparent;
}
QScrollBar::add-page:horizontal, QScrollBar::sub-page:horizontal {
    background: transparent;
}
QProgressBar {
    background: #0d1219;
    border: none;
    border-radius: 3px;
    min-height: 6px;
    max-height: 6px;
    text-align: center;
    color: transparent;
}
QProgressBar::chunk {
    background: #3f7bf0;
    border-radius: 3px;
}
QSplitter::handle {
    background: transparent;
}
QSplitter::handle:hover {
    background: #1b2330;
}
QStatusBar {
    background: transparent;
    color: #748094;
    border-top: 1px solid #1b2230;
}
QStatusBar::item {
    border: none;
}
QToolTip {
    background: #1b2230;
    color: #dbe3ef;
    border: 1px solid #2c3646;
    padding: 6px 8px;
}
QMenu {
    background: #161c26;
    border: 1px solid #2c3646;
    border-radius: 8px;
    padding: 5px;
}
QMenu::item {
    padding: 6px 18px;
    border-radius: 5px;
}
QMenu::item:selected {
    background: #22304a;
}
QCheckBox {
    spacing: 8px;
    color: #c6d0de;
}
QDialog QLabel#dialogTitle {
    font-size: 18px;
    font-weight: 700;
}
"""


class DarkStyle(QProxyStyle):
    """Fusion 的深色补充：把复选框画成清晰的蓝色圆角方块 + 白色对勾。"""

    def __init__(self) -> None:
        super().__init__("Fusion")

    def drawPrimitive(self, element, option, painter, widget=None):  # noqa: N802
        if element == QStyle.PrimitiveElement.PE_IndicatorCheckBox:
            self._draw_check(painter, option)
            return
        super().drawPrimitive(element, option, painter, widget)

    @staticmethod
    def _draw_check(painter: QPainter, option) -> None:
        checked = bool(option.state & QStyle.StateFlag.State_On)
        enabled = bool(option.state & QStyle.StateFlag.State_Enabled)
        rect = QRectF(option.rect)
        size = min(17.0, rect.width(), rect.height())
        box = QRectF(0, 0, size, size)
        box.moveCenter(rect.center())
        painter.save()
        painter.setRenderHint(QPainter.RenderHint.Antialiasing, True)
        if checked:
            painter.setBrush(QColor(ACCENT if enabled else "#2a3a55"))
            painter.setPen(QPen(QColor("#79a8ff" if enabled else "#35435a"), 1.0))
            painter.drawRoundedRect(box, 4.0, 4.0)
            pen = QPen(QColor("#ffffff" if enabled else "#6c7c96"), 1.9)
            pen.setCapStyle(Qt.PenCapStyle.RoundCap)
            pen.setJoinStyle(Qt.PenJoinStyle.RoundJoin)
            painter.setPen(pen)
            path = QPainterPath()
            path.moveTo(box.x() + 4.0, box.y() + 8.7)
            path.lineTo(box.x() + 7.2, box.y() + 11.8)
            path.lineTo(box.x() + 13.0, box.y() + 5.6)
            painter.drawPath(path)
        else:
            painter.setBrush(QColor("#0f141c" if enabled else "#141a23"))
            painter.setPen(QPen(QColor("#3a465a" if enabled else "#28313f"), 1.0))
            painter.drawRoundedRect(box, 4.0, 4.0)
        painter.restore()


def apply_theme(app: QApplication) -> None:
    app.setStyle(DarkStyle())
    font = QFont("Microsoft YaHei UI")
    font.setPointSizeF(9.75)
    app.setFont(font)

    palette = QPalette()
    palette.setColor(QPalette.ColorRole.Window, QColor(BG))
    palette.setColor(QPalette.ColorRole.WindowText, QColor(TEXT))
    palette.setColor(QPalette.ColorRole.Base, QColor(FIELD))
    palette.setColor(QPalette.ColorRole.AlternateBase, QColor(PANEL_2))
    palette.setColor(QPalette.ColorRole.Text, QColor(TEXT))
    palette.setColor(QPalette.ColorRole.Button, QColor(PANEL_2))
    palette.setColor(QPalette.ColorRole.ButtonText, QColor(TEXT))
    palette.setColor(QPalette.ColorRole.BrightText, QColor("#ffffff"))
    palette.setColor(QPalette.ColorRole.Highlight, QColor(ACCENT))
    palette.setColor(QPalette.ColorRole.HighlightedText, QColor("#ffffff"))
    palette.setColor(QPalette.ColorRole.ToolTipBase, QColor(PANEL))
    palette.setColor(QPalette.ColorRole.ToolTipText, QColor(TEXT))
    palette.setColor(QPalette.ColorRole.PlaceholderText, QColor(MUTED_2))
    palette.setColor(QPalette.ColorRole.Link, QColor(ACCENT))
    palette.setColor(QPalette.ColorGroup.Disabled, QPalette.ColorRole.Text, QColor("#5a6577"))
    palette.setColor(QPalette.ColorGroup.Disabled, QPalette.ColorRole.ButtonText, QColor("#5a6577"))
    palette.setColor(QPalette.ColorGroup.Disabled, QPalette.ColorRole.WindowText, QColor("#5a6577"))
    app.setPalette(palette)
    app.setStyleSheet(QSS)
