package com.writebugs.videochanger.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.writebugs.videochanger.core.Storage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(activity: android.app.Activity) {
    var granted by remember { mutableStateOf(Storage.hasAllFiles()) }
    var showAbout by remember { mutableStateOf(false) }
    val autoStart = activity.intent?.getBooleanExtra("auto_start", false) ?: false
    val appLabel = remember(activity) {
        activity.applicationInfo.loadLabel(activity.packageManager).toString()
    }

    // Android 13+ 需要运行时授予通知权限，否则前台服务的进度通知不显示
    val notifLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
    ) { }
    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            val ok = androidx.core.content.ContextCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.POST_NOTIFICATIONS,
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!ok) notifLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            appLabel,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            "B站视频下载",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { showAbout = true }) { Text("关于") }
                },
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            if (!granted) {
                AllFilesBanner(
                    onGrant = { Storage.requestAllFiles(activity) },
                    onRecheck = { granted = Storage.hasAllFiles() },
                )
            }
            DownloadScreen(
                initialUrl = activity.intent?.getStringExtra("video_url"),
                autoStart = autoStart,
            )
        }
    }

    if (showAbout) {
        AboutDialog(onDismiss = { showAbout = false })
    }
}

@Composable
private fun AllFilesBanner(onGrant: () -> Unit, onRecheck: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.primaryContainer) {
        Column(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "下载与转存需要「所有文件访问」权限（写入 Download / Movies 目录）",
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onGrant) { Text("去授权") }
                OutlinedButton(onClick = onRecheck) { Text("已授权，重试") }
            }
        }
    }
}
