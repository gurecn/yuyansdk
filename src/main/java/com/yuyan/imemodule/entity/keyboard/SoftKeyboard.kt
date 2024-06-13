package com.yuyan.imemodule.entity.keyboard

import android.view.KeyEvent
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.manager.InputModeSwitcherManager.ToggleStates
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import java.util.LinkedList

/**
 * Class used to represent a soft keyboard definition, including the height, the
 * background image, the image for high light, the keys, etc.
 * 一个软件盘的定义，包括按键的排列布局，宽度高度。
 * The width of the soft keyboard. 键盘的宽度
 * The height of the soft keyboard. 键盘的高度
 */
class SoftKeyboard(var skbCoreWidth: Int, var skbCoreHeight: Int) {
    private var skbValue = 0

    /**
     * this member is Used to indicate that the
     * soft keyboard should be displayed in uppercase. 是否是标准键盘的大写
     */
    private var mIsQwertyUpperCase = true

    /**
     * Rows in this soft keyboard. Each row has a id. Only matched rows will be
     * enabled. 按键排列的行的链表，每个元素都是一行。
     */
    private var mKeyRows = listOf<List<SoftKey>>()

    fun setSkbValue(skbValue: Int) {
        this.skbValue = skbValue
    }


    fun setSoftKeys(rows: List<List<SoftKey>>) {
        mKeyRows = rows
    }

    /**
     * 设置键盘核心的宽度和高度（不包括padding），并根据新的宽度和高度，调整键盘中各行的top和bottom，调整行中的按键的尺寸。
     */
    fun setSkbCoreSize() {
        for (keyRow in mKeyRows) {
            for (softKey in keyRow) {
                softKey.setSkbCoreSize(skbCoreWidth, skbCoreHeight)
            }
        }
    }

    /**
     * 设置键盘核心的宽度和高度（不包括padding），并根据新的宽度和高度，调整键盘中各行的top和bottom，调整行中的按键的尺寸。
     */
    fun setSkbCoreSize(skbCoreWidth: Int, skbCoreHeight: Int) {
        if (skbCoreWidth == this.skbCoreWidth && skbCoreHeight == this.skbCoreHeight) {
            return
        }
        this.skbCoreWidth = skbCoreWidth
        this.skbCoreHeight = skbCoreHeight
        setSkbCoreSize()
    }

    val keyXMargin: Int
        /**
         * 按键左右间隔距离
         */
        get() = (instance.keyXMargin * skbCoreWidth).toInt()
    val keyYMargin: Int
        /**
         * 按键上下间隔距离
         */
        get() = (instance.keyYMargin * skbCoreHeight).toInt()
    val row: List<List<SoftKey>>
        get() = mKeyRows

    /**
     * 根据坐标查找按键，如果坐标在某个按键区域内，就返回这个按键，如果坐标不在所有的按键区域内，返回离它最近的按键。
     * 可以在判断坐标在某个按键区域内的时候，并且加上判断离它最近的按键，这样就只需要一次遍历就行了。
     */
    fun mapToKey(x: Int, y: Int): SoftKey? {
        val rowNum = mKeyRows.size
        for (row in 0 until rowNum) {
            val keyRow: List<SoftKey> = mKeyRows[row]
            for (sKey in keyRow) {
                val mLeft = sKey.mLeft
                val mRight = sKey.mRight
                val mTop = sKey.mTop
                val mBottom = sKey.mBottom
                if (mLeft <= x && mTop <= y && mRight > x && mBottom > y) {
                    return sKey
                }
            }
        }
        return null
    }

    /**
     * 根据code值查询按键，由于符号键无code&部分键盘可能存在重复键，因此该方式可能无法精确查询。
     */
    fun getKeyByCode(code: Int): SoftKey? {
        for (keyRow in mKeyRows) {
            for (sKey in keyRow) {
                if (sKey.keyCode == code) return sKey
            }
        }
        return null
    }

    /**
     * 改变键盘的状态，并且根据键盘状态中的mKeyStates[]来设置每个按键。
     *
     * @param toggleStates 整个键盘的状态
     */
    fun enableToggleStates(toggleStates: ToggleStates?) {
        if (null == toggleStates) return
        val isQwerty = toggleStates.mQwerty
        val isQwertyUpperCase = toggleStates.mQwertyUpperCase
        val isUpperLock = toggleStates.isUpperLock
        val needUpdateQwerty = isQwerty && mIsQwertyUpperCase != isQwertyUpperCase
        val stateEnter = toggleStates.mStateEnter
        for (keyRow in mKeyRows) {
            for (key in keyRow) {
                if (needUpdateQwerty) {  //1
                    if (key.keyCode >= KeyEvent.KEYCODE_A && key.keyCode <= KeyEvent.KEYCODE_Z) {
                        key.changeCase(isQwertyUpperCase)
                    }
                }
                if (key is SoftKeyToggle) {
                    if (key.keyCode == InputModeSwitcherManager.USERDEF_KEYCODE_SHIFT_1) { //2
                        if (isUpperLock) {
                            key.enableToggleState(2)
                        } else if (isQwertyUpperCase) {
                            key.enableToggleState(1)
                        } else {
                            key.enableToggleState(0)
                        }
                    }
                    if (key.keyCode == KeyEvent.KEYCODE_ENTER) {
                        key.enableToggleState(stateEnter)
                    }
                }
            }
        }
        mIsQwertyUpperCase = isQwertyUpperCase
    }
}
