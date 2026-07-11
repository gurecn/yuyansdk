package com.yuyan.imemodule.service

import android.view.KeyEvent
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.inputmethod.RimeEngine

object InputDispatcher {

    // 初始化输入法
    @Synchronized
    fun initImeSchema(schema: String) {
        RimeEngine.selectSchema(schema)
        nativeUpdateImeOption()
    }

    fun getCurrentRimeSchema(): String {
        return RimeEngine.getCurrentRimeSchema()
    }

    // 传入一个键码
    fun inputKeyCode(event: KeyEvent) {
        DictDecoder.inputAction()
        RimeEngine.onNormalKey(event)
    }

    // 是否输入完毕，等待上屏。
    val isFinish: Boolean
        get() = RimeEngine.isFinish()

    // 选择某个候选拼音
    fun selectPrefix(index: Int) {
        DictDecoder.selectPrefix(index)
        RimeEngine.selectPinyin(index)
    }

    // 执行选择动作，选择了index指向的词语
    fun getWordSelectedWord(index: Int) {
        if (DictDecoder.isAssociate) RimeEngine.selectAssociation(index)
        else if(!isFinish)RimeEngine.selectCandidate(index)
    }

    // 删除操作
    fun deleteAction() {
        DictDecoder.deleteAction()
        RimeEngine.onDeleteKey()
    }

    // 重置输入状态
    fun reset() {
        DictDecoder.reset()
        RimeEngine.reset()
    }

    // 释放内存
    fun resetIme() {
        RimeEngine.destroy()
        initImeSchema(AppPrefs.getInstance().internal.pinyinModeRime.getValue())
    }

    // 根据输入的字符查询候选词
    fun getAssociateWord(words: String) {
        DictDecoder.getAssociateWord(words)
        RimeEngine.predictAssociationWords(words)
    }

    // 刷新引擎配置
    fun nativeUpdateImeOption() {
        val chineseFanTi = AppPrefs.getInstance().input.chineseFanTi.getValue()
        RimeEngine.setImeOption("traditionalization", chineseFanTi)
        val emojiInput = AppPrefs.getInstance().input.emojiInput.getValue()
        RimeEngine.setImeOption("emoji", emojiInput)
    }

    fun setCharCase(charCase: Int) {
        RimeEngine.setCharCase(charCase)
    }
}