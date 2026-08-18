package com.yuyan.imemodule.utils

/**
 * 分词工具：URL 原子识别 + 中英文分词 + 合并时空格重建
 */
private val URL_PATTERN = Regex(
    // 前缀 https:// http:// 或 www.（前面不能是 ASCII 字母/数字/_/.，防止匹配 awww.x）
    // 注意：必须用显式 ASCII 类而非 \w —— Android 的 ICU 正则中 \w 是 Unicode 感知的（汉字属于 \w），
    // 会导致 "看https://..." 的负前瞻失败、URL 掉进分词器被切碎
    // URL 主体：非空白、非汉字(\u4e00-\u9fff)、非中文标点(\u3000-\u303f 全角标点 \uff00-\uffef)
    "(?i)(?<![A-Za-z0-9_.])(?:https?://|www\\.)[^\\s\\u4e00-\\u9fff\\u3000-\\u303f\\uff00-\\uffef]+"
)

// URL 尾部需剥离的半角标点（避免吞掉句子标点，如 "见https://a.com," 的逗号）
private val URL_TRAILING_TRIM = setOf('.', ',', ';', ':', '!', '?', ')', '"', '\'', '>', ']', '}')

fun segmentText(text: String): List<String> {
    if (text.isBlank()) return emptyList()
    val result = mutableListOf<String>()
    var cursor = 0
    for (match in URL_PATTERN.findAll(text)) {
        val start = match.range.first
        var end = match.range.last + 1
        // 剥离尾部标点，但保留 前缀+至少1个字符（www.x 之类不误剥）
        val prefixLen = match.value.indexOf("://").let { if (it >= 0) it + 3 else 4 }
        while (end > start + prefixLen + 1 && text[end - 1] in URL_TRAILING_TRIM) end--
        if (end <= start + prefixLen) continue // 仅剩前缀，视为误匹配
        if (start > cursor) segmentRange(text, cursor, start, result)
        result.add(text.substring(start, end))
        cursor = end
    }
    if (cursor < text.length) segmentRange(text, cursor, text.length, result)
    return result
}

private fun segmentRange(text: String, from: Int, to: Int, out: MutableList<String>) {
    val boundaries = java.text.BreakIterator.getWordInstance(java.util.Locale.CHINA)
    boundaries.setText(text.substring(from, to))
    var start = boundaries.first()
    var end = boundaries.next()
    while (end != java.text.BreakIterator.DONE) {
        val token = text.substring(from + start, from + end)
        if (!token.isBlank()) out.add(token)
        start = end
        end = boundaries.next()
    }
}

/**
 * 按选择顺序（原序）合并 token。
 * 空格重建：相邻两个 token 边界字符都是 ASCII 字母/数字时补一个空格
 * —— 英文单词之间补回空格；标点紧跟不补；中文与任何 token 之间不补。
 */
fun mergeTokens(tokens: List<String>, selectedIndices: Collection<Int>): String {
    val sb = StringBuilder()
    var prev: String? = null
    for (index in selectedIndices.sorted()) {
        val token = tokens.getOrNull(index) ?: continue
        val p = prev
        if (p != null && p.isNotEmpty() && token.isNotEmpty() &&
            p.last().isAsciiAlnum() && token.first().isAsciiAlnum()
        ) {
            sb.append(' ')
        }
        sb.append(token)
        prev = token
    }
    return sb.toString()
}

private fun Char.isAsciiAlnum(): Boolean =
    this in 'a'..'z' || this in 'A'..'Z' || this in '0'..'9'
