package com.writebugs.videochanger.bili

import java.net.URLEncoder
import java.security.MessageDigest

/**
 * B站 WBI 签名。
 *
 * 流程：/x/web-interface/nav 返回 img_url / sub_url → 取出两个 key →
 * 按固定表重排出 mixinKey（前 32 位）→ 参数排序 + wts → md5(query + mixinKey) = w_rid
 */
object Wbi {

    private val MIXIN_KEY_ENC_TAB = intArrayOf(
        46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
        33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
        61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
        36, 20, 34, 44, 52,
    )

    /** 从 nav 的两个 URL 里取出文件名，再重排出 32 位 mixinKey。 */
    fun mixinKey(imgUrl: String, subUrl: String): String {
        val raw = keyFromUrl(imgUrl) + keyFromUrl(subUrl)
        val builder = StringBuilder()
        for (index in MIXIN_KEY_ENC_TAB) {
            if (index < raw.length) builder.append(raw[index])
        }
        return builder.take(32).toString()
    }

    /** 生成带 w_rid / wts 的查询串（不含问号）。 */
    fun sign(params: Map<String, String>, mixinKey: String): String {
        val all = params.toMutableMap()
        all["wts"] = (System.currentTimeMillis() / 1000).toString()
        val query = all.entries
            .sortedBy { it.key }
            .joinToString("&") { "${it.key}=${encode(it.value)}" }
        return "$query&w_rid=${md5(query + mixinKey)}"
    }

    private fun encode(value: String): String =
        URLEncoder.encode(value, "UTF-8")
            .replace("+", "%20")
            .replace("%21", "!")
            .replace("%27", "'")
            .replace("%28", "(")
            .replace("%29", ")")
            .replace("%2A", "*")

    fun md5(text: String): String =
        MessageDigest.getInstance("MD5")
            .digest(text.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

    private fun keyFromUrl(url: String): String =
        url.substringAfterLast('/').substringBefore('.')
}
