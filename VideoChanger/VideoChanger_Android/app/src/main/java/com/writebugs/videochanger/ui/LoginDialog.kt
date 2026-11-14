package com.writebugs.videochanger.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.writebugs.videochanger.bili.QrLogin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * 扫码登录对话框：展示 B站 二维码，轮询直到确认。
 * 成功后把 SESSDATA 交给调用方保存。
 */
@Composable
fun LoginDialog(
    onDismiss: () -> Unit,
    onSuccess: (String) -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var status by remember { mutableStateOf("正在生成二维码…") }
    var expired by remember { mutableStateOf(false) }
    var generation by remember { mutableStateOf(0) }
    var savedPath by remember { mutableStateOf<String?>(null) }

    fun saveToGallery(image: Bitmap) {
        val name = "bili_login_qr_${System.currentTimeMillis()}.png"
        val values = android.content.ContentValues().apply {
            put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, name)
            put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(
                android.provider.MediaStore.Images.Media.RELATIVE_PATH,
                "Pictures/VideoChanger",
            )
        }
        val resolver = context.contentResolver
        val uri = runCatching {
            resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        }.getOrNull()
        if (uri != null) {
            runCatching {
                resolver.openOutputStream(uri)?.use { out ->
                    image.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
            }
            savedPath = "Pictures/VideoChanger/$name"
            android.widget.Toast.makeText(
                context,
                "二维码已保存到相册",
                android.widget.Toast.LENGTH_SHORT,
            ).show()
        } else {
            savedPath = "保存失败，请重试"
            android.widget.Toast.makeText(
                context,
                "保存失败，请重试",
                android.widget.Toast.LENGTH_SHORT,
            ).show()
        }
    }

    LaunchedEffect(generation) {
        bitmap = null
        expired = false
        status = "正在生成二维码…"
        val login = QrLogin()
        val session = try {
            withContext(Dispatchers.IO) { login.generate() }
        } catch (exc: Exception) {
            status = "生成失败：${exc.message}"
            return@LaunchedEffect
        }

        bitmap = withContext(Dispatchers.Default) { runCatching { qrBitmap(session.url) }.getOrNull() }
        status = "用 B站 App 扫一扫，并在手机上确认"

        repeat(90) {
            delay(2000)
            val result = withContext(Dispatchers.IO) {
                runCatching { login.poll(session.key) }.getOrDefault(QrLogin.Poll.Failed("网络错误"))
            }
            when (result) {
                is QrLogin.Poll.Success -> {
                    status = "登录成功"
                    android.widget.Toast.makeText(
                        context,
                        "登录成功",
                        android.widget.Toast.LENGTH_SHORT,
                    ).show()
                    onSuccess(result.sessdata)
                    onDismiss()
                    return@LaunchedEffect
                }
                is QrLogin.Poll.Scanned -> status = "已扫码，请在手机上点「确认登录」"
                is QrLogin.Poll.Expired -> {
                    status = "二维码已过期"
                    expired = true
                    return@LaunchedEffect
                }
                is QrLogin.Poll.Failed -> status = "失败：${result.message}"
                QrLogin.Poll.Waiting -> Unit
            }
        }
        status = "等待超时，请重试"
        expired = true
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("扫码登录 B站") },
        text = {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "登录二维码",
                        modifier = Modifier.size(220.dp),
                    )
                }
                Text(status, style = MaterialTheme.typography.bodyMedium)
                savedPath?.let {
                    Text(
                        "已保存到 $it\n打开 B站 → 扫一扫 → 相簿，选这张图",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        dismissButton = {
            bitmap?.let { image ->
                TextButton(onClick = { saveToGallery(image) }) { Text("存二维码到相册") }
            }
        },
        confirmButton = {
            if (expired) {
                TextButton(onClick = { generation++ }) { Text("刷新二维码") }
            } else {
                TextButton(onClick = onDismiss) { Text("取消") }
            }
        },
    )
}

private fun qrBitmap(text: String, size: Int = 512): Bitmap {
    val matrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap.setPixel(x, y, if (matrix.get(x, y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
        }
    }
    return bitmap
}
