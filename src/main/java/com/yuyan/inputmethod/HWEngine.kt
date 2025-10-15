package com.yuyan.inputmethod

import com.yuyan.imemodule.application.Launcher
import com.yuyan.imemodule.callback.IHandWritingCallBack
import com.yuyan.imemodule.libs.pinyin4j.PinyinHelper
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinCaseType
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinOutputFormat
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinToneType
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinVCharType
import com.yuyan.imemodule.utils.thread.ThreadPoolUtils
import com.yuyan.inputmethod.core.CandidateListItem
import com.yuyan.inputmethod.core.HandWriting
import java.util.Collections

object HWEngine {
    private var isRecognitionState = false
    private var  nextDatas  =  Collections.synchronizedList(mutableListOf<Pair<MutableList<Short?>, IHandWritingCallBack>?>())
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

    fun getSettingOptions(): Array<String?> {
        val arrayList: ArrayList<String> = ArrayList(10)
        arrayList.add("Emoji")
        arrayList.add("0")
        arrayList.add("EnableFuzzy")
        arrayList.add("0")
        arrayList.add("Fuzzy")
        arrayList.add("0")
        arrayList.add("9KeyCorrect")
        arrayList.add("0")
        arrayList.add("26KeyCorrect")
        arrayList.add("0")
        return arrayList.toTypedArray() as Array<String?>
    }

    fun recognitionData(strokes: MutableList<Short?>, recogResult: IHandWritingCallBack){
        nextDatas.add(Pair(strokes, recogResult))
        if(isRecognitionState) return
        isRecognitionState = true
        ThreadPoolUtils.Companion.executeSingleton {
            while (true) {
                if(nextDatas.isEmpty()) break
                val data = nextDatas.removeAt(0)
                nextDatas.clear()
                HandWriting.reset()
                val strokesData = data?.first?.toMutableList()
                val intArray = strokesData!!.filterNotNull().map { it.toInt() }.toIntArray()
                HandWriting.inputHWPoints(intArray)
                val candidatesPyComposition = HandWriting.getCandidatesPyComposition()
                val candidates = candidatesPyComposition[0]
                if (candidates != null && candidates.isNotEmpty()) {
                    val recogResultData = data.second
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
            isRecognitionState = false
        }
    }
}