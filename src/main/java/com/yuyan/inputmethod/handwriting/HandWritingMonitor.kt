package com.yuyan.inputmethod.handwriting

import com.yuyan.imemodule.callback.IHandWritingCallBack

interface HandWritingMonitor {
    fun initHdw(): Boolean
    fun recognitionData(strokes: MutableList<Short?>, recogResult: IHandWritingCallBack)
}
