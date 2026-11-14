package com.writebugs.videochanger.ui

import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.writebugs.videochanger.bili.BiliApi
import com.writebugs.videochanger.bili.PageInfo
import com.writebugs.videochanger.core.Storage
import com.writebugs.videochanger.service.DownloadService
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val QUALITY_OPTIONS = listOf(
    120 to "自动",
    80 to "1080P",
    64 to "720P",
    32 to "480P",
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DownloadScreen(initialUrl: String? = null, autoStart: Boolean = false) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("vc", 0) }
    val scope = rememberCoroutineScope()
    val state by DownloadService.state.collectAsState()

    var url by rememberSaveable { mutableStateOf(initialUrl ?: "") }
    var rememberSession by rememberSaveable {
        mutableStateOf(prefs.getBoolean("remember_sessdata", true))
    }
    var sessdata by rememberSaveable {
        mutableStateOf(
            if (prefs.getBoolean("remember_sessdata", true)) {
                prefs.getString("sessdata", "").orEmpty()
            } else {
                ""
            },
        )
    }
    var qn by rememberSaveable { mutableIntStateOf(120) }
    var outDir by rememberSaveable {
        mutableStateOf(
            prefs.getString("out_dir", "").orEmpty().ifBlank {
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                    "VideoChanger",
                ).absolutePath
            },
        )
    }
    var deleteSource by rememberSaveable { mutableStateOf(prefs.getBoolean("delete_src", false)) }
    var showLogin by remember { mutableStateOf(false) }
    var autoStarted by rememberSaveable { mutableStateOf(false) }
    var resolving by remember { mutableStateOf(false) }
    var pageOptions by remember { mutableStateOf<List<PageInfo>>(emptyList()) }
    var showPagePicker by remember { mutableStateOf(false) }
    var selectedPages by remember { mutableStateOf<Set<Int>>(emptySet()) }

    val downloadDir = remember {
        File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "VideoChanger/_dl",
        )
    }

    // 选输出目录（SAF 目录树 → 主存储区域路径）
    val dirPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val uri = result.data?.data ?: return@rememberLauncherForActivityResult
        val path = runCatching {
            val docId = DocumentsContract.getTreeDocumentId(uri)
            if (docId.startsWith("primary:")) {
                File(Environment.getExternalStorageDirectory(), docId.removePrefix("primary:")).absolutePath
            } else {
                null
            }
        }.getOrNull()
        if (path != null) {
            outDir = path
            prefs.edit().putString("out_dir", path).apply()
        } else {
            android.widget.Toast.makeText(
                context, "这个位置不是手机内置存储，换一个文件夹试试", android.widget.Toast.LENGTH_LONG,
            ).show()
        }
    }

    fun pickOutputDir() {
        dirPicker.launch(
            android.content.Intent(android.content.Intent.ACTION_OPEN_DOCUMENT_TREE)
                .addFlags(
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                ),
        )
    }

    fun startDownload(pages: IntArray) {
        DownloadService.start(context, url.trim(), qn, sessdata.trim(), pages, outDir, deleteSource)
    }

    fun resolveAndStart() {
        val link = url.trim()
        if (link.length <= 3) return
        resolving = true
        scope.launch {
            val parsed = withContext(Dispatchers.IO) {
                runCatching {
                    val api = BiliApi(sessdata.trim())
                    val (id, page) = api.parseInput(link)
                    api.videoInfo(id, page).pages
                }
            }
            resolving = false
            val pages = parsed.getOrElse { error ->
                android.widget.Toast.makeText(
                    context,
                    error.message ?: "链接解析失败",
                    android.widget.Toast.LENGTH_LONG,
                ).show()
                return@launch
            }
            if (pages.size > 1) {
                pageOptions = pages
                selectedPages = emptySet()
                showPagePicker = true
            } else {
                startDownload(intArrayOf(0))
            }
        }
    }

    if (showPagePicker) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showPagePicker = false },
            title = { Text("选择分P（共 ${pageOptions.size} P）") },
            text = {
                Column(Modifier.fillMaxWidth()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { selectedPages = pageOptions.map { it.index }.toSet() }) {
                            Text("全选")
                        }
                        TextButton(onClick = { selectedPages = emptySet() }) { Text("清空") }
                    }
                    Column(
                        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        pageOptions.forEach { page ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedPages = if (page.index in selectedPages) {
                                            selectedPages - page.index
                                        } else {
                                            selectedPages + page.index
                                        }
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Checkbox(
                                    checked = page.index in selectedPages,
                                    onCheckedChange = { checked ->
                                        selectedPages = if (checked) {
                                            selectedPages + page.index
                                        } else {
                                            selectedPages - page.index
                                        }
                                    },
                                )
                                Text(
                                    "P${page.index}  ${page.title.ifBlank { "未命名" }}",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = selectedPages.isNotEmpty(),
                    onClick = {
                        val pages = selectedPages.sorted().toIntArray()
                        showPagePicker = false
                        startDownload(pages)
                    },
                ) { Text("下载选中（${selectedPages.size}）") }
            },
            dismissButton = {
                TextButton(onClick = { showPagePicker = false }) { Text("取消") }
            },
        )
    }

    LaunchedEffect(autoStart) {
        if (autoStart && !autoStarted && url.trim().length > 3 && !state.running) {
            autoStarted = true
            startDownload(intArrayOf(0))
        }
    }

    val statusText = when {
        state.running -> state.stage + if (state.speed.isNotBlank()) " · ${state.speed}" else ""
        state.cancelled -> "已取消"
        state.finished && state.success -> "完成：${state.outputPath}"
        state.finished && !state.success -> state.message
        else -> "把 B站 视频链接粘到上面，然后点「下载并转换」"
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact = maxWidth < 600.dp
        val contentWidth = maxWidth.coerceAtMost(720.dp)
        Column(
            Modifier.align(Alignment.TopCenter)
                .width(contentWidth)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = if (compact) 12.dp else 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
        // ── 链接 ──
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("视频链接 / BV 号 / 分享文案") },
            placeholder = { Text("粘贴 B站 分享文案，或输入 BV 号") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { resolveAndStart() }),
            modifier = Modifier.fillMaxWidth(),
        )

        // ── 画质 ──
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text("画质", style = MaterialTheme.typography.labelLarge)
            QUALITY_OPTIONS.forEach { (value, label) ->
                FilterChip(
                    selected = qn == value,
                    onClick = { qn = value },
                    enabled = !state.running,
                    label = { Text(label) },
                )
            }
        }

        // ── 主操作 ──
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                enabled = !state.running && !resolving && url.trim().length > 3,
                onClick = { resolveAndStart() },
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    when {
                        resolving -> "解析中…"
                        state.running -> "下载中…"
                        else -> "下载并转换"
                    },
                )
            }
            if (state.running) {
                OutlinedButton(onClick = { DownloadService.cancel() }) { Text("取消") }
            }
        }

        // ── 进度与状态 ──
        if (state.running) {
            if (state.indeterminate) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            } else {
                LinearProgressIndicator(progress = { state.progress }, modifier = Modifier.fillMaxWidth())
            }
        }
        Text(statusText, style = MaterialTheme.typography.bodyMedium)

        HorizontalDivider()

        // ── 账号 ──
        Text("账号（可选，登录后可下 1080P）", style = MaterialTheme.typography.labelLarge)
        if (compact) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = sessdata,
                    onValueChange = { sessdata = it },
                    label = { Text("SESSDATA") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedButton(
                    onClick = { showLogin = true },
                    enabled = !state.running,
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("扫码登录") }
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = sessdata,
                    onValueChange = { sessdata = it },
                    label = { Text("SESSDATA") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                OutlinedButton(onClick = { showLogin = true }, enabled = !state.running) { Text("扫码登录") }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Switch(
                checked = rememberSession,
                onCheckedChange = { checked ->
                    rememberSession = checked
                    prefs.edit().putBoolean("remember_sessdata", checked).apply()
                    if (checked) {
                        if (sessdata.isNotBlank()) {
                            prefs.edit().putString("sessdata", sessdata).apply()
                        } else {
                            android.widget.Toast.makeText(
                                context, "还没有登录，先点「扫码登录」", android.widget.Toast.LENGTH_SHORT,
                            ).show()
                        }
                    } else {
                        prefs.edit().remove("sessdata").apply()
                        sessdata = ""
                        android.widget.Toast.makeText(
                            context, "已清除本地登录信息", android.widget.Toast.LENGTH_SHORT,
                        ).show()
                    }
                },
            )
            Text(
                "记住登录状态（只存本机，关闭即清除）",
                style = MaterialTheme.typography.bodySmall,
            )
        }

        if (showLogin) {
            LoginDialog(
                onDismiss = { showLogin = false },
                onSuccess = { value ->
                    sessdata = value
                    if (rememberSession) {
                        prefs.edit().putString("sessdata", value).apply()
                    } else {
                        prefs.edit().remove("sessdata").apply()
                    }
                },
            )
        }

        HorizontalDivider()

        // ── 输出 ──
        Text("输出", style = MaterialTheme.typography.labelLarge)
        if (compact) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = outDir,
                    onValueChange = {
                        outDir = it
                        prefs.edit().putString("out_dir", it).apply()
                    },
                    label = { Text("保存到") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedButton(
                    onClick = { pickOutputDir() },
                    enabled = !state.running,
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("选择") }
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = outDir,
                    onValueChange = {
                        outDir = it
                        prefs.edit().putString("out_dir", it).apply()
                    },
                    label = { Text("保存到") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                OutlinedButton(
                    onClick = { pickOutputDir() },
                    enabled = !state.running,
                ) { Text("选择") }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Switch(
                checked = deleteSource,
                onCheckedChange = {
                    deleteSource = it
                    prefs.edit().putBoolean("delete_src", it).apply()
                },
                enabled = !state.running,
            )
            Text("转完删除源分片（省一半空间）", style = MaterialTheme.typography.bodySmall)
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = {
                    downloadDir.mkdirs()
                    if (!Storage.openFolder(context, downloadDir)) {
                        android.widget.Toast.makeText(
                            context, "打不开文件管理器：${downloadDir.absolutePath}", android.widget.Toast.LENGTH_LONG,
                        ).show()
                    }
                },
            ) { Text("打开下载目录") }

            OutlinedButton(
                onClick = {
                    val dir = File(outDir)
                    dir.mkdirs()
                    if (!Storage.openFolder(context, dir)) {
                        android.widget.Toast.makeText(
                            context, "打不开文件管理器：${dir.absolutePath}", android.widget.Toast.LENGTH_LONG,
                        ).show()
                    }
                },
            ) { Text("打开输出目录") }
        }

        // ── 说明 ──
        Text(
            "• 充电视频、大会员专享、番剧影视等付费内容无法下载（B站 服务端限制，本工具不做绕过）\n" +
                "• 下载在前台服务里跑，切后台 / 锁屏会继续，通知栏可取消",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        }
    }
}
