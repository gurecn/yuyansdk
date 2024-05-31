package com.yuyan.imemodule.view.keyboard.container

import android.content.Context
import android.widget.RelativeLayout
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.service.DecodingInfo
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import com.yuyan.imemodule.view.keyboard.InputView

/**
 * 软键盘View的集装箱，主持一个软件盘View。
 */
open class BaseContainer(@JvmField var mContext: Context) : RelativeLayout(mContext) {
    //输入法服务
    @JvmField
    protected var inputView: InputView? = null

    //输入法变换器
    @JvmField
    protected var mInputModeSwitcher: InputModeSwitcherManager? = null

    /**
     * Decoding result to show. 词库解码对象
     */
    @JvmField
    protected var mDecInfo: DecodingInfo? = null
    fun setService(
        inputView: InputView?,
        decInfo: DecodingInfo?,
        inputModeSwitcher: InputModeSwitcherManager?
    ) {
        this.inputView = inputView
        mDecInfo = decInfo
        mInputModeSwitcher = inputModeSwitcher
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measuredWidth = instance.skbWidth
        val measuredHeight = instance.skbHeight
        val widthMeasure = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY)
        val heightMeasure = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasure, heightMeasure)
    }
}
