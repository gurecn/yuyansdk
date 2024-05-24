package com.yuyan.imemodule.callback

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
    fun onClickCloseKeyboard()
    fun onClickClearCandidate()
}
