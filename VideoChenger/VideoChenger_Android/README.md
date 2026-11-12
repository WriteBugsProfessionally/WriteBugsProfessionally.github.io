# VideoChanger (Android)

一个轻量的 B站 视频下载器：粘贴链接 → 下载 → 无损转成 MP4。

纯 Kotlin + Jetpack Compose 实现，**不含 ffmpeg、不含 Python、不含任何外部二进制**，APK 约 7 MB。

## 功能

- **下载即转封装**：B站 dash 流下载后，用系统 `MediaExtractor` / `MediaMuxer` 纯流复制成 MP4（不重编码）
- **扫码登录**：用 B站 App 扫二维码获取 SESSDATA，不接触账号密码；可选择是否保存在本机
- **画质选择**：自动 / 1080P / 720P / 480P（实际受账号权限限制）
- **多线程下载**：6 连接 Range 分片 + 并发测速选最快 CDN，线路失败自动换线重试
- **多 P 批量下载**：分P 列表复选框勾选，逐个下载转换，单个失败不影响其他
- **前台服务**：切后台 / 锁屏继续下载，通知栏显示进度并可取消
- **输出目录可配置**：默认 `Movies/VideoChanger`，可通过系统目录选择器更换

## 系统要求

- Android 8.0（API 26）及以上
- **arm64-v8a**（64 位）
- 需要授予「所有文件访问」权限（写入 Download / Movies 目录），以及通知权限（显示下载进度）

## 构建

环境：JDK 17~21、Android SDK（compileSdk 35）、Gradle 8.9。

```bash
# 用 Android Studio 打开本目录，或命令行：
./gradlew :app:assembleDebug        # 调试包
./gradlew :app:assembleRelease      # 发布包（未签名）
```

发布包签名：在项目根目录创建 `keystore.properties`（**不要提交**）：

```properties
storeFile=keystore/release.jks
storePassword=你的密码
keyAlias=你的别名
keyPassword=你的密码
```

没有这个文件时 `assembleRelease` 会产出**未签名** APK，可用 `apksigner` 自行签名。

## 实现说明

```
Kotlin + Jetpack Compose          界面
        │
OkHttp + B站 API（WBI 签名）       取 dash 播放地址
        │
  video.m4s / audio.m4s          6 线程分片下载 → Download/VideoChanger/_dl/
        │
MediaExtractor + MediaMuxer      纯流复制 → MP4
        │
  Movies/VideoChanger/*.mp4
```

几个值得说明的点：

- **WBI 签名**：B站 的播放地址接口需要 `w_rid` 签名（mixinKey = 打乱后的 img_key + sub_key，再对参数排序做 MD5），实现在 `bili/Wbi.kt`
- **不用 ffmpeg**：只做流复制，系统自带的 `MediaExtractor` / `MediaMuxer` 足够；好处是 APK 小、没有 GPL 义务
- **不用 yt-dlp**：早期版本用 Chaquopy 打包 Python + yt-dlp（43 MB），后来发现单功能场景完全可以用 Kotlin 直连 API，于是砍掉了整层
- **为什么没有「B站缓存转 MP4」**：Android 11+ 禁止第三方 App 读取其他 App 的 `Android/data/` 目录，实测 `MANAGE_EXTERNAL_STORAGE`、`READ_EXTERNAL_STORAGE`、`targetSdk 29 + requestLegacyExternalStorage` 全部被系统拒绝，所以放弃了这个功能

## 已知限制

- B站 接口随时可能变（WBI 签名、dash 字段），变了就需要更新代码
- 部分视频的部分流会返回 HTTP 403（版权/区域限制），不是代码问题
- 只支持 arm64：32 位老机型无法安装
- 需要「所有文件访问」权限，国内应用商店通常不批这类权限，因此定位是自行构建 / 侧载

## 许可

本项目代码为 [MIT](LICENSE)。第三方组件（OkHttp / ZXing / AndroidX / Compose，均为 Apache-2.0）见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)。

## 免责声明

本项目与哔哩哔哩无关联。请仅用于下载你有权访问的内容（自己的投稿、已获授权的素材、个人离线观看），
不要用于传播他人作品或商业用途。使用本工具产生的一切后果由使用者自行承担。
