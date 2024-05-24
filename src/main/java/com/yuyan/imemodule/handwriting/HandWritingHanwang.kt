package com.yuyan.imemodule.handwriting

import com.hanvon.inputmethod.library.Native
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.handwriting.entity.HwrRecogResult
import com.yuyan.imemodule.handwriting.entity.HwrRecogResultItem
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import com.yuyan.imemodule.prefs.behavior.WritingRCMode
import java.io.File

class HandWritingHanwang : HandWritingMonitor {
    override fun initHdw(): Boolean {
        // step. 1 初始化
        Native.nativeHwInitWorkspace()
        val dictPath = CustomConstant.HDW_HANWANG_DICT_PATH + File.separator + "HW_REC_CN.bin"
        // Step. 2 设置核心字典和识别语言
        sInitState = Native.nativeHwSetDicAndLanguage(dictPath, Native.HW_RC_LANGUAGE_CN)
        // Step. 3 设置识别模式
        val rcMode = getInstance().handwriting.handWritingRCMode.getValue()
        when (rcMode) {
            WritingRCMode.SENTENCE -> {
                Native.nativeHwSetMode(Native.HWRC_SENTENCE)
            }
            WritingRCMode.OVERLAP -> {
                Native.nativeHwSetMode(Native.HWRC_OVERLAP)
            }
            else -> {
                Native.nativeHwSetMode(Native.HWRC_SENTENCE_OVERLAP)
            }
        }
        // Step. 4 设置识别范围
        Native.nativeHwSetRange(Native.ALC_CHS_GB18030)
        // Step. 5 设置是否支持倾斜
        Native.nativeHwSetSlantScope(20)
        Native.nativeHwGetVersion()
        Native.nativeHwGetDictVersion()
        return sInitState
    }

    override fun recognitionData(strokes: List<Short?>, recogResult: HwrRecogResult): Boolean {
        val countTemp = strokes.size
        var i = 0
        val mTracksTemp = ShortArray(countTemp + 2)
        for (sh in strokes) {
            mTracksTemp[i++] = sh!!
        }
        mTracksTemp[i++] = -1
        mTracksTemp[i] = -1
        return if (sInitState) {
            var count = Native.nativeHwRecognize(mTracksTemp)
            val recogItemList = ArrayList<HwrRecogResultItem>()
            while (count > 0) {
                val resultItem = HwrRecogResultItem()
                resultItem.result = Native.nativeHwGetResult()
                recogItemList.add(resultItem)
                count--
            }
            recogResult.resultItemList = recogItemList
            true
        } else {
            initHdw()
            false
        }
    }

    override fun hciHwrRelease() {
        sInitState = false
        Native.nativeHwReleaseWorkspace()
    }

    companion object {
        private var sInitState = false
    }
}
