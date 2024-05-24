package com.yuyan.imemodule.handwriting

import com.yuyan.imemodule.handwriting.entity.HwrRecogResult

interface HandWritingMonitor {
    fun initHdw(): Boolean
    fun recognitionData(strokes: List<Short?>, recogResult: HwrRecogResult): Boolean
    fun hciHwrRelease()
}
