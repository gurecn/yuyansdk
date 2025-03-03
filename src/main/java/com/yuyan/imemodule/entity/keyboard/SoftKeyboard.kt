package com.yuyan.imemodule.entity.keyboard

import com.yuyan.imemodule.singleton.EnvironmentSingleton
/**
 * Class used to represent a soft keyboard definition, including the height, the
 * background image, the image for high light, the keys, etc.
 * 一个软件盘的定义，包括按键的排列布局，宽度高度。
 * The width of the soft keyboard. 键盘的宽度
 * The height of the soft keyboard. 键盘的高度
 */
class SoftKeyboard(var mKeyRows: List<List<SoftKey>>) {
    var skbCoreWidth: Int = 0
    var skbCoreHeight: Int = 0

    /**
     * 设置键盘核心的宽度和高度（不包括padding），并根据新的宽度和高度，调整键盘中各行的top和bottom，调整行中的按键的尺寸。
     */
    fun setSkbCoreSize(skbCoreWidth: Int, skbCoreHeight: Int) {
        if (skbCoreWidth == this.skbCoreWidth && skbCoreHeight == this.skbCoreHeight)return
        this.skbCoreWidth = skbCoreWidth
        this.skbCoreHeight = skbCoreHeight
        for (keyRow in mKeyRows) {
            for (softKey in keyRow) {
                softKey.setSkbCoreSize(skbCoreWidth, skbCoreHeight)
            }
        }
    }

    val keyXMargin: Int
        /**
         * 按键左右间隔距离
         */
        get() = (EnvironmentSingleton.instance.keyXMargin * skbCoreWidth).toInt()
    val keyYMargin: Int
        /**
         * 按键上下间隔距离
         */
        get() = (EnvironmentSingleton.instance.keyYMargin * skbCoreHeight).toInt()

    /**
     * 根据坐标查找按键，如果坐标在某个按键区域内，就返回这个按键，如果坐标不在所有的按键区域内，返回离它最近的按键。
     * 可以在判断坐标在某个按键区域内的时候，并且加上判断离它最近的按键，这样就只需要一次遍历就行了。
     */
    fun mapToKey(x: Int, y: Int): SoftKey? {
        for (element in mKeyRows) {
            for (sKey in element) {
                if (sKey.mLeft <= x && sKey.mTop <= y && sKey.mRight > x && sKey.mBottom > y) return sKey
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
}
