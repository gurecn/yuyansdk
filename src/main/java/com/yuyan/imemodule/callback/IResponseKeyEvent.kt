package com.yuyan.imemodule.callback

import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.inputmethod.core.CandidateListItem

interface IResponseKeyEvent {
    fun responseKeyEvent(sKey: SoftKey)
    fun responseLongKeyEvent(showText: String?)
    fun responseHandwritingResultEvent(words: ArrayList<CandidateListItem?>)
}
