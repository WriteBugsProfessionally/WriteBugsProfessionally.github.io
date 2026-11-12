package com.writebugs.videochanger.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/** 关于：版本、作者、许可、隐私说明。 */
@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val version = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "?"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("关于 VideoChanger") },
        text = {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("版本 $version")
                Text("作者：WriteBugsProfessionally")
                Text("许可：MIT（本项目代码）")

                HorizontalDivider()

                Text(
                    "数据与隐私",
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    "下载和转封装都在本机完成。B站 API 只用来取播放地址；" +
                        "SESSDATA 仅保存在 App 私有目录，可在界面上关闭「记住登录状态」一键清除，不会上传到任何服务器。",
                    style = MaterialTheme.typography.bodySmall,
                )

                Text(
                    "技术说明",
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    "转封装使用系统 MediaExtractor / MediaMuxer，纯流复制不重编码；" +
                        "APK 内不含 ffmpeg，也不含 Python。",
                    style = MaterialTheme.typography.bodySmall,
                )

                Text(
                    "开源组件",
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    "OkHttp、ZXing、AndroidX、Jetpack Compose（均为 Apache-2.0）",
                    style = MaterialTheme.typography.bodySmall,
                )

                HorizontalDivider()

                Text(
                    "本项目与哔哩哔哩无关联，仅供下载你有权访问的内容。",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("关闭") }
        },
    )
}
