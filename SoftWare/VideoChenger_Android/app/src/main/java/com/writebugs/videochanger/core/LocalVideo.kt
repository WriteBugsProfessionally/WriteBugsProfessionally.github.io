package com.writebugs.videochanger.core

import java.io.File

/** 下载落到本地的一对分片（video.m4s + audio.m4s）。 */
data class LocalVideo(
    val video: File,
    val audio: File,
    val folder: File,
    val title: String,
) {
    val size: Long
        get() = (if (video.isFile) video.length() else 0L) +
            (if (audio.isFile) audio.length() else 0L)
}

object LocalVideos {

    private val ILLEGAL = Regex("""[<>:"/\\|?*\x00-\x1F]""")
    private val RESERVED = buildSet {
        addAll(listOf("CON", "PRN", "AUX", "NUL"))
        for (i in 1..9) { add("COM$i"); add("LPT$i") }
    }

    /** 下载目录里是否已经有可转封装的分片。 */
    fun forFolder(folder: File, title: String): LocalVideo? {
        val video = File(folder, "video.m4s")
        val audio = File(folder, "audio.m4s")
        if (!video.isFile || !audio.isFile) return null
        return LocalVideo(video, audio, folder, title)
    }

    /** 清洗成合法文件名（保留中文）。 */
    fun sanitizeFileName(name: String, maxLength: Int = 120): String {
        var cleaned = ILLEGAL.replace(name, "_").trim().trimEnd('.', ' ')
        if (cleaned.isEmpty()) cleaned = "video"
        if (cleaned.uppercase() in RESERVED) cleaned = "_$cleaned"
        cleaned = cleaned.take(maxLength).trimEnd('.', ' ')
        return cleaned.ifEmpty { "video" }
    }
}
