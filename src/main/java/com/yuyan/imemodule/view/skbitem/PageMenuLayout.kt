package com.yuyan.imemodule.view.skbitem

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.yuyan.imemodule.view.skbitem.adapter.EntranceAdapter
import com.yuyan.imemodule.view.skbitem.adapter.PageViewPagerAdapter
import com.yuyan.imemodule.view.skbitem.holder.PageMenuViewHolderCreator
import kotlin.math.ceil

/**
 * describe: 分页菜单控件
 * @author Gaolei
 */
class PageMenuLayout<T>(context: Context) : RelativeLayout(context, null, 0) {
    private var mViewPager: CustomViewPager? = null

    /**
     * 行数
     */
    private var mRowCount = DEFAULT_ROW_COUNT

    /**
     * 列数
     */
    private var mSpanCount = DEFAULT_SPAN_COUNT

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        mViewPager = CustomViewPager(context)
        addView(
            mViewPager,
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        )
    }

    fun setPageDatas(datas: List<T>, creator: PageMenuViewHolderCreator<T>) {
        setPageDatas(mRowCount, mSpanCount, datas, creator)
    }

    val pageCount: Int
        /**
         * @return 菜单总页数
         */
        get() = if (mViewPager != null && mViewPager!!.adapter != null) {
            mViewPager!!.adapter!!.count
        } else {
            0
        }

    /**
     * 页面滚动监听
     */
    fun setOnPageListener(pageListener: OnPageChangeListener?) {
        if (mViewPager != null) {
            mViewPager!!.addOnPageChangeListener(pageListener!!)
        }
    }

    /**
     * 设置行数
     */
    fun setRowCount(rowCount: Int) {
        mRowCount = rowCount
    }

    /**
     * 设置列数
     */
    fun setSpanCount(spanCount: Int) {
        mSpanCount = spanCount
    }

    private fun setPageDatas(
        rowCount: Int,
        spanCount: Int,
        datas: List<T>,
        creator: PageMenuViewHolderCreator<T>
    ) {
        mRowCount = rowCount
        mSpanCount = spanCount
        if (mRowCount == 0 || mSpanCount == 0) {
            return
        }
        val pageSize = mRowCount * mSpanCount
        val pageCount = ceil(datas.size * 1.0 / pageSize).toInt()
        val viewList: MutableList<View> = ArrayList()
        for (index in 0 until pageCount) {
            val recyclerView = RecyclerView(this.context)
            recyclerView.setLayoutParams(
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            recyclerView.setLayoutManager(GridLayoutManager(this.context, mSpanCount))
            val entranceAdapter = EntranceAdapter(creator, datas, index, pageSize)
            recyclerView.setAdapter(entranceAdapter)
            viewList.add(recyclerView)
        }
        val adapter = PageViewPagerAdapter(viewList)
        mViewPager!!.setAdapter(adapter)
    }

    companion object {
        private const val DEFAULT_ROW_COUNT = 2
        private const val DEFAULT_SPAN_COUNT = 5
    }
}
