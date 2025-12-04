package com.yuyan.inputmethod

import com.yuyan.imemodule.application.Launcher
import com.yuyan.imemodule.callback.IHandWritingCallBack
import com.yuyan.imemodule.libs.pinyin4j.PinyinHelper
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinCaseType
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinOutputFormat
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinToneType
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinVCharType
import com.yuyan.inputmethod.core.CandidateListItem
import com.yuyan.inputmethod.core.HandWriting

object HWEngine {
    private var mHanyuPinyinOutputFormat: HanyuPinyinOutputFormat

    init {
        HandWriting.init(Launcher.instance.context)
        HandWriting.setProperties()
        HandWriting.selectInputMode(5)
        mHanyuPinyinOutputFormat = HanyuPinyinOutputFormat()
        mHanyuPinyinOutputFormat.caseType = HanyuPinyinCaseType.LOWERCASE
        mHanyuPinyinOutputFormat.toneType = HanyuPinyinToneType.WITH_TONE_MARK
        mHanyuPinyinOutputFormat.vCharType = HanyuPinyinVCharType.WITH_U_UNICODE
    }

    fun recognitionData(strokes: MutableList<Short?>, recogResult: IHandWritingCallBack){
        HandWriting.reset()
        val strokesData = strokes.toMutableList()
        val intArray = strokesData.filterNotNull().map { it.toInt() }.toIntArray()
        HandWriting.inputHWPoints(intArray)
        val candidatesPyComposition = HandWriting.getCandidatesPyComposition()
        val candidates = candidatesPyComposition[0]
        if (candidates != null && candidates.isNotEmpty()) {
            val recogResultData = recogResult
            val recogResultItems = ArrayList<CandidateListItem>()
            for (candidate in candidates) {
                recogResultItems.add(
                    CandidateListItem(
                        PinyinHelper.toHanYuPinyin(candidate!!, mHanyuPinyinOutputFormat, "'").ifEmpty { candidate }, candidate
                    )
                )
            }
            recogResultData.onSucess(recogResultItems.toTypedArray())
        }
    }
}