package com.yuyan.imemodule.view.widget

import android.content.Context
import android.graphics.Rect
import android.view.WindowInsets
import android.widget.LinearLayout

/**
 * 解决嵌套RecycleView（含输入框）时，键盘遮挡输入框问题
 */
class CustomLinearLayout(context: Context?) : LinearLayout(context) {
    override fun fitSystemWindows(insets: Rect): Boolean {
        insets.left = 0
        insets.top = 0
        insets.right = 0
        return super.fitSystemWindows(insets)
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        return super.onApplyWindowInsets(
            insets.replaceSystemWindowInsets(0, 0, 0, insets.systemWindowInsetBottom)
        )
    }
}

