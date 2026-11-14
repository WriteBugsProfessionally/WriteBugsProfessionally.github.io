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

    /** 解析 BV / av / 完整链接 / B站分享文案；返回 (bvid|avId, page)。 */
    fun parseInput(raw: String): Pair<String, Int> {
        val text = raw.trim()
        if (text.isEmpty()) throw BiliException("请输入视频链接、BV 号或分享文案")

        val rawPage = pageOf(text)
        findVideoId(text)?.let { return it to rawPage }

        val url = extractUrl(text)
            ?: throw BiliException("没有识别到视频链接或 BV 号")
        val resolved = resolveUrl(url)
        val combined = resolved.url + "\n" + resolved.html
        val id = findVideoId(combined) ?: throw unsupported(resolved.url)
        val page = pageOf(resolved.url).takeIf { resolved.url.contains("p=") } ?: rawPage
        return id to page
    }

    private data class Resolved(val url: String, val html: String)

    private val urlRegex = Regex("""https?://[^\s<>"']+""", RegexOption.IGNORE_CASE)
    private val bareShortRegex = Regex("""(?:b23\.tv|bili2233\.cn)/[^\s<>"']+""", RegexOption.IGNORE_CASE)
    private val canonicalVideoRegex = Regex(
        """https?://(?:www\.|m\.)?bilibili\.com/video/(BV[0-9A-Za-z]{10}|av\d+)""",
        RegexOption.IGNORE_CASE,
    )
    private val bvRegex = Regex("""(BV[0-9A-Za-z]{10})""")
    private val avRegex = Regex("""av(\d+)""", RegexOption.IGNORE_CASE)
    private val pageRegex = Regex("""[?&]p=(\d+)""")

    private fun pageOf(text: String): Int =
        pageRegex.find(text)?.groupValues?.get(1)?.toIntOrNull()?.coerceAtLeast(1) ?: 1

    private fun findVideoId(text: String): String? {
        canonicalVideoRegex.find(text)?.groupValues?.get(1)?.let { return normalizeId(it) }
        bvRegex.find(text)?.value?.let { return it }
        avRegex.find(text)?.groupValues?.get(1)?.let { return "av$it" }
        return null
    }

    private fun normalizeId(id: String): String =
        if (id.startsWith("av", ignoreCase = true)) "av${id.drop(2)}" else id

    private fun extractUrl(text: String): String? {
        val raw = urlRegex.find(text)?.value
            ?: bareShortRegex.find(text)?.value?.let { "https://$it" }
            ?: return null
        return raw.trimEnd(
            ')', '）', ']', '】', '」', '』', ',', '，', '.', '。',
            ';', '；', '!', '！', '?', '？', '"', '\'',
        )
    }

    private fun hostOf(url: String): String? =
        runCatching { java.net.URI(url).host?.lowercase() }.getOrNull()

    private fun isBiliHost(url: String): Boolean {
        val host = hostOf(url) ?: return false
        return host == "b23.tv" || host == "bili2233.cn" ||
            host == "bilibili.com" || host.endsWith(".bilibili.com")
    }

    /** 跟随 b23.tv / bili2233.cn 的跳转；同时兜底解析最终页面里的 BV。 */
    private fun resolveUrl(url: String): Resolved {
        if (!isBiliHost(url)) {
            throw BiliException("只支持 B站链接、b23.tv 短链或 BV 号")
        }
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", userAgent)
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                val finalUrl = response.request.url.toString()
                val html = if (findVideoId(finalUrl) == null) {
                    response.body?.let { body ->
                        runCatching { body.string().take(128 * 1024) }.getOrDefault("")
                    }.orEmpty()
                } else {
                    ""
                }
                if (!response.isSuccessful && findVideoId(finalUrl) == null) {
                    throw BiliException("分享链接解析失败：HTTP ${response.code}")
                }
                Resolved(finalUrl, html)
            }
        } catch (exc: BiliException) {
            throw exc
        } catch (exc: Exception) {
            throw BiliException("分享链接解析失败：${exc.message ?: "网络错误"}")
        }
    }

    private fun unsupported(url: String): Nothing {
        val host = hostOf(url).orEmpty()
        val message = when {
            host == "b23.tv" || host == "bili2233.cn" ->
                "分享链接已失效，或不是普通视频"
            host.contains("live.bilibili.com") ->
                "这是直播链接，暂不支持下载"
            url.contains("/bangumi/") || url.contains("/anime/") ->
                "这是番剧/影视链接，暂不支持下载"
            url.contains("/cheese/") ->
                "这是课程链接，暂不支持下载"
            host == "t.bilibili.com" || url.contains("/opus/") || url.contains("/read/") ->
                "这是动态/专栏链接，不是普通视频"
            host.contains("passport.bilibili.com") || url.contains("login") ->
                "分享链接需要登录或已失效"
            else ->
                "没有从分享链接里解析到 BV 号（链接可能已失效或不是视频）"
        }
        throw BiliException(message)
    }

    /** 把 B站 的错误码翻译成人话，尤其是付费/权限类。 */
    private fun friendly(code: Int, message: String): String = when (code) {
        -404, -10403, 87008, 62012 -> "该视频需要充电或大会员，当前账号无权观看"
        -403 -> "无权访问该视频（可能需要充电/大会员）"
        62002 -> "稿件不可见（可能已删除或仅粉丝可见）"
        62004 -> "稿件正在审核中"
        -352 -> "接口签名校验失败，请更新到最新版本"
        -400 -> "请求参数有误（视频可能已下架）"
        else -> if (message.isNotBlank()) "$message（code=$code）" else "请求失败（code=$code）"
    }

    fun videoInfo(id: String, page: Int): VideoInfo {
        val isAv = id.startsWith("av")
        val key = if (isAv) "aid" else "bvid"
        val value = if (isAv) id.removePrefix("av") else id
        val json = JSONObject(get("https://api.bilibili.com/x/web-interface/view?$key=$value"))
        val viewCode = json.optInt("code")
        if (viewCode != 0) {
            throw BiliException(friendly(viewCode, json.optString("message")))
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
        val playCode = json.optInt("code")
        if (playCode != 0) {
            throw BiliException(friendly(playCode, json.optString("message")))
        }
        val data = json.getJSONObject("data")
        val dash = data.optJSONObject("dash")
            ?: throw BiliException("没有可用的视频流（番剧/付费内容请在 B站 App 内观看）")

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
