package com.yuyan.imemodule.service

import android.view.KeyEvent
import androidx.lifecycle.MutableLiveData
import com.yuyan.inputmethod.core.CandidateListItem
import com.yuyan.inputmethod.core.Kernel

/**
 * 词库解码操作对象
 */
object DecodingInfo {

    var activeCandidate = 0  //当前显示候选词位置
    var activeCandidateBar = 0  //当前显示候选词位置
    // 候选词列表
    val candidatesLiveData = MutableLiveData<List<CandidateListItem>>()

    // 是否是联想词
    var isAssociate = false
    // 是否重置
    private var isReset = false

    /**
     * 重置
     */
    fun reset() {
        isAssociate = false
        isReset = true
        activeCandidate = 0
        activeCandidateBar = 0
        candidatesLiveData.value = emptyList()
        Kernel.reset()
    }

    val isCandidatesListEmpty: Boolean
        // 候选词列表是否为空
        get() = candidatesLiveData.value.isNullOrEmpty()

    val candidateSize: Int
        // 候选词列表是否为空
        get() = if(isCandidatesListEmpty) 0 else candidatesLiveData.value!!.size


    val candidates: List<CandidateListItem>
        // 候选词列表是否为空
        get() = candidatesLiveData.value?:emptyList()

    fun getCurrentRimeSchema(): String {
        return Kernel.getCurrentRimeSchema()
    }

    // 增加拼写字符
    fun inputAction(event: KeyEvent) {
        isReset = false
        activeCandidate = 0
        activeCandidateBar = 0
        Kernel.inputKeyCode(event)
        isAssociate = false
    }

    /**
     * 选择拼音
     * @param position 选择的position
     */
    fun selectPrefix(position: Int) {
        activeCandidate = 0
        activeCandidateBar = 0
        Kernel.selectPrefix(position)
    }

    val prefixs: Array<String>  //获取拼音组合
        get() = Kernel.prefixs

    /**
     * 删除
     */
    fun deleteAction() {
        activeCandidate = 0
        activeCandidateBar = 0
        if (isEngineFinish || isAssociate) {
            reset()
        } else {
            Kernel.deleteAction()
        }
    }

    val isFinish: Boolean
        get() = isEngineFinish && isCandidatesListEmpty

    val isEngineFinish: Boolean
        get() = Kernel.isFinish

    val composingStrForDisplay: String   //获取显示的拼音字符串/
        get() = Kernel.wordsShowPinyin

    val composingStrForCommit: String   // 获取输入的拼音字符串
        get() = Kernel.wordsShowPinyin.replace("'", "").ifEmpty { getCandidate(0)?.text?:""}

    val nextPageCandidates: Int   // 获取下一页的候选词
        get() {
            val cands = Kernel.nextPageCandidates
            if (cands.isNotEmpty()) {
                candidatesLiveData.postValue(candidatesLiveData.value?.plus(cands))
                return cands.size
            }
            return 0
        }

    /**
     * 选择一个候选词，且重新获取候选词列表
     */
    fun chooseDecodingCandidate(candId: Int): String {
        activeCandidate = 0
        activeCandidateBar = 0
        if (candId >= 0) Kernel.getWordSelectedWord(candId)
        val newCandidates = Kernel.candidates
        return if(newCandidates.isNotEmpty()){
            candidatesLiveData.value = newCandidates
            Kernel.commitText
        } else if(candId in 0..<candidateSize){
            Kernel.commitText.ifEmpty { candidatesLiveData.value!![candId].text }
        } else ""
    }

    /**
     * 对输入的拼音进行查询。
     */
    fun updateDecodingCandidate() {
        activeCandidate = 0
        activeCandidateBar = 0
        candidatesLiveData.value = Kernel.candidates
    }

    /**
     * 获得指定的候选词
     */
    fun getCandidate(candId: Int): CandidateListItem? {
        return candidatesLiveData.value?.getOrNull(candId)
    }

    // 更新候选词
    fun cacheCandidates(words: Array<CandidateListItem>) {
        activeCandidate = 0
        activeCandidateBar = 0
        isReset = false
        candidatesLiveData.value = words.asList()
    }

    /**
     * 根据输入的字符查询候选词
     */
    fun getAssociateWord(words: String) {
        Kernel.getAssociateWord(words)
    }
}