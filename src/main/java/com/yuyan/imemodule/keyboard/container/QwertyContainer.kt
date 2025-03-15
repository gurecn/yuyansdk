package com.yuyan.imemodule.keyboard.container

import android.annotation.SuppressLint
import android.content.Context
import com.yuyan.imemodule.utils.KeyboardLoaderUtil.Companion.instance
import com.yuyan.imemodule.keyboard.InputView
import com.yuyan.imemodule.keyboard.TextKeyboard
import com.yuyan.imemodule.keyboard.HandwritingKeyboard


/**
 * 全键盘容器
 *
 * 包含输入键盘[TextKeyboard]。
 *
 * 与九宫格键盘容器[T9TextContainer]、数字键键盘容器[NumberContainer]相比，全键盘不包含拼音选择栏。
 */
@SuppressLint("ViewConstructor")
class QwertyContainer(context: Context?, inputView: InputView, skbValue: Int = 0) : InputBaseContainer(context, inputView) {
    private var mSkbValue: Int = 0
    init {
        mSkbValue = skbValue
    }

    /**
     * 更新软键盘布局
     */
    override fun updateSkbLayout() {
        if (null == mMajorView) {
            mMajorView = HandwritingKeyboard(context)
            val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            addView(mMajorView, params)
            mMajorView!!.setResponseKeyEvent(inputView)
        }
        val softKeyboard = instance.getSoftKeyboard(mSkbValue)
        mMajorView!!.setSoftKeyboard(softKeyboard)
        mMajorView!!.invalidate()
    }
}
