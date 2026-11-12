"""主窗口：扫描、重复组浏览、预览、智能勾选、安全删除。"""

from __future__ import annotations

import os
import threading
import traceback
from dataclasses import dataclass
from datetime import datetime

from PySide6.QtCore import QEvent, QSettings, QSize, Qt, QThread, Signal
from PySide6.QtGui import QColor, QGuiApplication, QKeySequence, QShortcut
from PySide6.QtWidgets import (
    QAbstractItemView,
    QComboBox,
    QDialog,
    QFileDialog,
    QFrame,
    QHBoxLayout,
    QHeaderView,
    QLabel,
    QListWidget,
    QListWidgetItem,
    QMainWindow,
    QMessageBox,
    QProgressBar,
    QPushButton,
    QScrollArea,
    QSplitter,
    QStackedWidget,
    QStatusBar,
    QTableView,
    QVBoxLayout,
    QWidget,
)

from .. import __version__
from ..cache import HashCache
from ..config import KEEP_POLICIES, AppSettings
from ..fileops import move_to_quarantine, open_file, open_in_explorer, recycle_files
from ..models import DuplicateGroup, FileEntry, ScanResult
from ..scanner import ScanOptions, scan
from ..util import (
    IMAGE_EXTS,
    VIDEO_EXTS,
    format_elapsed,
    format_mtime,
    human_size,
    norm_key,
    shorten_path,
)
from . import theme
from .empty_folder_dialog import EmptyFolderDialog
from .icons import app_icon, icon, logo_pixmap
from .models import CheckDelegate, FileTableModel, GroupTableModel
from .preview import PreviewLoader, image_dimensions
from .settings_dialog import SettingsDialog
from .theme import enable_dark_titlebar
from .widgets import (
    Badge,
    Divider,
    ElidedLabel,
    EmptyState,
    StatTile,
    ThumbView,
    make_section_title,
    meta_row,
)


class ScanThread(QThread):
    # 注意：这里必须用 object 而不是 int。Qt 的 int 是 32 位，遇到 >2 GB 的
    # 总字节数会抛 OverflowError，而异常发生在工作线程里，处理不当会让界面卡死。
    phaseChanged = Signal(str, str, object, object)
    progressed = Signal(object, object, object, object, float)
    errorOccurred = Signal(str, str)
    resultReady = Signal(object)
    crashed = Signal(str)

    def __init__(self, roots: list[str], options: ScanOptions, parent=None) -> None:
        super().__init__(parent)
        self.roots = list(roots)
        self.options = options
        self.cancel_event = threading.Event()

    def run(self) -> None:
        cache = None
        try:
            if self.options.use_cache:
                cache = HashCache()
            result = scan(
                self.roots,
                self.options,
                cache=cache,
                on_phase=lambda *args: self.phaseChanged.emit(*args),
                on_progress=lambda *args: self.progressed.emit(*args),
                on_error=lambda path, msg: self.errorOccurred.emit(path, msg),
                cancel=self.cancel_event,
            )
            self.resultReady.emit(result)
        except Exception:
            self.crashed.emit(traceback.format_exc())
        finally:
            if cache is not None:
                cache.close()


@dataclass(slots=True)
class DeleteReport:
    removed: list[str]
    failures: list[tuple[str, str]]
    folder: str = ""


class DeleteThread(QThread):
    done = Signal(object)

    def __init__(self, paths: list[str], mode: str, quarantine_dir: str, parent=None) -> None:
        super().__init__(parent)
        self.paths = list(paths)
        self.mode = mode
        self.quarantine_dir = quarantine_dir

    def run(self) -> None:
        try:
            if self.mode == "quarantine":
                outcome = move_to_quarantine(self.paths, self.quarantine_dir)
                report = DeleteReport(
                    removed=[src for src, _dest in outcome.moved],
                    failures=outcome.failures,
                    folder=outcome.folder,
                )
            else:
                outcome = recycle_files(self.paths)
                report = DeleteReport(removed=outcome.removed, failures=outcome.failures)
        except Exception as exc:
            report = DeleteReport(removed=[], failures=[(p, str(exc)) for p in self.paths])
        self.done.emit(report)


class ConfirmDeleteDialog(QDialog):
    def __init__(
        self,
        count: int,
        total_size: int,
        paths: list[str],
        mode: str,
        parent: QWidget | None = None,
    ) -> None:
        super().__init__(parent)
        self.confirmed = False
        self._titlebar_done = False
        self.setWindowTitle("确认删除")
        self.setModal(True)
        self.resize(620, 430)

        layout = QVBoxLayout(self)
        layout.setContentsMargins(24, 22, 24, 18)
        layout.setSpacing(12)

        title = QLabel("确认要删除这些副本吗？")
        title.setObjectName("dialogTitle")
        layout.addWidget(title)

        action_text = "移入回收站" if mode == "recycle" else "移动到隔离文件夹"
        summary = QLabel(
            f"即将把 <b>{count}</b> 个文件{action_text}，预计释放 <b>{human_size(total_size)}</b>。"
        )
        summary.setObjectName("muted")
        summary.setWordWrap(True)
        layout.addWidget(summary)

        listing = QListWidget()
        listing.setObjectName("rootList")
        listing.setSelectionMode(QAbstractItemView.SelectionMode.NoSelection)
        listing.setFocusPolicy(Qt.FocusPolicy.NoFocus)
        listing.setHorizontalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff)
        listing.setTextElideMode(Qt.TextElideMode.ElideMiddle)
        for path in paths[:400]:
            item = QListWidgetItem(shorten_path(path, 84))
            item.setToolTip(path)
            listing.addItem(item)
        if len(paths) > 400:
            listing.addItem(f"…… 另外 {len(paths) - 400} 个文件")
        layout.addWidget(listing, 1)

        note = QLabel(
            "每组至少保留 1 个文件，程序不会允许整组删除。"
            + (" 删除后可在 Windows 回收站中还原。" if mode == "recycle" else " 文件会按时间归档到隔离文件夹，可手动移回。")
        )
        note.setObjectName("hint")
        note.setWordWrap(True)
        layout.addWidget(note)

        buttons = QHBoxLayout()
        buttons.addStretch(1)
        cancel = QPushButton("取消")
        cancel.setObjectName("ghost")
        cancel.clicked.connect(self.reject)
        confirm = QPushButton(action_text)
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


class MainWindow(QMainWindow):
    scanCompleted = Signal(object)
    scanStarted = Signal()

    def __init__(self, qsettings: QSettings | None = None) -> None:
        super().__init__()
        self.qs = qsettings or QSettings("WriteBugsProfessionally", "SameFileChecker")
        self.settings = self._load_settings()
        self.roots: list[str] = self._load_roots()
        self.result: ScanResult | None = None
        self.scan_thread: ScanThread | None = None
        self.delete_thread: DeleteThread | None = None
        self._scanning = False
        self._deleting = False
        self._closing = False
        self._scan_errors = 0
        self._current_entry: FileEntry | None = None
        self._preview_path: str | None = None
        self._dims_cache: dict[tuple[str, int], str] = {}
        self._titlebar_done = False

        self.group_model = GroupTableModel(self)
        self.file_model = FileTableModel(self)
        self.preview_loader = PreviewLoader(self)

        self.setWindowTitle(f"SameFileChecker · 重复文件检测 v{__version__}")
        self.setWindowIcon(app_icon())
        self.setMinimumSize(1100, 700)
        self.setAcceptDrops(True)
        self._build_ui()
        self._restore_window_state()
        self._connect_signals()
        self._refresh_root_list()
        self._reset_result_ui()
        self._sync_policy_combo()
        self._update_delete_button()

    # ------------------------------------------------------------------ 设置
    def _load_settings(self) -> AppSettings:
        qs = self.qs
        defaults = AppSettings()
        settings = AppSettings()
        try:
            settings.min_size_kb = max(0, int(qs.value("scan/min_kb", defaults.min_size_kb)))
            settings.ext_preset = str(qs.value("scan/ext_preset", defaults.ext_preset))
            settings.custom_exts = str(qs.value("scan/custom_exts", defaults.custom_exts))
            settings.workers = max(1, min(16, int(qs.value("scan/workers", defaults.workers))))
            settings.include_hidden = str(qs.value("scan/include_hidden", defaults.include_hidden)).lower() in ("true", "1")
            settings.use_cache = str(qs.value("scan/use_cache", defaults.use_cache)).lower() in ("true", "1")
            settings.exclude_dirs = str(qs.value("scan/exclude_dirs", defaults.exclude_dirs))
            settings.auto_select = str(qs.value("result/auto_select", defaults.auto_select)).lower() in ("true", "1")
            settings.policy = str(qs.value("result/policy", defaults.policy))
            settings.delete_mode = str(qs.value("result/delete_mode", defaults.delete_mode))
            settings.quarantine_dir = str(qs.value("result/quarantine_dir", defaults.quarantine_dir))
        except (TypeError, ValueError):
            return defaults
        if settings.policy not in {key for key, _ in KEEP_POLICIES}:
            settings.policy = defaults.policy
        if settings.delete_mode not in {"recycle", "quarantine"}:
            settings.delete_mode = defaults.delete_mode
        return settings

    def _save_settings(self) -> None:
        qs = self.qs
        settings = self.settings
        qs.setValue("scan/min_kb", int(settings.min_size_kb))
        qs.setValue("scan/ext_preset", settings.ext_preset)
        qs.setValue("scan/custom_exts", settings.custom_exts)
        qs.setValue("scan/workers", int(settings.workers))
        qs.setValue("scan/include_hidden", settings.include_hidden)
        qs.setValue("scan/use_cache", settings.use_cache)
        qs.setValue("scan/exclude_dirs", settings.exclude_dirs)
        qs.setValue("result/auto_select", settings.auto_select)
        qs.setValue("result/policy", settings.policy)
        qs.setValue("result/delete_mode", settings.delete_mode)
        qs.setValue("result/quarantine_dir", settings.quarantine_dir)
        qs.setValue("scan/roots", self.roots)

    def _load_roots(self) -> list[str]:
        raw = self.qs.value("scan/roots", [])
        if isinstance(raw, str):
            raw = [raw]
        roots: list[str] = []
        if isinstance(raw, (list, tuple)):
            for item in raw:
                text = str(item)
                if text and os.path.isdir(text) and norm_key(text) not in {norm_key(r) for r in roots}:
                    roots.append(os.path.normpath(text))
        return roots

    # ------------------------------------------------------------------ 界面
    def _build_ui(self) -> None:
        central = QWidget()
        outer = QVBoxLayout(central)
        outer.setContentsMargins(0, 0, 0, 0)
        outer.setSpacing(0)

        content = QWidget()
        content_layout = QVBoxLayout(content)
        content_layout.setContentsMargins(22, 16, 22, 12)
        content_layout.setSpacing(13)

        content_layout.addWidget(self._build_header())
        content_layout.addWidget(self._build_scan_card())
        content_layout.addWidget(self._build_stats_row())
        content_layout.addWidget(self._build_body(), 1)

        outer.addWidget(content, 1)
        outer.addWidget(self._build_action_bar())
        self.setCentralWidget(central)

        status = QStatusBar(self)
        status.setSizeGripEnabled(False)
        self.setStatusBar(status)
        status.showMessage("就绪")
        version = QLabel(f"v{__version__}")
        version.setObjectName("hint")
        status.addPermanentWidget(version)

    def _build_header(self) -> QWidget:
        header = QWidget()
        layout = QHBoxLayout(header)
        layout.setContentsMargins(2, 0, 2, 0)
        layout.setSpacing(13)

        logo = QLabel()
        logo.setPixmap(logo_pixmap(38, dpr=2.0))
        logo.setFixedSize(40, 40)
        layout.addWidget(logo)

        titles = QVBoxLayout()
        titles.setContentsMargins(0, 0, 0, 0)
        titles.setSpacing(1)
        title = QLabel("SameFileChecker")
        title.setObjectName("appTitle")
        subtitle = QLabel("找出内容相同、名字不同的重复图片与视频 —— 只比对内容，不看文件名")
        subtitle.setObjectName("appSubtitle")
        titles.addWidget(title)
        titles.addWidget(subtitle)
        layout.addLayout(titles)
        layout.addStretch(1)

        self.ffmpeg_hint = QLabel()
        self.ffmpeg_hint.setObjectName("hint")
        self.ffmpeg_hint.setToolTip("有 FFmpeg 时，视频预览会显示首帧；没有也不影响查重。")
        layout.addWidget(self.ffmpeg_hint)

        self.empty_button = QPushButton("  空文件夹")
        self.empty_button.setObjectName("ghost")
        self.empty_button.setIcon(icon("folder-minus", theme.TEXT_DIM, 15))
        self.empty_button.setToolTip("扫描并清理没有任何文件的文件夹")
        self.empty_button.setCursor(Qt.CursorShape.PointingHandCursor)
        self.empty_button.clicked.connect(self._open_empty_folders)
        layout.addWidget(self.empty_button)

        self.settings_button = QPushButton("  设置")
        self.settings_button.setObjectName("ghost")
        self.settings_button.setIcon(icon("settings", theme.TEXT_DIM, 16))
        self.settings_button.setCursor(Qt.CursorShape.PointingHandCursor)
        self.settings_button.clicked.connect(self._open_settings)
        layout.addWidget(self.settings_button)
        return header

    def _build_scan_card(self) -> QWidget:
        card = QFrame()
        card.setObjectName("card")
        layout = QVBoxLayout(card)
        layout.setContentsMargins(16, 13, 16, 13)
        layout.setSpacing(9)

        top = QHBoxLayout()
        top.setSpacing(8)
        top.addWidget(make_section_title("扫描范围"))
        hint = QLabel("可以直接把文件夹拖进窗口")
        hint.setObjectName("hint")
        top.addWidget(hint)
        top.addStretch(1)

        self.stop_button = QPushButton("  停止")
        self.stop_button.setObjectName("danger")
        self.stop_button.setIcon(icon("stop", "#ff9aa3", 13))
        self.stop_button.clicked.connect(self.stop_scan)
        self.stop_button.setVisible(False)
        top.addWidget(self.stop_button)

        self.start_button = QPushButton("  开始扫描")
        self.start_button.setObjectName("primary")
        self.start_button.setIcon(icon("play", "#ffffff", 13))
        self.start_button.setMinimumWidth(132)
        self.start_button.clicked.connect(self.start_scan)
        top.addWidget(self.start_button)
        layout.addLayout(top)

        body = QHBoxLayout()
        body.setSpacing(8)
        self.root_list = QListWidget()
        self.root_list.setObjectName("rootList")
        self.root_list.setFixedHeight(60)
        self.root_list.setHorizontalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff)
        self.root_list.itemSelectionChanged.connect(self._on_root_selection_changed)
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
        progress_labels = QHBoxLayout()
        progress_labels.setSpacing(10)
        self.scan_phase = QLabel("准备中…")
        self.scan_phase.setObjectName("muted")
        self.scan_detail = QLabel("")
        self.scan_detail.setObjectName("hint")
        progress_labels.addWidget(self.scan_phase)
        progress_labels.addStretch(1)
        progress_labels.addWidget(self.scan_detail)
        progress_layout.addLayout(progress_labels)
        self.progress_widget.setVisible(False)
        layout.addWidget(self.progress_widget)
        return card

    def _build_stats_row(self) -> QWidget:
        row = QWidget()
        layout = QHBoxLayout(row)
        layout.setContentsMargins(0, 0, 0, 0)
        layout.setSpacing(10)
        self.stat_files = StatTile("枚举文件")
        self.stat_groups = StatTile("重复组", accent="statValueAccent")
        self.stat_dupes = StatTile("重复文件")
        self.stat_wasted = StatTile("可释放空间", accent="statValueGreen")
        for tile in (self.stat_files, self.stat_groups, self.stat_dupes, self.stat_wasted):
            layout.addWidget(tile)
        return row

    def _build_body(self) -> QWidget:
        self.splitter = QSplitter(Qt.Orientation.Horizontal)
        self.splitter.setHandleWidth(12)
        self.splitter.setChildrenCollapsible(False)
        self.splitter.addWidget(self._build_group_panel())
        self.splitter.addWidget(self._build_file_panel())
        self.splitter.addWidget(self._build_preview_panel())
        self.splitter.setStretchFactor(0, 0)
        self.splitter.setStretchFactor(1, 1)
        self.splitter.setStretchFactor(2, 0)
        self.splitter.setSizes([342, 760, 340])
        return self.splitter

    def _build_group_panel(self) -> QWidget:
        panel = QFrame()
        panel.setObjectName("panel")
        panel.setMinimumWidth(300)
        panel.setMaximumWidth(460)
        layout = QVBoxLayout(panel)
        layout.setContentsMargins(13, 12, 13, 8)
        layout.setSpacing(7)

        header = QHBoxLayout()
        header.setSpacing(7)
        header.addWidget(make_section_title("重复组"))
        self.group_badge = Badge("0 组")
        header.addWidget(self.group_badge)
        header.addStretch(1)
        sort_hint = QLabel("按可释放排序")
        sort_hint.setObjectName("hint")
        header.addWidget(sort_hint)
        layout.addLayout(header)

        self.group_stack = QStackedWidget()
        self.group_empty = EmptyState(
            "search",
            "还没有扫描结果",
            "添加要扫描的文件夹，然后点击“开始扫描”。\n程序只按内容比对，与文件名无关。",
        )
        self.group_table = QTableView()
        self.group_table.setModel(self.group_model)
        self.group_table.setSelectionBehavior(QAbstractItemView.SelectionBehavior.SelectRows)
        self.group_table.setSelectionMode(QAbstractItemView.SelectionMode.SingleSelection)
        self.group_table.setEditTriggers(QAbstractItemView.EditTrigger.NoEditTriggers)
        self.group_table.setShowGrid(False)
        self.group_table.setAlternatingRowColors(True)
        self.group_table.setWordWrap(False)
        self.group_table.setFocusPolicy(Qt.FocusPolicy.StrongFocus)
        self.group_table.verticalHeader().setVisible(False)
        self.group_table.verticalHeader().setDefaultSectionSize(37)
        self.group_table.setTextElideMode(Qt.TextElideMode.ElideRight)
        header_view = self.group_table.horizontalHeader()
        header_view.setHighlightSections(False)
        header_view.setStretchLastSection(False)
        widths = (38, 46, 78, 88, 44)
        for index, width in enumerate(widths):
            header_view.setSectionResizeMode(index, QHeaderView.ResizeMode.Fixed)
            self.group_table.setColumnWidth(index, width)
        self.group_stack.addWidget(self.group_empty)
        self.group_stack.addWidget(self.group_table)
        layout.addWidget(self.group_stack, 1)
        return panel

    def _build_file_panel(self) -> QWidget:
        panel = QFrame()
        panel.setObjectName("panel")
        layout = QVBoxLayout(panel)
        layout.setContentsMargins(13, 12, 13, 8)
        layout.setSpacing(7)

        header = QHBoxLayout()
        header.setSpacing(8)
        self.group_title = QLabel("组内文件")
        self.group_title.setObjectName("groupTitle")
        self.group_meta = QLabel("")
        self.group_meta.setObjectName("hint")
        header.addWidget(self.group_title)
        header.addWidget(self.group_meta)
        header.addStretch(1)
        layout.addLayout(header)

        legend = QHBoxLayout()
        legend.setContentsMargins(1, 0, 0, 0)
        legend.setSpacing(14)
        self.legend_delete = QLabel("✓ 勾选 = 删除")
        self.legend_delete.setObjectName("legendDelete")
        self.legend_delete.setToolTip("勾选的文件会在确认后移入回收站")
        self.legend_keep = QLabel("○ 未勾选 = 保留")
        self.legend_keep.setObjectName("legendKeep")
        self.legend_keep.setToolTip("不勾选的文件会保留在原处")
        legend.addWidget(self.legend_delete)
        legend.addWidget(self.legend_keep)
        legend.addStretch(1)
        layout.addLayout(legend)

        self.file_stack = QStackedWidget()
        self.file_empty = EmptyState(
            "image",
            "选择左侧的一个重复组",
            "这里会列出组内所有文件；标题、路径、修改时间不同，内容完全一致。",
        )
        self.file_table = QTableView()
        self.file_table.setModel(self.file_model)
        self.file_table.setSelectionBehavior(QAbstractItemView.SelectionBehavior.SelectRows)
        self.file_table.setSelectionMode(QAbstractItemView.SelectionMode.SingleSelection)
        self.file_table.setEditTriggers(QAbstractItemView.EditTrigger.NoEditTriggers)
        self.file_table.setShowGrid(False)
        self.file_table.setAlternatingRowColors(True)
        self.file_table.setWordWrap(False)
        self.file_table.verticalHeader().setVisible(False)
        self.file_table.verticalHeader().setDefaultSectionSize(40)
        self.file_table.setTextElideMode(Qt.TextElideMode.ElideMiddle)
        self.file_table.setIconSize(QSize(16, 16))
        self.file_table.setItemDelegateForColumn(0, CheckDelegate(self.file_table))
        file_header = self.file_table.horizontalHeader()
        file_header.setHighlightSections(False)
        file_header.setStretchLastSection(False)
        file_header.setSectionResizeMode(0, QHeaderView.ResizeMode.Fixed)
        file_header.setSectionResizeMode(1, QHeaderView.ResizeMode.Stretch)
        for index, width in ((0, 52), (2, 70), (3, 128), (4, 180)):
            file_header.setSectionResizeMode(index, QHeaderView.ResizeMode.Fixed)
            self.file_table.setColumnWidth(index, width)
        self.file_stack.addWidget(self.file_empty)
        self.file_stack.addWidget(self.file_table)
        layout.addWidget(self.file_stack, 1)
        return panel

    def _build_preview_panel(self) -> QWidget:
        panel = QFrame()
        panel.setObjectName("panel")
        panel.setMinimumWidth(290)
        panel.setMaximumWidth(400)

        panel_layout = QVBoxLayout(panel)
        panel_layout.setContentsMargins(0, 0, 0, 0)
        panel_layout.setSpacing(0)

        scroll = QScrollArea()
        scroll.setObjectName("previewScroll")
        scroll.setWidgetResizable(True)
        scroll.setFrameShape(QFrame.Shape.NoFrame)
        scroll.setHorizontalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff)
        scroll.setStyleSheet(
            "QScrollArea#previewScroll { background: transparent; border: none; }"
            "QScrollArea#previewScroll > QWidget > QWidget { background: transparent; }"
        )

        content = QWidget()
        content.setObjectName("previewContent")
        layout = QVBoxLayout(content)
        layout.setContentsMargins(15, 10, 15, 10)
        layout.setSpacing(7)

        layout.addWidget(make_section_title("预览"))
        self.thumb = ThumbView()
        self.thumb.setMinimumHeight(170)
        layout.addWidget(self.thumb, 1)

        self.preview_name = QLabel("未选择文件")
        self.preview_name.setObjectName("previewName")
        self.preview_name.setWordWrap(False)
        layout.addWidget(self.preview_name)

        self.meta_size = meta_row("大小")
        self.meta_time = meta_row("修改时间")
        self.meta_dims = meta_row("分辨率")
        self.meta_kind = meta_row("类型")
        for container, _value in (self.meta_size, self.meta_time, self.meta_dims, self.meta_kind):
            layout.addWidget(container)

        self.preview_path = ElidedLabel("—")
        self.preview_path.setObjectName("previewPath")
        layout.addWidget(self.preview_path)

        actions = QHBoxLayout()
        actions.setSpacing(7)
        self.open_button = QPushButton("打开位置")
        self.open_button.setObjectName("ghost")
        self.open_button.setIcon(icon("open", theme.TEXT_DIM, 15))
        self.open_button.clicked.connect(self._open_current_location)
        self.copy_button = QPushButton("复制路径")
        self.copy_button.setObjectName("ghost")
        self.copy_button.setIcon(icon("copy", theme.TEXT_DIM, 15))
        self.copy_button.clicked.connect(self._copy_current_path)
        actions.addWidget(self.open_button)
        actions.addWidget(self.copy_button)
        layout.addLayout(actions)

        layout.addWidget(Divider())

        self.keep_button = QPushButton("保留这个文件，勾选其他副本")
        self.keep_button.setObjectName("ghost")
        self.keep_button.setIcon(icon("check", theme.ACCENT, 15))
        self.keep_button.clicked.connect(self._keep_current_file)
        layout.addWidget(self.keep_button)
        self.keep_hint = QLabel("适合手工挑选要留下的那一份")
        self.keep_hint.setObjectName("hint")
        layout.addWidget(self.keep_hint)
        self._set_preview_actions_enabled(False)
        scroll.setWidget(content)
        panel_layout.addWidget(scroll)
        return panel

    def _build_action_bar(self) -> QWidget:
        bar = QFrame()
        bar.setObjectName("actionBar")
        bar.setFixedHeight(62)
        layout = QHBoxLayout(bar)
        layout.setContentsMargins(22, 0, 22, 0)
        layout.setSpacing(9)

        policy_label = QLabel("保留策略")
        policy_label.setObjectName("hint")
        layout.addWidget(policy_label)

        self.policy_combo = QComboBox()
        self.policy_combo.setFixedWidth(188)
        from ..config import KEEP_POLICIES

        for key, label in KEEP_POLICIES:
            self.policy_combo.addItem(label, key)
        self.policy_combo.currentIndexChanged.connect(self._on_policy_changed)
        layout.addWidget(self.policy_combo)

        self.smart_button = QPushButton("重新智能勾选")
        self.smart_button.setObjectName("ghost")
        self.smart_button.setIcon(icon("sparkles", theme.TEXT_DIM, 15))
        self.smart_button.setToolTip("按当前保留策略，为所有重复组重新勾选要删除的副本")
        self.smart_button.clicked.connect(self._apply_policy_to_all)
        layout.addWidget(self.smart_button)

        layout.addStretch(1)

        self.selection_summary = QLabel("尚未勾选要删除的文件")
        self.selection_summary.setObjectName("selectionSummary")
        layout.addWidget(self.selection_summary)

        self.export_button = QPushButton("导出报告")
        self.export_button.setObjectName("ghost")
        self.export_button.setIcon(icon("export", theme.TEXT_DIM, 15))
        self.export_button.clicked.connect(self._export_report)
        layout.addWidget(self.export_button)

        self.delete_button = QPushButton("移入回收站")
        self.delete_button.setObjectName("danger")
        self.delete_button.setIcon(icon("trash", "#ff9aa3", 15))
        self.delete_button.setMinimumWidth(126)
        self.delete_button.clicked.connect(self._delete_selected)
        layout.addWidget(self.delete_button)
        return bar

    # ------------------------------------------------------------------ 连接
    def _connect_signals(self) -> None:
        self.group_table.selectionModel().currentRowChanged.connect(self._on_group_current_changed)
        self.file_table.selectionModel().currentRowChanged.connect(self._on_file_current_changed)
        self.file_table.doubleClicked.connect(lambda index: self._open_file_at(index.row()))
        self.file_model.selectionChanged.connect(self._on_selection_changed)
        self.file_model.lastCopyWarning.connect(
            lambda: self._flash_status("每组至少要保留 1 个文件，不能把整组都删掉。")
        )
        self.file_table.viewport().installEventFilter(self)
        self.group_model.selectionSummaryChanged.connect(self._on_selection_changed)
        self.preview_loader.loaded.connect(self._on_preview_loaded)
        self.preview_loader.failed.connect(self._on_preview_failed)
        self.preview_loader.started.connect(self._on_preview_started)

        self._add_shortcut("Ctrl+O", self._choose_folder)
        self._add_shortcut("F5", self.start_scan)
        self._add_shortcut("Esc", self.stop_scan)
        self._add_shortcut("Ctrl+E", self._export_report)

        self.ffmpeg_hint.setText("视频预览：FFmpeg 已就绪" if self.preview_loader.ffmpeg_available else "视频预览需安装 FFmpeg")
        self.ffmpeg_hint.setVisible(not self.preview_loader.ffmpeg_available)

    def _add_shortcut(self, key: str, handler) -> None:
        shortcut = QShortcut(QKeySequence(key), self)
        shortcut.activated.connect(handler)

    def eventFilter(self, watched, event) -> bool:  # noqa: N802
        if watched is self.file_table.viewport() and event.type() == QEvent.Type.Resize:
            self._update_file_columns()
        return super().eventFilter(watched, event)

    def _update_file_columns(self) -> None:
        """窗口较窄时隐藏“所在文件夹”列，把空间留给文件名（完整路径仍在 tooltip 里）。"""
        width = self.file_table.viewport().width()
        self.file_table.setColumnHidden(4, width < 560)

    def _restore_window_state(self) -> None:
        geometry = self.qs.value("window/geometry")
        if geometry is not None:
            self.restoreGeometry(geometry)
        else:
            screen = QGuiApplication.primaryScreen()
            if screen is not None:
                available = screen.availableGeometry()
                width = min(1460, max(1100, available.width() - 100))
                height = min(920, max(700, available.height() - 90))
                self.resize(width, height)
                frame = self.frameGeometry()
                frame.moveCenter(available.center())
                self.move(frame.topLeft())
            else:
                self.resize(1440, 900)
        state = self.qs.value("window/splitter")
        if state is not None:
            self.splitter.restoreState(state)

    # ------------------------------------------------------------------ 扫描范围
    def add_roots(self, paths: list[str]) -> int:
        """添加扫描目录，返回实际新增数量。"""
        if self._scanning:
            return 0
        existing = {norm_key(root) for root in self.roots}
        added = 0
        for path in paths:
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
            self._refresh_root_list()
            self.qs.setValue("scan/roots", self.roots)
            self._flash_status(f"已添加 {added} 个文件夹，点击“开始扫描”")
        return added

    def _choose_folder(self) -> None:
        if self._scanning:
            return
        start = self.roots[-1] if self.roots else os.path.expanduser("~")
        folder = QFileDialog.getExistingDirectory(self, "选择要扫描的文件夹", start)
        if folder:
            self.add_roots([folder])

    def _refresh_root_list(self) -> None:
        self.root_list.blockSignals(True)
        self.root_list.clear()
        if not self.roots:
            placeholder = QListWidgetItem("还没有选择文件夹 —— 点击“添加文件夹”或把文件夹拖进来")
            placeholder.setFlags(Qt.ItemFlag.NoItemFlags)
            placeholder.setForeground(QColor(theme.MUTED_2))
            self.root_list.addItem(placeholder)
        else:
            for root in self.roots:
                item = QListWidgetItem(icon("folder", theme.MUTED, 16), root)
                item.setToolTip(root)
                item.setSizeHint(QSize(0, 24))
                self.root_list.addItem(item)
        self.root_list.blockSignals(False)
        self.remove_button.setEnabled(bool(self.roots) and self.root_list.currentRow() >= 0)
        if not self._scanning:
            self.start_button.setEnabled(bool(self.roots))

    def _on_root_selection_changed(self) -> None:
        self.remove_button.setEnabled(
            bool(self.roots) and self.root_list.currentRow() >= 0 and not self._scanning
        )

    def _remove_selected_root(self) -> None:
        if self._scanning:
            return
        row = self.root_list.currentRow()
        if 0 <= row < len(self.roots):
            removed = self.roots.pop(row)
            self._refresh_root_list()
            self.qs.setValue("scan/roots", self.roots)
            self._flash_status(f"已移除：{removed}")

    # ------------------------------------------------------------------ 扫描
    def start_scan(self) -> None:
        if self._scanning or self._deleting:
            return
        roots = [root for root in self.roots if os.path.isdir(root)]
        if not roots:
            self._show_message(
                "无法开始扫描",
                "请先添加至少一个存在的文件夹。",
                QMessageBox.Icon.Warning,
            )
            return
        options = self.settings.build_options()
        self._reset_result_ui()
        self._scan_errors = 0
        self._scanning = True
        self._set_scanning_ui(True)
        self.scanStarted.emit()

        thread = ScanThread(roots, options, self)
        thread.phaseChanged.connect(self._on_scan_phase)
        thread.progressed.connect(self._on_scan_progress)
        thread.errorOccurred.connect(self._on_scan_error)
        thread.resultReady.connect(self._on_scan_finished)
        thread.crashed.connect(self._on_scan_crashed)
        self.scan_thread = thread
        thread.start()

    def stop_scan(self) -> None:
        if self.scan_thread is not None and self.scan_thread.isRunning():
            self.scan_thread.cancel_event.set()
            self.stop_button.setEnabled(False)
            self.scan_phase.setText("正在停止…")
            self._flash_status("正在取消扫描，请稍候…")

    def _set_scanning_ui(self, scanning: bool) -> None:
        self.start_button.setVisible(not scanning)
        self.start_button.setEnabled(not scanning and bool(self.roots))
        self.stop_button.setVisible(scanning)
        self.stop_button.setEnabled(scanning)
        self.add_button.setEnabled(not scanning)
        self.remove_button.setEnabled(not scanning and self.root_list.currentRow() >= 0)
        self.root_list.setEnabled(not scanning)
        self.settings_button.setEnabled(not scanning)
        self.empty_button.setEnabled(not scanning)
        self.policy_combo.setEnabled(not scanning)
        self.smart_button.setEnabled(not scanning and bool(self.result))
        self.export_button.setEnabled(not scanning and bool(self.result))
        self.progress_widget.setVisible(scanning)
        if scanning:
            self.progress_bar.setRange(0, 0)
            self.scan_phase.setText("正在枚举文件…")
            self.scan_detail.setText("")
            self.statusBar().showMessage("正在扫描…")
            self.delete_button.setEnabled(False)
        else:
            self.delete_button.setEnabled(bool(self.result) and bool(self.group_model.checked_paths()))
            self._update_delete_button()

    def _on_scan_phase(self, key: str, label: str, total_bytes: int, total_files: int) -> None:
        if not self._scanning:
            return
        self.scan_phase.setText(label)
        if total_bytes > 0:
            self.progress_bar.setRange(0, 1000)
            self.progress_bar.setValue(0)
            detail = f"{total_files:,} 个候选文件"
            if self._scan_errors:
                detail += f" · 已跳过 {self._scan_errors}"
            self.scan_detail.setText(detail)
        else:
            self.progress_bar.setRange(0, 0)
            self.scan_detail.setText("")

    def _on_scan_progress(
        self, done: int, total: int, files_done: int, files_total: int, speed: float
    ) -> None:
        if not self._scanning:
            return
        if total > 0:
            ratio = min(1.0, done / total)
            if self.progress_bar.maximum() != 1000:
                self.progress_bar.setRange(0, 1000)
            self.progress_bar.setValue(int(ratio * 1000))
            parts = [f"{human_size(done)} / {human_size(total)}"]
            if speed > 64 * 1024:
                parts.append(f"{human_size(speed)}/s")
                remaining = (total - done) / speed
                if remaining > 1:
                    parts.append(f"剩余约 {format_elapsed(remaining)}")
            if self._scan_errors:
                parts.append(f"跳过 {self._scan_errors}")
            self.scan_detail.setText(" · ".join(parts))
        else:
            if self.progress_bar.maximum() != 0:
                self.progress_bar.setRange(0, 0)
            text = f"已发现 {files_done:,} 个文件"
            if self._scan_errors:
                text += f" · 跳过 {self._scan_errors}"
            self.scan_detail.setText(text)

    def _on_scan_error(self, path: str, message: str) -> None:
        self._scan_errors += 1
        self.scan_phase.setToolTip(f"{path}\n{message}")

    def _on_scan_crashed(self, details: str) -> None:
        self._scanning = False
        self.scan_thread = None
        self._set_scanning_ui(False)
        self._show_message(
            "扫描失败",
            "扫描过程中发生意外错误，已停止。\n\n" + details.strip().splitlines()[-1],
            QMessageBox.Icon.Critical,
        )
        self.statusBar().showMessage("扫描失败", 6000)

    def _on_scan_finished(self, result: ScanResult) -> None:
        if self._closing:
            return
        self._scanning = False
        self.scan_thread = None
        self._set_scanning_ui(False)

        if result.cancelled:
            self.statusBar().showMessage("扫描已取消", 5000)
            self._populate_empty_after_cancel()
            return

        self.result = result
        self._update_stats()
        self._set_preview_actions_enabled(False)
        policy = self.settings.policy if self.settings.auto_select else None
        self.group_model.set_groups(result.groups, policy=policy)
        self.group_badge.setText(f"{len(result.groups)} 组")
        self.smart_button.setEnabled(bool(result.groups))
        self.export_button.setEnabled(True)

        stats = result.stats
        if result.groups:
            self.group_stack.setCurrentWidget(self.group_table)
            self.group_table.selectRow(0)
            if self.file_model.group_index != 0:
                self._load_group(0)
            self.group_table.setFocus()
            message = (
                f"扫描完成：{stats.files_seen:,} 个文件，{stats.groups} 组重复，"
                f"可释放 {human_size(stats.wasted)}，用时 {format_elapsed(stats.elapsed)}"
            )
            if result.errors:
                message += f"（{len(result.errors)} 个文件读取失败）"
            self.statusBar().showMessage(message, 15000)
        else:
            self._populate_empty_result(result)

        self._on_selection_changed()
        self._update_delete_button()
        self.scanCompleted.emit(result)

    def _populate_empty_after_cancel(self) -> None:
        self.group_stack.setCurrentWidget(self.group_empty)
        self.file_stack.setCurrentWidget(self.file_empty)
        self.group_badge.setText("0 组")
        self.statusBar().showMessage("扫描已取消", 5000)

    def _populate_empty_result(self, result: ScanResult) -> None:
        self.group_stack.setCurrentWidget(self.group_empty)
        self.file_stack.setCurrentWidget(self.file_empty)
        self.group_title.setText("组内文件")
        self.group_meta.setText("")
        self.group_badge.setText("0 组")
        self._clear_preview()
        stats = result.stats
        if stats.files_seen == 0:
            self.group_empty.set_text(
                "没有找到符合条件的文件",
                "检查文件类型、最小文件大小，或是否勾选了“包含隐藏文件”。",
            )
        elif stats.candidates == 0:
            self.group_empty.set_text(
                "没有大小相同的文件",
                f"共扫描 {stats.files_seen:,} 个文件，没有两个文件大小一致，因此不可能重复。",
            )
        else:
            self.group_empty.set_text(
                "没有发现重复文件",
                f"共扫描 {stats.files_seen:,} 个文件，全部内容都不相同。",
            )
        self.statusBar().showMessage(
            f"扫描完成：没有发现重复文件（{stats.files_seen:,} 个文件，用时 {format_elapsed(stats.elapsed)}）",
            12000,
        )

    def _update_stats(self) -> None:
        if self.result is None:
            for tile in (self.stat_files, self.stat_groups, self.stat_dupes, self.stat_wasted):
                tile.set_value("—")
            return
        stats = self.result.stats
        self.stat_files.set_value(f"{stats.files_seen:,}")
        self.stat_groups.set_value(str(stats.groups))
        self.stat_dupes.set_value(f"{stats.duplicate_files:,}")
        self.stat_wasted.set_value(human_size(stats.wasted))

    def _reset_result_ui(self) -> None:
        self.result = None
        self._current_entry = None
        self._preview_path = None
        self.group_model.set_groups([])
        self.file_model.bind_group(-1, None, [])
        self.group_badge.setText("0 组")
        self.group_title.setText("组内文件")
        self.group_meta.setText("")
        self.group_stack.setCurrentWidget(self.group_empty)
        self.group_empty.set_text(
            "还没有扫描结果",
            "添加要扫描的文件夹，然后点击“开始扫描”。\n程序只按内容比对，与文件名无关。",
        )
        self.file_stack.setCurrentWidget(self.file_empty)
        self.thumb.clear("选择文件以预览", "search")
        self.preview_name.setText("未选择文件")
        for _container, value in (self.meta_size, self.meta_time, self.meta_dims, self.meta_kind):
            value.setText("—")
        self.preview_path.set_full_text("—")
        self._set_preview_actions_enabled(False)
        self._update_stats()
        self.selection_summary.setText("尚未勾选要删除的文件")
        self.smart_button.setEnabled(False)
        self.export_button.setEnabled(False)
        self._update_delete_button()

    # ------------------------------------------------------------------ 结果浏览
    def _on_group_current_changed(self, current, _previous) -> None:
        self._load_group(current.row() if current.isValid() else -1)

    def _load_group(self, row: int) -> None:
        group = self.group_model.group_at(row)
        if group is None:
            self.file_model.bind_group(-1, None, [])
            self.file_stack.setCurrentWidget(self.file_empty)
            self.group_title.setText("组内文件")
            self.group_meta.setText("")
            self._clear_preview()
            return
        checks = self.group_model.checks_for(row)
        self.file_model.bind_group(row, group, checks)
        self.file_stack.setCurrentWidget(self.file_table)
        self.group_title.setText(f"第 {row + 1} 组")
        self.group_meta.setText(
            f"{group.count} 个文件 · 每个 {human_size(group.file_size)} · 可释放 {human_size(group.wasted)}"
        )
        if group.files:
            self.file_table.selectRow(0)
        self._on_selection_changed()

    def _on_file_current_changed(self, current, _previous) -> None:
        row = current.row() if current.isValid() else -1
        entry = self.file_model.entry_at(row)
        if entry is None:
            self._clear_preview()
        else:
            self._show_entry(entry)

    def _show_entry(self, entry: FileEntry) -> None:
        self._current_entry = entry
        self._preview_path = entry.path
        self.preview_name.setText(entry.name)
        self.preview_name.setToolTip(entry.path)
        self.meta_size[1].setText(human_size(entry.size))
        self.meta_time[1].setText(format_mtime(entry.mtime_ns))
        dims = self._dimensions_for(entry)
        self.meta_dims[1].setText(dims)
        self.meta_kind[1].setText(entry.kind)
        self.preview_path.set_full_text(entry.directory or entry.path)
        self._set_preview_actions_enabled(True)
        self.thumb.set_placeholder_text("正在生成预览…")
        self.thumb.set_image(None, "video" if entry.ext in VIDEO_EXTS else "image")
        self.preview_loader.request(entry)

    def _clear_preview(self) -> None:
        self._current_entry = None
        self._preview_path = None
        self.thumb.clear("选择文件以预览", "search")
        self.preview_name.setText("未选择文件")
        self.preview_name.setToolTip("")
        for _container, value in (self.meta_size, self.meta_time, self.meta_dims, self.meta_kind):
            value.setText("—")
        self.preview_path.set_full_text("—")
        self._set_preview_actions_enabled(False)

    def _set_preview_actions_enabled(self, enabled: bool) -> None:
        self.open_button.setEnabled(enabled)
        self.copy_button.setEnabled(enabled)
        self.keep_button.setEnabled(enabled)

    def _on_preview_started(self, path: str) -> None:
        if path == self._preview_path:
            self.thumb.set_placeholder_text("正在生成预览…")

    def _on_preview_loaded(self, path: str, image, note: str) -> None:
        if path != self._preview_path:
            return
        self.thumb.set_image(image)
        if self._current_entry is not None and note:
            self.meta_kind[1].setText(note)

    def _on_preview_failed(self, path: str, reason: str) -> None:
        if path != self._preview_path:
            return
        text = "无法预览该文件"
        if self._current_entry is not None and self._current_entry.ext in VIDEO_EXTS and not self.preview_loader.ffmpeg_available:
            text = "安装 FFmpeg 后可预览视频首帧"
        self.thumb.clear(text, "file")
        self.thumb.setToolTip(reason)

    def _dimensions_for(self, entry: FileEntry) -> str:
        key = (entry.path, entry.mtime_ns)
        cached = self._dims_cache.get(key)
        if cached is not None:
            return cached
        if entry.ext in IMAGE_EXTS:
            size = image_dimensions(entry.path)
            text = f"{size.width()} × {size.height()}" if size.isValid() else "—"
        elif entry.ext in VIDEO_EXTS:
            text = "视频"
        else:
            text = "—"
        self._dims_cache[key] = text
        return text

    def _open_current_location(self) -> None:
        if self._current_entry is None:
            return
        if not open_in_explorer(self._current_entry.path):
            self._flash_status("无法打开资源管理器")

    def _copy_current_path(self) -> None:
        if self._current_entry is None:
            return
        QGuiApplication.clipboard().setText(self._current_entry.path)
        self._flash_status("路径已复制到剪贴板")

    def _open_file_at(self, row: int) -> None:
        entry = self.file_model.entry_at(row)
        if entry is not None and not open_file(entry.path):
            self._flash_status("无法打开该文件")

    def _keep_current_file(self) -> None:
        row = self.file_table.currentIndex().row()
        if row < 0:
            return
        self.file_model.keep_only(row)
        self._flash_status("已保留这个文件，并勾选同组的其他副本")

    # ------------------------------------------------------------------ 勾选/策略
    def _on_selection_changed(self) -> None:
        count, size = self.group_model.checked_summary()
        if count:
            self.selection_summary.setText(f"将删除 {count:,} 个文件 · 预计释放 {human_size(size)}")
        else:
            self.selection_summary.setText("尚未勾选要删除的文件")
        self._update_delete_button()
        self.group_model.refresh_row(self.group_table.currentIndex().row())
        if count:
            self.delete_button.setEnabled(not self._scanning and not self._deleting)
        else:
            self.delete_button.setEnabled(False)

    def _update_delete_button(self) -> None:
        text = "移入回收站" if self.settings.delete_mode == "recycle" else "移动到隔离区"
        self.delete_button.setText(text)
        if self.settings.delete_mode == "recycle":
            self.delete_button.setToolTip("把勾选的文件移入 Windows 回收站（可还原）")
        else:
            self.delete_button.setToolTip("把勾选的文件移动到隔离文件夹，按时间归档")

    def _sync_policy_combo(self) -> None:
        self.policy_combo.blockSignals(True)
        index = self.policy_combo.findData(self.settings.policy)
        self.policy_combo.setCurrentIndex(max(0, index))
        self.policy_combo.blockSignals(False)

    def _on_policy_changed(self) -> None:
        self.settings.policy = str(self.policy_combo.currentData())
        self.qs.setValue("result/policy", self.settings.policy)
        if self.result is not None and self.group_model.groups:
            self.group_model.apply_policy(self.settings.policy)
            self._refresh_current_checks()
            self._flash_status(f"已按“{self.policy_combo.currentText()}”重新勾选")

    def _apply_policy_to_all(self) -> None:
        if self.result is None or not self.group_model.groups:
            return
        self.group_model.apply_policy(self.settings.policy)
        self._refresh_current_checks()
        self._flash_status(f"已按“{self.policy_combo.currentText()}”重新勾选全部重复组")

    def _refresh_current_checks(self) -> None:
        row = self.group_table.currentIndex().row()
        group = self.group_model.group_at(row)
        if group is not None:
            previous = max(0, self.file_table.currentIndex().row())
            checks = self.group_model.checks_for(row)
            self.file_model.bind_group(row, group, checks)
            if group.files:
                self.file_table.selectRow(min(previous, len(group.files) - 1))
        self._on_selection_changed()

    # ------------------------------------------------------------------ 删除
    def _delete_selected(self) -> None:
        if self.result is None or self._deleting or self._scanning:
            return
        paths = self.group_model.checked_paths()
        if not paths:
            return
        _count, size = self.group_model.checked_summary()
        dialog = ConfirmDeleteDialog(
            len(paths), size, paths, self.settings.delete_mode, self
        )
        if dialog.exec() != QDialog.DialogCode.Accepted or not dialog.confirmed:
            return

        mode = self.settings.delete_mode
        self._deleting = True
        self.delete_button.setEnabled(False)
        self.delete_thread = DeleteThread(paths, mode, self.settings.quarantine_dir, self)
        self.delete_thread.done.connect(self._on_delete_finished)
        self.delete_thread.start()
        action = "移入回收站" if mode == "recycle" else "移动到隔离文件夹"
        self.statusBar().showMessage(f"正在把 {len(paths):,} 个文件{action}…")

    def _on_delete_finished(self, report: DeleteReport) -> None:
        self._deleting = False
        self.delete_thread = None
        if self._closing:
            return
        removed_count = len(report.removed)
        if removed_count:
            self._apply_deletions(report.removed)
            if self.settings.delete_mode == "recycle":
                message = f"已把 {removed_count:,} 个文件移入回收站"
            else:
                message = f"已把 {removed_count:,} 个文件移动到 {report.folder}"
            self.statusBar().showMessage(message, 10000)
            self._flash_status(message)
        if report.failures:
            lines = [f"{os.path.basename(path)}：{reason}" for path, reason in report.failures[:6]]
            if len(report.failures) > 6:
                lines.append(f"…… 另外 {len(report.failures) - 6} 个")
            self._show_message(
                "部分文件未能处理",
                f"{len(report.failures)} 个文件没有删除成功：\n\n" + "\n".join(lines),
                QMessageBox.Icon.Warning,
            )
        self._on_selection_changed()

    def _apply_deletions(self, removed: list[str]) -> None:
        if self.result is None:
            return
        removed_keys = {norm_key(path) for path in removed}
        old_checks: dict[str, bool] = {}
        for group, checks in zip(self.group_model.groups, self.group_model.selection):
            for entry, checked in zip(group.files, checks):
                old_checks[norm_key(entry.path)] = checked

        new_groups: list[DuplicateGroup] = []
        preserved: list[list[bool]] = []
        for group in self.group_model.groups:
            remaining = [entry for entry in group.files if norm_key(entry.path) not in removed_keys]
            if len(remaining) < 2:
                continue
            checks = [old_checks.get(norm_key(entry.path), False) for entry in remaining]
            if all(checks):
                checks[0] = False
            new_groups.append(DuplicateGroup(group.digest, remaining))
            preserved.append(checks)

        stats = self.result.stats
        stats.groups = len(new_groups)
        stats.duplicate_files = sum(group.count for group in new_groups)
        stats.wasted = sum(group.wasted for group in new_groups)
        self.result.groups = new_groups

        current = max(0, self.group_table.currentIndex().row())
        self.group_model.set_groups(new_groups, preserved=preserved)
        self.group_badge.setText(f"{len(new_groups)} 组")
        self._update_stats()
        self.smart_button.setEnabled(bool(new_groups))
        self.export_button.setEnabled(True)

        if new_groups:
            row = min(current, len(new_groups) - 1)
            self.group_stack.setCurrentWidget(self.group_table)
            self.group_table.selectRow(row)
            if self.file_model.group_index != row:
                self._load_group(row)
        else:
            self.group_stack.setCurrentWidget(self.group_empty)
            self.file_stack.setCurrentWidget(self.file_empty)
            self.group_title.setText("组内文件")
            self.group_meta.setText("")
            self._clear_preview()
            self.group_empty.set_text(
                "重复文件已处理完",
                "当前已经没有剩余的可删除副本；可以添加文件夹开始新的扫描。",
            )
        self._on_selection_changed()
        self._update_delete_button()

    # ------------------------------------------------------------------ 导出
    def _export_report(self) -> None:
        if self.result is None:
            return
        default_name = f"SameFileChecker_报告_{datetime.now():%Y%m%d_%H%M}.csv"
        path, _filter = QFileDialog.getSaveFileName(
            self,
            "导出扫描报告",
            os.path.join(os.path.expanduser("~"), default_name),
            "CSV 表格 (*.csv);;JSON 数据 (*.json)",
        )
        if not path:
            return
        from ..report import write_csv, write_json

        selected = {norm_key(p) for p in self.group_model.checked_paths()}
        try:
            if path.lower().endswith(".json"):
                write_json(path, self.result, selected, self.settings.policy_label())
            else:
                write_csv(path, self.result, selected, self.settings.policy_label())
        except OSError as exc:
            self._show_message("导出失败", str(exc), QMessageBox.Icon.Critical)
            return
        self._flash_status(f"报告已导出：{path}")
        self.statusBar().showMessage(f"报告已导出：{path}", 8000)

    # ------------------------------------------------------------------ 设置
    def _open_empty_folders(self) -> None:
        if self._scanning or self._deleting:
            return
        dialog = EmptyFolderDialog(self.roots, self)
        dialog.exec()

    def _open_settings(self) -> None:
        dialog = SettingsDialog(self.settings, self)
        if dialog.exec() != QDialog.DialogCode.Accepted:
            return
        self.settings = dialog.result_settings()
        self._save_settings()
        self._sync_policy_combo()
        self._update_delete_button()
        self._flash_status("设置已保存，将在下一次扫描时生效")

    # ------------------------------------------------------------------ 通用
    def _show_message(self, title: str, text: str, icon_type=QMessageBox.Icon.Information) -> None:
        box = QMessageBox(self)
        box.setIcon(icon_type)
        box.setWindowTitle(title)
        box.setText(text)
        button = box.addButton("知道了", QMessageBox.ButtonRole.AcceptRole)
        box.setDefaultButton(button)
        box.exec()

    def showEvent(self, event) -> None:  # noqa: N802
        super().showEvent(event)
        if not self._titlebar_done:
            self._titlebar_done = True
            enable_dark_titlebar(self)

    def select_group(self, row: int) -> None:
        """供自动化/截图脚本选中某一组。"""
        if 0 <= row < len(self.group_model.groups):
            self.group_table.selectRow(row)
            if self.file_model.group_index != row:
                self._load_group(row)

    def _flash_status(self, text: str) -> None:
        self.statusBar().showMessage(text, 5000)

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

    def closeEvent(self, event) -> None:  # noqa: N802
        if self.delete_thread is not None and self.delete_thread.isRunning():
            self._show_message(
                "正在处理文件",
                "删除/隔离操作还没有完成。为安全起见，请等状态栏提示完成后再关闭窗口。",
                QMessageBox.Icon.Information,
            )
            event.ignore()
            return
        if self.scan_thread is not None and self.scan_thread.isRunning():
            self.scan_thread.cancel_event.set()
            if not self.scan_thread.wait(8000):
                self._show_message(
                    "正在停止扫描",
                    "扫描线程还没有退出，请稍候再关闭窗口。",
                    QMessageBox.Icon.Information,
                )
                event.ignore()
                return
        self._closing = True
        self.preview_loader.shutdown()
        self.qs.setValue("window/geometry", self.saveGeometry())
        self.qs.setValue("window/splitter", self.splitter.saveState())
        self._save_settings()
        self.qs.sync()
        super().closeEvent(event)
