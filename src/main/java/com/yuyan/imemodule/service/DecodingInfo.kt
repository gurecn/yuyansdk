package com.yuyan.imemodule.service

import android.view.KeyEvent
import androidx.lifecycle.MutableLiveData
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.inputmethod.core.Candidate
import com.yuyan.inputmethod.core.CandidateListItem
import com.yuyan.inputmethod.core.Kernel

/**
 * 词库解码操作对象
 */
object DecodingInfo {
    private var mCurrentColumn = 0 // 缓存当前候选词所占宽度
    // 候选词列表
    val candidatesLiveData = MutableLiveData<List<Candidate>>()

    // 是否是联想词
    var isAssociate = false
    // 是否重置
    var isReset = false

    /**
     * 重置
     */
    fun reset() {
        isAssociate = false
        isReset = true
        candidatesLiveData.postValue(emptyList())
        Kernel.reset()
    }

    val isCandidatesListEmpty: Boolean
        // 候选词列表是否为空
        get() = candidatesLiveData.value.isNullOrEmpty()

    val candidateSize: Int
        // 候选词列表是否为空
        get() = if(isCandidatesListEmpty) 0 else candidatesLiveData.value!!.size


    val candidates: List<Candidate>
        // 候选词列表是否为空
        get() = candidatesLiveData.value?:emptyList()

    // 增加拼写字符
    fun inputAction(keycode: Int) {
        isReset = false
        if (Kernel.unHandWriting()) {
            Kernel.inputKeyCode(keycode)
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
        get() = if(Kernel.unHandWriting()) Kernel.isFinish else isCandidatesListEmpty || isReset

    val composingStrForDisplay: String   //获取显示的拼音字符串/
        get() = Kernel.wordsShowPinyin

    val composingStrForCommit: String   // 获取输入的拼音字符串
        get() = Kernel.wordsShowPinyin.replace("'", "")

    val nextPageCandidates: Int   // 获取下一页的候选词
        get() {
            val cands = Kernel.nextPageCandidates
            if (cands.isNotEmpty()) {
                candidatesLiveData.postValue(candidatesLiveData.value?.plus(calculateColumn(cands)))
                return cands.size
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
            candidatesLiveData.postValue(calculateColumn(Kernel.candidates.asList()))
            Kernel.commitText
        } else if(candId in 0..<candidateSize){
            val choice = candidatesLiveData.value!![candId].text
            reset()
            choice
        } else ""
    }

    /**
     * 获得指定的候选词
     */
    fun getCandidate(candId: Int): Candidate? {
        return candidatesLiveData.value?.getOrNull(candId)
    }

    // 更新候选词
    fun cacheCandidates(words: ArrayList<CandidateListItem>) {
        isReset = false
        candidatesLiveData.postValue(calculateColumn(words))
    }

    private fun calculateColumn(list:List<CandidateListItem>):List<Candidate> {
        val candidatesTemp = list.map { (comment, text) ->
            Candidate(comment, text, 0)
        }
        for (position in candidatesTemp.indices) {
            val candidate = candidatesTemp[position]
            val count = getSymbolsCount(candidate.text)
            if (mCurrentColumn + count <= 60) {
                mCurrentColumn += count
            } else {
                if(position == 0) {
                    if(candidates.isNotEmpty()) {
                        candidates.last().spanSize = candidates.last().spanSize?.plus(60)?.minus(mCurrentColumn)
                    }
                } else {
                    candidatesTemp[position - 1].spanSize = candidatesTemp[position - 1].spanSize?.plus(60)?.minus(mCurrentColumn)
                }
                mCurrentColumn = count
            }
            candidate.spanSize = count
        }
        return candidatesTemp
    }

    /**
     * 根据词长计算当前候选词需占的列数
     */
    private fun getSymbolsCount(data: String?): Int {
        return if (data?.isNotBlank() == true) {
            val x = if(InputModeSwitcherManager.isChinese)data.length else data.length/2
            if(x > 8) 60
            else if(x >= 6) 30
            else if(x >= 4) 20
            else if(x == 3) 15
            else if(x == 2) 12
            else  10
        } else 0
    }
}