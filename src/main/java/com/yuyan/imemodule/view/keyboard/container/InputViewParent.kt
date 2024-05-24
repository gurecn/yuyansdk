package com.yuyan.imemodule.view.keyboard.container

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

// 根布局集装箱，此为所有键盘布局共享对象
class InputViewParent @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var mLastContainer: View? = null
    fun showView(child: View?) {
        if (child == null) return
        (child as? InputBaseContainer)?.updateStates()
        if (child.parent == null) {
            super.addView(child)
        } else if (child.parent !== this) {
            (child.parent as ViewGroup).removeView(child)
            super.addView(child)
        }
        if (child === mLastContainer) return
        child.visibility = VISIBLE
        child.requestLayout()
        if (mLastContainer != null) {
            hideView(mLastContainer!!)
        }
        mLastContainer = child
    }

    fun hideView(child: View) {
        child.visibility = GONE
    }
}
