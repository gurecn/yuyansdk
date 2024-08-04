package com.yuyan.imemodule.view.keyboard.container

import android.content.Context
import android.view.ViewGroup
import com.yuyan.imemodule.utils.KeyboardLoaderUtil.Companion.instance
import com.yuyan.imemodule.view.keyboard.InputView
import com.yuyan.imemodule.view.keyboard.TextKeyboard

class QwertyTextContainer(context: Context?, inputView: InputView, skbValue: Int = 0) : InputBaseContainer(context, inputView) {
    private var mSkbValue: Int = 0
    init {
        mSkbValue = skbValue
    }

    /**
     * 更新软键盘布局
     */
    override fun updateSkbLayout() {
        if (null == mMajorView) {
            mMajorView = TextKeyboard(context)
            val params: ViewGroup.LayoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            addView(mMajorView, params)
            mMajorView!!.setResponseKeyEvent(inputView)
        }
        val softKeyboard = instance.getSoftKeyboard(mSkbValue)
        mMajorView!!.setSoftKeyboard(softKeyboard)
        mMajorView!!.invalidate()
    }
}
