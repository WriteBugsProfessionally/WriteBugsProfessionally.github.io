# VideoChanger

基于 FFmpeg 的 Windows 桌面小工具，三个功能：

| 页签 | 功能 |
| --- | --- |
| **TS 合并** | 把连续的 `.ts` 分片按顺序无损拼接成 MP4；也支持 `m3u8` 播放列表（含 AES-128 加密的 HLS 缓存） |
| **B站下载** | 输入 B站链接，用 yt-dlp 下载并自动合并为 MP4（支持 SESSDATA 登录画质） |
| **B站缓存转 MP4** | 把客户端/APP 缓存目录中的 `video.m4s` + `audio.m4s`（或 Windows 客户端 `<cid>-<page>-<id>.m4s`）无损封装为 MP4 |

所有外部进程都在后台线程中运行，界面不会卡死；随时可以点“取消”终止任务。

> ## 免责声明
>
> - 本工具**仅用于处理你本机已拥有、且你有权访问**的媒体文件（例如你自己录制的录像、
>   你自己账号已缓存的视频）。**请勿**用于下载、转存、传播你无权使用的内容。
> - 本项目与哔哩哔哩（bilibili）及其关联公司**没有任何关系**，不是官方产品，
>   也未获得其授权或认可。
> - 使用本工具即表示你同意自行遵守所在地区法律、以及你所使用平台的服务条款。
>   因使用本工具产生的一切后果由使用者自行承担，作者不承担任何责任。
> - 软件按 “AS IS” 提供，不附带任何明示或暗示的担保。详见 [LICENSE](LICENSE)。

## 依赖

**本仓库与 Releases 中的发布包都不包含 FFmpeg 和 yt-dlp**，需要你自行安装
（这样本项目就不涉及 GPL 二进制的再分发）。程序会按以下顺序查找它们：

1. 程序同级目录下的 `bin\`（打包版）或项目根目录的 `bin\`（源码运行）
2. 系统 `PATH`

安装方式（任选其一）：

```powershell
winget install Gyan.FFmpeg          # 安装 ffmpeg
# yt-dlp：从 https://github.com/yt-dlp/yt-dlp/releases 下载 yt-dlp.exe 放进 PATH
```

或者直接下载后放进程序目录的 `bin\`：

```
VideoChanger\
├── VideoChanger.exe
└── bin\
    ├── ffmpeg.exe
    └── yt-dlp.exe
```

> 只合并本地文件时，其实只需要 `ffmpeg`；`yt-dlp` 只有「B站下载」页用得到。

## 下载

到 [Releases](../../releases) 下载 `VideoChanger-win64.zip`，解压后双击 `VideoChanger.exe`。

- Windows 10 1809+ / Windows 11，x64
- 不需要安装 Python、不需要 VC++ 运行库，绿色免安装
- 未签名，首次运行可能被 SmartScreen 拦截（“更多信息”→“仍要运行”）

## 项目结构

```
VideoChanger/
├── src/
│   ├── core.py          # 路径解析、命令构造、缓存扫描（无 GUI，便于测试）
│   └── main.py          # PySide6 界面
├── tests/
│   ├── smoke_test.py    # 端到端：合成媒体 + 真实调用 ffmpeg
│   └── worker_test.py   # 后台线程/进度/取消
├── licenses/            # LGPL-3.0、GPL-3.0 全文
├── build.spec           # PyInstaller 配置
├── build.bat            # 一键打包
├── requirements.txt     # 锁定版本的依赖
├── LICENSE              # MIT（本项目自身代码）
├── THIRD_PARTY_NOTICES.md
└── bin/                 # 不入库；自行放置 ffmpeg.exe / yt-dlp.exe
```

## 开发运行

需要 Python 3.10+（已在 3.14 上验证）。

```bat
python -m venv .venv
.venv\Scripts\python.exe -m pip install -r requirements.txt
.venv\Scripts\python.exe src\main.py
```

国内网络较慢时可加镜像：

```bat
.venv\Scripts\python.exe -m pip install -i https://mirrors.aliyun.com/pypi/simple/ -r requirements.txt
```

### 测试

```bat
.venv\Scripts\python.exe -I tests\smoke_test.py
set QT_QPA_PLATFORM=offscreen
.venv\Scripts\python.exe tests\worker_test.py
```

`smoke_test.py` 会合成 TS 分片、m4s 缓存、加密 HLS 缓存并真实调用 ffmpeg 验证。
它需要 `ffprobe` 来校验时长；没装时只会跳过时长断言，不会判失败。

## 打包

先把 `ffmpeg.exe` / `yt-dlp.exe` 放进项目根目录的 `bin\`（可选），然后：

```bat
build.bat
```

或手动执行（注意用 `-m PyInstaller`，不要直接调 `Scripts\pyinstaller.exe`——
虚拟环境被整体移动过时，那个启动器里的绝对路径会失效）：

```bat
.venv\Scripts\python.exe -m PyInstaller build.spec --noconfirm --clean
```

产物在 `dist\VideoChanger\`：

```
dist\VideoChanger\
├── VideoChanger.exe
├── _internal\            # Python / PySide6 运行时
├── bin\                  # 你在本地放进去的 ffmpeg/yt-dlp（如有）
└── THIRD_PARTY_NOTICES.md
```

## 使用说明

### 默认保存位置（三个页签通用）

每个页签都有「默认保存位置」一行：可以直接填路径（支持 `~`）、点「浏览…」选目录，
或点「恢复默认」还原。**改动立即生效并持久保存**，下次启动仍是改过的位置。

| 页签 | 默认值 | 说明 |
| --- | --- | --- |
| TS 合并 | 留空 | 留空 = 保存到第一个文件所在目录；输入 m3u8 时默认放在缓存目录的上一级 |
| B站下载 | `~/Downloads` | 下载文件与临时 cookies 的落地目录 |
| B站缓存转 MP4 | `~/Videos` | 批量转换的输出目录 |

### TS 合并

1. 添加 `.ts` 文件（可多选；也可直接拖入文件或整个文件夹）。也可以直接拖入含 `m3u8` 的缓存文件夹——
   这类文件夹在列表里显示为**一个文件夹条目**（内部自动使用它的 `local.m3u8`）。
   拖**上一级父目录**也可以：程序会往下找出每个视频文件夹，各自成为一个条目。
2. 需要时用“上移 / 下移 / 按文件名排序”调整拼接顺序（自然排序，`第2集` 在 `第10集` 前）。
3. 点“开始合并”→ 直接输出到上方的「默认保存位置」，同名文件自动加 `(2)`、`(3)`，不会覆盖；
   想临时换目录或改名 → 点按钮右侧的 **▾** →「另存为…」。
   列表里有多个播放列表时会**逐个批量合并**（文件名取自各自的文件夹名）。

合并使用流复制（`-c copy`），不重新编码，速度快、无画质损失。只保留视频和音频流，
避免 TS 里的数据流导致 MP4 封装失败。

**加密 HLS（QQ浏览器等缓存）**：这类目录里是 `xxx.m3u8` + 上千个 AES-128 加密的 `.ts`
+ `enc.key`。分片本身是密文，逐个合并必然失败（`Invalid data found`）。
把整个文件夹拖进来，程序会自动选中 `local.m3u8` 并交给 ffmpeg 按播放列表重新封装，
实测 39 分钟 720P 视频约 15 秒完成。

### B站下载

1. 粘贴视频链接（`https://www.bilibili.com/video/BV...`）。
2. 选择输出目录。
3. 1080P 及以上画质需要登录 Cookie：浏览器登录后，在开发者工具 →
   Application → Cookies 中复制 `SESSDATA` 的值，填入输入框。
4. 点击“开始下载”，完成后自动合并为 MP4。

B站接口变化频繁，下载失败时先把 `bin\yt-dlp.exe` 换成最新版。

### B站缓存转 MP4

支持两种缓存格式：

| 来源 | 目录 | 音视频文件 |
| --- | --- | --- |
| 手机 APP / 旧版客户端 | `<视频目录>/64/`、`<视频目录>/80/` | `video.m4s` + `audio.m4s` |
| Windows 客户端 | `C:\Users\<你>\Videos\bilibili\<cid>\` | `<cid>-<page>-<id>.m4s`，`302xx` 为音频（如 `30280`） |

1. 添加 B站缓存文件夹（拖入文件夹或其中任意 `.m4s` 均可）。
2. 程序会递归查找音视频配对；同一视频存在多个清晰度时只取最高清的一份。
3. 选择输出目录，点击“开始转换”。

Windows 客户端的 `.m4s` 开头有 9 字节占位头，程序会自动跳过
（`-f mov -skip_initial_bytes`），不需要手动处理。
标题优先读取缓存里的 `entry.json` / `videoInfo.json`，读不到时使用目录名。

## 常见问题

- **提示“找不到可执行文件：ffmpeg”**：说明没装 ffmpeg。见上面「依赖」，或者把
  `ffmpeg.exe` 放进程序同级的 `bin\`。
- **扫描不到缓存**：确认选的是具体视频目录，而不是上一层的 `bilibili` 根目录。
  Windows 客户端缓存在 `C:\Users\<你>\Videos\bilibili\<cid>\`，文件名形如
  `<cid>-1-30280.m4s`；APP 缓存则要求目录里同时有 `video.m4s` 与 `audio.m4s`。
- **合并 TS 报“输入数据无法识别”**：分片可能是加密的 HLS 密文，改用同目录的
  `.m3u8` 播放列表。
- **合并后没有声音/画面**：源分片编码不一致（例如中途换了分辨率或编码），
  流复制无法补救，需要重新编码。
- **下载画质只有 480P**：未登录。填入有效 SESSDATA 后重试；部分番剧还需要大会员账号。
- **杀毒软件误报**：PyInstaller 打包的程序偶发误报，可自行从源码打包或添加信任。

## 第三方组件与许可

- 本项目自身代码：[MIT](LICENSE)
- PySide6 / Qt：LGPLv3（动态链接，DLL 可替换，全文见 `licenses/`）
- Python 运行时：PSF License
- FFmpeg / yt-dlp：**不随本项目分发**，由使用者自行安装

完整说明见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)。

## English

A small Windows GUI for FFmpeg with three tools: merge `.ts` segments (including
AES-128 encrypted HLS caches via `m3u8`), download videos with yt-dlp, and remux
Bilibili client caches (`video.m4s`/`audio.m4s` or `<cid>-<page>-<id>.m4s`) into MP4.

FFmpeg and yt-dlp are **not bundled** — install them yourself (they are looked up in
the app's `bin\` directory first, then `PATH`). The app itself is MIT licensed.
This project is unofficial and not affiliated with Bilibili. Use it only for media
you own or are authorized to access.

---

## 关于

- **作者**：WriteBugsProfessionally
- **许可**：本项目代码为 [MIT](LICENSE)；FFmpeg / Qt 等第三方组件的授权见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)
- **版本**：0.3.1
- **说明**：本项目与哔哩哔哩无关联，仅用于处理你有权访问的媒体内容

界面菜单 **帮助 → 关于** 里也能看到版本、作者与依赖说明。
