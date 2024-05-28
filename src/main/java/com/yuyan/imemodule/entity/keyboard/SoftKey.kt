package com.yuyan.imemodule.entity.keyboard

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.KeyEvent
import com.yuyan.imemodule.manager.KeyIconManager.Companion.instance
import java.util.Locale

/**
 * 按键的属性
 */
open class SoftKey {
    /**
     * Used to indicate the type and attributes of this key. the lowest 8 bits
     * should be reserved for SoftkeyToggle. 按键的属性和类型，最低的8位留给软键盘变换状态。
     */
    var stateId = 0

    /** key的文本  */
    private var mkeyLabel: String = ""

    /** key角标符号文本  */
    private var mKeyLabelSmall: String = ""

    /** key助记符号文本  */
    var keyMnemonic: String? = null

    /** key的code  */
    var keyCode = 0

    /** 键盘上下左右位置百分比 ，mLeft = (int) (mLeftF * skbWidth);  */
    var mLeftF = -1f
    private var mRightF = 0f
    var mTopF = -1f
    private var mBottomF = 0f
    var widthF = 0f
    var heightF = 0.25f

    /** 键盘上下左右位置坐标边界;  */
    var mLeft = 0
    var mRight = 0
    var mTop = 0
    var mBottom = 0

    /**
     * The current pressed state of this key.
     * 键盘布局点击时设置状态，非初始化设置。 See [com.yuyan.imemodule.view.keyboard.TextKeyboard] */
    @JvmField
    var pressed = false

    constructor(label: String = "", labelSmall: String = "") {
        mkeyLabel = label
        mKeyLabelSmall = labelSmall
    }

    constructor(code: Int = 0, label: String = "", labelSmall: String = "") {
        keyCode = code
        mkeyLabel = label
        mKeyLabelSmall = labelSmall
    }

    constructor(code: Int, label: String = "", labelSmall: String = "", keyMnemonic: String = "") : this(code, label, labelSmall) {
        this.keyMnemonic = keyMnemonic
    }

    /**
     * Informs the key that it has been pressed, in case it needs to change its appearance or
     * state.
     */
    fun onPressed() {
        pressed = !pressed
    }

    /**
     * Changes the pressed state of the key.
     * @see .onPressed
     */
    fun onReleased() {
        pressed = !pressed
    }

    fun setKeyDimensions(left: Float, top: Float, right: Float, bottom: Float) {
        mLeftF = left
        mTopF = top
        mRightF = right
        mBottomF = bottom
    }

    /**
     * 设置按键的区域
     *
     * @param skbWidth  键盘的宽度
     * @param skbHeight  键盘的高度
     */
    fun setSkbCoreSize(skbWidth: Int, skbHeight: Int) {
        mLeft = (mLeftF * skbWidth).toInt()
        mRight = (mRightF * skbWidth).toInt()
        mTop = (mTopF * skbHeight).toInt()
        mBottom = (mBottomF * skbHeight).toInt()
    }

    open val keyIcon: Drawable?
        get() = instance!!.getDefaultKeyIcon(keyCode, stateId)

    open val keyLabel: String?
        /**
         * 获取按键的显示字符
         */
        get() =  mkeyLabel

    /**
     * 获取按键角标字符
     */
    fun getmKeyLabelSmall(): String {
        return mKeyLabelSmall
    }

    /**
     * 获取按键角标字符
     */
    fun getkeyLabel(): String {
        return mkeyLabel
    }

    /**
     * 大小写转换
     */
    open fun changeCase(upperCase: Boolean) {
        mkeyLabel = if (upperCase) {
            mkeyLabel.uppercase()
        } else {
            mkeyLabel.lowercase()
        }
    }

    val isKeyCodeKey: Boolean
        /**
         * 是否是系统的keycode
         */
        get() = keyCode > 0
    val isUserDefKey: Boolean
        /**
         * 是否是用户定义的keycode
         */
        get() = keyCode < 0
    val isUniStrKey: Boolean
        /**
         * 是否是字符按键
         */
        get() = keyCode == 0

    /**
     * 是否有重复按下功能，即连长按这个按键是否执行重复的操作。
     */
    fun repeatable(): Boolean {
        return keyCode == KeyEvent.KEYCODE_DEL
    }

    fun width(): Int {
        return mRight - mLeft
    }

    fun height(): Int {
        return mBottom - mTop
    }

    override fun toString(): String {
        return "keyCode: " + keyCode + "  keyLabel: " + keyLabel
    }
}
