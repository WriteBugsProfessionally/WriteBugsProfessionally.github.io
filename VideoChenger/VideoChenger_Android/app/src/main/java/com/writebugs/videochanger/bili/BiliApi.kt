package com.writebugs.videochanger.bili

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/** 下载用的单个流地址。 */
data class StreamRef(
    val url: String,
    val backup: List<String>,
    val quality: Int,
    val codecId: Int,
    val sizeHint: Long,
)

data class PageInfo(val index: Int, val title: String, val cid: Long)

data class VideoInfo(
    val bvid: String,
    val cid: Long,
    val title: String,
    val pages: List<PageInfo> = emptyList(),
)

data class PlayInfo(val video: List<StreamRef>, val audio: List<StreamRef>)

class BiliException(message: String) : Exception(message)

/**
 * B站 API 的最小实现：取视频信息 + 取 dash 播放地址。
 * 只依赖公开接口，登录是可选的（1080P+ 需要 SESSDATA）。
 */
class BiliApi(private val sessdata: String = "") {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private var mixinKey: String? = null

    private val userAgent =
        "Mozilla/5.0 (Linux; Android 12) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/122.0.0.0 Mobile Safari/537.36"

    private fun get(url: String, referer: String = "https://www.bilibili.com/"): String {
        val builder = Request.Builder()
            .url(url)
            .header("User-Agent", userAgent)
            .header("Referer", referer)
            .header("Accept", "application/json, text/plain, */*")
        if (sessdata.isNotBlank()) {
            builder.header("Cookie", "SESSDATA=$sessdata")
        }
        client.newCall(builder.build()).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw BiliException("HTTP ${response.code}")
            }
            return body
        }
    }

    private fun ensureMixinKey(): String {
        mixinKey?.let { return it }
        val json = JSONObject(get("https://api.bilibili.com/x/web-interface/nav"))
        val data = json.optJSONObject("data") ?: throw BiliException("拿不到 wbi key")
        val wbi = data.optJSONObject("wbi_img") ?: throw BiliException("拿不到 wbi_img")
        val img = wbi.optString("img_url")
        val sub = wbi.optString("sub_url")
        if (img.isBlank() || sub.isBlank()) throw BiliException("wbi key 为空")
        return Wbi.mixinKey(img, sub).also { mixinKey = it }
    }

    /** 解析 BV / av / 完整链接；返回 (bvid|avId, page)。 */
    fun parseInput(raw: String): Pair<String, Int> {
        val text = raw.trim()
        val page = Regex("""[?&]p=(\d+)""").find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 1
        val bv = Regex("""(BV[0-9A-Za-z]{10})""").find(text)?.value
        if (bv != null) return bv to page
        val av = Regex("""av(\d+)""", RegexOption.IGNORE_CASE).find(text)?.groupValues?.get(1)
        if (av != null) return "av$av" to page
        throw BiliException("链接里没有识别到 BV 号")
    }

    fun videoInfo(id: String, page: Int): VideoInfo {
        val isAv = id.startsWith("av")
        val key = if (isAv) "aid" else "bvid"
        val value = if (isAv) id.removePrefix("av") else id
        val json = JSONObject(get("https://api.bilibili.com/x/web-interface/view?$key=$value"))
        if (json.optInt("code") != 0) {
            throw BiliException("${json.optString("message", "取视频信息失败")}（code=${json.optInt("code")}）")
        }
        val data = json.getJSONObject("data")
        val title = data.optString("title")
        val pages = data.optJSONArray("pages")
        val pageList = mutableListOf<PageInfo>()
        if (pages != null) {
            for (i in 0 until pages.length()) {
                val p = pages.getJSONObject(i)
                pageList += PageInfo(
                    index = p.optInt("page", i + 1),
                    title = p.optString("part"),
                    cid = p.optLong("cid"),
                )
            }
        }
        val cid = if (pageList.isNotEmpty()) {
            val index = (page - 1).coerceIn(0, pageList.size - 1)
            pageList[index].cid
        } else {
            data.optLong("cid")
        }
        if (cid == 0L) throw BiliException("拿不到 cid")
        return VideoInfo(data.optString("bvid", id), cid, title, pageList)
    }

    /**
     * 取 dash 播放地址。qn 只是"期望值"，服务端会按账号权限降级；
     * 返回列表已按画质从高到低排好。
     */
    fun playUrl(bvid: String, cid: Long, expectQn: Int = 120): PlayInfo {
        val params = mapOf(
            "bvid" to bvid,
            "cid" to cid.toString(),
            "qn" to expectQn.toString(),
            "fnval" to "4048",   // dash + 4K + HDR + Dolby
            "fnver" to "0",
            "fourk" to "1",
        )
        val query = Wbi.sign(params, ensureMixinKey())
        val json = JSONObject(
            get("https://api.bilibili.com/x/player/wbi/playurl?$query",
                referer = "https://www.bilibili.com/video/$bvid"),
        )
        if (json.optInt("code") != 0) throw BiliException(json.optString("message", "取播放地址失败"))
        val data = json.getJSONObject("data")
        val dash = data.optJSONObject("dash") ?: throw BiliException("这个视频没有 dash 流（可能是番剧或需要登录）")

        fun parse(arrayName: String): List<StreamRef> {
            val array = dash.optJSONArray(arrayName) ?: return emptyList()
            val list = mutableListOf<StreamRef>()
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                val base = item.optString("baseUrl").ifBlank { item.optString("base_url") }
                if (base.isBlank()) continue
                val backupArray = item.optJSONArray("backupUrl") ?: item.optJSONArray("backup_url")
                val backups = mutableListOf<String>()
                if (backupArray != null) {
                    for (j in 0 until backupArray.length()) backups += backupArray.optString(j)
                }
                list += StreamRef(
                    url = base,
                    backup = backups,
                    quality = item.optInt("id"),
                    codecId = item.optInt("codecid"),
                    sizeHint = item.optLong("size", 0L),
                )
            }
            // AVC(7) 兼容性最好：同画质优先它，其次按画质从高到低
            return list.sortedWith(
                compareByDescending<StreamRef> { it.quality }.thenBy { if (it.codecId == 7) 0 else 1 },
            )
        }

        val video = parse("video")
        val audio = parse("audio")
        if (video.isEmpty()) throw BiliException("没有可用的视频流")
        return PlayInfo(video, audio)
    }
}
