"""可复用的界面小部件：统计卡片、缩略图、空状态。"""

from __future__ import annotations

from PySide6.QtCore import QRectF, QSize, Qt
from PySide6.QtGui import QColor, QFont, QImage, QPainter, QPainterPath, QPixmap
from PySide6.QtWidgets import (
    QFrame,
    QHBoxLayout,
    QLabel,
    QSizePolicy,
    QVBoxLayout,
    QWidget,
)

from . import theme
from .icons import icon


class StatTile(QFrame):
    """顶部统计卡片：标题 + 大数字。"""

    def __init__(self, caption: str, value: str = "—", accent: str = "", parent: QWidget | None = None) -> None:
        super().__init__(parent)
        self.setObjectName("statTile")
        self.setSizePolicy(QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Fixed)
        self.setFixedHeight(62)

        layout = QVBoxLayout(self)
        layout.setContentsMargins(14, 9, 14, 8)
        layout.setSpacing(1)

        self.value_label = QLabel(value)
        self.value_label.setObjectName(accent or "statValue")
        value_font = QFont("Consolas")
        value_font.setPointSizeF(14.0)
        value_font.setBold(True)
        self.value_label.setFont(value_font)

        self.caption_label = QLabel(caption)
        self.caption_label.setObjectName("statCaption")

        layout.addWidget(self.value_label)
        layout.addWidget(self.caption_label)

    def set_value(self, text: str) -> None:
        self.value_label.setText(text)


class Divider(QFrame):
    def __init__(self, parent: QWidget | None = None) -> None:
        super().__init__(parent)
        self.setObjectName("hsep")
        self.setFrameShape(QFrame.Shape.NoFrame)


class Badge(QLabel):
    def __init__(self, text: str = "", parent: QWidget | None = None) -> None:
        super().__init__(text, parent)
        self.setObjectName("badge")
        self.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.setSizePolicy(QSizePolicy.Policy.Maximum, QSizePolicy.Policy.Fixed)


class EmptyState(QWidget):
    """图标 + 标题 + 说明的居中空状态。"""

    def __init__(
        self,
        icon_name: str,
        title: str,
        hint: str,
        parent: QWidget | None = None,
        icon_size: int = 56,
    ) -> None:
        super().__init__(parent)
        layout = QVBoxLayout(self)
        layout.setContentsMargins(24, 24, 24, 24)
        layout.setSpacing(8)
        layout.setAlignment(Qt.AlignmentFlag.AlignCenter)

        self._icon_label = QLabel()
        self._icon_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self._icon_label.setPixmap(icon(icon_name, theme.MUTED_2, icon_size).pixmap(icon_size, icon_size))

        self.title_label = QLabel(title)
        self.title_label.setObjectName("emptyTitle")
        self.title_label.setAlignment(Qt.AlignmentFlag.AlignCenter)

        self.hint_label = QLabel(hint)
        self.hint_label.setObjectName("emptyHint")
        self.hint_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.hint_label.setWordWrap(True)
        self.hint_label.setMaximumWidth(420)

        layout.addStretch(1)
        layout.addWidget(self._icon_label, 0, Qt.AlignmentFlag.AlignHCenter)
        layout.addSpacing(4)
        layout.addWidget(self.title_label)
        layout.addWidget(self.hint_label)
        layout.addStretch(1)

    def set_text(self, title: str, hint: str) -> None:
        self.title_label.setText(title)
        self.hint_label.setText(hint)


class ThumbView(QWidget):
    """圆角缩略图，支持等比缩放和占位文字。"""

    def __init__(self, parent: QWidget | None = None) -> None:
        super().__init__(parent)
        self._image: QImage | None = None
        self._placeholder = "选择文件以预览"
        self._placeholder_icon = "image"
        self.setMinimumHeight(190)
        self.setSizePolicy(QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Expanding)

    def set_image(self, image: QImage | None, placeholder_icon: str = "image") -> None:
        self._image = image
        self._placeholder_icon = placeholder_icon
        self.update()

    def clear(self, text: str = "选择文件以预览", icon_name: str = "search") -> None:
        self._image = None
        self._placeholder = text
        self._placeholder_icon = icon_name
        self.update()

    def set_placeholder_text(self, text: str) -> None:
        self._placeholder = text
        self.update()

    def paintEvent(self, event) -> None:  # noqa: N802
        painter = QPainter(self)
        painter.setRenderHint(QPainter.RenderHint.Antialiasing, True)
        painter.setRenderHint(QPainter.RenderHint.SmoothPixmapTransform, True)

        rect = QRectF(self.rect()).adjusted(0.5, 0.5, -0.5, -0.5)
        path = QPainterPath()
        path.addRoundedRect(rect, 10, 10)
        painter.fillPath(path, QColor("#0b0f15"))
        painter.setPen(QColor(theme.BORDER))
        painter.drawPath(path)

        painter.save()
        painter.setClipPath(path)

        if self._image is not None and not self._image.isNull():
            image = self._image
            available = rect.adjusted(10, 10, -10, -10)
            scale = min(available.width() / max(1, image.width()), available.height() / max(1, image.height()))
            width = image.width() * scale
            height = image.height() * scale
            target = QRectF(
                available.x() + (available.width() - width) / 2,
                available.y() + (available.height() - height) / 2,
                width,
                height,
            )
            painter.drawImage(target, image)
        else:
            painter.setPen(QColor(theme.MUTED_2))
            font = QFont(self.font())
            font.setPointSizeF(9.0)
            painter.setFont(font)
            pixmap: QPixmap = icon(self._placeholder_icon, "#3a4658", 48).pixmap(48, 48)
            icon_rect = QRectF(
                rect.center().x() - 24,
                rect.center().y() - 44,
                48,
                48,
            )
            painter.drawPixmap(icon_rect.toRect(), pixmap)
            painter.drawText(
                QRectF(rect.x() + 12, rect.center().y() + 12, rect.width() - 24, 40),
                Qt.AlignmentFlag.AlignHCenter | Qt.AlignmentFlag.AlignTop,
                self._placeholder,
            )
        painter.restore()


class ElidedLabel(QLabel):
    """单行路径标签：按宽度中间省略，完整内容放在 tooltip 里。"""

    def __init__(self, text: str = "", parent: QWidget | None = None) -> None:
        super().__init__(parent)
        self._full_text = text
        self.setSizePolicy(QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Fixed)
        self.setToolTip(text)
        self.setText(text)

    def set_full_text(self, text: str) -> None:
        self._full_text = text or ""
        self.setToolTip(self._full_text)
        self._refresh()

    def full_text(self) -> str:
        return self._full_text

    def resizeEvent(self, event) -> None:  # noqa: N802
        super().resizeEvent(event)
        self._refresh()

    def _refresh(self) -> None:
        width = max(10, self.width() - 2)
        self.setText(
            self.fontMetrics().elidedText(
                self._full_text, Qt.TextElideMode.ElideMiddle, width
            )
        )


def hline() -> Divider:
    return Divider()


def meta_row(label_text: str) -> tuple[QWidget, QLabel]:
    """预览区“标签 / 值”一行。"""
    container = QWidget()
    row = QHBoxLayout(container)
    row.setContentsMargins(0, 0, 0, 0)
    row.setSpacing(8)
    label = QLabel(label_text)
    label.setObjectName("hint")
    label.setFixedWidth(52)
    value = QLabel("—")
    value.setObjectName("muted")
    value.setTextInteractionFlags(Qt.TextInteractionFlag.TextSelectableByMouse)
    value.setSizePolicy(QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Preferred)
    row.addWidget(label)
    row.addWidget(value, 1)
    return container, value


def make_section_title(text: str) -> QLabel:
    label = QLabel(text)
    label.setObjectName("sectionTitle")
    return label


def size_hint(widget: QWidget) -> QSize:
    return widget.sizeHint()
