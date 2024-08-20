package com.yuyan.imemodule.view.keyboard.container

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.yuyan.imemodule.R
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.service.DecodingInfo
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.KeyboardLoaderUtil
import com.yuyan.imemodule.view.keyboard.InputView
import kotlin.math.abs

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
        val measuredWidth = EnvironmentSingleton.instance.skbWidth
        val measuredHeight = EnvironmentSingleton.instance.skbHeight
        val widthMeasure = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY)
        val heightMeasure = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasure, heightMeasure)
    }

    /**
     * 设置键盘高度
     */
    fun setKeyboardHeight() {
        val softKeyboardHeight = EnvironmentSingleton.instance.skbHeight
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, softKeyboardHeight)
        val rootView = LayoutInflater.from(context).inflate(R.layout.layout_ime_keyboard_height_shadow, this, false)
        rootView.layoutParams = lp
        this.addView(rootView)
        rootView.findViewById<View>(R.id.ll_keyboard_height_reset).setOnClickListener { _: View? ->
            EnvironmentSingleton.instance.keyBoardHeightRatio = 0.3f
            EnvironmentSingleton.instance.initData()
            KeyboardLoaderUtil.instance.clearKeyboardMap()
            updateSkbLayout()
            rootView.layoutParams = lp
        }
        rootView.findViewById<View>(R.id.ll_keyboard_height_sure).setOnClickListener { removeView(rootView) }
        val lastY = floatArrayOf(0f)
        rootView.findViewById<View>(R.id.iv_keyboard_height_Top)
            .setOnTouchListener { v12: View, event: MotionEvent ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> lastY[0] = event.y
                    MotionEvent.ACTION_MOVE -> {
                        val y = event.y
                        if (abs((y - lastY[0]).toDouble()) > 20) {
                            var rat = EnvironmentSingleton.instance.keyBoardHeightRatio
                            if (y < lastY[0]) { // 手指向上移动
                                rat += 0.01f
                            } else { // 向下移动
                                rat -= 0.01f
                            }
                            lastY[0] = y
                            EnvironmentSingleton.instance.keyBoardHeightRatio = rat
                            EnvironmentSingleton.instance.initData()
                            KeyboardLoaderUtil.instance.clearKeyboardMap()
                            updateSkbLayout()
                            val l = LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                EnvironmentSingleton.instance.skbHeight
                            )
                            rootView.setLayoutParams(l)
                        }
                    }

                    MotionEvent.ACTION_UP -> v12.performClick()
                }
                true
            }
    }
}
