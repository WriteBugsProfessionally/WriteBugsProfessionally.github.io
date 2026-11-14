# VideoChanger Android

手机版只做**一件事**：

> **粘贴 B站 链接 → 下载 → 无损转成 MP4，存到 `Movies/VideoChanger`。**

纯 Kotlin 实现，**没有 Python、没有 ffmpeg 二进制，发布 APK 约 7 MB**。

## 功能

| 功能 | 说明 |
| --- | --- |
| 分享链接 | 支持整段分享文案、`b23.tv` / `bili2233.cn` 短链、`av` 号、分 P |
| 扫码登录 | 用 B站 App 扫二维码拿 SESSDATA（不输密码），可选是否保存在本机 |
| 画质选择 | 自动 / 1080P / 720P / 480P（实际受账号权限限制） |
| 多线程下载 | 6 连接 Range 分片 + 并发测速选最快 CDN |
| 转封装 | `MediaExtractor` + `MediaMuxer`，纯流复制不重编码 |
| 打开目录 | 下载目录 / 输出目录一键跳转 |
| 实时速度 | 界面显示 `x.xx MB/s` |
| **前台服务** | 下载/转封装跑在 `dataSync` 前台服务里，**切后台、锁屏都会继续**，通知栏显示进度 |

## 技术栈

```
Kotlin + Jetpack Compose         界面
      ↓
OkHttp + B站 API（WBI 签名）      拿 dash 流地址
      ↓                         ↓
  video.m4s  audio.m4s        Download/VideoChanger/_dl/<标题>/
      ↓
MediaExtractor + MediaMuxer   纯流复制 → MP4
      ↓
Movies/VideoChanger/<标题>.mp4
```

## 为什么没有「缓存转 MP4」

早期版本做过，**在真机上验证后放弃**：

> Android 11+ 系统禁止第三方 App 读取其他 App 的 `/sdcard/Android/data/` 目录。
> 实测 `MANAGE_EXTERNAL_STORAGE`（含设置里手动开启）、`READ_EXTERNAL_STORAGE`、
> `targetSdk 29 + requestLegacyExternalStorage` 全部被系统拒绝 —— 不是权限没给，是这条路被封死。

同类工具（MT管理器等）也是靠 Shizuku / Root 才能进那个目录。相比之下「在线下载」既绕开了限制，
也不需要用户装额外的工具，所以直接砍掉了缓存转换功能。

## 本机环境

| 组件 | 版本 / 路径 |
| --- | --- |
| JDK | `D:\Apps\Java\jdk-21`（Studio 自带 JDK 25 太新，AGP 8.7 不支持） |
| Gradle | 8.9，`D:\Apps\gradle-8.9` |
| Android SDK | `C:\Users\junha\AppData\Local\Android\Sdk` |
| AGP / Kotlin | 8.7.3 / 2.0.21 |
| compileSdk / minSdk | 35 / 26 |

## 构建

```powershell
$env:JAVA_HOME = 'D:\Apps\Java\jdk-21'
cd D:\MyPackage\src\com\WriteBugsProfessionally\SoftWare\VideoChenger_Android
D:\Apps\gradle-8.9\bin\gradle.bat :app:assembleDebug
```

产物：`app\build\outputs\apk\debug\app-debug.apk`（11.8 MB）

## 调试入口（免手打链接）

```powershell
# 指定链接并自动开始下载+转换
adb shell am start -n com.writebugs.videochanger/.MainActivity `
  -e video_url "BV1GJ411x7h7" --ez auto_start true
```

## 真机验证记录

| 设备 | 系统 | 结果 |
| --- | --- | --- |
| vivo V1986A | Android 12 | 480P 下载 + 转换通过 |
| iQOO Z9 Turbo | **Android 16** | **1080P：69.7 MB 视频流 32 秒下完（≈2.2 MB/s）+ 5.4 MB 音频，输出 75.2 MB MP4（212.3 秒，h264+aac）** |

## 已知限制

1. **接口易变**：B站 的 WBI 签名和 dash 接口随时可能改，改了就得出新版（这也是桌面版用 yt-dlp 的原因）
2. **画质受账号限制**：未登录一般 480P/720P，1080P 及以上需要 SESSDATA
3. **CDN 波动**：B站 会给多条线路（含 P2P 的 `mcdn.bilivideo.cn`），速度差异可达 8 倍
4. **SESSDATA 是凭证**：存在 App 私有目录，可在界面上一键关闭"记住登录状态"立即清除
5. **前台服务类型**：用的是 `dataSync`，Android 14+ 需要 `FOREGROUND_SERVICE_DATA_SYNC` 权限（已声明）
6. **短链有时效**：`b23.tv` 任意短链可能过期，过期后需要重新分享，或直接使用 BV / av 号

## 免责声明

仅用于下载你有权访问的内容（自己的投稿、已获授权的素材、个人离线观看）。
请勿用于传播他人作品或商业用途。本项目与哔哩哔哩无关联。

---

## 关于

- **作者**：WriteBugsProfessionally
- **许可**：本项目代码为 [MIT](LICENSE)；OkHttp / ZXing / AndroidX / Compose 均为 Apache-2.0，见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)
- **版本**：1.0.1（versionCode 7）
- **说明**：本项目与哔哩哔哩无关联，仅用于下载你有权访问的内容

App 内右上角 **「关于」** 可以看到版本、作者、隐私说明和开源组件列表。

## 更新记录

### 1.0.1

- 支持 B站 分享文案、`b23.tv` / `bili2233.cn` 短链、`av` 号、分 P
- 短链失效、番剧/直播/动态/专栏等不支持的类型给出明确提示
- 修复下载完成后通知栏残留进度通知
- 修复取消下载被误报为“部分失败”
- 不同尺寸设备自适应布局；浅色主题下状态栏/导航栏使用深色图标
- 通知小图标改为透明底白色矢量，去掉重复的大图标

### 1.0.0

- 只保留「B站视频下载」一个功能，移除缓存转换（Android 11+ 系统禁止读取其他 App 的 `Android/data`）
- 扫码登录（B站 官方二维码接口），SESSDATA 可选保存 / 一键清除
- 6 线程分片下载 + 并发测速选最快 CDN
- 前台服务保活，切后台/锁屏继续下载，通知栏显示进度
- 黑白分割新图标（自适应图标 + 多尺寸 ICO）
- 多 P 视频**复选框批量下载**（全选 / 清空，逐个下载+转换，失败不中断）
- 输出目录可配置（SAF 选目录，记住上次选择）
- 「转完删除源分片」开关（省一半空间）
- 下载可取消（界面按钮 + 通知栏「取消」）
- CDN 线路自动重试（某条线路 403/超时就换下一条）
- 通知栏使用自绘图标
