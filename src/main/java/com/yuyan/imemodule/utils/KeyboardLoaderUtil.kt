package com.yuyan.imemodule.utils

import android.view.KeyEvent
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.entity.keyboard.SoftKeyToggle
import com.yuyan.imemodule.entity.keyboard.SoftKeyboard
import com.yuyan.imemodule.entity.keyboard.ToggleState
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.view.keyboard.lx17PYKeyPreset
import com.yuyan.imemodule.view.keyboard.qwertyKeyPreset
import com.yuyan.imemodule.view.keyboard.qwertyPYKeyPreset
import com.yuyan.imemodule.view.keyboard.t9NumberKeyPreset
import com.yuyan.imemodule.view.keyboard.t9PYKeyPreset
import java.util.LinkedList

/**
 * 键盘加载类  包括中文9键  中文26键 英文26键
 */
class KeyboardLoaderUtil private constructor() {

    fun clearKeyboardMap() {
        mSoftKeyboardMap.clear()
    }

    private fun loadBaseSkb(skbValue: Int): SoftKeyboard {
        // shift键状态
        val shiftToggleStates = LinkedList<ToggleState>()
        shiftToggleStates.add(ToggleState(0))
        shiftToggleStates.add(ToggleState(1))
        shiftToggleStates.add(ToggleState(2))

        val softKeyboard: SoftKeyboard?
        val numberLine = AppPrefs.getInstance().keyboardSetting.abcNumberLine.getValue()
        val rows: MutableList<List<SoftKey>> = LinkedList()
        if (numberLine) {
            val qwertyKeys = createNumberLineKeys(arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0))
            rows.add(qwertyKeys.asList())
        }
        when(skbValue){
            0x1000 -> {  // 1000  拼音全键
                var keyBeans = mutableListOf<SoftKey>()
                val keys = arrayListOf(
                    arrayOf(45, 51, 33, 46, 48, 53, 49, 37, 43, 44),
                    arrayOf(29, 47, 32, 34, 35, 36, 38, 39, 40),
                    arrayOf(75, 54, 52, 31, 50, 30, 42, 41, KeyEvent.KEYCODE_DEL),)
                var qwertyKeys = createQwertyPYKeys(keys[0])
                keyBeans.addAll(qwertyKeys)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                qwertyKeys = createQwertyPYKeys(keys[1])
                qwertyKeys.first().apply {
                    mLeftF = 0.06f
                }
                keyBeans.addAll(qwertyKeys)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                qwertyKeys = createQwertyPYKeys(keys[2])
                qwertyKeys.first().apply {
                    widthF = 0.147f
                }
                qwertyKeys.last().apply {
                    widthF = 0.147f
                }
                keyBeans.addAll(qwertyKeys)
                rows.add(keyBeans)
                keyBeans = lastRows(numberLine)
                rows.add(keyBeans)
            }
            0x2000 -> {  // 2000  T9键键
                var keyBeans: MutableList<SoftKey> = LinkedList()
                val keyDeleteOrder = if(AppPrefs.getInstance().keyboardSetting.deleteLocationTop.getValue())Pair(KeyEvent.KEYCODE_DEL, KeyEvent.KEYCODE_AT) else Pair(KeyEvent.KEYCODE_AT, KeyEvent.KEYCODE_DEL)
                val keys = arrayListOf(
                    arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12, 75, 9, 10, keyDeleteOrder.first),
                    arrayOf(11, 12, 13, KeyEvent.KEYCODE_CLEAR),
                    arrayOf(14, 15, 16, keyDeleteOrder.second),)
                var t9Key = createT9Keys(keys[0])
                t9Key.first().apply {
                    widthF = 0.18f
                    heightF = 0.75f
                }
                t9Key.last().widthF = 0.18f
                keyBeans.addAll(t9Key)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                t9Key = createT9Keys(keys[1])
                t9Key.first().mLeftF = 0.185f
                t9Key.last().widthF = 0.18f
                keyBeans.addAll(t9Key)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                t9Key = createT9Keys(keys[2])
                t9Key.first().mLeftF = 0.185f
                t9Key.last().widthF = 0.18f
                keyBeans.addAll(t9Key)
                rows.add(keyBeans)
                keyBeans = lastRows(numberLine)
                rows.add(keyBeans)
            }
            0x3000 -> {// 3000 手写键盘
                var keyBeans: MutableList<SoftKey> = LinkedList()
                var handwritingKey = createHandwritingKey(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12) // 符号站位
                handwritingKey.mLeftF = 0.815f
                handwritingKey.heightF = 0.50f
                keyBeans.add(handwritingKey)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                handwritingKey = createHandwritingKey(KeyEvent.KEYCODE_DEL)
                handwritingKey.mLeftF = 0.815f
                keyBeans.add(handwritingKey)
                rows.add(keyBeans)
                keyBeans = lastRows(numberLine)
                rows.add(keyBeans)
            }
            0x4000 -> {// 4000 英文全键
                var keyBeans: MutableList<SoftKey> = LinkedList()
                val keys = arrayListOf(
                    arrayOf(45, 51, 33, 46, 48, 53, 49, 37, 43, 44),
                    arrayOf(29, 47, 32, 34, 35, 36, 38, 39, 40),
                    arrayOf(54, 52, 31, 50, 30, 42, 41, KeyEvent.KEYCODE_DEL),)
                var qwertyKeys = createQwertyKeys(keys[0])
                keyBeans.addAll(qwertyKeys)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                qwertyKeys = createQwertyKeys(keys[1])
                qwertyKeys.first().apply {
                    mLeftF = 0.06f
                }
                keyBeans.addAll(qwertyKeys)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                val softKeyToggle = createKeyToggle(-1)
                softKeyToggle.widthF = 0.147f
                softKeyToggle.setToggleStates(shiftToggleStates)
                keyBeans.add(softKeyToggle)
                keyBeans.addAll(createQwertyKeys(keys[2]))
                keyBeans.last().apply {
                    widthF = 0.147f
                }
                rows.add(keyBeans)
                keyBeans = lastRows(numberLine)
                keyBeans[keyBeans.size -2].stateId = 1
                rows.add(keyBeans)
            }
            0x5000 -> {  // 5000 数字键盘
                var keyBeans: MutableList<SoftKey> = LinkedList()
                val keyDelete = if(AppPrefs.getInstance().keyboardSetting.deleteLocationTop.getValue())Pair(KeyEvent.KEYCODE_DEL, KeyEvent.KEYCODE_AT) else Pair(KeyEvent.KEYCODE_AT, KeyEvent.KEYCODE_DEL)
                val keys = arrayListOf(
                    arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12, 8, 9, 10, keyDelete.first),
                    arrayOf(11, 12, 13, 0),
                    arrayOf(14, 15, 16, keyDelete.second),)
                var t9Keys = createT9NumberKeys(keys[0])
                t9Keys.first().apply {
                    widthF = 0.18f
                    heightF = 0.75f
                }
                t9Keys.last().widthF = 0.18f
                keyBeans.addAll(t9Keys)
                rows.add(keyBeans)

                keyBeans = LinkedList()
                t9Keys = createT9NumberKeys(keys[1])
                t9Keys.first().mLeftF = 0.185f
                t9Keys.last().widthF = 0.18f
                keyBeans.addAll(t9Keys)
                rows.add(keyBeans)

                keyBeans = LinkedList()
                t9Keys = createT9NumberKeys(keys[2])
                t9Keys.first().mLeftF = 0.185f
                t9Keys.last().widthF = 0.18f
                keyBeans.addAll(t9Keys)
                rows.add(keyBeans)
                keyBeans = lastRows(numberLine, true)
                rows.add(keyBeans)
            }
            0x6000 -> {     // 6000 乱序17键盘
                var keyBeans: MutableList<SoftKey> = LinkedList()
                val keys = arrayListOf(
                    arrayOf(36, 47, 54, 30, 52, 41),
                    arrayOf(40, 32, 53, 51, 38, 42),
                    arrayOf(31, 45, 35, 34, 48, 67),)
                var lX17Keys = createLX17Keys(keys[0])
                keyBeans.addAll(lX17Keys)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                lX17Keys = createLX17Keys(keys[1])
                keyBeans.addAll(lX17Keys)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                lX17Keys = createLX17Keys(keys[2])
                keyBeans.addAll(lX17Keys)
                rows.add(keyBeans)
                keyBeans = lastRows(numberLine)
                rows.add(keyBeans)
            }
        }
        softKeyboard = getSoftKeyboard(skbValue, rows, numberLine)
        mSoftKeyboardMap[skbValue] = softKeyboard
        return softKeyboard
    }

    // 键盘最后一行（各键盘统一，数字键盘稍微不同）
    private fun lastRows(numberLine: Boolean, isNumKeyboard: Boolean = false): MutableList<SoftKey> {
        // enter键状态
        val enterToggleStates = LinkedList<ToggleState>()
        enterToggleStates.add(ToggleState("去往", 0))
        enterToggleStates.add(ToggleState("搜索", 1))
        enterToggleStates.add(ToggleState("发送", 2))
        enterToggleStates.add(ToggleState("下一个", 3))
        enterToggleStates.add(ToggleState("完成", 4))
        val keyBeans = mutableListOf<SoftKey>()
        val t9Keys =
        if(isNumKeyboard){
            createT9Keys(arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3, InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4,
                InputModeSwitcherManager.USER_DEF_KEYCODE_RETURN_6, 7, KeyEvent.KEYCODE_SPACE))
        } else if(!numberLine){
            createT9Keys(arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3, InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4,
                InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5, KeyEvent.KEYCODE_SPACE, InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2))
        } else {
            createT9Keys(arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3, InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4,
                KeyEvent.KEYCODE_SPACE, InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2))
        }
        if(t9Keys.size == 5){
            t9Keys[0].widthF = 0.09f
            t9Keys[1].widthF = 0.09f
            t9Keys[2].widthF = 0.147f
            t9Keys[3].widthF = 0.336f
            t9Keys[4].widthF = 0.147f
        } else {
            t9Keys[0].widthF = 0.18f
            t9Keys[1].widthF = 0.147f
            t9Keys[2].widthF = 0.336f
            t9Keys[3].widthF = 0.147f
        }
        keyBeans.addAll(t9Keys)
        val softKeyToggle = createKeyToggle(KeyEvent.KEYCODE_ENTER)
        softKeyToggle.widthF = 0.18f
        softKeyToggle.stateId = 0
        softKeyToggle.setToggleStates(enterToggleStates)
        keyBeans.add(softKeyToggle)
        return keyBeans
    }

    fun changeSKBNumberRow() {
        for (skbValue in mSoftKeyboardMap.keys) {
            loadBaseSkb(skbValue)
        }
    }

    fun getSoftKeyboard(skbValue: Int): SoftKeyboard {
        var softKeyboard = mSoftKeyboardMap[skbValue]
        if (softKeyboard == null) {
            softKeyboard = loadBaseSkb(skbValue)
        }
        return softKeyboard
    }

    /** 生成键盘布局，主要用于计算键盘边界 */
    private fun getSoftKeyboard(skbValue: Int, rows: List<List<SoftKey>>, isNumberRow: Boolean): SoftKeyboard {
        val skbWidth = EnvironmentSingleton.instance.skbWidth
        val softKeyboardHeight = EnvironmentSingleton.instance.skbHeight
        val softKeyboard = SoftKeyboard(skbWidth, softKeyboardHeight)
        var lastKeyBottom = 0f
        var lastKeyRight: Float
        var lastKeyTop: Float
        for (rowBean in rows) {
            lastKeyTop = lastKeyBottom  // 新行top为上一行bottom
            lastKeyRight = 0.005f // 新行x从0.005开始
            for (keyBean in rowBean) {
                var keyXPos = keyBean.mLeftF
                var keyYPos = keyBean.mTopF
                val keyWidth = keyBean.widthF
                val keyHeight = keyBean.heightF
                if(keyXPos == -1f || keyYPos == -1f || isNumberRow) {
                    if (keyXPos == -1f) keyXPos = lastKeyRight
                    if (keyYPos == -1f) keyYPos = lastKeyTop
                    if (isNumberRow) {
                        keyBean.setKeyDimensions(keyXPos, keyYPos/1.15f, keyHeight/1.15f)
                    } else {
                        keyBean.setKeyDimensions(keyXPos, keyYPos)
                    }
                }
                lastKeyRight = keyXPos + keyWidth
                lastKeyTop = keyYPos
                lastKeyBottom = keyYPos + keyHeight
            }
        }
        softKeyboard.setSoftKeys(rows)
        softKeyboard.setSkbCoreSize()
        softKeyboard.setSkbValue(skbValue)
        return softKeyboard
    }

    private fun createT9Keys(codes: Array<Int>): Array<SoftKey> {
        val softKeys = mutableListOf<SoftKey>()
        for(code in codes){
            val labels = t9PYKeyPreset[code]
            softKeys.add(SoftKey(code, labels?.getOrNull(0) ?: "", labels?.getOrNull(1)?: "").apply {
                widthF = 0.21f
            })
        }
        return softKeys.toTypedArray()
    }

    private fun createT9NumberKeys(codes: Array<Int>): Array<SoftKey> {
        val softKeys = mutableListOf<SoftKey>()
        for(code in codes){
            val labels = t9NumberKeyPreset[code]
            softKeys.add(SoftKey(code, labels?.getOrNull(0) ?: "", labels?.getOrNull(1) ?: "").apply {
                widthF = 0.21f
            })
        }
        return softKeys.toTypedArray()
    }

    private fun createQwertyPYKeys(codes: Array<Int>): Array<SoftKey> {
        val softKeys = mutableListOf<SoftKey>()
        for(code in codes){
            val labels = qwertyPYKeyPreset[code]
            softKeys.add(SoftKey(code, labels?.getOrNull(0) ?: "", labels?.getOrNull(1) ?: "").apply {
                widthF = 0.099f
            })
        }
        return softKeys.toTypedArray()
    }

    private fun createQwertyKeys(codes: Array<Int>): Array<SoftKey> {
        val softKeys = mutableListOf<SoftKey>()
        for(code in codes){
            val labels = qwertyKeyPreset[code]
            softKeys.add(SoftKey(code, labels?.getOrNull(0) ?: "", labels?.getOrNull(1) ?: "").apply {
                widthF = 0.099f
            })
        }
        return softKeys.toTypedArray()
    }

    private fun createHandwritingKey(code: Int): SoftKey {
        val labels = qwertyPYKeyPreset[code]
        return SoftKey(code, labels?.getOrNull(0) ?: "", labels?.getOrNull(1) ?: "").apply {
            widthF = 0.18f
        }
    }

    private fun createNumberLineKeys(codes: Array<Int>): Array<SoftKey> {
        val softKeys = mutableListOf<SoftKey>()
        for(code in codes) {
            val softKey = SoftKey(code.toString()).apply {
                widthF = 0.099f
                heightF = 0.15f
            }
            softKeys.add(softKey)
        }
        return softKeys.toTypedArray()
    }

    private fun createLX17Keys(codes: Array<Int>): Array<SoftKey> {
        val softKeys = mutableListOf<SoftKey>()
        for(code in codes){
            val labels = lx17PYKeyPreset[code]
            softKeys.add(SoftKey(code, labels?.getOrNull(0) ?: "", labels?.getOrNull(1) ?: "", labels?.getOrNull(2) ?: "").apply {
                widthF = 0.165f
            })
        }
        return softKeys.toTypedArray()
    }

    private fun createKeyToggle(code: Int): SoftKeyToggle {
        return SoftKeyToggle(code)
    }

    companion object {
        private var mInstance: KeyboardLoaderUtil? = null
        private val mSoftKeyboardMap = HashMap<Int, SoftKeyboard?>() //缓存所有可用键盘
        @JvmStatic
        val instance: KeyboardLoaderUtil
            get() {
                if (null == mInstance) mInstance = KeyboardLoaderUtil()
                return mInstance!!
            }
    }
}