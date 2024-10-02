package com.yuyan.imemodule.callback

import com.yuyan.imemodule.prefs.behavior.SkbMenuMode

/**
 * 候选词视图监听器接口
 * @ClassName CandidateViewListener
 */
interface CandidateViewListener {
    fun onClickChoice(choiceId: Int)  //选择候选词的处理函数
    fun onClickMore(level: Int)  // 加载更多候选词
    fun onClickSetting()  //切换设置界面
    fun onClickMenu(skbMenuMode: SkbMenuMode)  //选择设置菜单
    fun onClickClearCandidate()  //清空候选词
    fun onClickClearClipBoard() //清空剪切板
}
