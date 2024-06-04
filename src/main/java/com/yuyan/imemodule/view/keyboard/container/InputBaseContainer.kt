package com.yuyan.imemodule.view.keyboard.container

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import com.yuyan.imemodule.R
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.KeyboardLoaderUtil
import com.yuyan.imemodule.view.keyboard.InputView
import com.yuyan.imemodule.view.keyboard.TextKeyboard
import kotlin.math.abs

open class InputBaseContainer(context: Context?, inputView: InputView?) : BaseContainer(context!!, inputView) {

    //主要视图：软键盘视图。
    @JvmField
    protected var mMajorView: TextKeyboard? = null

    /**
     * 刷新按键状态，当前主要解决切换键盘时enter键状态切换
     */
    fun updateStates() {
        mMajorView!!.updateStates(mInputModeSwitcher!!)
    }

    /**
     * 设置键盘高度
     */
    fun setKeyboardHeight() {
        val softKeyboardHeight = EnvironmentSingleton.instance.skbHeight
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, softKeyboardHeight)
        val rootView =
            LayoutInflater.from(context).inflate(R.layout.layout_ime_keyboard_height_shadow, null)
        rootView.setLayoutParams(lp)
        this.addView(rootView)
        rootView.findViewById<View>(R.id.ll_keyboard_height_reset).setOnClickListener { _: View? ->
            EnvironmentSingleton.instance.keyBoardHeightRatio = 0.3f
            EnvironmentSingleton.instance.initData()
            KeyboardLoaderUtil.instance.clearKeyboardMap()
            updateSkbLayout()
            rootView.setLayoutParams(lp)
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
