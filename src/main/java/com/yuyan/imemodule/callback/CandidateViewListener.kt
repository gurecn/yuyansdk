package com.yuyan.imemodule.callback

import com.yuyan.imemodule.prefs.behavior.SkbMenuMode

/**
 * 候选词视图监听器接口
 * @ClassName CandidateViewListener
 */
interface CandidateViewListener {
    /**
     * 选择了候选词的处理函数
     */
    fun onClickChoice(choiceId: Int)
    fun onClickMore(level: Int, position: Int)
    fun onClickSetting()
    fun onClickMenu(skbMenuMode: SkbMenuMode)
    fun onClickCloseKeyboard()
    fun onClickClearCandidate()
    fun onClickClearClipBoard()
}
