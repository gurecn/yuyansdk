package com.yuyan.imemodule.service

import android.view.KeyEvent
import androidx.lifecycle.MutableLiveData
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.inputmethod.core.CandidateListItem
import com.yuyan.inputmethod.core.Kernel

/**
 * 词库解码操作对象
 */
object DecodingInfo {
    // 候选词列表
    val candidatesLiveData = MutableLiveData<List<CandidateListItem?>>()

    // 是否是联想词
    var isAssociate = false

    /**
     * 重置
     */
    fun reset() {
        isAssociate = false
        candidatesLiveData.postValue(emptyList())
        Kernel.reset()
    }

    val isCandidatesListEmpty: Boolean
        // 候选词列表是否为空
        get() = candidatesLiveData.value.isNullOrEmpty()

    // 增加拼写字符
    fun inputAction(keycode: Int, inputMode: InputModeSwitcherManager) {
        if (Kernel.unHandWriting()) {
            Kernel.inputKeyCode(keycode, inputMode)
            isAssociate = false
        } else if(keycode == KeyEvent.KEYCODE_DEL) {  // 手写删除符号
            candidatesLiveData.postValue(emptyList())
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
        get() = if(Kernel.unHandWriting()) Kernel.isFinish else candidatesLiveData.value.isNullOrEmpty()

    val composingStrForDisplay: String   //获取显示的拼音字符串/
        get() = Kernel.wordsShowPinyin

    val composingStrForCommit: String   // 获取输入的拼音字符串
        get() = Kernel.wordsShowPinyin.replace("'", "")

    val nextPageCandidates: Int   // 获取下一页的候选词
        get() {
            val candidates = Kernel.nextPageCandidates
            if (candidates.isNotEmpty()) {
                candidatesLiveData.postValue(candidatesLiveData.value?.plus(candidates))
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
            if (candId >= 0) Kernel.getWordSelectedWord(candId)
            candidatesLiveData.postValue(Kernel.candidates.asList())
            Kernel.commitText
        } else if(candId >=0 && candidatesLiveData.value!!.size > candId){
            val choice = candidatesLiveData.value!![candId]?.text?:""
            reset()
            choice
        } else ""
    }

    /**
     * 获得指定的候选词
     */
    fun getCandidate(candId: Int): CandidateListItem? {
        return candidatesLiveData.value?.getOrNull(candId)
    }

    // 更新候选词
    fun cacheCandidates(words: ArrayList<CandidateListItem?>) {
        candidatesLiveData.postValue(words)
    }
}