package com.writebugs.videochanger.core

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import java.io.File
import java.nio.ByteBuffer

/**
 * 方案 C：用系统 API 做 m4s → MP4 的转封装（不重编码）。
 *
 * MediaExtractor 读出音视频轨道的压缩样本，MediaMuxer 原样写进新的 MP4。
 * 不依赖 ffmpeg 二进制，APK 里不需要任何外部可执行文件。
 */
object Remux {

    private const val BUFFER_SIZE = 1 shl 20 // 1 MB
    private val BOX_TYPES = setOf("ftyp", "styp", "moov", "mdat", "moof", "sidx", "free")

    data class Result(val ok: Boolean, val message: String)

    /** 探测 MP4 真正起始偏移（B站 Windows 客户端缓存头部有 9 字节占位）。 */
    fun detectOffset(file: File, probe: Int = 64): Int {
        val length = file.length()
        if (length <= 8) return 0
        val head = ByteArray(minOf(probe.toLong(), length).toInt())
        file.inputStream().use { stream -> stream.read(head) }
        for (offset in 0 until head.size - 8) {
            val size = ((head[offset].toInt() and 0xFF) shl 24) or
                ((head[offset + 1].toInt() and 0xFF) shl 16) or
                ((head[offset + 2].toInt() and 0xFF) shl 8) or
                (head[offset + 3].toInt() and 0xFF)
            val type = String(head, offset + 4, 4, Charsets.US_ASCII)
            if (type in BOX_TYPES && size in 8..(1 shl 30)) return offset
        }
        return 0
    }

    /**
     * 转封装。失败时返回 ok=false + 原因，调用方可以据此决定是否退回 ffmpeg。
     *
     * @param tempDir 需要去头部时使用的临时目录（通常传 context.cacheDir）
     */
    fun convert(
        video: File,
        audio: File,
        output: File,
        tempDir: File,
        onProgress: (Float) -> Unit = {},
    ): Result {
        var videoExtractor: MediaExtractor? = null
        var audioExtractor: MediaExtractor? = null
        var muxer: MediaMuxer? = null
        val tempFiles = mutableListOf<File>()

        return try {
            output.parentFile?.mkdirs()

            val videoInput = prepare(video, tempDir).also { if (it != video) tempFiles += it }
            val audioInput = prepare(audio, tempDir).also { if (it != audio) tempFiles += it }

            videoExtractor = MediaExtractor().apply { setDataSource(videoInput.absolutePath) }
            audioExtractor = MediaExtractor().apply { setDataSource(audioInput.absolutePath) }

            val videoTrack = firstTrack(videoExtractor, "video/")
            val audioTrack = firstTrack(audioExtractor, "audio/")
            if (videoTrack < 0 && audioTrack < 0) {
                return Result(false, "两个文件里都没有可用的音视频轨道")
            }
            if (videoTrack >= 0) videoExtractor.selectTrack(videoTrack)
            if (audioTrack >= 0) audioExtractor.selectTrack(audioTrack)

            muxer = MediaMuxer(output.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            val videoOut = if (videoTrack >= 0) {
                muxer.addTrack(videoExtractor.getTrackFormat(videoTrack))
            } else {
                -1
            }
            val audioOut = if (audioTrack >= 0) {
                muxer.addTrack(audioExtractor.getTrackFormat(audioTrack))
            } else {
                -1
            }
            if (videoOut < 0 && audioOut < 0) {
                return Result(false, "媒体封装器拒绝了这两个轨道（可能缺少 csd 信息）")
            }

            val videoReader = if (videoOut >= 0) TrackReader(videoExtractor) else null
            val audioReader = if (audioOut >= 0) TrackReader(audioExtractor) else null
            var videoPending = videoReader?.advance() ?: false
            var audioPending = audioReader?.advance() ?: false
            if (!videoPending && !audioPending) {
                return Result(false, "读不到任何采样数据")
            }

            val totalBytes = (videoInput.length() + audioInput.length()).coerceAtLeast(1L)
            var written = 0L
            val info = MediaCodec.BufferInfo()

            muxer.start()
            while (videoPending || audioPending) {
                val takeVideo = when {
                    !videoPending -> false
                    !audioPending -> true
                    else -> videoReader!!.presentationTimeUs <= audioReader!!.presentationTimeUs
                }
                if (takeVideo) {
                    videoReader!!.writeTo(muxer, videoOut, info)
                    written += videoReader.size
                    videoPending = videoReader.advance()
                } else {
                    audioReader!!.writeTo(muxer, audioOut, info)
                    written += audioReader.size
                    audioPending = audioReader.advance()
                }
                onProgress((written.toFloat() / totalBytes).coerceIn(0f, 1f))
            }
            muxer.stop()
            onProgress(1f)
            Result(true, "OK")
        } catch (exc: Throwable) {
            runCatching { muxer?.stop() }
            runCatching { if (output.exists()) output.delete() }
            Result(false, "${exc.javaClass.simpleName}: ${exc.message ?: "未知错误"}")
        } finally {
            runCatching { videoExtractor?.release() }
            runCatching { audioExtractor?.release() }
            runCatching { muxer?.release() }
            tempFiles.forEach { runCatching { it.delete() } }
        }
    }

    /** 头部有偏移时复制一份去头的临时文件；MediaExtractor 不支持跳过字节。 */
    private fun prepare(file: File, tempDir: File): File {
        val offset = detectOffset(file)
        if (offset <= 0) return file
        tempDir.mkdirs()
        val stripped = File(tempDir, "strip_${System.currentTimeMillis()}_${file.name}")
        file.inputStream().use { input ->
            input.skip(offset.toLong())
            stripped.outputStream().use { output -> input.copyTo(output, 1 shl 16) }
        }
        return stripped
    }

    private fun firstTrack(extractor: MediaExtractor, prefix: String): Int {
        for (index in 0 until extractor.trackCount) {
            val mime = extractor.getTrackFormat(index).getString(MediaFormat.KEY_MIME) ?: continue
            if (mime.startsWith(prefix)) return index
        }
        return -1
    }

    private class TrackReader(private val extractor: MediaExtractor) {
        val buffer: ByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE)
        var size: Int = 0
        var presentationTimeUs: Long = 0
        var flags: Int = 0
        private var ended = false

        fun advance(): Boolean {
            if (ended) return false
            buffer.clear()
            val read = extractor.readSampleData(buffer, 0)
            if (read < 0) {
                ended = true
                return false
            }
            size = read
            presentationTimeUs = extractor.sampleTime
            flags = extractor.sampleFlags
            extractor.advance()
            return true
        }

        fun writeTo(muxer: MediaMuxer, track: Int, info: MediaCodec.BufferInfo) {
            info.set(0, size, presentationTimeUs, flags)
            muxer.writeSampleData(track, buffer, info)
        }
    }
}
