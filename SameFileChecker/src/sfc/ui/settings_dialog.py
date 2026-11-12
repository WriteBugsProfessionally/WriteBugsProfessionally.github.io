"""扫描设置对话框。"""

from __future__ import annotations

import os
from dataclasses import replace

from PySide6.QtCore import Qt
from PySide6.QtWidgets import (
    QCheckBox,
    QComboBox,
    QDialog,
    QDialogButtonBox,
    QFileDialog,
    QFormLayout,
    QHBoxLayout,
    QLabel,
    QLineEdit,
    QMessageBox,
    QPushButton,
    QSpinBox,
    QVBoxLayout,
    QWidget,
)

from ..cache import HashCache
from ..config import (
    DELETE_MODES,
    EXT_PRESETS,
    KEEP_POLICIES,
    AppSettings,
    default_quarantine_dir,
)
from ..util import human_size
from .theme import enable_dark_titlebar
from .widgets import Divider


class SettingsDialog(QDialog):
    def __init__(self, settings: AppSettings, parent: QWidget | None = None) -> None:
        super().__init__(parent)
        self.setWindowTitle("扫描设置")
        self.setModal(True)
        self.setMinimumWidth(600)
        self._settings = settings
        self._cache_info = self._read_cache_info()
        self._titlebar_done = False

        root = QVBoxLayout(self)
        root.setContentsMargins(24, 20, 24, 18)
        root.setSpacing(14)

        title = QLabel("扫描设置")
        title.setObjectName("dialogTitle")
        subtitle = QLabel("修改会在下一次扫描时生效")
        subtitle.setObjectName("hint")
        root.addWidget(title)
        root.addWidget(subtitle)

        # ---------- 扫描规则 ----------
        root.addWidget(self._section("扫描规则"))
        form = QFormLayout()
        form.setHorizontalSpacing(18)
        form.setVerticalSpacing(10)
        form.setLabelAlignment(Qt.AlignmentFlag.AlignRight | Qt.AlignmentFlag.AlignVCenter)
        form.setFieldGrowthPolicy(QFormLayout.FieldGrowthPolicy.AllNonFixedFieldsGrow)

        self.min_size = QSpinBox()
        self.min_size.setRange(0, 1024 * 1024)
        self.min_size.setSuffix(" KB")
        self.min_size.setValue(max(0, settings.min_size_kb))
        self.min_size.setFixedWidth(140)
        form.addRow("最小文件大小", self.min_size)

        type_row = QWidget()
        type_layout = QVBoxLayout(type_row)
        type_layout.setContentsMargins(0, 0, 0, 0)
        type_layout.setSpacing(6)
        self.preset = QComboBox()
        for key, (label, _exts) in EXT_PRESETS.items():
            self.preset.addItem(label, key)
        index = self.preset.findData(settings.ext_preset)
        self.preset.setCurrentIndex(max(0, index))
        self.custom_exts = QLineEdit(settings.custom_exts)
        self.custom_exts.setPlaceholderText("例如：jpg,png,heic,mp4")
        self.custom_exts.setEnabled(settings.ext_preset == "custom")
        self.custom_exts.setCursorPosition(0)
        self.preset.currentIndexChanged.connect(self._on_preset_changed)
        type_layout.addWidget(self.preset)
        type_layout.addWidget(self.custom_exts)
        form.addRow("文件类型", type_row)

        workers_row = QWidget()
        workers_layout = QHBoxLayout(workers_row)
        workers_layout.setContentsMargins(0, 0, 0, 0)
        workers_layout.setSpacing(10)
        self.workers = QSpinBox()
        self.workers.setRange(1, 16)
        self.workers.setValue(max(1, min(16, settings.workers)))
        self.workers.setFixedWidth(90)
        worker_hint = QLabel("机械硬盘建议 2–4，固态硬盘可更高")
        worker_hint.setObjectName("hint")
        workers_layout.addWidget(self.workers)
        workers_layout.addWidget(worker_hint, 1)
        form.addRow("并行读取线程", workers_row)

        self.include_hidden = QCheckBox("包含隐藏 / 系统属性的文件")
        self.include_hidden.setChecked(settings.include_hidden)
        form.addRow("", self.include_hidden)

        self.exclude_dirs = QLineEdit(settings.exclude_dirs)
        self.exclude_dirs.setPlaceholderText("文件夹名，用分号分隔；例如 Windows; AppData; node_modules")
        self.exclude_dirs.setCursorPosition(0)
        form.addRow("跳过文件夹", self.exclude_dirs)

        root.addLayout(form)

        # ---------- 结果处理 ----------
        root.addWidget(Divider())
        root.addWidget(self._section("结果处理"))
        form2 = QFormLayout()
        form2.setHorizontalSpacing(18)
        form2.setVerticalSpacing(10)
        form2.setLabelAlignment(Qt.AlignmentFlag.AlignRight | Qt.AlignmentFlag.AlignVCenter)
        form2.setFieldGrowthPolicy(QFormLayout.FieldGrowthPolicy.AllNonFixedFieldsGrow)

        self.policy = QComboBox()
        for key, label in KEEP_POLICIES:
            self.policy.addItem(label, key)
        self.policy.setCurrentIndex(max(0, self.policy.findData(settings.policy)))
        form2.addRow("默认保留策略", self.policy)

        self.auto_select = QCheckBox("扫描完成后自动勾选要删除的副本")
        self.auto_select.setChecked(settings.auto_select)
        form2.addRow("", self.auto_select)

        delete_row = QWidget()
        delete_layout = QVBoxLayout(delete_row)
        delete_layout.setContentsMargins(0, 0, 0, 0)
        delete_layout.setSpacing(6)
        self.delete_mode = QComboBox()
        for key, label in DELETE_MODES:
            self.delete_mode.addItem(label, key)
        self.delete_mode.setCurrentIndex(max(0, self.delete_mode.findData(settings.delete_mode)))
        self.delete_mode.currentIndexChanged.connect(self._on_delete_mode_changed)
        self.quarantine_dir = QLineEdit(settings.quarantine_dir)
        self.quarantine_dir.setCursorPosition(0)
        self.quarantine_dir.setEnabled(settings.delete_mode == "quarantine")
        browse = QPushButton("选择…")
        browse.setObjectName("ghost")
        browse.clicked.connect(self._browse_quarantine)
        quarantine_row = QWidget()
        q_layout = QHBoxLayout(quarantine_row)
        q_layout.setContentsMargins(0, 0, 0, 0)
        q_layout.setSpacing(8)
        q_layout.addWidget(self.quarantine_dir, 1)
        q_layout.addWidget(browse)
        self._browse_button = browse
        delete_layout.addWidget(self.delete_mode)
        delete_layout.addWidget(quarantine_row)
        form2.addRow("删除方式", delete_row)
        root.addLayout(form2)

        # ---------- 缓存 ----------
        root.addWidget(Divider())
        cache_row = QWidget()
        cache_layout = QHBoxLayout(cache_row)
        cache_layout.setContentsMargins(0, 0, 0, 0)
        cache_layout.setSpacing(10)
        self.use_cache = QCheckBox("使用哈希缓存（重复扫描时跳过未修改的文件）")
        self.use_cache.setChecked(settings.use_cache)
        self.cache_label = QLabel(self._cache_info)
        self.cache_label.setObjectName("hint")
        clear_button = QPushButton("清除缓存")
        clear_button.setObjectName("subtle")
        clear_button.clicked.connect(self._clear_cache)
        cache_layout.addWidget(self.use_cache)
        cache_layout.addWidget(self.cache_label, 1)
        cache_layout.addWidget(clear_button)
        root.addWidget(cache_row)

        root.addStretch(1)
        buttons = QDialogButtonBox()
        reset = QPushButton("恢复默认")
        reset.setObjectName("subtle")
        reset.clicked.connect(self._reset_defaults)
        cancel = buttons.addButton("取消", QDialogButtonBox.ButtonRole.RejectRole)
        save = buttons.addButton("保存设置", QDialogButtonBox.ButtonRole.AcceptRole)
        cancel.setObjectName("ghost")
        save.setObjectName("primary")
        buttons.rejected.connect(self.reject)
        buttons.accepted.connect(self._try_accept)

        footer = QHBoxLayout()
        footer.addWidget(reset)
        footer.addStretch(1)
        footer.addWidget(buttons)
        root.addLayout(footer)

        # 避免打开对话框时数字输入框的光标盖住第一个数字
        self.policy.setFocus()

    # ---------- 辅助 ----------
    def showEvent(self, event) -> None:  # noqa: N802
        super().showEvent(event)
        if not self._titlebar_done:
            self._titlebar_done = True
            enable_dark_titlebar(self)

    @staticmethod
    def _section(text: str) -> QLabel:
        label = QLabel(text)
        label.setObjectName("sectionTitle")
        return label

    def _on_preset_changed(self) -> None:
        self.custom_exts.setEnabled(self.preset.currentData() == "custom")

    def _on_delete_mode_changed(self) -> None:
        enabled = self.delete_mode.currentData() == "quarantine"
        self.quarantine_dir.setEnabled(enabled)
        self._browse_button.setEnabled(enabled)

    def _browse_quarantine(self) -> None:
        folder = QFileDialog.getExistingDirectory(self, "选择隔离文件夹", self.quarantine_dir.text())
        if folder:
            self.quarantine_dir.setText(os.path.normpath(folder))

    def _read_cache_info(self) -> str:
        cache = HashCache()
        try:
            path = cache.path
            count = cache.count()
            if not cache.ok:
                return "缓存不可用"
            size = os.path.getsize(path) if os.path.exists(path) else 0
            return f"已有 {count:,} 条记录 · {human_size(size)}"
        finally:
            cache.close()

    def _clear_cache(self) -> None:
        cache = HashCache()
        try:
            cache.clear()
            cache.close()
        finally:
            pass
        self.cache_label.setText(self._read_cache_info())

    def _reset_defaults(self) -> None:
        defaults = AppSettings()
        self.min_size.setValue(defaults.min_size_kb)
        self.preset.setCurrentIndex(max(0, self.preset.findData(defaults.ext_preset)))
        self.custom_exts.setText(defaults.custom_exts)
        self.workers.setValue(defaults.workers)
        self.include_hidden.setChecked(defaults.include_hidden)
        self.exclude_dirs.setText(defaults.exclude_dirs)
        self.policy.setCurrentIndex(max(0, self.policy.findData(defaults.policy)))
        self.auto_select.setChecked(defaults.auto_select)
        self.delete_mode.setCurrentIndex(max(0, self.delete_mode.findData(defaults.delete_mode)))
        self.quarantine_dir.setText(defaults.quarantine_dir or default_quarantine_dir())
        self.use_cache.setChecked(defaults.use_cache)

    def _try_accept(self) -> None:
        if self.preset.currentData() == "custom":
            text = self.custom_exts.text().strip()
            if not text:
                QMessageBox.warning(self, "设置不完整", "请填写至少一个扩展名，或选择其他文件类型。")
                return
        if self.delete_mode.currentData() == "quarantine":
            folder = self.quarantine_dir.text().strip()
            if not folder:
                QMessageBox.warning(self, "设置不完整", "请选择隔离文件夹。")
                return
            try:
                os.makedirs(folder, exist_ok=True)
            except OSError as exc:
                QMessageBox.warning(self, "无法创建隔离文件夹", str(exc))
                return
        self.accept()

    def result_settings(self) -> AppSettings:
        return replace(
            self._settings,
            min_size_kb=int(self.min_size.value()),
            ext_preset=str(self.preset.currentData()),
            custom_exts=self.custom_exts.text().strip(),
            workers=int(self.workers.value()),
            include_hidden=self.include_hidden.isChecked(),
            exclude_dirs=self.exclude_dirs.text().strip(),
            policy=str(self.policy.currentData()),
            auto_select=self.auto_select.isChecked(),
            delete_mode=str(self.delete_mode.currentData()),
            quarantine_dir=self.quarantine_dir.text().strip() or default_quarantine_dir(),
            use_cache=self.use_cache.isChecked(),
        )
