package com.yuyan.imemodule.view.keyboard.container

import android.annotation.SuppressLint
import android.content.Context
import android.widget.RelativeLayout
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.service.DecodingInfo
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import com.yuyan.imemodule.view.keyboard.InputView

/**
 * 软键盘View集装箱
 * 所有软键盘（输入、符号、设置等）父容器View。
 */
@SuppressLint("ViewConstructor")
open class BaseContainer(@JvmField var mContext: Context, inputView: InputView) : RelativeLayout(mContext) {
    //输入法服务
    @JvmField
    protected var inputView: InputView

    //输入法变换器
    @JvmField
    protected var mInputModeSwitcher: InputModeSwitcherManager? = null

    /**
     * Decoding result to show. 词库解码对象
     */
    @JvmField
    protected var mDecInfo: DecodingInfo? = null

    /**
     * 更新软键盘布局
     */
    open fun updateSkbLayout(){
    }

    init {
        this.inputView = inputView
        mDecInfo = inputView.mDecInfo
        mInputModeSwitcher = inputView.mInputModeSwitcher
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measuredWidth = instance.skbWidth
        val measuredHeight = instance.skbHeight
        val widthMeasure = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY)
        val heightMeasure = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasure, heightMeasure)
    }
}
