"""VideoChanger 图形界面（PySide6）。

三个功能页：
1. TS 合并：把连续的 .ts 分片无损拼接成 MP4
2. B站下载：调用 yt-dlp 下载并合并为 MP4
3. B站缓存转 MP4：把客户端/APP 缓存的 video.m4s + audio.m4s 封装成 MP4

所有耗时的外部进程都放在 QThread 中执行，界面不会冻结。
"""

from __future__ import annotations

import json
import os
import re
import shutil
import subprocess
import sys
import tempfile
import time
from pathlib import Path

from PySide6.QtCore import QFileInfo, QSettings, QSize, Qt, QThread, QTimer, QUrl, Signal
from PySide6.QtGui import QAction, QColor, QDesktopServices, QFont, QIcon, QPainter, QPen
from PySide6.QtWidgets import (
    QAbstractItemView,
    QApplication,
    QCheckBox,
    QFileDialog,
    QFileIconProvider,
    QFormLayout,
    QGroupBox,
    QHBoxLayout,
    QLabel,
    QLineEdit,
    QListWidget,
    QListWidgetItem,
    QMainWindow,
    QMenu,
    QMessageBox,
    QPlainTextEdit,
    QProgressBar,
    QPushButton,
    QSplitter,
    QTabWidget,
    QToolButton,
    QVBoxLayout,
    QWidget,
)

import core


def app_icon_path() -> Path:
    """应用图标：打包后在 _MEIPASS/assets，开发时在项目根目录 assets/。"""
    base = Path(getattr(sys, "_MEIPASS", Path(__file__).resolve().parent.parent))
    return base / "assets" / "app.ico"


# ---------------------------------------------------------------------------
# 后台进程
# ---------------------------------------------------------------------------
class ProcessWorker(QThread):
    """顺序执行一条或多条外部命令，并流式输出日志/进度。"""

    log = Signal(str)
    progress = Signal(int)          # 0-100
    step = Signal(int, int, str)    # 当前序号, 总数, 描述
    done = Signal(bool, str)        # 成功与否, 提示信息

    def __init__(self, commands, labels=None, progress_re=None,
                 success_message="完成", parent=None):
        super().__init__(parent)
        self.commands = [list(cmd) for cmd in commands]
        self.labels = list(labels or [""] * len(self.commands))
        self.progress_re = re.compile(progress_re) if progress_re else None
        self.success_message = success_message
        self._proc: subprocess.Popen | None = None
        self._cancelled = False

    # -- 外部调用 ---------------------------------------------------------
    def cancel(self):
        self._cancelled = True
        proc = self._proc
        if proc is not None and proc.poll() is None:
            self.log.emit("正在取消，请稍候…")
            self._kill_tree(proc.pid)

    @staticmethod
    def _kill_tree(pid: int):
        try:
            if os.name == "nt":
                subprocess.run(
                    ["taskkill", "/F", "/T", "/PID", str(pid)],
                    stdout=subprocess.DEVNULL,
                    stderr=subprocess.DEVNULL,
                    creationflags=core.CREATE_NO_WINDOW,
                )
            else:
                os.kill(pid, 9)
        except Exception:
            pass

    @staticmethod
    def _display_cmd(cmd) -> str:
        """打印命令时隐藏 SESSDATA。"""
        safe = []
        hide_next = False
        for arg in cmd:
            if hide_next:
                safe.append("Cookie:SESSDATA=***" if "SESSDATA=" in arg else "***")
                hide_next = False
                continue
            if arg == "--add-header":
                hide_next = True
            if "SESSDATA=" in arg:
                arg = re.sub(r"SESSDATA=[^;]*", "SESSDATA=***", arg)
            safe.append(arg)
        return subprocess.list2cmdline(safe)

    # -- QThread ----------------------------------------------------------
    def run(self):
        total = len(self.commands)
        for index, cmd in enumerate(self.commands):
            if self._cancelled:
                break
            label = self.labels[index] if index < len(self.labels) else ""
            if total > 1:
                self.step.emit(index + 1, total, label)
                self.log.emit(f"===== [{index + 1}/{total}] {label} =====")
            ok, message = self._run_one(cmd, index, total)
            if not ok:
                self.done.emit(False, message)
                return

        if self._cancelled:
            self.done.emit(False, "已取消")
        else:
            self.progress.emit(100)
            self.done.emit(True, self.success_message)

    def _run_one(self, cmd, index: int, total: int) -> tuple[bool, str]:
        self.log.emit("$ " + self._display_cmd(cmd))
        try:
            self._proc = subprocess.Popen(
                cmd,
                stdin=subprocess.DEVNULL,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                text=True,
                encoding="utf-8",
                errors="replace",
                bufsize=1,
                creationflags=core.CREATE_NO_WINDOW,
            )
        except FileNotFoundError:
            return False, f"找不到可执行文件：{cmd[0]}"
        except OSError as exc:
            return False, f"启动进程失败：{exc}"

        last_logged = -1
        try:
            assert self._proc.stdout is not None
            for raw in self._proc.stdout:
                if self._cancelled:
                    break
                line = raw.rstrip("\r\n")
                if not line:
                    continue

                if self.progress_re is not None:
                    match = self.progress_re.search(line)
                    if match:
                        percent = float(match.group(1))
                        overall = int((index + percent / 100.0) * 100 / total)
                        self.progress.emit(max(0, min(100, overall)))
                        bucket = int(percent) // 5
                        if bucket != last_logged:
                            last_logged = bucket
                            self.log.emit(line)
                        continue

                self.log.emit(line)
        finally:
            code = self._proc.wait()
            self._proc = None

        if self._cancelled:
            return False, "已取消"
        if code != 0:
            return False, self._describe_failure(code)
        return True, "完成"

    @staticmethod
    def _describe_failure(code: int) -> str:
        """把 ffmpeg 的负 errno 转成能看懂的中文提示。"""
        signed = code - 0x100000000 if code > 0x7FFFFFFF else code
        hints = {
            -1094995529: "输入数据无法识别。加密的 HLS 分片必须走同目录的 .m3u8，"
                         "不能逐个当 .ts 合并",
            -2: "找不到输入文件",
            -13: "没有读取权限",
        }
        hint = hints.get(signed)
        if hint:
            return f"命令执行失败（退出码 {code}）：{hint}"
        return f"命令执行失败（退出码 {code}），详情见日志。"


# ---------------------------------------------------------------------------
# 可拖拽列表
# ---------------------------------------------------------------------------
class DropListWidget(QListWidget):
    """可拖入文件的列表：空列表画成虚线投放区，拖动经过时高亮，条目带系统文件图标。"""

    paths_dropped = Signal(list)

    def __init__(self, parent=None):
        super().__init__(parent)
        self.setAcceptDrops(True)
        self.setSelectionMode(QAbstractItemView.SelectionMode.ExtendedSelection)
        self.setAlternatingRowColors(True)
        self.setIconSize(QSize(18, 18))
        self.setUniformItemSizes(True)
        self._placeholder = "把文件拖到这里"
        self._drag_active = False
        self._icon_provider = QFileIconProvider()
        self._icon_cache: dict[tuple[bool, str], QIcon] = {}

    # -- 空状态 / 拖动高亮 -------------------------------------------------
    def set_placeholder(self, text: str):
        self._placeholder = text
        self.viewport().update()

    def _set_drag_active(self, active: bool):
        if self._drag_active != active:
            self._drag_active = active
            self.viewport().update()

    def paintEvent(self, event):
        super().paintEvent(event)
        if self.count() and not self._drag_active:
            return
        painter = QPainter(self.viewport())
        painter.setRenderHint(QPainter.RenderHint.Antialiasing)
        rect = self.viewport().rect().adjusted(4, 4, -5, -5)
        if self._drag_active:
            painter.setPen(QPen(QColor("#2f6feb"), 2, Qt.PenStyle.DashLine))
            painter.setBrush(QColor(47, 111, 235, 22))
        else:
            painter.setPen(QPen(QColor("#c5c9cd"), 2, Qt.PenStyle.DashLine))
            painter.setBrush(QColor("#fafbfc"))
        painter.drawRoundedRect(rect, 8, 8)
        if not self.count():
            painter.setPen(QColor("#8b9298"))
            painter.drawText(rect, Qt.AlignmentFlag.AlignCenter, self._placeholder)
        painter.end()

    # -- 条目图标 -----------------------------------------------------------
    def icon_for(self, path, is_dir: bool | None = None) -> QIcon:
        """按「文件夹 / 扩展名」缓存系统图标，避免上千个分片时逐个查 shell。"""
        file_path = Path(path)
        if is_dir is None:
            is_dir = file_path.is_dir()
        key = (bool(is_dir), "" if is_dir else file_path.suffix.lower())
        cached = self._icon_cache.get(key)
        if cached is None:
            if is_dir:
                cached = self._icon_provider.icon(QFileIconProvider.IconType.Folder)
            else:
                cached = self._icon_provider.icon(QFileInfo(str(file_path)))
            self._icon_cache[key] = cached
        return cached

    # -- 拖放 ---------------------------------------------------------------
    def _accept_urls(self, event) -> bool:
        if event.mimeData().hasUrls():
            event.acceptProposedAction()
            return True
        return False

    def dragEnterEvent(self, event):
        if self._accept_urls(event):
            self._set_drag_active(True)
        else:
            super().dragEnterEvent(event)

    def dragMoveEvent(self, event):
        if not self._accept_urls(event):
            super().dragMoveEvent(event)

    def dragLeaveEvent(self, event):
        self._set_drag_active(False)
        super().dragLeaveEvent(event)

    def dropEvent(self, event):
        self._set_drag_active(False)
        paths = [url.toLocalFile() for url in event.mimeData().urls() if url.toLocalFile()]
        if paths:
            event.acceptProposedAction()
            self.paths_dropped.emit(paths)
        else:
            super().dropEvent(event)


# ---------------------------------------------------------------------------
# 页签基类
# ---------------------------------------------------------------------------
class BaseTab(QWidget):
    log = Signal(str)

    def __init__(self):
        super().__init__()
        self.settings = QSettings(core.APP_NAME, core.APP_NAME)
        self.worker: ProcessWorker | None = None
        self._temp_dirs: list[Path] = []

        self.progress_bar = QProgressBar()
        self.progress_bar.setRange(0, 100)
        self.progress_bar.setValue(0)
        self.status_label = QLabel("就绪")
        self.status_label.setWordWrap(True)
        self.cancel_btn = QPushButton("取消")
        self.cancel_btn.setEnabled(False)
        self.cancel_btn.clicked.connect(self._cancel)

    def bottom_bar(self) -> QVBoxLayout:
        box = QVBoxLayout()
        box.addWidget(self.status_label)
        row = QHBoxLayout()
        row.addWidget(self.progress_bar, 1)
        row.addWidget(self.cancel_btn)
        box.addLayout(row)
        return box

    # -- 运行控制 ---------------------------------------------------------
    def start_worker(self, worker: ProcessWorker, indeterminate: bool = True):
        if self.worker is not None:
            return
        self.worker = worker
        worker.log.connect(self.log)
        worker.progress.connect(self._on_progress)
        worker.step.connect(self._on_step)
        worker.done.connect(self._on_done)
        worker.finished.connect(self._on_worker_finished)

        if indeterminate:
            self.progress_bar.setRange(0, 0)
        else:
            self.progress_bar.setRange(0, 100)
            self.progress_bar.setValue(0)

        self.cancel_btn.setEnabled(True)
        self.set_controls_enabled(False)
        self.set_status("处理中…")
        worker.start()

    def _cancel(self):
        if self.worker is not None:
            self.worker.cancel()

    def _on_progress(self, percent: int):
        if self.progress_bar.maximum() == 0:
            self.progress_bar.setRange(0, 100)
        self.progress_bar.setValue(max(0, min(100, percent)))

    def _on_step(self, index: int, total: int, label: str):
        if total > 1:
            self.set_status(f"处理中…（{index}/{total}）{label}")

    def _on_done(self, ok: bool, message: str):
        self.progress_bar.setRange(0, 100)
        self.progress_bar.setValue(100 if ok else 0)
        self.cancel_btn.setEnabled(False)
        self.set_controls_enabled(True)
        self.set_status(message, ok)
        self.cleanup_temp_dirs()
        self.on_finished(ok, message)

    def _on_worker_finished(self):
        worker = self.worker
        self.worker = None
        if worker is not None:
            worker.deleteLater()

    def cleanup_temp_dirs(self):
        for directory in self._temp_dirs:
            shutil.rmtree(directory, ignore_errors=True)
        self._temp_dirs.clear()

    # -- 可覆写 -----------------------------------------------------------
    def set_controls_enabled(self, enabled: bool):
        pass

    def on_finished(self, ok: bool, message: str):
        pass

    def set_status(self, text: str, ok: bool | None = None):
        color = "" if ok is None else ("color:#0a7d32;" if ok else "color:#c62828;")
        self.status_label.setStyleSheet(color)
        self.status_label.setText(text)

    # -- 默认保存位置（三个页签共用同一套交互）----------------------------
    def build_dir_row(
        self,
        key: str,
        default: str | Path,
        label: str = "",
        placeholder: str = "",
    ) -> tuple[QHBoxLayout, QLineEdit, list[QWidget]]:
        """生成「路径框 + 浏览… + 恢复默认」一行。

        返回 (布局, 输入框, 需要在任务运行期间禁用的控件)。
        key 是 QSettings 键，改完立即持久化，下次启动仍是改过的位置。
        """
        default_text = str(default)
        edit = QLineEdit(self.settings.value(key, default_text, type=str))
        edit.setPlaceholderText(placeholder or default_text or "留空则跟随源文件所在目录")
        edit.setToolTip("默认保存位置；留空表示跟随源文件所在目录")
        edit.editingFinished.connect(lambda: self._save_dir_setting(key, edit))

        browse_btn = QPushButton("浏览…")
        browse_btn.clicked.connect(lambda: self._choose_save_dir(edit, key, default_text))
        reset_btn = QPushButton("恢复默认")
        reset_btn.setToolTip(f"恢复为：{default_text or '跟随源文件所在目录'}")
        reset_btn.clicked.connect(lambda: self._reset_save_dir(edit, key, default_text))

        row = QHBoxLayout()
        if label:
            row.addWidget(QLabel(label))
        row.addWidget(edit, 1)
        row.addWidget(browse_btn)
        row.addWidget(reset_btn)
        return row, edit, [edit, browse_btn, reset_btn]

    def _save_dir_setting(self, key: str, edit: QLineEdit):
        self.settings.setValue(key, edit.text().strip())

    def _choose_save_dir(self, edit: QLineEdit, key: str, default_text: str):
        start = edit.text().strip() or default_text or str(Path.home())
        directory = QFileDialog.getExistingDirectory(self, "选择默认保存位置", start)
        if directory:
            edit.setText(directory)
            self.settings.setValue(key, directory)

    def _reset_save_dir(self, edit: QLineEdit, key: str, default_text: str):
        edit.setText(default_text)
        self.settings.setValue(key, default_text)

    @staticmethod
    def resolve_dir(text: str, fallback: Path) -> Path:
        """输入框内容 → 路径：空白回退到 fallback，支持 ~ 展开。"""
        text = (text or "").strip()
        return Path(text).expanduser() if text else Path(fallback)


# ---------------------------------------------------------------------------
# TS 合并
# ---------------------------------------------------------------------------
class TSMergeTab(BaseTab):
    def __init__(self):
        super().__init__()

        layout = QVBoxLayout(self)
        layout.setContentsMargins(12, 12, 12, 12)

        hint = QLabel(
            "按列表顺序拼接 .ts 文件；支持拖入文件或整个文件夹。可用“按文件名排序”整理分集顺序。\n"
            "也支持 m3u8 播放列表（含 QQ浏览器等 AES-128 加密的 HLS 缓存）：把整个缓存文件夹拖进来即可。\n"
            "「开始合并」会直接输出到上面的默认保存位置；要临时换目录或改名，点右侧箭头选「另存为…」。"
        )
        hint.setWordWrap(True)
        layout.addWidget(hint)

        self.file_list = DropListWidget()
        self.file_list.paths_dropped.connect(self._add_paths)
        self.file_list.set_placeholder("把 .ts / .m3u8 文件\n或整个文件夹拖到这里")
        self.file_list.setToolTip("将 .ts 文件、.m3u8 播放列表，或包含它们的文件夹拖到这里")
        layout.addWidget(self.file_list, 1)

        row = QHBoxLayout()
        self.add_btn = QPushButton("添加文件…")
        self.add_btn.clicked.connect(self._choose_files)
        self.up_btn = QPushButton("上移")
        self.up_btn.clicked.connect(lambda: self._move_selected(-1))
        self.down_btn = QPushButton("下移")
        self.down_btn.clicked.connect(lambda: self._move_selected(1))
        self.sort_btn = QPushButton("按文件名排序")
        self.sort_btn.clicked.connect(self._sort_items)
        self.remove_btn = QPushButton("移除选中")
        self.remove_btn.clicked.connect(self._remove_selected)
        self.clear_btn = QPushButton("清空")
        self.clear_btn.clicked.connect(self.file_list.clear)
        for button in (self.add_btn, self.up_btn, self.down_btn,
                       self.sort_btn, self.remove_btn, self.clear_btn):
            row.addWidget(button)
        row.addStretch(1)
        layout.addLayout(row)

        save_row, self.save_dir_edit, save_widgets = self.build_dir_row(
            "ts/output_dir",
            "",
            label="默认保存位置：",
            placeholder="留空 = 保存到第一个文件所在目录",
        )
        layout.addLayout(save_row)

        merge_row = QHBoxLayout()
        merge_row.setSpacing(0)
        self.merge_btn = QPushButton("开始合并")
        self.merge_btn.setMinimumHeight(34)
        self.merge_btn.setToolTip("按「默认保存位置」直接合并，不再询问")
        self.merge_btn.clicked.connect(lambda: self._start_merge(ask_path=False))
        merge_row.addWidget(self.merge_btn, 1)

        self.merge_menu_btn = QToolButton()
        self.merge_menu_btn.setMinimumHeight(34)
        self.merge_menu_btn.setFixedWidth(30)
        self.merge_menu_btn.setArrowType(Qt.ArrowType.DownArrow)
        self.merge_menu_btn.setPopupMode(QToolButton.ToolButtonPopupMode.InstantPopup)
        self.merge_menu_btn.setToolTip("更多：另存为…")
        merge_menu = QMenu(self)
        save_as_action = merge_menu.addAction("另存为…")
        save_as_action.triggered.connect(lambda: self._start_merge(ask_path=True))
        merge_menu.addSeparator()
        open_dir_action = merge_menu.addAction("打开默认保存位置")
        open_dir_action.triggered.connect(self._open_save_dir)
        self.merge_menu_btn.setMenu(merge_menu)
        merge_row.addWidget(self.merge_menu_btn)
        layout.addLayout(merge_row)
        layout.addLayout(self.bottom_bar())

        self._controls = [self.add_btn, self.up_btn, self.down_btn, self.sort_btn,
                          self.remove_btn, self.clear_btn, self.merge_btn,
                          self.merge_menu_btn, *save_widgets, self.file_list]

    def set_controls_enabled(self, enabled: bool):
        for widget in self._controls:
            widget.setEnabled(enabled)

    # -- 列表管理 ---------------------------------------------------------
    def _add_paths(self, paths):
        existing = {
            self.file_list.item(i).data(Qt.ItemDataRole.UserRole)
            for i in range(self.file_list.count())
        }
        added = 0
        for raw in paths:
            path = Path(raw)
            # (实际用于合并的文件, 列表里显示的文字, 是否按文件夹显示)
            entries: list[tuple[Path, str, bool]] = []
            if path.is_dir():
                playlist_dirs = core.find_playlist_dirs(path)
                if playlist_dirs:
                    # 一个视频一个文件夹：拖上层父目录时自动认出下面每个视频，
                    # 列表里显示「文件夹」本身，而不是上千个加密分片
                    entries = [
                        (playlist, folder.name, True)
                        for folder in playlist_dirs
                        if (playlist := core.find_playlist(folder)) is not None
                    ]
                else:
                    entries = [
                        (file, file.name, False)
                        for file in sorted(
                            (f for f in path.rglob("*") if f.is_file() and f.suffix.lower() == ".ts"),
                            key=lambda f: core.natural_key(str(f)),
                        )
                    ]
            elif path.is_file() and (path.suffix.lower() == ".ts" or core.is_playlist(path)):
                entries = [(path, path.name, False)]
            else:
                continue

            for file, display, is_folder in entries:
                key = str(file.resolve())
                if key in existing:
                    continue
                existing.add(key)
                item = QListWidgetItem(display)
                item.setData(Qt.ItemDataRole.UserRole, key)
                if is_folder:
                    item.setToolTip(f"{file.parent}\n（合并时使用 {file.name}）")
                    item.setIcon(self.file_list.icon_for(file.parent, is_dir=True))
                else:
                    item.setToolTip(key)
                    item.setIcon(self.file_list.icon_for(file))
                self.file_list.addItem(item)
                added += 1

        if added:
            self.set_status(f"已添加 {added} 项（共 {self.file_list.count()} 项）")

    def _choose_files(self):
        start = self.settings.value("ts/last_dir", str(Path.home()), type=str)
        files, _ = QFileDialog.getOpenFileNames(
            self, "选择分片或播放列表", start,
            "视频分片 (*.ts);;HLS 播放列表 (*.m3u8);;所有文件 (*)",
        )
        if files:
            self.settings.setValue("ts/last_dir", str(Path(files[0]).parent))
            self._add_paths(files)

    def _move_selected(self, offset: int):
        rows = sorted((self.file_list.row(item) for item in self.file_list.selectedItems()))
        if not rows:
            return
        if offset < 0:
            for row in rows:
                if row > 0 and row - 1 not in rows:
                    item = self.file_list.takeItem(row)
                    self.file_list.insertItem(row - 1, item)
                    item.setSelected(True)
        else:
            for row in reversed(rows):
                if row < self.file_list.count() - 1 and row + 1 not in rows:
                    item = self.file_list.takeItem(row)
                    self.file_list.insertItem(row + 1, item)
                    item.setSelected(True)

    def _sort_items(self):
        items = [self.file_list.takeItem(0) for _ in range(self.file_list.count())]
        items.sort(key=lambda item: core.natural_key(item.text()))
        for item in items:
            self.file_list.addItem(item)

    def _remove_selected(self):
        for item in self.file_list.selectedItems():
            self.file_list.takeItem(self.file_list.row(item))

    # -- 开始合并 ---------------------------------------------------------
    # ask_path=True：先弹「另存为…」对话框（临时换目录/改名）
    # ask_path=False：直接按默认保存位置输出，同名自动加 (2)、(3)
    def _start_merge(self, ask_path: bool = False):
        files = [
            self.file_list.item(i).data(Qt.ItemDataRole.UserRole)
            for i in range(self.file_list.count())
        ]
        if not files:
            QMessageBox.information(self, "提示", "请先添加至少一个 .ts 文件或 m3u8 播放列表。")
            return

        missing = [file for file in files if not Path(file).is_file()]
        if missing:
            QMessageBox.warning(self, "文件不存在", "\n".join(missing[:5]))
            return

        playlists = [file for file in files if core.is_playlist(file)]
        if playlists and len(playlists) != len(files):
            QMessageBox.warning(
                self, "输入混合",
                "m3u8 播放列表不能和 .ts 分片混在一起合并，请只保留播放列表本身。",
            )
            return
        if len(playlists) > 1:
            self._start_batch_merge(playlists, ask_path)
            return

        first = Path(files[0])
        save_dir = self.save_dir_edit.text().strip()
        if playlists:
            stem = first.parent.name
            # 未指定保存位置时放在缓存目录的上一级，避免往缓存文件夹里写大文件
            base_dir = Path(save_dir).expanduser() if save_dir else first.parent.parent
        else:
            stem = first.stem
            base_dir = Path(save_dir).expanduser() if save_dir else first.parent
        default_output = base_dir / f"{stem}_合并.mp4"
        if ask_path:
            chosen, _ = QFileDialog.getSaveFileName(
                self, "另存为", str(default_output), "MP4 视频 (*.mp4);;所有文件 (*)"
            )
            if not chosen:
                return
            output_path = Path(chosen)
            if output_path.suffix.lower() != ".mp4":
                output_path = output_path.with_suffix(".mp4")
        else:
            # 直接合并：同名时自动加 (2)、(3)，绝不覆盖已有文件
            output_path = core.unique_path(default_output)

        if output_path.resolve() in {Path(file).resolve() for file in files}:
            QMessageBox.warning(self, "输出路径无效", "输出文件不能与输入文件相同。")
            return

        try:
            output_path.parent.mkdir(parents=True, exist_ok=True)
        except OSError as exc:
            QMessageBox.critical(self, "错误", f"无法创建输出目录：{exc}")
            return

        if playlists:
            # 加密 HLS 必须交给 ffmpeg 解析播放列表，由它用同目录的 key 解密
            command = core.build_hls_merge_cmd(playlists[0], output_path)
            label = "合并 m3u8"
        else:
            temp_dir = Path(tempfile.mkdtemp(prefix="bili-ts-tool-"))
            self._temp_dirs.append(temp_dir)
            filelist = core.write_concat_filelist(files, temp_dir / "filelist.txt")
            command = core.build_ts_merge_cmd(filelist, output_path)
            label = "合并 TS"

        self.settings.setValue("ts/last_dir", str(first.parent))
        worker = ProcessWorker(
            [command],
            labels=[label],
            success_message=f"合并完成：{output_path}",
        )
        self.start_worker(worker, indeterminate=True)

    def _start_batch_merge(self, playlists: list[str], ask_path: bool) -> None:
        """多个播放列表：逐个合并到默认保存位置（文件名取自各自的文件夹名）。"""
        if ask_path:
            QMessageBox.information(
                self, "批量合并",
                f"当前有 {len(playlists)} 个播放列表，会统一输出到默认保存位置。\n"
                "只想改其中一个的名字或位置时，请先把它单独留在列表里。",
            )
        save_dir = self.save_dir_edit.text().strip()
        base_dir = Path(save_dir).expanduser() if save_dir else Path(playlists[0]).parent.parent
        try:
            base_dir.mkdir(parents=True, exist_ok=True)
        except OSError as exc:
            QMessageBox.critical(self, "错误", f"无法创建输出目录：{exc}")
            return

        commands: list[list[str]] = []
        labels: list[str] = []
        used: set[str] = set()
        for item in playlists:
            playlist = Path(item)
            stem = core.sanitize_filename(playlist.parent.name)
            output = base_dir / f"{stem}_合并.mp4"
            counter = 2
            while output.name.lower() in used or output.exists():
                output = base_dir / f"{stem}_合并 ({counter}).mp4"
                counter += 1
            used.add(output.name.lower())
            commands.append(core.build_hls_merge_cmd(playlist, output))
            labels.append(output.name)

        self.settings.setValue("ts/last_dir", str(Path(playlists[0]).parent))
        worker = ProcessWorker(
            commands,
            labels=labels,
            success_message=f"合并完成：共 {len(commands)} 个视频 → {base_dir}",
        )
        self.start_worker(worker, indeterminate=True)

    def _open_save_dir(self):
        target = self.resolve_dir(self.save_dir_edit.text(), Path.home())
        try:
            target.mkdir(parents=True, exist_ok=True)
        except OSError as exc:
            QMessageBox.critical(self, "错误", f"无法打开目录：{exc}")
            return
        QDesktopServices.openUrl(QUrl.fromLocalFile(str(target)))


# ---------------------------------------------------------------------------
# B站下载
# ---------------------------------------------------------------------------
class BiliDownloadTab(BaseTab):
    def __init__(self):
        super().__init__()

        layout = QVBoxLayout(self)
        layout.setContentsMargins(12, 12, 12, 12)

        hint = QLabel(
            "输入 B站视频链接，使用 yt-dlp 下载并自动合并为 MP4。\n"
            "1080P 及以上画质需要登录 Cookie：可从浏览器开发者工具中复制 SESSDATA。"
        )
        hint.setWordWrap(True)
        layout.addWidget(hint)

        form = QFormLayout()
        form.setFieldGrowthPolicy(QFormLayout.FieldGrowthPolicy.ExpandingFieldsGrow)

        self.url_edit = QLineEdit()
        self.url_edit.setPlaceholderText("https://www.bilibili.com/video/BVxxxxxxxxxx")
        form.addRow("视频链接：", self.url_edit)

        output_row, self.output_edit, output_widgets = self.build_dir_row(
            "download/output_dir", Path.home() / "Downloads"
        )
        form.addRow("默认保存位置：", output_row)

        self.cookie_edit = QLineEdit()
        self.cookie_edit.setPlaceholderText("可选，只填 SESSDATA 的值")
        self.cookie_edit.setEchoMode(QLineEdit.EchoMode.Password)
        self.cookie_edit.setClearButtonEnabled(True)
        self.show_cookie = QCheckBox("显示")
        self.show_cookie.toggled.connect(self._toggle_cookie_visibility)
        cookie_row = QHBoxLayout()
        cookie_row.addWidget(self.cookie_edit, 1)
        cookie_row.addWidget(self.show_cookie)
        form.addRow("SESSDATA：", cookie_row)

        layout.addLayout(form)
        layout.addStretch(1)

        buttons = QHBoxLayout()
        self.download_btn = QPushButton("开始下载")
        self.download_btn.setMinimumHeight(34)
        self.download_btn.clicked.connect(self._start_download)
        self.update_btn = QPushButton("更新 yt-dlp")
        self.update_btn.clicked.connect(self._update_ytdlp)
        buttons.addWidget(self.download_btn, 2)
        buttons.addWidget(self.update_btn, 1)
        layout.addLayout(buttons)
        layout.addLayout(self.bottom_bar())

        self._controls = [self.url_edit, self.cookie_edit, self.show_cookie,
                          *output_widgets, self.download_btn, self.update_btn]

    def set_controls_enabled(self, enabled: bool):
        for widget in self._controls:
            widget.setEnabled(enabled)

    def _toggle_cookie_visibility(self, visible: bool):
        mode = QLineEdit.EchoMode.Normal if visible else QLineEdit.EchoMode.Password
        self.cookie_edit.setEchoMode(mode)

    def _start_download(self):
        url = self.url_edit.text().strip()
        if not url.lower().startswith(("http://", "https://")):
            QMessageBox.warning(self, "链接无效", "请输入以 http:// 或 https:// 开头的视频链接。")
            return

        output_dir = self.resolve_dir(self.output_edit.text(), Path.home() / "Downloads")
        try:
            output_dir.mkdir(parents=True, exist_ok=True)
        except OSError as exc:
            QMessageBox.critical(self, "错误", f"无法创建输出目录：{exc}")
            return

        self.settings.setValue("download/output_dir", str(output_dir))

        cookies_path = None
        cookie_value = self.cookie_edit.text().strip()
        if cookie_value:
            temp_dir = Path(tempfile.mkdtemp(prefix="bili-ts-tool-"))
            self._temp_dirs.append(temp_dir)
            cookies_path = core.write_cookie_file(
                core.normalize_cookie(cookie_value), temp_dir / "cookies.txt"
            )

        worker = ProcessWorker(
            [core.build_ytdlp_cmd(url, output_dir, cookies_path=cookies_path)],
            labels=["下载"],
            progress_re=core.YTDLP_PROGRESS_RE,
            success_message=f"下载完成：{output_dir}",
        )
        self.start_worker(worker, indeterminate=False)

    def _update_ytdlp(self):
        worker = ProcessWorker(
            [[core.ytdlp_path(), "-U"]],
            labels=["更新 yt-dlp"],
            success_message="yt-dlp 更新检查完成。",
        )
        self.start_worker(worker, indeterminate=True)


# ---------------------------------------------------------------------------
# B站缓存转 MP4
# ---------------------------------------------------------------------------
class LocalCacheTab(BaseTab):
    def __init__(self):
        super().__init__()

        layout = QVBoxLayout(self)
        layout.setContentsMargins(12, 12, 12, 12)

        hint = QLabel(
            "选择 B站客户端/APP 的缓存文件夹。支持两种格式：\n"
            "APP 的 video.m4s + audio.m4s，Windows 客户端的 <cid>-<page>-<id>.m4s 平铺目录。\n"
            "会递归扫描，并自动跳过同一视频的多个清晰度副本，只转换最高清晰度。"
        )
        hint.setWordWrap(True)
        layout.addWidget(hint)

        self.folder_list = DropListWidget()
        self.folder_list.paths_dropped.connect(self._add_folders)
        self.folder_list.set_placeholder("把 B站缓存文件夹拖到这里\n（APP 缓存或 Windows 客户端缓存）")
        self.folder_list.setToolTip("将缓存文件夹拖到这里")
        layout.addWidget(self.folder_list, 1)

        row = QHBoxLayout()
        self.add_btn = QPushButton("添加缓存文件夹…")
        self.add_btn.clicked.connect(self._choose_folders)
        self.remove_btn = QPushButton("移除选中")
        self.remove_btn.clicked.connect(self._remove_selected)
        self.clear_btn = QPushButton("清空")
        self.clear_btn.clicked.connect(self.folder_list.clear)
        row.addWidget(self.add_btn)
        row.addWidget(self.remove_btn)
        row.addWidget(self.clear_btn)
        row.addStretch(1)
        layout.addLayout(row)

        output_row, self.output_edit, output_widgets = self.build_dir_row(
            "cache/output_dir", Path.home() / "Videos", label="默认保存位置："
        )
        layout.addLayout(output_row)

        self.convert_btn = QPushButton("开始转换")
        self.convert_btn.setMinimumHeight(34)
        self.convert_btn.clicked.connect(self._start_convert)
        layout.addWidget(self.convert_btn)
        layout.addLayout(self.bottom_bar())

        self._controls = [self.add_btn, self.remove_btn, self.clear_btn,
                          *output_widgets, self.convert_btn, self.folder_list]

    def set_controls_enabled(self, enabled: bool):
        for widget in self._controls:
            widget.setEnabled(enabled)

    def _add_folders(self, paths):
        existing = {
            self.folder_list.item(i).data(Qt.ItemDataRole.UserRole)
            for i in range(self.folder_list.count())
        }
        added = 0
        for raw in paths:
            path = Path(raw)
            if path.is_file() and path.suffix.lower() == ".m4s":
                path = path.parent
            if not path.is_dir():
                continue
            key = str(path.resolve())
            if key in existing:
                continue
            existing.add(key)
            item = QListWidgetItem(key)
            item.setData(Qt.ItemDataRole.UserRole, key)
            item.setToolTip(key)
            item.setIcon(self.folder_list.icon_for(key, is_dir=True))
            self.folder_list.addItem(item)
            added += 1
        if added:
            self.set_status(f"已添加 {added} 个文件夹")

    def _choose_folders(self):
        start = self.settings.value(
            "cache/last_folder", str(Path.home()), type=str
        )
        directory = QFileDialog.getExistingDirectory(self, "选择 B站缓存文件夹", start)
        if directory:
            self.settings.setValue("cache/last_folder", directory)
            self._add_folders([directory])

    def _remove_selected(self):
        for item in self.folder_list.selectedItems():
            self.folder_list.takeItem(self.folder_list.row(item))

    def _start_convert(self):
        folders = [
            self.folder_list.item(i).data(Qt.ItemDataRole.UserRole)
            for i in range(self.folder_list.count())
        ]
        if not folders:
            QMessageBox.information(self, "提示", "请先添加缓存文件夹。")
            return

        output_dir = self.resolve_dir(self.output_edit.text(), Path.home() / "Videos")
        try:
            output_dir.mkdir(parents=True, exist_ok=True)
        except OSError as exc:
            QMessageBox.critical(self, "错误", f"无法创建输出目录：{exc}")
            return

        QApplication.setOverrideCursor(Qt.CursorShape.WaitCursor)
        try:
            pairs: list[core.M4sPair] = []
            seen: set[str] = set()
            for folder in folders:
                for pair in core.find_m4s_pairs(folder):
                    key = str(pair.video)
                    if key not in seen:
                        seen.add(key)
                        pairs.append(pair)
        finally:
            QApplication.restoreOverrideCursor()

        if not pairs:
            QMessageBox.information(
                self, "未找到缓存",
                "所选文件夹中未找到可转换的缓存：\n"
                "· APP 缓存：目录需同时包含 video.m4s 与 audio.m4s\n"
                "· Windows 客户端：<cid>-<page>-<id>.m4s（302xx 为音频）\n"
                "请选择具体的视频缓存目录再试。",
            )
            return

        commands: list[list[str]] = []
        labels: list[str] = []
        used_names: set[str] = set()
        for pair in pairs:
            base = core.sanitize_filename(core.guess_title(pair.folder))
            output = output_dir / f"{base}.mp4"
            counter = 2
            while output.name.lower() in used_names or output.exists():
                output = output_dir / f"{base} ({counter}).mp4"
                counter += 1
            used_names.add(output.name.lower())
            commands.append(core.build_m4s_cmd(pair.video, pair.audio, output))
            labels.append(output.name)

        self.settings.setValue("cache/output_dir", str(output_dir))
        worker = ProcessWorker(
            commands,
            labels=labels,
            success_message=f"转换完成：共 {len(commands)} 个视频 → {output_dir}",
        )
        self.start_worker(worker, indeterminate=True)


# ---------------------------------------------------------------------------
# 主窗口
# ---------------------------------------------------------------------------
class MainWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle(f"VideoChanger v{core.__version__} — TS 合并 / B站视频转 MP4")
        self.resize(980, 760)

        # 菜单栏：帮助 → 关于
        help_menu = self.menuBar().addMenu("帮助")
        about_action = QAction("关于 VideoChanger", self)
        about_action.triggered.connect(self._show_about)
        help_menu.addAction(about_action)

        central = QWidget()
        outer = QVBoxLayout(central)
        outer.setContentsMargins(10, 10, 10, 10)

        self.ts_tab = TSMergeTab()
        self.download_tab = BiliDownloadTab()
        self.cache_tab = LocalCacheTab()
        self.all_tabs = [self.ts_tab, self.download_tab, self.cache_tab]

        tab_widget = QTabWidget()
        for tab, title in zip(self.all_tabs, ["TS 合并", "B站下载", "B站缓存转 MP4"]):
            tab_widget.addTab(tab, title)
            tab.log.connect(self.append_log)

        log_box = QGroupBox("日志")
        log_layout = QVBoxLayout(log_box)
        self.log_view = QPlainTextEdit()
        self.log_view.setReadOnly(True)
        self.log_view.setMaximumBlockCount(5000)
        log_font = QFont("Consolas")
        log_font.setStyleHint(QFont.StyleHint.Monospace)
        log_font.setPointSize(9)
        self.log_view.setFont(log_font)
        log_layout.addWidget(self.log_view, 1)
        log_buttons = QHBoxLayout()
        log_buttons.addStretch(1)
        clear_btn = QPushButton("清空日志")
        clear_btn.clicked.connect(self.log_view.clear)
        log_buttons.addWidget(clear_btn)
        log_layout.addLayout(log_buttons)

        splitter = QSplitter(Qt.Orientation.Vertical)
        splitter.addWidget(tab_widget)
        splitter.addWidget(log_box)
        splitter.setStretchFactor(0, 3)
        splitter.setStretchFactor(1, 1)
        splitter.setSizes([500, 220])
        outer.addWidget(splitter, 1)

        self.setCentralWidget(central)

        self.append_log(f"ffmpeg：{core.ffmpeg_path()}")
        self.append_log(f"yt-dlp：{core.ytdlp_path()}")

        missing = [
            name for name, path in (("ffmpeg", core.ffmpeg_path()), ("yt-dlp", core.ytdlp_path()))
            if not Path(path).is_file()
        ]
        if missing:
            self.append_log(
                "⚠ 未找到 " + "、".join(missing) +
                "。请安装后加入 PATH，或把可执行文件放进程序同级的 bin\\ 目录。"
            )

    def _show_about(self):
        QMessageBox.about(
            self,
            "关于 VideoChanger",
            f"<h3>VideoChanger v{core.__version__}</h3>"
            "<p><b>作者</b>：WriteBugsProfessionally<br>"
            "<b>许可</b>：MIT（本项目代码）</p>"
            "<p><b>依赖</b>：本工具调用 <code>ffmpeg</code> 与 <code>yt-dlp</code>，"
            "二者<strong>不随本项目分发</strong>，请自行安装。"
            "FFmpeg 通常以 GPL 授权，详见项目根目录的 "
            "<code>THIRD_PARTY_NOTICES.md</code> 与 <code>licenses/</code>。</p>"
            "<p><b>隐私</b>：所有处理都在本机完成，SESSDATA 只写在本地 cookie 文件里，"
            "不会上传到任何服务器。</p>"
            "<p>本项目与哔哩哔哩无关联，请仅处理你有权访问的内容。</p>"
        )

    def append_log(self, text: str):
        timestamp = time.strftime("%H:%M:%S")
        for line in str(text).splitlines() or [""]:
            self.log_view.appendPlainText(f"[{timestamp}] {line}")

    def closeEvent(self, event):
        running = [tab for tab in self.all_tabs if tab.worker is not None]
        if running:
            answer = QMessageBox.question(
                self, "任务进行中", "还有任务正在运行，确定退出吗？"
            )
            if answer != QMessageBox.StandardButton.Yes:
                event.ignore()
                return
            for tab in running:
                tab.worker.cancel()
            for tab in running:
                tab.worker.wait(5000)
        event.accept()


def run_self_test() -> None:
    """无界面诊断：把二进制路径解析与可执行性写入 JSON，供打包后验证。"""
    ffmpeg = core.ffmpeg_path()
    ytdlp = core.ytdlp_path()
    report = {
        "frozen": bool(getattr(sys, "frozen", False)),
        "executable": sys.executable,
        "ffmpeg_path": ffmpeg,
        "ytdlp_path": ytdlp,
        "ffmpeg_exists": Path(ffmpeg).is_file(),
        "ytdlp_exists": Path(ytdlp).is_file(),
    }
    try:
        result = subprocess.run(
            [ffmpeg, "-version"],
            stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
            text=True, encoding="utf-8", errors="replace",
            timeout=20, creationflags=core.CREATE_NO_WINDOW,
        )
        report["ffmpeg_ok"] = result.returncode == 0
        report["ffmpeg_version"] = (result.stdout or "").splitlines()[0]
    except Exception as exc:  # noqa: BLE001 - 诊断需要记录任何异常
        report["ffmpeg_ok"] = False
        report["ffmpeg_error"] = str(exc)

    output = Path(os.environ.get("BILI_TS_TOOL_SELFTEST_OUT") or (Path.cwd() / "selftest.json"))
    output.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")


def main():
    if "--self-test" in sys.argv:
        run_self_test()
        return

    selftest = os.environ.get("BILI_TS_TOOL_SELFTEST") == "1"
    if selftest:
        os.environ.setdefault("QT_QPA_PLATFORM", "offscreen")

    app = QApplication(sys.argv)
    app.setApplicationName(core.APP_NAME)
    app.setOrganizationName(core.APP_NAME)
    icon_path = app_icon_path()
    if icon_path.is_file():
        app.setWindowIcon(QIcon(str(icon_path)))
    if sys.platform == "win32":
        app.setFont(QFont("Microsoft YaHei UI", 9))

    window = MainWindow()
    window.show()

    if selftest:
        QTimer.singleShot(1500, app.quit)

    sys.exit(app.exec())


if __name__ == "__main__":
    main()
