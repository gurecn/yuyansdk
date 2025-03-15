package com.yuyan.imemodule.keyboard.container

import android.annotation.SuppressLint
import android.content.Context
import com.yuyan.imemodule.keyboard.InputView
import com.yuyan.imemodule.keyboard.TextKeyboard

/**
 * 输入键盘容器父类，用于进行输入键盘相关处理（判断）。
 *
 * 与基类不同的是，该类内置输入键盘[TextKeyboard],用于键盘更新、按键响应操作。
 */
@SuppressLint("ViewConstructor")
open class InputBaseContainer(context: Context?, inputView: InputView) : BaseContainer(context!!, inputView) {

    //主要视图：软键盘视图。
    @JvmField
    protected var mMajorView: TextKeyboard? = null

    /**
     * 刷新按键状态，当前主要解决切换键盘时enter键状态切换
     */
    fun updateStates() {
        mMajorView!!.updateStates()
    }
}
