package com.writebugs.videochanger.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.writebugs.videochanger.MainActivity
import com.writebugs.videochanger.bili.BiliDownloader
import com.writebugs.videochanger.core.LocalVideos
import com.writebugs.videochanger.core.Remux
import java.io.File
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** 下载任务在 UI 上展示的状态。 */
data class DownloadUiState(
    val running: Boolean = false,
    val stage: String = "",
    val progress: Float = 0f,
    val indeterminate: Boolean = true,
    val speed: String = "",
    val finished: Boolean = false,
    val success: Boolean = false,
    val cancelled: Boolean = false,
    val message: String = "",
    val outputPath: String = "",
)

/**
 * 前台服务：下载 + 转封装都在这里跑。
 * 切后台 / 锁屏不会被杀，通知栏有进度，也可以直接取消。
 */
class DownloadService : Service() {

    companion object {
        private const val CHANNEL_ID = "download"
        private const val NOTIFICATION_ID = 1001

        const val ACTION_CANCEL = "com.writebugs.videochanger.CANCEL"

        const val EXTRA_URL = "url"
        const val EXTRA_QN = "qn"
        const val EXTRA_SESSDATA = "sessdata"
        const val EXTRA_PAGES = "pages"
        const val EXTRA_OUT_DIR = "out_dir"
        const val EXTRA_DELETE_SRC = "delete_src"

        private val _state = MutableStateFlow(DownloadUiState())
        val state = _state.asStateFlow()

        @Volatile
        private var serviceRef: DownloadService? = null

        fun start(
            context: Context,
            url: String,
            qn: Int,
            sessdata: String,
            pages: IntArray,
            outDir: String,
            deleteSource: Boolean,
        ) {
            val intent = Intent(context, DownloadService::class.java).apply {
                putExtra(EXTRA_URL, url)
                putExtra(EXTRA_QN, qn)
                putExtra(EXTRA_SESSDATA, sessdata)
                putExtra(EXTRA_PAGES, pages)
                putExtra(EXTRA_OUT_DIR, outDir)
                putExtra(EXTRA_DELETE_SRC, deleteSource)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun cancel() {
            serviceRef?.requestCancel()
        }
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var job: Job? = null
    private var lastNotify = 0L
    private var lastBytes = 0L
    private var lastTick = 0L

    @Volatile
    private var cancelled = false

    /** 桌面名称：正式版是 VideoChanger，debug 版是 VideoChanger Dev。 */
    private val appLabel by lazy {
        applicationInfo.loadLabel(packageManager).toString()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        serviceRef = this
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_CANCEL) {
            requestCancel()
            return START_NOT_STICKY
        }

        val cmd = intent ?: run {
            stopSelf()
            return START_NOT_STICKY
        }
        val url = cmd.getStringExtra(EXTRA_URL).orEmpty()
        if (url.isBlank()) {
            stopSelf()
            return START_NOT_STICKY
        }

        val qn = cmd.getIntExtra(EXTRA_QN, 120)
        val sessdata = cmd.getStringExtra(EXTRA_SESSDATA).orEmpty()
        val pages = (cmd.getIntArrayExtra(EXTRA_PAGES) ?: intArrayOf(0)).toList().ifEmpty { listOf(0) }
        val outDir = cmd.getStringExtra(EXTRA_OUT_DIR).orEmpty()
        val deleteSource = cmd.getBooleanExtra(EXTRA_DELETE_SRC, false)

        cancelled = false
        _state.value = DownloadUiState(running = true, stage = "准备中…")
        startForeground(NOTIFICATION_ID, buildNotification("准备中…", 0, true))

        job?.cancel()
        job = scope.launch { runDownload(url, qn, sessdata, pages, outDir, deleteSource) }
        return START_NOT_STICKY
    }

    fun requestCancel() {
        if (!_state.value.running) return
        cancelled = true
        job?.cancel()
        _state.value = DownloadUiState(
            running = false,
            finished = true,
            cancelled = true,
            message = "已取消",
        )
        runCatching {
            val manager = getSystemService(NotificationManager::class.java)
            manager.cancel(NOTIFICATION_ID)
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private suspend fun runDownload(
        url: String,
        qn: Int,
        sessdata: String,
        pages: List<Int>,
        outDir: String,
        deleteSource: Boolean,
    ) {
        try {
            val downloadDir = File(
                android.os.Environment.getExternalStoragePublicDirectory(
                    android.os.Environment.DIRECTORY_DOWNLOADS,
                ),
                "VideoChanger/_dl",
            )
            val outputDir = if (outDir.isNotBlank()) File(outDir) else File(
                android.os.Environment.getExternalStoragePublicDirectory(
                    android.os.Environment.DIRECTORY_MOVIES,
                ),
                "VideoChanger",
            )
            downloadDir.mkdirs()
            outputDir.mkdirs()

            val targets = mutableListOf<String>()
            val failures = mutableListOf<String>()
            val total = pages.size

            for ((index, page) in pages.withIndex()) {
                if (cancelled) throw CancellationException("已取消")
                val prefix = if (total > 1) "第 ${index + 1}/$total 个 · " else ""
                lastBytes = 0L
                lastTick = 0L
                try {
                    val outcome = BiliDownloader(sessdata).download(
                        url,
                        downloadDir,
                        qn,
                        page,
                        isCancelled = { cancelled },
                        onStage = { stage -> update(stage = prefix + stage, indeterminate = true) },
                        onProgress = { done, all -> onBytes(done, all, prefix) },
                    )
                    if (cancelled) throw CancellationException("已取消")

                    update(stage = prefix + "转封装中…", indeterminate = true, progress = 0f)
                    val item = LocalVideos.forFolder(outcome.folder, outcome.title)
                        ?: throw IllegalStateException("下载完成但分片缺失")
                    val target = File(outputDir, LocalVideos.sanitizeFileName(item.title) + ".mp4")

                    val result = Remux.convert(item.video, item.audio, target, cacheDir) { f ->
                        if (cancelled) throw CancellationException("已取消")
                        update(stage = prefix + "转封装中…", progress = f, indeterminate = false)
                    }
                    if (!result.ok) throw IllegalStateException(result.message)

                    if (deleteSource) runCatching { outcome.folder.deleteRecursively() }
                    targets += target.absolutePath
                } catch (exc: CancellationException) {
                    throw exc
                } catch (exc: Exception) {
                    // BiliDownloader 在分片循环里把取消包装成了 BiliException("已取消")，
                    // 这里要还原成真正的取消，不能记成“部分失败”。
                    if (cancelled || exc.message == "已取消") {
                        throw CancellationException("已取消")
                    }
                    failures += (exc.message ?: exc.javaClass.simpleName)
                }
            }

            _state.value = DownloadUiState(
                running = false,
                finished = true,
                success = failures.isEmpty(),
                stage = "完成",
                progress = 1f,
                indeterminate = false,
                message = when {
                    failures.isEmpty() && targets.size == 1 -> targets.first()
                    failures.isEmpty() -> "全部完成，共 ${targets.size} 个"
                    targets.isEmpty() -> "失败：${failures.first()}"
                    else -> "部分失败：完成 ${targets.size} 个，失败 ${failures.size} 个：${failures.first()}"
                },
                outputPath = targets.firstOrNull().orEmpty(),
            )
            notifyFinal(
                if (failures.isEmpty()) "下载完成" else "部分失败",
                if (failures.isEmpty()) "共 ${targets.size} 个文件" else failures.first(),
            )
        } catch (exc: CancellationException) {
            _state.value = DownloadUiState(
                running = false,
                finished = true,
                cancelled = true,
                message = "已取消",
            )
        } catch (exc: Exception) {
            if (cancelled) {
                _state.value = DownloadUiState(
                    running = false,
                    finished = true,
                    cancelled = true,
                    message = "已取消",
                )
            } else {
                val message = exc.message ?: exc.javaClass.simpleName
                _state.value = DownloadUiState(
                    running = false,
                    finished = true,
                    success = false,
                    message = message,
                )
                notifyFinal("下载失败", message)
            }
        } finally {
            // 完成/失败后必须移除进度通知，否则它会以最后百分比一直挂在通知栏。
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun onBytes(done: Long, total: Long, prefix: String = "") {
        val now = SystemClock.elapsedRealtime()
        var speed = _state.value.speed
        if (lastTick > 0 && now - lastTick >= 500) {
            val delta = done - lastBytes
            val time = now - lastTick
            if (delta > 0 && time > 0) {
                speed = "%.2f MB/s".format(delta / 1024f / 1024f / (time / 1000f))
            }
            lastBytes = done
            lastTick = now
        } else if (lastTick == 0L) {
            lastTick = now
            lastBytes = done
        }
        val fraction = if (total > 0) (done.toFloat() / total).coerceIn(0f, 1f) else 0f
        update(
            stage = prefix + "下载中…",
            progress = fraction,
            indeterminate = total <= 0,
            speed = speed,
        )
        if (now - lastNotify > 800) {
            lastNotify = now
            getSystemService(NotificationManager::class.java).notify(
                NOTIFICATION_ID,
                buildNotification(
                    "下载中 ${(fraction * 100).toInt()}% · $speed",
                    (fraction * 100).toInt(),
                    total <= 0,
                ),
            )
        }
    }

    private fun update(
        stage: String? = null,
        progress: Float? = null,
        indeterminate: Boolean? = null,
        speed: String? = null,
    ) {
        val current = _state.value
        _state.value = current.copy(
            running = true,
            stage = stage ?: current.stage,
            progress = progress ?: current.progress,
            indeterminate = indeterminate ?: current.indeterminate,
            speed = speed ?: current.speed,
        )
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "下载任务",
            NotificationManager.IMPORTANCE_LOW,
        ).apply { setShowBadge(false) }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun contentIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun cancelIntent(): PendingIntent {
        val intent = Intent(this, DownloadService::class.java).apply {
            action = ACTION_CANCEL
        }
        return PendingIntent.getService(
            this,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun buildNotification(text: String, percent: Int, indeterminate: Boolean): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(com.writebugs.videochanger.R.drawable.ic_stat_download)
            .setContentTitle(appLabel)
            .setContentText(text)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentIntent())
            .addAction(0, "取消", cancelIntent())
            .setProgress(100, percent.coerceIn(0, 100), indeterminate)
            .build()
    }

    private fun notifyFinal(title: String, text: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(com.writebugs.videochanger.R.drawable.ic_stat_download)
            .setContentTitle("$appLabel · $title")
            .setContentText(text)
            .setAutoCancel(true)
            .setContentIntent(contentIntent())
            .build()
        getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID + 1, notification)
    }

    override fun onDestroy() {
        if (serviceRef === this) serviceRef = null
        job?.cancel()
        scope.cancel()
        super.onDestroy()
    }
}
