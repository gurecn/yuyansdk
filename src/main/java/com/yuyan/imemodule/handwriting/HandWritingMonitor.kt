package com.yuyan.imemodule.handwriting

import com.yuyan.imemodule.callback.IHandWritingCallBack

interface HandWritingMonitor {
    fun recognitionData(strokes: MutableList<Short?>, recogResult: IHandWritingCallBack)
}
