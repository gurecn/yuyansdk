package com.yuyan.imemodule.manager.layout

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 网格布局管理。
 */
class CustomGridLayoutManager(context: Context, spanCount: Int) : GridLayoutManager(context, spanCount) {
    private var relayoutRetryCount = 0

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        return try {
            super.scrollVerticallyBy(dy, recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            recoverFromInconsistency(recycler, e)
            0
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
            relayoutRetryCount = 0
        } catch (e: IndexOutOfBoundsException) {
            recoverFromInconsistency(recycler, e)
        }
    }

    private fun recoverFromInconsistency(recycler: RecyclerView.Recycler?, e: IndexOutOfBoundsException) {
        recycler?.let { removeAndRecycleAllViews(it) }
        if (relayoutRetryCount++ >= MAX_RELAYOUT_RETRY) {
            relayoutRetryCount = 0
            return
        }
        requestLayout()
    }

    private companion object {
        const val MAX_RELAYOUT_RETRY = 2
    }
}