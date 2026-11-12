# SameFileChecker

按**内容**而不是文件名，找出机械硬盘上重复的图片和视频。

很多“重复文件”只是名字不同：`IMG_1024.jpg`、`IMG_1024 - 副本.jpg`、`川西旅行_照片(1).jpg` 可能是同一张照片。SameFileChecker 只比对字节内容，名字、路径、修改时间都不影响判断。

Logo 沿用头像的黑白像素分割风格：左侧纸白、右侧墨黑，中间是像素放大镜与等号，表示“查找相同内容”。

> **公开源码版说明**：这个目录用于上传 GitHub，不含示例截图、演示素材、Windows 壁纸和 FFmpeg 二进制；包含源码、MIT License 和第三方许可证。

## 功能

- **字节级精确查重**：全文 BLAKE2b-256 校验，确认内容完全一致才归为一组
- **三阶段读取**：128 KB 文件头 → 中段 + 尾部 → 全文，尽量少读机械硬盘
- **大小预筛**：文件大小唯一的直接排除，连打开都不需要
- **哈希缓存**：SQLite 记录已算过的指纹，第二次扫描跳过未修改的文件
- **硬链接折叠**：同一个文件的多个硬链接不会被当成“可释放空间”
- **安全删除**：默认移入 Windows 回收站（可还原），也可移动到隔离文件夹
- **至少保留一份**：界面层面禁止把一整组都勾选删除
- **空文件夹清理**：独立模块，列出没有任何文件的空目录，勾选后移入回收站或直接删除
- **预览**：图片直接预览；视频用 FFmpeg 抽首帧（可选）
- **报告导出**：CSV（Excel 友好，带 BOM）和 JSON
- **命令行模式**：不启动界面，方便脚本化和排障

## 为什么适合机械硬盘

一块 4 TB 的机械硬盘如果直接对每个文件做全文哈希，可能要读几百 GB、跑几个小时。这个程序把读取拆成三级：

| 阶段 | 读取内容 | 目的 |
| --- | --- | --- |
| 1 | 前 128 KB（小文件直接全文） | 淘汰绝大多数“只是大小相同”的文件 |
| 2 | 中段 128 KB + 尾部 128 KB | 进一步区分头部长得一样的文件（例如同一相机/同一编码器） |
| 3 | 全文 | 只有仍然撞车的文件才读完整内容，确保结果绝对可靠 |

配合“按大小分组”，真正需要读内容的通常只是候选文件的一小部分。重复扫描时还会命中缓存，未修改的文件一个字节都不用读。

## 界面

扫描完成后：

1. 左侧按“可释放空间”排序列出重复组；
2. 中间列出组内文件，勾选要删除的副本；
3. 右侧预览图片/视频首帧，可直接打开所在位置；
4. 底部按保留策略自动勾选，也可以手工调整；
5. 点“移入回收站”，确认后删除，随时可从回收站还原。

文件列表里 **勾选 = 删除，不勾选 = 保留**。这个说明固定显示在列表上方，复选框列头也标着“删除”，底部会实时显示“将删除 N 个文件”。

默认保留策略是“保留修改时间最早的”；修改时间相同则优先保留文件名更短的那份（更像原件）。

## 空文件夹清理

主界面右上角的 **“空文件夹”** 按钮会打开独立清理窗口：

它的规则：

- 一个目录“空”= 里面没有任何文件，且所有子目录也都是空的；
- 只列出**最外层**空目录（`A/B` 都空时只列 `A`，删除 `A` 会连同 `B` 一起清掉）；
- **不会列出、也不会删除你选择的扫描根目录本身**；
- 默认跳过隐藏/系统文件夹、`node_modules` 等排除目录；这些目录即使为空也会让父目录不算空，避免误删；
- 符号链接、联接点、无权限目录同样会让父目录不算空；
- 勾选后可选“移入回收站”或“直接删除（空文件夹）”；
- 空文件夹列表同样是“勾选 = 删除，不勾选 = 保留”；
- 删除前会**再次检查**目标目录确实没有文件，扫描后新放进来的文件不会被误删。

## 目录结构

```
SameFileChecker/
├── src/
│   ├── main.py               # 入口：默认 GUI，--cli 进入命令行
│   └── sfc/
│       ├── scanner.py        # 重复文件查重引擎
│       ├── hashing.py        # BLAKE2b 分阶段摘要
│       ├── cache.py          # SQLite 哈希缓存
│       ├── emptyfolders.py   # 空文件夹扫描/删除引擎
│       ├── models.py         # 数据模型
│       ├── fileops.py        # 回收站 / 隔离区 / 资源管理器
│       ├── report.py         # CSV / JSON
│       ├── cli.py            # 命令行模式
│       ├── config.py         # 设置与预设
│       └── ui/               # PySide6 界面
│           └── empty_folder_dialog.py
├── tests/                    # 引擎单元测试
├── tools/                    # 图标、演示数据、截图、UI 冒烟测试
├── assets/                   # 图标与版本信息
├── build.spec                # PyInstaller 配置
├── build.bat                 # 一键打包
├── requirements.txt
└── README.md
```

## 开发运行

需要 Python 3.10+（在 Python 3.14 上验证）。

```bat
python -m venv .venv
.venv\Scripts\python.exe -m pip install -r requirements.txt
.venv\Scripts\python.exe src\main.py
```

国内网络慢时可以用镜像：

```bat
.venv\Scripts\python.exe -m pip install -i https://mirrors.aliyun.com/pypi/simple/ -r requirements.txt
```

## 打包

```bat
build.bat
```

产物在 `dist\SameFileChecker\`：

```
dist\SameFileChecker\
├── SameFileChecker.exe
└── _internal\              # Python / PySide6 运行时
```

整个 `dist\SameFileChecker\` 目录可以直接复制到别的电脑使用（Windows 10/11 64 位）。

> 程序没有做代码签名，Windows SmartScreen 可能提示“未知发布者”，选择“更多信息 → 仍要运行”即可。

**视频预览**：程序不内置 FFmpeg（体积太大）。需要预览视频首帧时，任选一种方式：

- 把 `ffmpeg.exe` 放到 `SameFileChecker.exe` 同级的 `bin\` 目录，或
- 安装 FFmpeg 并加入 PATH。

没有 FFmpeg 不影响查重，只是视频预览显示占位图标。

**程序数据位置**：哈希缓存在 `%LOCALAPPDATA%\SameFileChecker\hash_cache.db`，崩溃日志在 `%LOCALAPPDATA%\SameFileChecker\error.log`。扫描过程只读文件，不会上传任何内容。

## 使用建议

- **机械硬盘**：并行线程数保持 2–4。太多线程会让磁头来回寻道，反而更慢。
- **首次扫描**：会读候选文件的头部；如果重复很多，还会读完整文件。4 TB 硬盘第一次跑几十分钟是正常的。
- **后续扫描**：缓存命中后通常只花枚举目录的时间。
- **文件类型**：默认只扫图片 + 视频。要查文档/压缩包等，在设置里改成“所有文件”。
- **最小大小**：默认 1 KB，避免把空文件、缩略图缓存之类的小文件卷进来。
- 扫描前建议关闭正在写入素材的下载/同步软件，避免文件在扫描过程中变化（程序会跳过这类文件并记录）。

## 命令行模式

```bat
:: 扫描并打印结果
.venv\Scripts\python.exe src\main.py --cli D:\Photos D:\Videos

:: 导出报告
.venv\Scripts\python.exe src\main.py --cli D:\Photos --csv 报告.csv --json 报告.json

:: 只扫图片，最小 100 KB，4 线程
.venv\Scripts\python.exe src\main.py --cli D:\Photos --exts jpg,png,heic --min-size 100KB --workers 4
```

常用参数：

| 参数 | 说明 |
| --- | --- |
| `--all-files` | 扫描所有类型（默认图片/视频/音频） |
| `--exts jpg,png,mp4` | 自定义扩展名 |
| `--min-size 10MB` | 最小文件大小，支持 KB/MB/GB |
| `--workers 4` | 并行读线程数 |
| `--no-cache` | 本次不使用缓存 |
| `--csv` / `--json` | 导出报告 |
| `--quiet` | 不打印进度 |

退出码：`0` 成功；`2` 没有可扫描目录；`130` 被取消。命令行模式不会删除文件。

## 测试

```bat
:: 引擎单元测试（重复文件）
.venv\Scripts\python.exe tests\test_engine.py

:: 引擎单元测试（空文件夹）
.venv\Scripts\python.exe tests\test_emptyfolders.py

```

公开源码包不附带演示素材和示例截图。

## 已知限制

- 只做**字节级**查重：重新压缩、缩放、转码后的“看起来一样”的图片/视频不会被判定为重复。
- HEIC/RAW 等格式能否预览取决于 Qt 的解码插件；不能预览不影响查重。
- 回收站依赖 Windows：网络共享、部分可移动磁盘或关闭了回收站的分区可能无法还原；重要文件建议在设置中改用“隔离文件夹”。
- 硬链接只算一份占用空间，因此不会出现在“可释放空间”里。
- 空文件夹清理只处理目录，不会删除文件；隐藏/系统/排除目录、符号链接和无权限目录会让父目录不算空，因此可能少列一些目录（这是为了安全）。
- 扫描是只读的；只有你在确认对话框中明确同意后，程序才会移动/删除文件。

## 实现要点

- 哈希：`hashlib.blake2b`，头/中段/尾用 128 位摘要，全文用 256 位；所有摘要混入文件大小。
- 缓存键：规范化路径 + 文件大小 + 修改时间（100 ns），任一变化就重算。
- 并发：`ThreadPoolExecutor`，哈希和读盘释放 GIL，UI 不卡；取消事件在每个 4 MB 块检查一次。
- 长路径：超过 240 字符时自动加 `\\?\` 前缀打开，界面仍显示普通路径。
- 枚举：`os.scandir` 递归，跳过符号链接/junction、隐藏系统目录、回收站和系统目录。
- 删除：`SHFileOperationW(FOF_ALLOWUNDO)` 走系统回收站；失败的文件保持原样并逐个报告。

## 许可证

代码使用 MIT License，见 `LICENSE`；PySide6/Qt、Python、PyInstaller 的许可见 `THIRD_PARTY_NOTICES.md` 和 `LICENSES/`。
