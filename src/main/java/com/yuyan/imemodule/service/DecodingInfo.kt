package com.yuyan.imemodule.service

import android.view.KeyEvent
import com.yuyan.inputmethod.core.CandidateListItem
import com.yuyan.inputmethod.core.Kernel

/**
 * 词库解码操作对象
 */
class DecodingInfo {
    //  候选词列表
    var mCandidatesList: MutableList<CandidateListItem?> = mutableListOf()

    /**
     * 是否是联想词
     */
    // 是否是联想词
    var isAssociate = false

    /**
     * 重置
     */
    fun reset() {
        isAssociate = false
        mCandidatesList.clear()
        Kernel.reset()
    }

    val isCandidatesListEmpty: Boolean
        // 候选词列表是否为空
        get() = mCandidatesList.size == 0

    // 增加拼写字符
    fun inputAction(keycode: Int) {
        if (Kernel.unHandWriting()) {
            Kernel.inputKeyCode(keycode)
            isAssociate = false
        } else if(keycode == KeyEvent.KEYCODE_DEL) {  // 手写删除符号
            mCandidatesList.clear()
        }
    }

    /**
     * 选择拼音
     * @param position 选择的position
     */
    fun selectPrefix(position: Int) {
        if (Kernel.unHandWriting()) {
            Kernel.selectPrefix(position)
        }
    }

    val prefixs: Array<String>  //获取拼音组合
        get() = Kernel.prefixs

    /**
     * 删除
     */
    fun deleteAction() {
        if (Kernel.unHandWriting()) {
            Kernel.deleteAction()
        } else {
            reset()
        }
    }

    val isFinish: Boolean  //是否输入完毕，等待上屏。
        get() = if(Kernel.unHandWriting()) Kernel.isFinish else mCandidatesList.isEmpty()

    val composingStrForDisplay: String   //获取显示的拼音字符串/
        get() = Kernel.wordsShowPinyin

    val composingStrForCommit: String   // 获取输入的拼音字符串
        get() = Kernel.wordsShowPinyin.replace("'", "")

    val nextPageCandidates: Int   // 获取下一页的候选词
        get() {
            val candidates = Kernel.nextPageCandidates
            if (candidates.isNotEmpty()) {
                mCandidatesList.addAll(candidates)
                return candidates.size
            }
            return 0
        }

    /**
     * 如果candId〉0，就选择一个候选词，并且重新获取一个候选词列表，选择的候选词存放在mComposingStr中，通过mDecInfo.
     * getComposingStrActivePart()取出来。如果candId小于0 ，就对输入的拼音进行查询。
     */
    fun chooseDecodingCandidate(candId: Int): String {
        return if (Kernel.unHandWriting()) {
            mCandidatesList.clear()
            if (candId >= 0) Kernel.getWordSelectedWord(candId)
            mCandidatesList.addAll(Kernel.candidates)
            Kernel.commitText
        } else if(candId >=0 && mCandidatesList.size > candId){
            val choice = mCandidatesList[candId]?.text?:""
            reset()
            choice
        } else ""
    }

    /**
     * 获得指定的候选词
     */
    fun getCandidate(candId: Int): CandidateListItem? {
        return mCandidatesList.getOrNull(candId)
    }

    // 更新候选词
    fun cacheCandidates(words: ArrayList<CandidateListItem?>) {
        mCandidatesList.clear()
        mCandidatesList.addAll(words)
    }
}