package com.yuyan.imemodule.utils

import android.view.KeyEvent
import com.yuyan.imemodule.data.theme.ThemeManager.prefs
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.entity.keyboard.SoftKeyToggle
import com.yuyan.imemodule.entity.keyboard.SoftKeyboard
import com.yuyan.imemodule.entity.keyboard.ToggleState
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.LogUtil.d
import com.yuyan.imemodule.view.keyboard.lx17PYKeyPreset
import com.yuyan.imemodule.view.keyboard.qwertyPYKeyPreset
import com.yuyan.imemodule.view.keyboard.t9NumberKeyPreset
import com.yuyan.imemodule.view.keyboard.t9PYKeyPreset
import java.util.LinkedList
import kotlin.math.max

/**
 * 键盘加载类  包括中文9键  中文26键 英文26键
 */
class KeyboardLoaderUtil private constructor() {
    // 数字行
    private var skbNumberRow: MutableList<SoftKey> = LinkedList()
    fun clearKeyboardMap() {
        d(TAG, "clearKeyboardMap")
        mSoftKeyboardMap.clear()
    }

    private fun loadBaseSkb(skbValue: Int): SoftKeyboard {
        d(TAG, "loadBaseSkb")
        // shift键状态
        val shiftToggleStates = LinkedList<ToggleState>()
        shiftToggleStates.add(ToggleState(0))
        shiftToggleStates.add(ToggleState(1))
        shiftToggleStates.add(ToggleState(2))

        val softKeyboard: SoftKeyboard?
        val numberLine = prefs.abcNumberLine.getValue()
        d(TAG, "loadBaseSkb numberLine：$numberLine")
        if (numberLine && skbNumberRow.isEmpty()) {
            run {
                val qwertyKeys = createNumberLineKeys(arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0))
                qwertyKeys.first().apply {
                    mLeftF = 0.005f
                }
                skbNumberRow.addAll(qwertyKeys)
            }
        }
        val rows: MutableList<List<SoftKey>> = LinkedList()
        if (numberLine) {
            rows.add(skbNumberRow)
        }
        when(skbValue){
            0x1000 -> {  // 1000  拼音全键
                var keyBeans = mutableListOf<SoftKey>()
                var qwertyKeys = createQwertyKeys(arrayOf(45, 51, 33, 46, 48, 53, 49, 37, 43, 44))
                qwertyKeys.first().apply {
                    mLeftF = 0.005f
                }
                keyBeans.addAll(qwertyKeys)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                qwertyKeys = createQwertyKeys(arrayOf(29, 47, 32, 34, 35, 36, 38, 39, 40))
                qwertyKeys.first().apply {
                    mLeftF = 0.06f
                }
                keyBeans.addAll(qwertyKeys)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                qwertyKeys = createQwertyKeys(arrayOf(75, 54, 52, 31, 50, 30, 42, 41, KeyEvent.KEYCODE_DEL))
                qwertyKeys.first().apply {
                    mLeftF = 0.005f
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
                var t9Key = createT9Keys(arrayOf(75, 9, 10, KeyEvent.KEYCODE_AT))
                t9Key.first().mLeftF = 0.185f
                t9Key.last().widthF = 0.18f
                keyBeans.addAll(t9Key)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                t9Key = createT9Keys(arrayOf(11, 12, 13, KeyEvent.KEYCODE_CLEAR))
                t9Key.first().mLeftF = 0.185f
                t9Key.last().widthF = 0.18f
                keyBeans.addAll(t9Key)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                t9Key = createT9Keys(arrayOf(InputModeSwitcherManager.USERDEF_KEYCODE_LEFT_SYMBOL_12, 14, 15, 16, KeyEvent.KEYCODE_DEL))
                t9Key.first().apply {
                    mLeftF = 0.005f
                    mTopF = 0.005f
                    widthF = 0.18f
                    heightF = 0.745f
                }
                t9Key.last().widthF = 0.18f
                keyBeans.addAll(t9Key)
                rows.add(keyBeans)
                keyBeans = lastRows(numberLine)
                rows.add(keyBeans)
            }
            0x3000 -> {// 3000 手写键盘
                var keyBeans: MutableList<SoftKey> = LinkedList()
                var handwritingKey = createHandwritingKey(InputModeSwitcherManager.USERDEF_KEYCODE_LEFT_SYMBOL_12) // 符号站位
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
                var qwertyKeys = createQwertyKeys(arrayOf(45, 51, 33, 46, 48, 53, 49, 37, 43, 44))
                qwertyKeys.first().apply {
                    mLeftF = 0.005f
                }
                keyBeans.addAll(qwertyKeys)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                qwertyKeys = createQwertyKeys(arrayOf(29, 47, 32, 34, 35, 36, 38, 39, 40))
                qwertyKeys.first().apply {
                    mLeftF = 0.06f
                }
                keyBeans.addAll(qwertyKeys)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                val softKeyToggle = createKeyToggle(-1)
                softKeyToggle.mLeftF = 0.005f
                softKeyToggle.widthF = 0.147f
                softKeyToggle.setToggleStates(shiftToggleStates)
                keyBeans.add(softKeyToggle)
                keyBeans.addAll(createQwertyKeys(arrayOf(54, 52, 31, 50, 30, 42, 41, KeyEvent.KEYCODE_DEL)))
                keyBeans.last().apply {
                    widthF = 0.147f
                }
                rows.add(keyBeans)
                keyBeans = lastRows(numberLine)
                rows.add(keyBeans)
            }
            0x5000 -> {  // 5000 数字键盘
                var keyBeans: MutableList<SoftKey> = LinkedList()
                var t9Keys = createT9NumberKeys(arrayOf(8, 9, 10, KeyEvent.KEYCODE_AT))
                t9Keys.first().mLeftF = 0.185f
                t9Keys.last().widthF = 0.18f
                keyBeans.addAll(t9Keys)
                rows.add(keyBeans)

                keyBeans = LinkedList()
                t9Keys = createT9NumberKeys(arrayOf(11, 12, 13, 0))
                t9Keys.first().mLeftF = 0.185f
                t9Keys.last().widthF = 0.18f
                keyBeans.addAll(t9Keys)
                rows.add(keyBeans)

                keyBeans = LinkedList()
                t9Keys = createT9NumberKeys(arrayOf(InputModeSwitcherManager.USERDEF_KEYCODE_LEFT_SYMBOL_12, 14, 15, 16, KeyEvent.KEYCODE_DEL))
                t9Keys.first().apply {
                    mLeftF = 0.005f
                    mTopF = 0f
                    widthF = 0.18f
                    heightF = 0.75f
                }
                t9Keys.last().widthF = 0.18f
                keyBeans.addAll(t9Keys)
                rows.add(keyBeans)
                keyBeans = lastRows(numberLine, true)
                rows.add(keyBeans)
            }
            0x6000 -> {     // 6000 乱序17键盘
                var keyBeans: MutableList<SoftKey> = LinkedList()
                var lX17Keys = createLX17Keys(arrayOf(36, 47, 54, 30, 52, 41))
                lX17Keys.first().apply {
                    mLeftF = 0.005f
                }
                keyBeans.addAll(lX17Keys)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                lX17Keys = createLX17Keys(arrayOf(40, 32, 53, 51, 38, 42))
                lX17Keys.first().apply {
                    mLeftF = 0.005f
                }
                keyBeans.addAll(lX17Keys)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                lX17Keys = createLX17Keys(arrayOf(31, 45, 35, 34, 48, 42, 67))
                lX17Keys.first().apply {
                    mLeftF = 0.005f
                }
                keyBeans.addAll(lX17Keys)
                rows.add(keyBeans)
                keyBeans = lastRows(numberLine)
                rows.add(keyBeans)
            }
        }
        softKeyboard = getSoftKeyboard(skbValue, rows, numberLine)
        mSoftKeyboardMap[skbValue] = softKeyboard
        d(TAG, "loadBaseSkb finish:$skbValue")
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
            createT9Keys(arrayOf(InputModeSwitcherManager.USERDEF_KEYCODE_SYMBOL_ZH_3, InputModeSwitcherManager.USERDEF_KEYCODE_EMOJI_6,
                InputModeSwitcherManager.USERDEF_KEYCODE_RETURN_8, 7, KeyEvent.KEYCODE_SPACE))
        } else if(!numberLine){
            createT9Keys(arrayOf(InputModeSwitcherManager.USERDEF_KEYCODE_SYMBOL_ZH_3, InputModeSwitcherManager.USERDEF_KEYCODE_EMOJI_6,
                InputModeSwitcherManager.USERDEF_KEYCODE_NUMBER_7, KeyEvent.KEYCODE_SPACE, InputModeSwitcherManager.USERDEF_KEYCODE_LANG_2))
        } else {
            createT9Keys(arrayOf(InputModeSwitcherManager.USERDEF_KEYCODE_SYMBOL_ZH_3, InputModeSwitcherManager.USERDEF_KEYCODE_EMOJI_6,
                KeyEvent.KEYCODE_SPACE, InputModeSwitcherManager.USERDEF_KEYCODE_LANG_2))
        }
        t9Keys[0].mLeftF = 0.005f
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

    fun changeSKBNumberRow(numberLine: Boolean) {
        d(TAG, "changeSKBNumberRow numberLine:$numberLine")
        for (skbValue in mSoftKeyboardMap.keys) {
            loadBaseSkb(skbValue)
        }
    }

    fun getSoftKeyboard(skbValue: Int): SoftKeyboard {
        d(TAG, "getSoftKeyboard skbValue:$skbValue")
        var softKeyboard = mSoftKeyboardMap[skbValue]
        if (softKeyboard == null) {
            softKeyboard = loadBaseSkb(skbValue)
        }
        return softKeyboard
    }

    /**
     * 生成键盘布局，主要用于计算键盘边界
     */
    private fun getSoftKeyboard(skbValue: Int, rows: List<List<SoftKey>>, isNumberRow: Boolean): SoftKeyboard {
        val skbWidth = EnvironmentSingleton.instance.skbWidth
        val softKeyboardHeight = EnvironmentSingleton.instance.skbHeight
        val softKeyboard = SoftKeyboard(skbWidth, softKeyboardHeight)
        var rowBeanYPos = 0f
        var mKeyXPos: Float
        var mKeyYPos = 0f
        for (rowBean in rows) {
            mKeyYPos = max(mKeyYPos.toDouble(), rowBeanYPos.toDouble()).toFloat()
            mKeyXPos = 0f // 新行重新x从0开始
            softKeyboard.beginNewRow()
            for (keyBean in rowBean) {
                var keyXPos = keyBean.mLeftF
                var keyYPos = keyBean.mTopF
                val keyWidth = keyBean.widthF
                val keyHeight = keyBean.heightF
                if (keyXPos == -1f) keyXPos = mKeyXPos
                if (keyYPos == -1f) {
                    keyYPos = mKeyYPos
                } else if (keyYPos == 0.005f) { //拼音九键左侧符号栏
                    if (isNumberRow) { //数字行高度
                        keyYPos += 0.15f
                    }
                }
                val left: Float = keyXPos
                val right = left + keyWidth
                val top: Float = keyYPos
                val bottom = top + keyHeight
                if (isNumberRow) {
                    keyBean.setKeyDimensions(left, top / 1.15f, right, bottom / 1.15f)
                } else {
                    keyBean.setKeyDimensions(left, top, right, bottom)
                }
                mKeyXPos = right
                rowBeanYPos = max(rowBeanYPos.toDouble(), bottom.toDouble()).toFloat()
                softKeyboard.addSoftKey(keyBean)
            }
        }
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

    private fun createQwertyKeys(codes: Array<Int>): Array<SoftKey> {
        val softKeys = mutableListOf<SoftKey>()
        for(code in codes){
            val labels = qwertyPYKeyPreset[code]
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
        private val TAG = KeyboardLoaderUtil::class.java.getSimpleName()
        private var mInstance: KeyboardLoaderUtil? = null
        private val mSoftKeyboardMap = HashMap<Int, SoftKeyboard?>() //缓存所有可用键盘
        @JvmStatic
        val instance: KeyboardLoaderUtil?
            get() {
                if (null == mInstance) mInstance = KeyboardLoaderUtil()
                return mInstance
            }
    }
}