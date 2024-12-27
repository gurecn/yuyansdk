package com.yuyan.imemodule.callback

import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.prefs.behavior.PopupMenuMode
import com.yuyan.inputmethod.core.CandidateListItem

interface IResponseKeyEvent {
    fun responseKeyEvent(sKey: SoftKey)
    fun responseLongKeyEvent(result:Pair<PopupMenuMode, String>)
    fun responseHandwritingResultEvent(words: Array<CandidateListItem>)
}
