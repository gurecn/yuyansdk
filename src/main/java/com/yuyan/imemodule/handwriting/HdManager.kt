package com.yuyan.imemodule.handwriting

import com.yuyan.imemodule.handwriting.entity.HwrRecogResult

/**
 * User:Gaolei  gurecn@gmail.com
 * Date:2018/4/2
 * I'm glad to share my knowledge with you all.
 */
class HdManager private constructor() {
    private var handWritingMonitor: HandWritingMonitor? = null

    init {
        if (!sInitState) {
            initHdw()
        }
    }

    /**
     * 初始化方法
     */
    fun initHdw() {
        handWritingMonitor = HandWritingHanwang()
        sInitState = (handWritingMonitor as HandWritingHanwang).initHdw()
    }

    /**
     * 识别手势方法
     */
    fun recognitionData(strokes: List<Short?>, recogResult: HwrRecogResult): Boolean {
        if (sInitState && handWritingMonitor != null) {
            return handWritingMonitor!!.recognitionData(strokes, recogResult)
        } else {
            initHdw()
        }
        return false
    }

    /**
     * 释放识别资源
     */
    fun hciHwrRelease() {
        sInitState = false
        manager = null
        if (handWritingMonitor != null) handWritingMonitor!!.hciHwrRelease()
    }

    companion object {
        private var sInitState = false
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
