package com.yuyan.inputmethod.core

import android.view.KeyEvent
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import com.yuyan.inputmethod.RimeEngine
import com.yuyan.inputmethod.RimeEngine.destroy
import com.yuyan.inputmethod.RimeEngine.getNextPageCandidates
import com.yuyan.inputmethod.RimeEngine.getPrefixs
import com.yuyan.inputmethod.RimeEngine.isFinish
import com.yuyan.inputmethod.RimeEngine.onDeleteKey
import com.yuyan.inputmethod.RimeEngine.onNormalKey
import com.yuyan.inputmethod.RimeEngine.preCommitText
import com.yuyan.inputmethod.RimeEngine.selectCandidate
import com.yuyan.inputmethod.RimeEngine.selectPinyin
import com.yuyan.inputmethod.RimeEngine.selectSchema
import com.yuyan.inputmethod.RimeEngine.setImeOption
import com.yuyan.inputmethod.RimeEngine.showCandidates
import com.yuyan.inputmethod.RimeEngine.showComposition

object Kernel {
    private var isHandWriting = false

    /**
     * 初始化输入法
     */
    @Synchronized
    fun initWiIme(schema: String, inputMode: InputModeSwitcherManager? = null) {
        isHandWriting = schema == CustomConstant.SCHEMA_ZH_HANDWRITING
        selectSchema(schema, inputMode)
        nativeUpdateImeOption()
    }

    /**
     * 判断是否是手写模式
     */
    fun unHandWriting(): Boolean {
        return !isHandWriting
    }

    /**
     * 传入一个键码
     */
    fun inputKeyCode(keyCode: Int, event: KeyEvent) {
        onNormalKey(keyCode)
    }

    val isFinish: Boolean
        /**
         * 是否输入完毕，等待上屏。
         */
        get() = !isFinish()
    val candidates: Array<CandidateListItem>
        get() {
            return showCandidates
        }
    val nextPageCandidates: List<CandidateListItem>
        get() {
            return getNextPageCandidates()
        }
    val prefixs: Array<String>
        /**
         * 拿到候选词拼音
         */
        get() {
            return getPrefixs()
        }

    /**
     * 选择某个候选拼音
     */
    fun selectPrefix(index: Int) {
        selectPinyin(index)
    }

    /**
     * 执行选择动作，选择了index指向的词语
     */
    fun getWordSelectedWord(index: Int) {
        selectCandidate(index)
    }

    val wordsShowPinyin: CharSequence
        /**
         * 最上端拼音行
         */
        get() = showComposition
    val commitText: String
        /**
         * 得到即将上屏的候选词
         */
        get() {
            return preCommitText
        }

    /**
     * 删除操作
     */
    fun deleteAction() {
        onDeleteKey()
    }

    fun reset() {
        RimeEngine.reset()
    }

    /**
     * 释放内存,保存自造词库
     * 简体和繁体切换需要释放内存
     */
    fun freeIme() {
        destroy()
    }

    /**
     * 根据输入的字符查询候选词
     */
    fun getAssociateWord(words: String?) {
//        RimeEngine.INSTANCE.predictAssociationWords(words);
    }

    /**
     * 刷新引擎配置
     */
    fun nativeUpdateImeOption() {
        val chineseFanTi = getInstance().input.chineseFanTi.getValue()
        setImeOption("traditionalization", chineseFanTi)
        val emojiInput = getInstance().input.emojiInput.getValue()
        setImeOption("emoji", emojiInput)
    }
}
