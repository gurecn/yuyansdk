package com.yuyan.imemodule.manager.layout

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomLinearLayoutManager(context:Context , orientation:Int, reverseLayout:Boolean):
    LinearLayoutManager(context, orientation, reverseLayout) {
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (_:IndexOutOfBoundsException ) { }
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        try {
            return super.scrollVerticallyBy(dy, recycler, state)
        } catch (_:IndexOutOfBoundsException ) {}
        return  0
    }

}