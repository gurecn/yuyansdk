package com.yuyan.inputmethod

import com.yuyan.inputmethod.core.Rime

/**
 * 常用语存储，数据由 Rime 引擎保存在用户目录，三端共用同一份文件。
 */
object PhraseStore {

    private val t9KeyLetters = "adgjmptw".toSet()

    data class Entry(
        val content: String,
        val codes: List<String>,
        val keep: Boolean,
        val updatedAt: Long,
    ) {
        /** 编辑器展示用的字母编码 */
        val qwerty: String
            get() {
                val pinyin = codes.filter { code -> code.all { it in 'a'..'z' } }
                return pinyin.firstOrNull { code -> code.any { it !in t9KeyLetters } }
                    ?: pinyin.firstOrNull().orEmpty()
            }
    }

    fun list(): List<Entry> = Rime.getPhrases().lineSequence()
        .filter { it.isNotBlank() }
        .mapNotNull(::parse)
        .toList()

    fun query(code: String): List<Entry> {
        val needle = code.trim().lowercase()
        if (needle.isEmpty()) return emptyList()
        return list().filter { needle in it.codes }
    }

    fun add(codes: Collection<String>, content: String, keep: Boolean = false): Boolean {
        val csv = codes.asSequence().map { it.trim() }.filter { it.isNotEmpty() }.joinToString(",")
        if (csv.isEmpty() || content.isEmpty()) return false
        return Rime.addPhrase(csv, content, keep)
    }

    fun remove(content: String): Boolean = Rime.removePhrase(content)

    private fun parse(line: String): Entry? {
        val fields = line.split('\t')
        if (fields.size < 4) return null
        val codes = fields[0].split(',').filter { it.isNotEmpty() }
        val content = unescape(fields[3])
        if (codes.isEmpty() || content.isEmpty()) return null
        return Entry(
            content = content,
            codes = codes,
            keep = fields[1].trim() == "1",
            updatedAt = fields[2].trim().toLongOrNull() ?: 0L,
        )
    }

    private fun unescape(value: String): String {
        if ('\\' !in value) return value
        val builder = StringBuilder(value.length)
        var index = 0
        while (index < value.length) {
            val current = value[index]
            if (current != '\\' || index + 1 >= value.length) {
                builder.append(current)
                index++
                continue
            }
            when (val next = value[++index]) {
                't' -> builder.append('\t')
                'n' -> builder.append('\n')
                'r' -> builder.append('\r')
                '\\' -> builder.append('\\')
                else -> builder.append('\\').append(next)
            }
            index++
        }
        return builder.toString()
    }
}
