"""表格模型：重复组列表 + 组内文件列表（带勾选状态）。"""

from __future__ import annotations

from PySide6.QtCore import QAbstractTableModel, QEvent, QModelIndex, QRectF, Qt, Signal
from PySide6.QtGui import QColor, QFont, QPainter, QPainterPath, QPen
from PySide6.QtWidgets import (
    QApplication,
    QStyle,
    QStyledItemDelegate,
    QStyleOptionViewItem,
)

from ..models import DuplicateGroup, FileEntry
from ..util import format_mtime, human_size
from . import theme

_NUM_FONT = QFont("Consolas")
_NUM_FONT.setPointSizeF(9.0)

CHECK_ROLE = Qt.ItemDataRole.UserRole + 11


class CheckDelegate(QStyledItemDelegate):
    """自绘复选框，保证深色主题下勾选状态清晰一致。"""

    SIZE = 17

    def paint(self, painter: QPainter, option: QStyleOptionViewItem, index: QModelIndex) -> None:
        opt = QStyleOptionViewItem(option)
        self.initStyleOption(opt, index)
        opt.text = ""
        style = opt.widget.style() if opt.widget is not None else QApplication.style()
        style.drawPrimitive(QStyle.PrimitiveElement.PE_PanelItemViewItem, opt, painter, opt.widget)

        checked = bool(index.data(CHECK_ROLE))
        box = QRectF(0, 0, self.SIZE, self.SIZE)
        box.moveCenter(QRectF(option.rect).center())
        painter.save()
        painter.setRenderHint(QPainter.RenderHint.Antialiasing, True)
        if checked:
            painter.setPen(QPen(QColor("#79a8ff"), 1.0))
            painter.setBrush(QColor(theme.ACCENT))
            painter.drawRoundedRect(box, 4.5, 4.5)
            pen = QPen(QColor("#ffffff"), 1.9)
            pen.setCapStyle(Qt.PenCapStyle.RoundCap)
            pen.setJoinStyle(Qt.PenJoinStyle.RoundJoin)
            painter.setPen(pen)
            path = QPainterPath()
            path.moveTo(box.x() + 4.0, box.y() + 8.7)
            path.lineTo(box.x() + 7.2, box.y() + 11.8)
            path.lineTo(box.x() + 13.0, box.y() + 5.6)
            painter.drawPath(path)
        else:
            painter.setPen(QPen(QColor("#3a465a"), 1.0))
            painter.setBrush(QColor("#0f141c"))
            painter.drawRoundedRect(box, 4.5, 4.5)
        painter.restore()

    def editorEvent(self, event, model, option, index):  # noqa: N802
        if event.type() == QEvent.Type.MouseButtonRelease and event.button() == Qt.MouseButton.LeftButton:
            if option.rect.contains(event.position().toPoint()):
                checked = bool(index.data(CHECK_ROLE))
                model.setData(index, not checked, CHECK_ROLE)
                return True
        if event.type() == QEvent.Type.KeyPress and event.key() == Qt.Key.Key_Space:
            checked = bool(index.data(CHECK_ROLE))
            model.setData(index, not checked, CHECK_ROLE)
            return True
        return False


def _keep_index(group: DuplicateGroup, policy: str) -> int:
    files = group.files
    if not files:
        return 0
    if policy == "newest":
        return max(
            range(len(files)),
            key=lambda i: (files[i].mtime_ns, -len(files[i].name), files[i].path.casefold()),
        )
    if policy == "shortest_path":
        return min(range(len(files)), key=lambda i: (len(files[i].path), files[i].path.casefold()))
    if policy == "shortest_name":
        return min(
            range(len(files)),
            key=lambda i: (len(files[i].name), files[i].name.casefold(), len(files[i].path)),
        )
    # 最早修改优先；修改时间相同时保留文件名更短、更像“原件”的那份
    return min(
        range(len(files)),
        key=lambda i: (
            files[i].mtime_ns,
            len(files[i].name),
            files[i].name.casefold(),
            files[i].path.casefold(),
        ),
    )


class GroupTableModel(QAbstractTableModel):
    HEADERS = ("组", "文件", "单个大小", "可释放", "待删")

    selectionSummaryChanged = Signal()

    def __init__(self, parent=None) -> None:
        super().__init__(parent)
        self.groups: list[DuplicateGroup] = []
        self.selection: list[list[bool]] = []

    # ---------- Qt 接口 ----------
    def rowCount(self, parent=QModelIndex()) -> int:  # noqa: N802
        return 0 if parent.isValid() else len(self.groups)

    def columnCount(self, parent=QModelIndex()) -> int:  # noqa: N802
        return 0 if parent.isValid() else len(self.HEADERS)

    def headerData(self, section: int, orientation, role=Qt.ItemDataRole.DisplayRole):  # noqa: N802
        if orientation == Qt.Orientation.Horizontal and role == Qt.ItemDataRole.DisplayRole:
            return self.HEADERS[section]
        return None

    def data(self, index: QModelIndex, role=Qt.ItemDataRole.DisplayRole):
        if not index.isValid():
            return None
        group = self.groups[index.row()]
        column = index.column()

        if role == Qt.ItemDataRole.DisplayRole:
            if column == 0:
                return str(index.row() + 1)
            if column == 1:
                return str(group.count)
            if column == 2:
                return human_size(group.file_size, decimals=1)
            if column == 3:
                return human_size(group.wasted, decimals=1)
            if column == 4:
                count = sum(self.selection[index.row()])
                return str(count) if count else "—"
            return None

        if role == Qt.ItemDataRole.TextAlignmentRole:
            if column in (0, 1, 4):
                return int(Qt.AlignmentFlag.AlignCenter)
            return int(Qt.AlignmentFlag.AlignRight | Qt.AlignmentFlag.AlignVCenter)

        if role == Qt.ItemDataRole.FontRole:
            if column in (1, 2, 3):
                return _NUM_FONT
            return None

        if role == Qt.ItemDataRole.ForegroundRole:
            if column == 3:
                return QColor(theme.GREEN)
            if column == 4:
                count = sum(self.selection[index.row()])
                return QColor(theme.ACCENT) if count else QColor(theme.MUTED_2)
            if column == 2:
                return QColor(theme.TEXT_DIM)
            return None

        if role == Qt.ItemDataRole.ToolTipRole:
            lines = [
                f"第 {index.row() + 1} 组 · {group.count} 个文件 · 每个 {human_size(group.file_size)}",
                f"可释放 {human_size(group.wasted)}",
                "",
            ]
            lines.extend(group.files[i].path for i in range(min(group.count, 10)))
            if group.count > 10:
                lines.append(f"…… 还有 {group.count - 10} 个")
            return "\n".join(lines)
        return None

    # ---------- 数据操作 ----------
    def set_groups(
        self,
        groups: list[DuplicateGroup],
        policy: str | None = None,
        preserved: list[list[bool]] | None = None,
    ) -> None:
        self.beginResetModel()
        self.groups = groups
        if preserved is not None and len(preserved) == len(groups):
            self.selection = [list(row) for row in preserved]
        else:
            self.selection = [[False] * len(group.files) for group in groups]
        self.endResetModel()
        if policy and preserved is None:
            self.apply_policy(policy)
        self.selectionSummaryChanged.emit()

    def apply_policy(self, policy: str) -> None:
        if not self.groups:
            return
        for row, group in enumerate(self.groups):
            keep = _keep_index(group, policy)
            self.selection[row] = [i != keep for i in range(group.count)]
        top = self.index(0, 4)
        bottom = self.index(len(self.groups) - 1, 4)
        self.dataChanged.emit(top, bottom, [Qt.ItemDataRole.DisplayRole, Qt.ItemDataRole.ForegroundRole])
        self.selectionSummaryChanged.emit()

    def checks_for(self, row: int) -> list[bool]:
        if 0 <= row < len(self.selection):
            return self.selection[row]
        return []

    def refresh_row(self, row: int) -> None:
        if 0 <= row < len(self.groups):
            idx = self.index(row, 4)
            self.dataChanged.emit(idx, idx, [Qt.ItemDataRole.DisplayRole, Qt.ItemDataRole.ForegroundRole])

    def checked_paths(self) -> list[str]:
        result: list[str] = []
        for group, checks in zip(self.groups, self.selection):
            result.extend(entry.path for entry, checked in zip(group.files, checks) if checked)
        return result

    def checked_summary(self) -> tuple[int, int]:
        count = 0
        size = 0
        for group, checks in zip(self.groups, self.selection):
            for entry, checked in zip(group.files, checks):
                if checked:
                    count += 1
                    size += entry.size
        return count, size

    def group_at(self, row: int) -> DuplicateGroup | None:
        if 0 <= row < len(self.groups):
            return self.groups[row]
        return None


class FileTableModel(QAbstractTableModel):
    HEADERS = ("删除", "文件名", "大小", "修改时间", "所在文件夹")

    selectionChanged = Signal()
    lastCopyWarning = Signal()

    def __init__(self, parent=None) -> None:
        super().__init__(parent)
        self.group: DuplicateGroup | None = None
        self.group_index = -1
        self.checks: list[bool] = []
        self.group_model: GroupTableModel | None = None

    def bind_group(self, index: int, group: DuplicateGroup | None, checks: list[bool] | None) -> None:
        self.beginResetModel()
        self.group = group
        self.group_index = index
        self.checks = list(checks or [])
        self.endResetModel()

    def rowCount(self, parent=QModelIndex()) -> int:  # noqa: N802
        if parent.isValid() or self.group is None:
            return 0
        return len(self.group.files)

    def columnCount(self, parent=QModelIndex()) -> int:  # noqa: N802
        return 0 if parent.isValid() else len(self.HEADERS)

    def headerData(self, section: int, orientation, role=Qt.ItemDataRole.DisplayRole):  # noqa: N802
        if orientation == Qt.Orientation.Horizontal:
            if role == Qt.ItemDataRole.DisplayRole:
                return self.HEADERS[section]
            if role == Qt.ItemDataRole.ToolTipRole and section == 0:
                return "勾选表示删除这个副本；取消勾选表示保留。"
        return None

    def flags(self, index: QModelIndex):
        if not index.isValid():
            return Qt.ItemFlag.NoItemFlags
        return Qt.ItemFlag.ItemIsEnabled | Qt.ItemFlag.ItemIsSelectable

    def data(self, index: QModelIndex, role=Qt.ItemDataRole.DisplayRole):
        if not index.isValid() or self.group is None:
            return None
        entry = self.group.files[index.row()]
        column = index.column()

        if role == CHECK_ROLE and column == 0:
            return bool(self.checks[index.row()]) if index.row() < len(self.checks) else False

        if role == Qt.ItemDataRole.DisplayRole:
            if column == 1:
                return entry.name
            if column == 2:
                return human_size(entry.size)
            if column == 3:
                return format_mtime(entry.mtime_ns)
            if column == 4:
                return entry.directory
            return None

        if role == Qt.ItemDataRole.TextAlignmentRole:
            if column == 2:
                return int(Qt.AlignmentFlag.AlignRight | Qt.AlignmentFlag.AlignVCenter)
            if column == 0:
                return int(Qt.AlignmentFlag.AlignCenter)
            return int(Qt.AlignmentFlag.AlignLeft | Qt.AlignmentFlag.AlignVCenter)

        if role == Qt.ItemDataRole.FontRole and column == 2:
            return _NUM_FONT

        if role == Qt.ItemDataRole.ForegroundRole:
            if column in (3, 4):
                return QColor(theme.MUTED)
            return None

        if role == Qt.ItemDataRole.ToolTipRole:
            if column == 1:
                return entry.path
            if column == 4:
                return entry.directory
            return entry.path
        return None

    def setData(self, index: QModelIndex, value, role=Qt.ItemDataRole.EditRole) -> bool:  # noqa: N802
        if not index.isValid() or self.group is None:
            return False
        if role != CHECK_ROLE or index.column() != 0:
            return False
        row = index.row()
        checked = bool(value)
        if checked:
            unchecked = [i for i, flag in enumerate(self.checks) if not flag]
            if len(unchecked) == 1 and unchecked[0] == row:
                self.lastCopyWarning.emit()
                return False
        if row < len(self.checks) and self.checks[row] != checked:
            self.checks[row] = checked
            self.dataChanged.emit(index, index, [CHECK_ROLE])
            self._sync_group()
            self.selectionChanged.emit()
        return True

    def keep_only(self, row: int) -> None:
        if self.group is None or not (0 <= row < len(self.checks)):
            return
        self.checks = [i != row for i in range(len(self.checks))]
        if self.checks:
            left = self.index(0, 0)
            right = self.index(len(self.checks) - 1, 0)
            self.dataChanged.emit(left, right, [CHECK_ROLE])
        self._sync_group()
        self.selectionChanged.emit()

    def select_all_copies(self) -> None:
        if self.group is None or not self.checks:
            return
        self.checks = [i != 0 for i in range(len(self.checks))]
        left = self.index(0, 0)
        right = self.index(len(self.checks) - 1, 0)
        self.dataChanged.emit(left, right, [CHECK_ROLE])
        self._sync_group()
        self.selectionChanged.emit()

    def _sync_group(self) -> None:
        if self.group_model is not None and self.group_index >= 0:
            self.group_model.selection[self.group_index] = self.checks
            self.group_model.refresh_row(self.group_index)

    def checked_summary(self) -> tuple[int, int]:
        if self.group is None:
            return 0, 0
        count = 0
        size = 0
        for entry, checked in zip(self.group.files, self.checks):
            if checked:
                count += 1
                size += entry.size
        return count, size

    def entry_at(self, row: int) -> FileEntry | None:
        if self.group is not None and 0 <= row < len(self.group.files):
            return self.group.files[row]
        return None

    def is_checked(self, row: int) -> bool:
        return 0 <= row < len(self.checks) and self.checks[row]
