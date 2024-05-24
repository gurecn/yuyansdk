package com.yuyan.imemodule.view.skbitem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.view.skbitem.holder.AbstractHolder
import com.yuyan.imemodule.view.skbitem.holder.PageMenuViewHolderCreator

/**
 * @author Gaolei
 * describe: 分页菜单项列表适配器
 */
class EntranceAdapter<T>(
    private val mPageMenuViewHolderCreator: PageMenuViewHolderCreator<T>, private val mDatas: List<T>,
    /**
     * 页数下标,从0开始(通俗讲第几页)
     */
    private val mIndex: Int,
    /**
     * 每页显示最大条目个数
     */
    private val mPageSize: Int
) : RecyclerView.Adapter<AbstractHolder<T>>() {
    override fun getItemCount(): Int {
        return if (mDatas.size > (mIndex + 1) * mPageSize) mPageSize else mDatas.size - mIndex * mPageSize
    }

    override fun getItemId(position: Int): Long {
        return position + mIndex.toLong() * mPageSize
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractHolder<T> {
        val layoutId = mPageMenuViewHolderCreator.getLayoutId()
        val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return mPageMenuViewHolderCreator.createHolder(itemView)!!
    }

    override fun onBindViewHolder(holder: AbstractHolder<T>, position: Int) {
        val pos = position + mIndex * mPageSize
        holder.bindView(this, holder, mDatas[pos], pos)
    }

    /**
     * 获取指定索引对象
     * @param position  点击下标
     * @return  返回对象
     */
    fun getItemByPos(position: Int): T? {
        return if (mDatas.size <= position) null else mDatas[position]
    }
}
