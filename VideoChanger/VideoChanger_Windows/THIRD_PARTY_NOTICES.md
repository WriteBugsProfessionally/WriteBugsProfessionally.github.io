# 第三方组件与许可声明 / Third-Party Notices

本项目**不以任何形式分发** FFmpeg、yt-dlp 或其他视频下载/转码二进制。
这些程序需要使用者自行安装（详见 README）。因此本项目不承担它们的
许可义务，也不对其行为负责。

本项目自身的代码以 MIT 许可证发布，见 `LICENSE`。

---

## 1. PySide6 / Qt / shiboken6 — LGPLv3（随发布包分发）

发布版 Windows 构建中，`_internal/` 目录包含以下 LGPLv3 组件：

| 组件 | 版本 | 许可证 |
| --- | --- | --- |
| PySide6 (PySide6_Essentials) | 6.12.0 | LGPLv3 / GPLv3 / 商业许可（本项目按 LGPLv3 使用） |
| shiboken6 | 6.12.0 | LGPLv3 |
| Qt 6 运行库（Qt6Core / Qt6Gui / Qt6Widgets 等） | 6.12.0 | LGPLv3 |

**合规说明**

- 这些库以**动态链接**方式使用，DLL / PYD 均为 `_internal/` 下的**独立文件**，
  使用者可以自行替换为修改过的、接口兼容的同名库文件，无需重新编译本项目。
- 完整许可证文本见 [`licenses/LGPL-3.0.txt`](licenses/LGPL-3.0.txt) 与
  [`licenses/GPL-3.0.txt`](licenses/GPL-3.0.txt)（LGPLv3 以 GPLv3 为基础）。
- Qt / PySide6 源码获取地址：<https://download.qt.io/official_releases/qt/>
  以及 <https://code.qt.io/cgit/pyside/pyside-setup.git/>
- 本项目对 PySide6 / Qt 未做任何修改。

## 2. Python 运行时 — PSF License（随发布包分发）

发布版包含 Python 3.14 解释器（`python314.dll`、`base_library.zip` 等），
版权归 Python Software Foundation 所有，许可证文本见
<https://docs.python.org/3/license.html>。

## 3. 未分发的可选外部程序

以下程序**不在本仓库、也不在发布包中**，由使用者按 README 自行安装，
本项目仅通过命令行调用用户环境中的同名可执行文件：

| 程序 | 许可证 | 说明 |
| --- | --- | --- |
| FFmpeg | GPLv3（gyan.dev 等构建通常启用 `--enable-gpl`） | <https://ffmpeg.org/> |
| yt-dlp | Unlicense | <https://github.com/yt-dlp/yt-dlp> |

如果你自行把这两个程序放进 `bin/` 并分发给他人，**分发行为由你自己负责**，
需要遵守 FFmpeg 的 GPLv3 条款（随附许可证文本并提供对应源码）。

## 4. 开发/构建期使用的工具（不入发布包）

PyInstaller（GPLv2 with exception）、setuptools、pip 等仅用于构建，不随发布包分发。
