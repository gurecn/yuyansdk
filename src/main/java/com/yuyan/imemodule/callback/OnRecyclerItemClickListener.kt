package com.yuyan.imemodule.callback

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView条目点击响应事件监听
 * Created by Gaolei on 2017/12/12.
 */
fun interface OnRecyclerItemClickListener {
    fun onItemClick(parent: RecyclerView.Adapter<*>?, v: View?, position: Int)
}
