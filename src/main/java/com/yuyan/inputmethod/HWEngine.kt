package com.yuyan.inputmethod

import com.yuyan.imemodule.handwriting.HandwritingRecognizer
import com.yuyan.imemodule.handwriting.ShortStrokeParser
import com.yuyan.imemodule.application.Launcher
import com.yuyan.imemodule.callback.IHandWritingCallBack
import com.yuyan.imemodule.libs.pinyin4j.PinyinHelper
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinCaseType
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinOutputFormat
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinToneType
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinVCharType
import com.yuyan.inputmethod.core.CandidateListItem

object HWEngine {

    private val hanyuPinyinFormat = HanyuPinyinOutputFormat().apply {
        caseType = HanyuPinyinCaseType.LOWERCASE
        toneType = HanyuPinyinToneType.WITH_TONE_MARK
        vCharType = HanyuPinyinVCharType.WITH_U_UNICODE
    }

    fun prepare() {
        if (HandwritingRecognizer.isReady) return
        HandwritingRecognizer.initializeAsync(Launcher.instance.context)
    }

    fun recognitionData(strokes: MutableList<Short?>, recogResult: IHandWritingCallBack) {
        val parsed = ShortStrokeParser.parse(strokes)
        if (parsed.isEmpty()) return

        if (!HandwritingRecognizer.isReady && !HandwritingRecognizer.initialize(Launcher.instance.context)) return

        val candidates = HandwritingRecognizer.recognizeBlocking(parsed)
        if (candidates.isEmpty()) return

        val items = ArrayList<CandidateListItem>(candidates.size)
        val seen = HashSet<String>(candidates.size)
        for (candidate in candidates) {
            val text = candidate.text.trim()
            if (text.isEmpty()) continue
            if (!seen.add(text)) continue
            items.add(CandidateListItem(pinyinOf(text), text))
        }
        if (items.isEmpty()) return

        recogResult.onSucess(items.toTypedArray())
    }

    private fun pinyinOf(text: String): String =
        runCatching { PinyinHelper.toHanYuPinyin(text, hanyuPinyinFormat, "'") }.getOrNull().orEmpty().ifEmpty { text }
}
