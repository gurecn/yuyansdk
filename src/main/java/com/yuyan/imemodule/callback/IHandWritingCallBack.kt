package com.yuyan.imemodule.callback

import com.yuyan.inputmethod.core.CandidateListItem

fun interface IHandWritingCallBack {
    fun onSucess(item:Array<CandidateListItem>)
}