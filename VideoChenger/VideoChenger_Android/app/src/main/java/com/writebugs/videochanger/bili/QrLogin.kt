package com.writebugs.videochanger.bili

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLDecoder
import java.util.concurrent.TimeUnit

/**
 * B站扫码登录。
 *
 * 和网页版同一个流程，只是把二维码画在自己 App 里：
 *   1. /x/passport-login/web/qrcode/generate 拿 qrcode_key + url
 *   2. 用二维码库把 url 画成图，用户用 B站 App 扫
 *   3. 轮询 /qrcode/poll，成功后从回调 url 或 Set-Cookie 里取 SESSDATA
 *
 * 好处：不需要用户输密码，也不用教他开浏览器开发者工具。
 */
class QrLogin {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val userAgent =
        "Mozilla/5.0 (Linux; Android 12) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/122.0.0.0 Mobile Safari/537.36"

    data class Session(val key: String, val url: String)

    sealed interface Poll {
        /** 还没扫 */
        data object Waiting : Poll

        /** 已经扫了，等手机上点确认 */
        data object Scanned : Poll

        /** 二维码过期 */
        data object Expired : Poll

        data class Success(val sessdata: String) : Poll

        data class Failed(val message: String) : Poll
    }

    private data class Response(val body: String, val setCookies: List<String>)

    private fun get(url: String): Response {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", userAgent)
            .header("Referer", "https://www.bilibili.com/")
            .build()
        client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) throw BiliException("HTTP ${response.code}")
            return Response(body, response.headers.values("Set-Cookie"))
        }
    }

    fun generate(): Session {
        val response = get("https://passport.bilibili.com/x/passport-login/web/qrcode/generate")
        val json = JSONObject(response.body)
        if (json.optInt("code") != 0) {
            throw BiliException("二维码生成失败：${json.optString("message")}")
        }
        val data = json.getJSONObject("data")
        val key = data.optString("qrcode_key")
        val url = data.optString("url")
        if (key.isBlank() || url.isBlank()) throw BiliException("二维码数据为空")
        return Session(key, url)
    }

    fun poll(key: String): Poll {
        val response = get(
            "https://passport.bilibili.com/x/passport-login/web/qrcode/poll?qrcode_key=$key",
        )
        val json = JSONObject(response.body)
        if (json.optInt("code") != 0) {
            return Poll.Failed(json.optString("message", "轮询失败"))
        }
        val data = json.optJSONObject("data") ?: return Poll.Failed("轮询返回为空")

        return when (data.optInt("code")) {
            0 -> {
                val sessdata = extractFromUrl(data.optString("url"))
                    ?: extractFromCookies(response.setCookies)
                if (sessdata.isNullOrBlank()) {
                    Poll.Failed("登录成功，但没取到 SESSDATA")
                } else {
                    Poll.Success(sessdata)
                }
            }
            86090 -> Poll.Scanned
            86038 -> Poll.Expired
            else -> Poll.Waiting
        }
    }

    private fun extractFromUrl(url: String): String? {
        val query = url.substringAfter('?', "")
        return query.split('&')
            .firstOrNull { it.startsWith("SESSDATA=") }
            ?.substringAfter('=')
            ?.takeIf { it.isNotBlank() }
            ?.let { runCatching { URLDecoder.decode(it, "UTF-8") }.getOrDefault(it) }
    }

    private fun extractFromCookies(cookies: List<String>): String? {
        for (cookie in cookies) {
            val first = cookie.substringBefore(';')
            if (first.startsWith("SESSDATA=")) {
                return first.substringAfter('=')
            }
        }
        return null
    }
}
