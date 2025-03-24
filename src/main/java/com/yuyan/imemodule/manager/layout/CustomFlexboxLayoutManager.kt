package com.yuyan.imemodule.manager.layout

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager

class CustomFlexboxLayoutManager(context:Context): FlexboxLayoutManager(context) {
    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        try {
            return super.scrollVerticallyBy(dy, recycler, state)
        } catch (_:IndexOutOfBoundsException ) {   // 修复候选词快速滑动中，点击重输按钮清空候选词
        }
        return  0
    }
}