package com.yuyan.imemodule.handwriting

import com.yuyan.imemodule.callback.IHandWritingCallBack

/**
 * User:Gaolei  gurecn@gmail.com
 * Date:2018/4/2
 * I'm glad to share my knowledge with you all.
 */
class HdManager private constructor() {
    private var handWritingMonitor: HandWritingMonitor = HandWritingHanwang()

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
