# 第三方组件声明 / Third-party notices

本项目的**自身代码**采用 [MIT License](LICENSE)。
APK 中打包了以下开源组件，均以 Apache License 2.0 授权：

| 组件 | 用途 | 许可 |
| --- | --- | --- |
| [OkHttp](https://square.github.io/okhttp/) 4.12.0 | HTTP 客户端（B站 API、分片下载） | Apache-2.0 |
| [ZXing Core](https://github.com/zxing/zxing) 3.5.3 | 生成登录二维码 | Apache-2.0 |
| [AndroidX](https://developer.android.com/jetpack/androidx)（Core / Activity / Lifecycle） | 基础库 | Apache-2.0 |
| [Jetpack Compose](https://developer.android.com/jetpack/compose)（UI / Material3） | 界面 | Apache-2.0 |
| [Kotlin](https://kotlinlang.org/) 2.0.21 | 语言与标准库 | Apache-2.0 |

## 平台 API

音视频转封装使用 Android 系统自带的 `MediaExtractor` / `MediaMuxer`
（`android.media` 包），不打包任何 ffmpeg 或其他原生二进制，因此**不涉及
GPL/LGPL 的源码开放义务**。

## 未打包的内容

- **ffmpeg**：未使用、未打包
- **Python / yt-dlp**：未使用、未打包（早期版本用过，已移除）

## Apache License 2.0 全文

https://www.apache.org/licenses/LICENSE-2.0

```
Copyright 2009-2024 The respective owners.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
