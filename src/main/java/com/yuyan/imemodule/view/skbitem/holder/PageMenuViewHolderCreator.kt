package com.yuyan.imemodule.view.skbitem.holder

import android.view.View
import com.yuyan.imemodule.entity.SkbFunItem

/**
 * describe:PageMenuViewHolderCreator
 * @author Gaolei
 */
interface PageMenuViewHolderCreator<T> {
    fun createHolder(itemView: View?): AbstractHolder<T>?
    fun getLayoutId(): Int
}
