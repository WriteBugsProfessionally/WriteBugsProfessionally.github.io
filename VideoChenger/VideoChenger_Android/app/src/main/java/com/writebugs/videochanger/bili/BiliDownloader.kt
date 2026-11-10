package com.writebugs.videochanger.bili

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * 下载 B站 dash 流，落盘成 video.m4s + audio.m4s，交给 LocalVideos + Remux 处理。
 *
 * 速度相关：
 *  - B站 每条流会给 1 个主地址 + 若干备用 CDN，这里先并发实测挑最快的；
 *  - 再用 6 个连接按 Range 分片并行下载（服务器不支持 Range 时自动退回单连接）。
 */
class BiliDownloader(private val sessdata: String = "") {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private var referer = "https://www.bilibili.com/"

    private val userAgent =
        "Mozilla/5.0 (Linux; Android 12) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/122.0.0.0 Mobile Safari/537.36"

    private companion object {
        const val THREADS = 6
        const val PROBE_BYTES = 256L * 1024
        const val MIN_PARALLEL_SIZE = 2L * 1024 * 1024
    }

    data class Outcome(val folder: File, val title: String, val quality: Int)

    private fun request(url: String, range: String? = null): Request {
        val builder = Request.Builder()
            .url(url)
            .header("User-Agent", userAgent)
            .header("Referer", referer)
        if (range != null) builder.header("Range", range)
        if (sessdata.isNotBlank()) builder.header("Cookie", "SESSDATA=$sessdata")
        return builder.build()
    }

    fun download(
        rawUrl: String,
        outRoot: File,
        qn: Int = 120,
        page: Int = 0,
        isCancelled: () -> Boolean = { false },
        onStage: (String) -> Unit = {},
        onProgress: (Long, Long) -> Unit = { _, _ -> },
    ): Outcome {
        val api = BiliApi(sessdata)
        val (id, urlPage) = api.parseInput(rawUrl)
        val effectivePage = if (page > 0) page else urlPage

        onStage("解析视频信息…")
        val info = api.videoInfo(id, effectivePage)

        referer = "https://www.bilibili.com/video/${info.bvid}"
        onStage("获取播放地址…")
        val play = api.playUrl(info.bvid, info.cid, qn)
        val video = play.video.first()
        val audio = play.audio.firstOrNull()

        val partTitle = info.pages.getOrNull(effectivePage - 1)?.title?.takeIf { it.isNotBlank() }
        val displayTitle = when {
            info.pages.size <= 1 || partTitle == null -> info.title
            partTitle.contains(info.title) -> partTitle          // 分P标题已包含主标题，别重复拼
            info.title == partTitle -> info.title
            else -> "${info.title} - $partTitle"
        }

        val folder = File(outRoot, sanitize(displayTitle))
        folder.mkdirs()
        val videoFile = File(folder, "video.m4s")
        val audioFile = File(folder, "audio.m4s")

        onStage("选择最快线路…")
        val videoCandidates = orderedCandidates(video)
        val audioCandidates = if (audio != null) orderedCandidates(audio) else emptyList()

        val videoSize = contentLength(videoCandidates.first())
        val audioSize = if (audioCandidates.isNotEmpty()) contentLength(audioCandidates.first()) else 0L
        val total = videoSize + audioSize

        var done = 0L
        onStage("下载视频流…")
        done += fetch(videoCandidates, videoFile, videoSize, isCancelled) { bytes ->
            onProgress(done + bytes, total)
        }

        if (audioCandidates.isNotEmpty()) {
            onStage("下载音频流…")
            val base = done
            done += fetch(audioCandidates, audioFile, audioSize, isCancelled) { bytes ->
                onProgress(base + bytes, total)
            }
        }

        File(folder, "entry.json").writeText(
            JSONObject().put("title", displayTitle).toString(),
            Charsets.UTF_8,
        )
        onStage("下载完成")
        return Outcome(folder, displayTitle, video.quality)
    }

    /** 并发探一下各 CDN 的首 256KB，返回「最快优先」的线路列表，失败时依次回退。 */
    private fun orderedCandidates(stream: StreamRef): List<String> {
        val candidates = (listOf(stream.url) + stream.backup).distinct().take(4)
        if (candidates.size <= 1) return candidates

        val pool = Executors.newFixedThreadPool(candidates.size)
        return try {
            val tasks = candidates.map { url ->
                pool.submit<Pair<String, Long>> {
                    val started = System.currentTimeMillis()
                    client.newCall(request(url, "bytes=0-${PROBE_BYTES - 1}")).execute().use { resp ->
                        val bytes = resp.body?.bytes()?.size ?: 0
                        if (!resp.isSuccessful || bytes == 0) throw IllegalStateException("probe failed")
                        url to (System.currentTimeMillis() - started)
                    }
                }
            }
            val timed = tasks.mapNotNull { task ->
                runCatching { task.get(10, TimeUnit.SECONDS) }.getOrNull()
            }
            val fastest = timed.sortedBy { it.second }.map { it.first }
            val rest = candidates.filter { it !in fastest }
            val ordered = fastest + rest
            android.util.Log.i("BiliDL", "candidates=${candidates.size} fastest=${fastest.firstOrNull()?.let(::hostOf)}")
            ordered
        } catch (_: Exception) {
            candidates
        } finally {
            pool.shutdownNow()
        }
    }

    /** 先探 Content-Length（服务端一般会给），用于算总进度和分片。 */
    private fun contentLength(url: String): Long {
        return try {
            client.newCall(request(url, "bytes=0-0")).execute().use { response ->
                val range = response.header("Content-Range")
                if (range != null && range.contains('/')) {
                    range.substringAfter('/').toLongOrNull() ?: 0L
                } else {
                    response.header("Content-Length")?.toLongOrNull() ?: 0L
                }
            }
        } catch (_: Exception) {
            0L
        }
    }

    private fun fetch(
        urls: List<String>,
        target: File,
        knownSize: Long,
        isCancelled: () -> Boolean,
        onBytes: (Long) -> Unit,
    ): Long {
        var lastError: Exception? = null
        for (url in urls) {
            try {
                if (isCancelled()) throw BiliException("已取消")
                val size = if (knownSize > 0) knownSize else contentLength(url)
                android.util.Log.i("BiliDL", "fetch host=${hostOf(url)} size=$size")
                if (size >= MIN_PARALLEL_SIZE) {
                    val result = runCatching { parallelFetch(url, size, target, isCancelled, onBytes) }
                    if (result.isSuccess) {
                        android.util.Log.i("BiliDL", "parallel ok ${target.length()} bytes")
                        return size
                    }
                    android.util.Log.w("BiliDL", "parallel failed: ${result.exceptionOrNull()?.message}")
                    target.delete()
                }
                return singleFetch(url, target, isCancelled, onBytes)
            } catch (exc: Exception) {
                if (isCancelled()) throw BiliException("已取消")
                lastError = exc
                android.util.Log.w("BiliDL", "line failed ${hostOf(url)}: ${exc.message}")
                target.delete()
            }
        }
        throw BiliException("所有线路都失败：${lastError?.message ?: "未知错误"}")
    }

    private fun singleFetch(
        url: String,
        target: File,
        isCancelled: () -> Boolean,
        onBytes: (Long) -> Unit,
    ): Long {
        client.newCall(request(url)).execute().use { response ->
            if (!response.isSuccessful) {
                throw BiliException("HTTP ${response.code} @ ${hostOf(url)}")
            }
            val body = response.body ?: throw BiliException("响应为空")
            var written = 0L
            target.outputStream().use { output ->
                body.byteStream().use { input ->
                    val buffer = ByteArray(256 * 1024)
                    while (true) {
                        if (isCancelled()) throw BiliException("已取消")
                        val read = input.read(buffer)
                        if (read < 0) break
                        output.write(buffer, 0, read)
                        written += read
                        onBytes(written)
                    }
                }
            }
            if (written == 0L) throw BiliException("下载到 0 字节")
            return written
        }
    }

    private fun parallelFetch(
        url: String,
        size: Long,
        target: File,
        isCancelled: () -> Boolean,
        onBytes: (Long) -> Unit,
    ) {
        RandomAccessFile(target, "rw").use { it.setLength(size) }
        val chunk = (size + THREADS - 1) / THREADS
        val written = AtomicLong(0)
        val pool = Executors.newFixedThreadPool(THREADS)
        try {
            val tasks = (0 until THREADS).mapNotNull { index ->
                val start = index * chunk
                val end = minOf((index + 1) * chunk - 1, size - 1)
                if (start > end) return@mapNotNull null
                pool.submit {
                    RandomAccessFile(target, "rw").use { raf ->
                        client.newCall(request(url, "bytes=$start-$end")).execute().use { response ->
                            // 服务器忽略 Range 会返回 200，此时必须放弃分片
                            if (response.code != 206) {
                                throw BiliException("服务器不支持分片（HTTP ${response.code}）")
                            }
                            val body = response.body ?: throw BiliException("分片响应为空")
                            raf.seek(start)
                            var pos = start
                            body.byteStream().use { input ->
                                val buffer = ByteArray(128 * 1024)
                                while (true) {
                                    if (isCancelled()) throw BiliException("已取消")
                                    val read = input.read(buffer)
                                    if (read < 0) break
                                    raf.write(buffer, 0, read)
                                    pos += read
                                    onBytes(written.addAndGet(read.toLong()))
                                }
                            }
                            if (pos != end + 1) throw BiliException("分片长度不符")
                        }
                    }
                }
            }
            tasks.forEach { it.get(180, TimeUnit.SECONDS) }
        } finally {
            pool.shutdownNow()
        }
    }

    private fun hostOf(url: String): String =
        runCatching { java.net.URI(url).host }.getOrNull() ?: url

    private fun sanitize(name: String): String {
        val cleaned = name
            .replace(Regex("""[<>:"/\\|?*\x00-\x1F]"""), "_")
            .trim()
            .trimEnd('.', ' ')
            .take(80)
        return cleaned.ifEmpty { "video" }
    }
}
