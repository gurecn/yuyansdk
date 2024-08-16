package com.yuyan.imemodule.handwriting

import com.yuyan.imemodule.handwriting.entity.HwrRecogResult

class HandWritingHanwang : HandWritingMonitor {
    override fun initHdw(): Boolean {
        return false
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
        return false
    }

    override fun hciHwrRelease() {
        sInitState = false
    }

    companion object {
        private var sInitState = false
    }
}
