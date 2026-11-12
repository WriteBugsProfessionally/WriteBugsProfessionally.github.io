"""空文件夹清理对话框：扫描、列出、勾选、移入回收站或直接删除。"""

from __future__ import annotations

import os
import threading
import traceback

from PySide6.QtCore import QAbstractTableModel, QModelIndex, Qt, QThread, Signal
from PySide6.QtWidgets import (
    QAbstractItemView,
    QCheckBox,
    QComboBox,
    QDialog,
    QFileDialog,
    QFrame,
    QHBoxLayout,
    QHeaderView,
    QLabel,
    QListWidget,
    QListWidgetItem,
    QMessageBox,
    QProgressBar,
    QPushButton,
    QSizePolicy,
    QStackedWidget,
    QTableView,
    QVBoxLayout,
    QWidget,
)

from ..emptyfolders import (
    EmptyDeleteOutcome,
    EmptyScanOptions,
    EmptyScanResult,
    delete_empty_dirs,
    scan_empty_folders,
)
from ..fileops import open_in_explorer
from ..util import format_elapsed, norm_key, shorten_path
from . import theme
from .icons import icon
from .models import CHECK_ROLE, CheckDelegate
from .theme import enable_dark_titlebar
from .widgets import Badge, EmptyState, make_section_title


class EmptyFolderModel(QAbstractTableModel):
    HEADERS = ("删除", "文件夹路径")
    selectionChanged = Signal()

    def __init__(self, parent=None) -> None:
        super().__init__(parent)
        self.folders: list[str] = []
        self.checks: list[bool] = []

    def rowCount(self, parent=QModelIndex()) -> int:  # noqa: N802
        return 0 if parent.isValid() else len(self.folders)

    def columnCount(self, parent=QModelIndex()) -> int:  # noqa: N802
        return 0 if parent.isValid() else 2

    def headerData(self, section: int, orientation, role=Qt.ItemDataRole.DisplayRole):  # noqa: N802
        if orientation == Qt.Orientation.Horizontal:
            if role == Qt.ItemDataRole.DisplayRole:
                return self.HEADERS[section]
            if role == Qt.ItemDataRole.ToolTipRole and section == 0:
                return "勾选表示删除这个空文件夹；取消勾选表示保留。"
        return None

    def data(self, index: QModelIndex, role=Qt.ItemDataRole.DisplayRole):
        if not index.isValid():
            return None
        row = index.row()
        path = self.folders[row]

        if role == CHECK_ROLE and index.column() == 0:
            return bool(self.checks[row])

        if role == Qt.ItemDataRole.DisplayRole:
            if index.column() == 1:
                return path
            return None

        if role == Qt.ItemDataRole.ToolTipRole:
            return path

        if role == Qt.ItemDataRole.TextAlignmentRole:
            if index.column() == 0:
                return int(Qt.AlignmentFlag.AlignCenter)
            return int(Qt.AlignmentFlag.AlignLeft | Qt.AlignmentFlag.AlignVCenter)
        return None

    def flags(self, index: QModelIndex):
        if not index.isValid():
            return Qt.ItemFlag.NoItemFlags
        return Qt.ItemFlag.ItemIsEnabled | Qt.ItemFlag.ItemIsSelectable

    def setData(self, index: QModelIndex, value, role=Qt.ItemDataRole.EditRole) -> bool:  # noqa: N802
        if not index.isValid() or role != CHECK_ROLE or index.column() != 0:
            return False
        row = index.row()
        checked = bool(value)
        if self.checks[row] != checked:
            self.checks[row] = checked
            self.dataChanged.emit(index, index, [CHECK_ROLE])
            self.selectionChanged.emit()
        return True

    def set_folders(self, folders: list[str]) -> None:
        self.beginResetModel()
        self.folders = list(folders)
        self.checks = [False] * len(self.folders)
        self.endResetModel()
        self.selectionChanged.emit()

    def remove_folders(self, removed: list[str]) -> None:
        keys = {norm_key(path) for path in removed}
        if not keys:
            return
        self.beginResetModel()
        pairs = [
            (path, checked)
            for path, checked in zip(self.folders, self.checks)
            if norm_key(path) not in keys
        ]
        self.folders = [path for path, _checked in pairs]
        self.checks = [checked for _path, checked in pairs]
        self.endResetModel()
        self.selectionChanged.emit()

    def set_all(self, checked: bool) -> None:
        if not self.folders:
            return
        self.checks = [checked] * len(self.folders)
        top = self.index(0, 0)
        bottom = self.index(len(self.folders) - 1, 0)
        self.dataChanged.emit(top, bottom, [CHECK_ROLE])
        self.selectionChanged.emit()

    def checked_paths(self) -> list[str]:
        return [path for path, checked in zip(self.folders, self.checks) if checked]

    def checked_count(self) -> int:
        return sum(1 for checked in self.checks if checked)

    def folder_at(self, row: int) -> str | None:
        if 0 <= row < len(self.folders):
            return self.folders[row]
        return None


class EmptyScanThread(QThread):
    progressed = Signal(object, str)
    errorOccurred = Signal(str, str)
    resultReady = Signal(object)
    crashed = Signal(str)

    def __init__(self, roots: list[str], options: EmptyScanOptions, parent=None) -> None:
        super().__init__(parent)
        self.roots = list(roots)
        self.options = options
        self.cancel_event = threading.Event()

    def run(self) -> None:
        try:
            result = scan_empty_folders(
                self.roots,
                self.options,
                on_progress=lambda dirs, path: self.progressed.emit(dirs, path),
                on_error=lambda path, message: self.errorOccurred.emit(path, message),
                cancel=self.cancel_event,
            )
            self.resultReady.emit(result)
        except Exception:
            self.crashed.emit(traceback.format_exc())


class EmptyDeleteThread(QThread):
    done = Signal(object)

    def __init__(self, paths: list[str], mode: str, parent=None) -> None:
        super().__init__(parent)
        self.paths = list(paths)
        self.mode = mode

    def run(self) -> None:
        try:
            outcome = delete_empty_dirs(self.paths, self.mode)
        except Exception as exc:  # 防御：不能让删除线程静默失败
            outcome = EmptyDeleteOutcome(
                removed=[], failures=[(path, str(exc)) for path in self.paths]
            )
        self.done.emit(outcome)


class ConfirmEmptyDeleteDialog(QDialog):
    def __init__(
        self,
        count: int,
        mode: str,
        paths: list[str],
        parent: QWidget | None = None,
    ) -> None:
        super().__init__(parent)
        self.confirmed = False
        self.setWindowTitle("确认删除空文件夹")
        self.setModal(True)
        self.resize(620, 420)
        self._titlebar_done = False

        layout = QVBoxLayout(self)
        layout.setContentsMargins(24, 22, 24, 18)
        layout.setSpacing(12)

        title = QLabel("确认删除这些空文件夹吗？")
        title.setObjectName("dialogTitle")
        layout.addWidget(title)

        action = "移入回收站" if mode == "recycle" else "直接删除"
        summary = QLabel(f"即将把 <b>{count}</b> 个空文件夹{action}。")
        summary.setObjectName("muted")
        summary.setWordWrap(True)
        layout.addWidget(summary)

        listing = QListWidget()
        listing.setObjectName("rootList")
        listing.setSelectionMode(QAbstractItemView.SelectionMode.NoSelection)
        listing.setFocusPolicy(Qt.FocusPolicy.NoFocus)
        listing.setTextElideMode(Qt.TextElideMode.ElideMiddle)
        listing.setHorizontalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff)
        for path in paths[:400]:
            item = QListWidgetItem(shorten_path(path, 86))
            item.setToolTip(path)
            listing.addItem(item)
        if len(paths) > 400:
            listing.addItem(f"…… 另外 {len(paths) - 400} 个")
        layout.addWidget(listing, 1)

        note = QLabel(
            "删除前程序会再检查一次：只删除确实没有任何文件的文件夹；"
            "如果里面出现了文件，会跳过并在结果中提示。"
        )
        note.setObjectName("hint")
        note.setWordWrap(True)
        layout.addWidget(note)

        buttons = QHBoxLayout()
        buttons.addStretch(1)
        cancel = QPushButton("取消")
        cancel.setObjectName("ghost")
        cancel.clicked.connect(self.reject)
        confirm = QPushButton(action)
        confirm.setObjectName("danger")
        confirm.clicked.connect(self._confirm)
        buttons.addWidget(cancel)
        buttons.addWidget(confirm)
        layout.addLayout(buttons)

    def _confirm(self) -> None:
        self.confirmed = True
        self.accept()

    def showEvent(self, event) -> None:  # noqa: N802
        super().showEvent(event)
        if not self._titlebar_done:
            self._titlebar_done = True
            enable_dark_titlebar(self)


class EmptyFolderDialog(QDialog):
    def __init__(self, roots: list[str], parent: QWidget | None = None) -> None:
        super().__init__(parent)
        self.setWindowTitle("空文件夹清理")
        self.setModal(True)
        self.setMinimumSize(820, 600)
        self.resize(1000, 720)
        self.setAcceptDrops(True)
        self._titlebar_done = False

        self.roots: list[str] = []
        self.result: EmptyScanResult | None = None
        self.scan_thread: EmptyScanThread | None = None
        self.delete_thread: EmptyDeleteThread | None = None
        self._scanning = False
        self._deleting = False
        self._scan_errors = 0

        self.model = EmptyFolderModel(self)
        self.model.selectionChanged.connect(self._on_selection_changed)
        self._build_ui()
        self.add_roots(roots)
        self._reset_results()
        self._update_controls()

    # ------------------------------------------------------------------ UI
    def _build_ui(self) -> None:
        layout = QVBoxLayout(self)
        layout.setContentsMargins(22, 18, 22, 16)
        layout.setSpacing(12)

        title = QLabel("空文件夹清理")
        title.setObjectName("dialogTitle")
        subtitle = QLabel(
            "找出没有任何文件的文件夹；只列出最外层空目录，不会动你选择的根目录本身。"
        )
        subtitle.setObjectName("hint")
        subtitle.setWordWrap(True)
        layout.addWidget(title)
        layout.addWidget(subtitle)

        layout.addWidget(self._build_scan_card())
        layout.addWidget(self._build_result_panel(), 1)
        layout.addLayout(self._build_bottom_bar())

    def _build_scan_card(self) -> QWidget:
        card = QFrame()
        card.setObjectName("card")
        layout = QVBoxLayout(card)
        layout.setContentsMargins(16, 12, 16, 12)
        layout.setSpacing(9)

        top = QHBoxLayout()
        top.setSpacing(8)
        top.addWidget(make_section_title("扫描范围"))
        hint = QLabel("可添加多个文件夹")
        hint.setObjectName("hint")
        top.addWidget(hint)
        top.addStretch(1)

        self.include_hidden = QCheckBox("包含隐藏文件夹")
        self.include_hidden.setToolTip(
            "默认跳过隐藏/系统文件夹，并且它们会让父目录不算空，避免误删。"
        )
        top.addWidget(self.include_hidden)

        self.stop_button = QPushButton("  停止")
        self.stop_button.setObjectName("danger")
        self.stop_button.setIcon(icon("stop", "#ff9aa3", 13))
        self.stop_button.setVisible(False)
        self.stop_button.clicked.connect(self._stop_scan)
        top.addWidget(self.stop_button)

        self.start_button = QPushButton("  开始扫描")
        self.start_button.setObjectName("primary")
        self.start_button.setIcon(icon("play", "#ffffff", 13))
        self.start_button.setMinimumWidth(130)
        self.start_button.clicked.connect(self.start_scan)
        top.addWidget(self.start_button)
        layout.addLayout(top)

        body = QHBoxLayout()
        body.setSpacing(8)
        self.root_list = QListWidget()
        self.root_list.setObjectName("rootList")
        self.root_list.setFixedHeight(62)
        self.root_list.setHorizontalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff)
        self.root_list.itemSelectionChanged.connect(self._update_controls)
        body.addWidget(self.root_list, 1)

        side = QVBoxLayout()
        side.setSpacing(5)
        self.add_button = QPushButton("添加文件夹")
        self.add_button.setObjectName("ghost")
        self.add_button.setIcon(icon("folder-plus", theme.TEXT_DIM, 15))
        self.add_button.clicked.connect(self._choose_folder)
        self.remove_button = QPushButton("移除")
        self.remove_button.setObjectName("subtle")
        self.remove_button.clicked.connect(self._remove_selected_root)
        side.addWidget(self.add_button)
        side.addWidget(self.remove_button)
        body.addLayout(side)
        layout.addLayout(body)

        self.progress_widget = QWidget()
        progress_layout = QVBoxLayout(self.progress_widget)
        progress_layout.setContentsMargins(0, 3, 0, 0)
        progress_layout.setSpacing(6)
        self.progress_bar = QProgressBar()
        self.progress_bar.setTextVisible(False)
        progress_layout.addWidget(self.progress_bar)
        labels = QHBoxLayout()
        labels.setSpacing(10)
        self.scan_label = QLabel("正在扫描…")
        self.scan_label.setObjectName("muted")
        self.scan_detail = QLabel("")
        self.scan_detail.setObjectName("hint")
        labels.addWidget(self.scan_label)
        labels.addStretch(1)
        labels.addWidget(self.scan_detail)
        progress_layout.addLayout(labels)
        self.progress_widget.setVisible(False)
        layout.addWidget(self.progress_widget)
        return card

    def _build_result_panel(self) -> QWidget:
        panel = QFrame()
        panel.setObjectName("panel")
        layout = QVBoxLayout(panel)
        layout.setContentsMargins(14, 12, 14, 8)
        layout.setSpacing(7)

        header = QHBoxLayout()
        header.setSpacing(8)
        header.addWidget(make_section_title("空文件夹"))
        self.badge = Badge("0 个")
        header.addWidget(self.badge)
        header.addStretch(1)
        self.select_all_button = QPushButton("全选")
        self.select_all_button.setObjectName("subtle")
        self.select_all_button.clicked.connect(lambda: self._set_all(True))
        self.clear_all_button = QPushButton("全不选")
        self.clear_all_button.setObjectName("subtle")
        self.clear_all_button.clicked.connect(lambda: self._set_all(False))
        header.addWidget(self.select_all_button)
        header.addWidget(self.clear_all_button)
        hint = QLabel("默认不勾选，请确认后再删除")
        hint.setObjectName("hint")
        header.addWidget(hint)
        layout.addLayout(header)

        self.result_stack = QStackedWidget()
        self.empty_state = EmptyState(
            "folder",
            "还没有扫描",
            "添加文件夹后点击“开始扫描”。\n只列出最外层空文件夹，不会删除扫描根目录本身。",
        )
        self.table = QTableView()
        self.table.setModel(self.model)
        self.table.setSelectionBehavior(QAbstractItemView.SelectionBehavior.SelectRows)
        self.table.setSelectionMode(QAbstractItemView.SelectionMode.SingleSelection)
        self.table.setEditTriggers(QAbstractItemView.EditTrigger.NoEditTriggers)
        self.table.setShowGrid(False)
        self.table.setAlternatingRowColors(True)
        self.table.setWordWrap(False)
        self.table.setTextElideMode(Qt.TextElideMode.ElideMiddle)
        self.table.verticalHeader().setVisible(False)
        self.table.verticalHeader().setDefaultSectionSize(38)
        self.table.setItemDelegateForColumn(0, CheckDelegate(self.table))
        header_view = self.table.horizontalHeader()
        header_view.setHighlightSections(False)
        header_view.setDefaultAlignment(Qt.AlignmentFlag.AlignLeft | Qt.AlignmentFlag.AlignVCenter)
        header_view.setSectionResizeMode(0, QHeaderView.ResizeMode.Fixed)
        header_view.setSectionResizeMode(1, QHeaderView.ResizeMode.Stretch)
        self.table.setColumnWidth(0, 52)
        self.table.doubleClicked.connect(self._open_row)
        self.result_stack.addWidget(self.empty_state)
        self.result_stack.addWidget(self.table)
        layout.addWidget(self.result_stack, 1)
        return panel

    def _build_bottom_bar(self) -> QHBoxLayout:
        row = QHBoxLayout()
        row.setSpacing(8)
        self.status_label = QLabel("就绪")
        self.status_label.setObjectName("hint")
        self.status_label.setMinimumWidth(120)
        self.status_label.setSizePolicy(QSizePolicy.Policy.Ignored, QSizePolicy.Policy.Preferred)
        row.addWidget(self.status_label, 1)

        self.summary = QLabel("尚未勾选空文件夹")
        self.summary.setObjectName("selectionSummary")
        self.summary.setMinimumWidth(150)
        row.addWidget(self.summary)

        self.mode_combo = QComboBox()
        self.mode_combo.addItem("移入回收站", "recycle")
        self.mode_combo.addItem("直接删除（空文件夹）", "direct")
        self.mode_combo.setFixedWidth(165)
        self.mode_combo.setToolTip("移入回收站可还原；直接删除只适用于空文件夹，速度快")
        row.addWidget(self.mode_combo)

        self.delete_button = QPushButton("删除选中")
        self.delete_button.setObjectName("danger")
        self.delete_button.setIcon(icon("trash", "#ff9aa3", 15))
        self.delete_button.clicked.connect(self._delete_selected)
        row.addWidget(self.delete_button)

        self.close_button = QPushButton("关闭")
        self.close_button.setObjectName("ghost")
        self.close_button.clicked.connect(self.close)
        row.addWidget(self.close_button)
        return row

    # ------------------------------------------------------------------ 根目录
    def add_roots(self, paths: list[str]) -> int:
        existing = {norm_key(root) for root in self.roots}
        added = 0
        for path in paths:
            if not path:
                continue
            text = os.path.normpath(os.path.abspath(os.path.expanduser(str(path))))
            if not os.path.isdir(text):
                continue
            key = norm_key(text)
            if key in existing:
                continue
            self.roots.append(text)
            existing.add(key)
            added += 1
        if added:
            self._refresh_roots()
        return added

    def _choose_folder(self) -> None:
        if self._scanning or self._deleting:
            return
        start = self.roots[-1] if self.roots else os.path.expanduser("~")
        folder = QFileDialog.getExistingDirectory(self, "选择要扫描的文件夹", start)
        if folder:
            self.add_roots([folder])

    def _refresh_roots(self) -> None:
        self.root_list.blockSignals(True)
        self.root_list.clear()
        if not self.roots:
            placeholder = QListWidgetItem("还没有选择文件夹")
            placeholder.setFlags(Qt.ItemFlag.NoItemFlags)
            from PySide6.QtGui import QColor

            placeholder.setForeground(QColor(theme.MUTED_2))
            self.root_list.addItem(placeholder)
        else:
            for root in self.roots:
                item = QListWidgetItem(icon("folder", theme.MUTED, 16), root)
                item.setToolTip(root)
                self.root_list.addItem(item)
        self.root_list.blockSignals(False)
        self._update_controls()

    def _remove_selected_root(self) -> None:
        if self._scanning or self._deleting:
            return
        row = self.root_list.currentRow()
        if 0 <= row < len(self.roots):
            self.roots.pop(row)
            self._refresh_roots()

    # ------------------------------------------------------------------ 扫描
    def start_scan(self) -> None:
        if self._scanning or self._deleting:
            return
        roots = [root for root in self.roots if os.path.isdir(root)]
        if not roots:
            self._show_message("无法扫描", "请先添加至少一个存在的文件夹。", QMessageBox.Icon.Warning)
            return

        self._reset_results()
        self._scan_errors = 0
        self._scanning = True
        self.progress_widget.setVisible(True)
        self.progress_bar.setRange(0, 0)
        self.scan_label.setText("正在扫描文件夹…")
        self.scan_detail.setText("已扫描 0 个文件夹")
        self.scan_label.setToolTip("")
        self.status_label.setText("正在扫描…")
        self._update_controls()

        options = EmptyScanOptions(include_hidden=self.include_hidden.isChecked())
        thread = EmptyScanThread(roots, options, self)
        thread.progressed.connect(self._on_progress)
        thread.errorOccurred.connect(self._on_error)
        thread.resultReady.connect(self._on_finished)
        thread.crashed.connect(self._on_crashed)
        self.scan_thread = thread
        thread.start()

    def _stop_scan(self) -> None:
        if self.scan_thread is not None and self.scan_thread.isRunning():
            self.scan_thread.cancel_event.set()
            self.stop_button.setEnabled(False)
            self.scan_label.setText("正在停止…")
            self.status_label.setText("正在取消扫描…")

    def _on_progress(self, dirs_scanned: int, current: str) -> None:
        if not self._scanning:
            return
        text = f"已扫描 {dirs_scanned:,} 个文件夹"
        if self._scan_errors:
            text += f" · 跳过 {self._scan_errors}"
        self.scan_detail.setText(text)
        if current:
            self.scan_detail.setToolTip(current)

    def _on_error(self, path: str, message: str) -> None:
        self._scan_errors += 1
        self.scan_label.setToolTip(f"{path}\n{message}")

    def _on_finished(self, result: EmptyScanResult) -> None:
        self._scanning = False
        self.scan_thread = None
        self.progress_widget.setVisible(False)
        if result.cancelled:
            self.status_label.setText("扫描已取消")
            self._update_controls()
            return

        self.result = result
        self.model.set_folders(result.folders)
        self.badge.setText(f"{len(result.folders)} 个")
        if result.folders:
            self.result_stack.setCurrentWidget(self.table)
            self.table.selectRow(0)
        else:
            self.result_stack.setCurrentWidget(self.empty_state)
            if result.dirs_scanned == 0:
                self.empty_state.set_text("没有扫描到目录", "检查文件夹是否存在、是否有读取权限。")
            else:
                self.empty_state.set_text(
                    "没有发现空文件夹",
                    f"共扫描 {result.dirs_scanned:,} 个目录，没有找到可清理的空文件夹。",
                )

        message = (
            f"扫描完成：{result.dirs_scanned:,} 个目录，"
            f"{len(result.folders):,} 个空文件夹，用时 {format_elapsed(result.elapsed)}"
        )
        if result.errors:
            message += f"（{len(result.errors)} 个目录读取失败）"
            self.status_label.setToolTip(
                "\n".join(f"{error.path}：{error.message}" for error in result.errors[:20])
            )
        self.status_label.setText(message)
        self._update_controls()

    def _on_crashed(self, details: str) -> None:
        self._scanning = False
        self.scan_thread = None
        self.progress_widget.setVisible(False)
        self.status_label.setText("扫描失败")
        self._update_controls()
        last_line = details.strip().splitlines()[-1] if details.strip() else "未知错误"
        self._show_message("扫描失败", last_line, QMessageBox.Icon.Critical)

    # ------------------------------------------------------------------ 结果操作
    def _reset_results(self) -> None:
        self.result = None
        self.model.set_folders([])
        self.badge.setText("0 个")
        self.result_stack.setCurrentWidget(self.empty_state)
        self.empty_state.set_text(
            "还没有扫描",
            "添加文件夹后点击“开始扫描”。\n只列出最外层空文件夹，不会删除扫描根目录本身。",
        )
        self.summary.setText("尚未勾选空文件夹")
        self.delete_button.setEnabled(False)

    def _set_all(self, checked: bool) -> None:
        if self._scanning or self._deleting:
            return
        self.model.set_all(checked)
        self._on_selection_changed()

    def _on_selection_changed(self) -> None:
        count = self.model.checked_count()
        if count:
            self.summary.setText(f"已选 {count:,} 个空文件夹")
        else:
            self.summary.setText("尚未勾选空文件夹")
        self._update_controls()

    def _update_controls(self) -> None:
        has_rows = self.model.rowCount() > 0
        self.start_button.setVisible(not self._scanning)
        self.start_button.setEnabled(not self._scanning and bool(self.roots))
        self.stop_button.setVisible(self._scanning)
        self.stop_button.setEnabled(self._scanning)
        self.add_button.setEnabled(not self._scanning and not self._deleting)
        self.remove_button.setEnabled(
            not self._scanning and not self._deleting and self.root_list.currentRow() >= 0
        )
        self.root_list.setEnabled(not self._scanning and not self._deleting)
        self.include_hidden.setEnabled(not self._scanning and not self._deleting)
        self.table.setEnabled(not self._scanning and not self._deleting)
        self.select_all_button.setEnabled(has_rows and not self._scanning and not self._deleting)
        self.clear_all_button.setEnabled(has_rows and not self._scanning and not self._deleting)
        self.mode_combo.setEnabled(not self._scanning and not self._deleting)
        self.delete_button.setEnabled(
            has_rows and self.model.checked_count() > 0 and not self._scanning and not self._deleting
        )

    def _open_row(self, index) -> None:
        path = self.model.folder_at(index.row())
        if path and not open_in_explorer(path):
            self.status_label.setText("无法打开资源管理器")

    # ------------------------------------------------------------------ 删除
    def _delete_selected(self) -> None:
        if self._scanning or self._deleting:
            return
        paths = self.model.checked_paths()
        if not paths:
            return
        mode = str(self.mode_combo.currentData())
        confirm = ConfirmEmptyDeleteDialog(len(paths), mode, paths, self)
        if confirm.exec() != QDialog.DialogCode.Accepted or not confirm.confirmed:
            return

        self._deleting = True
        action = "移入回收站" if mode == "recycle" else "删除"
        self.status_label.setText(f"正在{action} {len(paths):,} 个空文件夹…")
        self._update_controls()
        thread = EmptyDeleteThread(paths, mode, self)
        thread.done.connect(self._on_delete_finished)
        self.delete_thread = thread
        thread.start()

    def _on_delete_finished(self, outcome: EmptyDeleteOutcome) -> None:
        self._deleting = False
        self.delete_thread = None
        self.model.remove_folders(outcome.removed)
        self.badge.setText(f"{len(self.model.folders)} 个")
        if not self.model.folders:
            self.result_stack.setCurrentWidget(self.empty_state)
            self.empty_state.set_text(
                "空文件夹已清理",
                "当前列表里没有剩余的空文件夹；可以重新扫描或关闭窗口。",
            )
        self.status_label.setText(f"已删除 {len(outcome.removed):,} 个空文件夹")
        self._update_controls()
        if outcome.failures:
            lines = [f"{shorten_path(path, 70)}：{reason}" for path, reason in outcome.failures[:6]]
            if len(outcome.failures) > 6:
                lines.append(f"…… 另外 {len(outcome.failures) - 6} 个")
            self._show_message(
                "部分空文件夹未处理",
                f"{len(outcome.failures)} 个目录没有删除成功：\n\n" + "\n".join(lines),
                QMessageBox.Icon.Warning,
            )

    # ------------------------------------------------------------------ 其他
    def _show_message(self, title: str, text: str, icon_type=QMessageBox.Icon.Information) -> None:
        box = QMessageBox(self)
        box.setIcon(icon_type)
        box.setWindowTitle(title)
        box.setText(text)
        button = box.addButton("知道了", QMessageBox.ButtonRole.AcceptRole)
        box.setDefaultButton(button)
        box.exec()

    def dragEnterEvent(self, event) -> None:  # noqa: N802
        if event.mimeData().hasUrls():
            event.acceptProposedAction()

    def dropEvent(self, event) -> None:  # noqa: N802
        folders: list[str] = []
        for url in event.mimeData().urls():
            local = url.toLocalFile()
            if not local:
                continue
            if os.path.isdir(local):
                folders.append(local)
            elif os.path.isfile(local):
                folders.append(os.path.dirname(local))
        if folders:
            self.add_roots(folders)
            event.acceptProposedAction()

    def showEvent(self, event) -> None:  # noqa: N802
        super().showEvent(event)
        if not self._titlebar_done:
            self._titlebar_done = True
            enable_dark_titlebar(self)

    def closeEvent(self, event) -> None:  # noqa: N802
        if self._deleting:
            self._show_message(
                "正在删除",
                "删除操作还没有完成，请等待状态栏提示完成后再关闭。",
                QMessageBox.Icon.Information,
            )
            event.ignore()
            return
        if self._scanning and self.scan_thread is not None:
            self.scan_thread.cancel_event.set()
            if not self.scan_thread.wait(5000):
                self._show_message(
                    "正在停止扫描",
                    "扫描线程还没有退出，请稍候再关闭窗口。",
                    QMessageBox.Icon.Information,
                )
                event.ignore()
                return
            self._scanning = False
        super().closeEvent(event)

    def reject(self) -> None:
        if self._scanning or self._deleting:
            self._show_message(
                "正在处理",
                "扫描或删除还没有完成，请等待状态结束后再关闭窗口。",
                QMessageBox.Icon.Information,
            )
            return
        super().reject()

    def accept(self) -> None:
        if self._scanning or self._deleting:
            return
        super().accept()
