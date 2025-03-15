package com.yuyan.inputmethod.handwriting

import com.yuyan.imemodule.callback.IHandWritingCallBack

/**
 * 手写
 */
class HdManager private constructor() {
    private var handWritingMonitor: HandWritingMonitor = HandWritingHanwang()

    init {
        handWritingMonitor.initHdw()
    }
    /**
     * 识别手势方法
     */
    fun recognitionData(strokes: MutableList<Short?>, recogResult: IHandWritingCallBack){
        handWritingMonitor.recognitionData(strokes, recogResult)
    }
    companion object {
        private var manager: HdManager? = null

        @JvmStatic
        @get:Synchronized
        val instance: HdManager?
            get() {
                if (manager == null) {
                    manager = HdManager()
                }
                return manager
            }
    }
}
