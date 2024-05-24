package com.yuyan.imemodule.view.skbitem.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

/**
 * @author Gaolei
 * description： ViewPager适配器
 */
class PageViewPagerAdapter(private var mViewList: List<View>?) : PagerAdapter() {
    init {
        if (mViewList == null) {
            mViewList = ArrayList()
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        container.removeView(mViewList.get(position));
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val realPosition = position % mViewList!!.size
        val view = mViewList!![realPosition]
        if (container == view.parent) {
            container.removeView(view)
        }
        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return if (mViewList!!.isEmpty()) {
            0
        } else mViewList!!.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }
}
