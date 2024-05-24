package com.yuyan.imemodule.view.skbitem

import android.content.Context
import androidx.viewpager.widget.ViewPager

/**
 * @author: Gaolei
 * 自定义viewpager，自适应高度
 */
class CustomViewPager(context: Context) : ViewPager(context) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            val h = child.measuredHeight
            if (h > height) {
                height = h
            }
        }
        val heightMeasure:Int = MeasureSpec.makeMeasureSpec(
            height,
            MeasureSpec.EXACTLY
        )
        super.onMeasure(widthMeasureSpec, heightMeasure)
    }
}
