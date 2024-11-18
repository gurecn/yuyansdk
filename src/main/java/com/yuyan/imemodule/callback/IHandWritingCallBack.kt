package com.yuyan.imemodule.callback

import com.yuyan.inputmethod.core.CandidateListItem

interface IHandWritingCallBack {
    fun onSucess(item:Array<CandidateListItem>)
}