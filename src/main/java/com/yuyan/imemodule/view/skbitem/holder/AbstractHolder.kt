package com.yuyan.imemodule.view.skbitem.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.view.skbitem.adapter.EntranceAdapter

/**
 * describe: AbstractHolder
 * @author Gaolei
 */
abstract class AbstractHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    init {
        initView(itemView)
    }

    protected abstract fun initView(itemView: View?)
    abstract fun bindView(
        adapter: EntranceAdapter<T>?,
        holder: RecyclerView.ViewHolder?,
        data: T,
        pos: Int
    )
}
