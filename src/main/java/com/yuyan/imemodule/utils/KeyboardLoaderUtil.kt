package com.yuyan.imemodule.utils

import android.view.KeyEvent
import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.entity.keyboard.SoftKeyToggle
import com.yuyan.imemodule.entity.keyboard.SoftKeyboard
import com.yuyan.imemodule.entity.keyboard.ToggleState
import com.yuyan.imemodule.keyboard.KeyPreset
import com.yuyan.imemodule.keyboard.KeyboardData
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.DoublePinyinSchemaMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.keyboard.doubleAbcMnemonicPreset
import com.yuyan.imemodule.keyboard.doubleFlyMnemonicPreset
import com.yuyan.imemodule.keyboard.doubleMSMnemonicPreset
import com.yuyan.imemodule.keyboard.doubleNaturalMnemonicPreset
import com.yuyan.imemodule.keyboard.doubleSogouMnemonicPreset
import com.yuyan.imemodule.keyboard.doubleZiguangMnemonicPreset
import com.yuyan.imemodule.keyboard.lx17MnemonicPreset
import com.yuyan.imemodule.prefs.behavior.SkbStyleMode
import java.util.LinkedList

/**
 * 键盘加载类  包括中文9键  中文26键 英文26键
 */
class KeyboardLoaderUtil private constructor() {
    private var rimeValue: String? = null
    private var mSkbValue: Int = 0
    private var numberLine: Boolean = false
    private var skbStyleMode: SkbStyleMode = SkbStyleMode.Yuyan
    fun clearKeyboardMap() {
        mSoftKeyboardMap.clear()
    }

    private fun loadBaseSkb(skbValue: Int): SoftKeyboard {
        skbStyleMode = ThemeManager.prefs.skbStyleMode.getValue()
        mSkbValue = skbValue
        // shift键状态
        // 直输状态
        val shiftToggleStates = LinkedList<ToggleState>()
        shiftToggleStates.add(ToggleState(0))
        shiftToggleStates.add(ToggleState(1))
        shiftToggleStates.add(ToggleState(2))
        // 拼写模式
        shiftToggleStates.add(ToggleState(3))
        shiftToggleStates.add(ToggleState(4))
        shiftToggleStates.add(ToggleState(5))

        val softKeyboard: SoftKeyboard?
        numberLine = AppPrefs.getInstance().keyboardSetting.abcNumberLine.getValue()
        val rows: MutableList<List<SoftKey>> = LinkedList()
        if (numberLine) {
            val qwertyKeys = createNumberLineKeys(arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0))
            rows.add(qwertyKeys.asList())
        }
        when(skbValue){
            InputModeSwitcherManager.MASK_SKB_LAYOUT_QWERTY_PINYIN -> {  // 1000  拼音全键
                rimeValue = AppPrefs.getInstance().internal.pinyinModeRime.getValue()
                var keyBeans = mutableListOf<SoftKey>()
                val keys = when (rimeValue) {
                    CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY + DoublePinyinSchemaMode.mspy,
                    CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY + DoublePinyinSchemaMode.sogou,
                    CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY + DoublePinyinSchemaMode.ziguang -> arrayListOf(
                        arrayOf(45, 51, 33, 46, 48, 53, 49, 37, 43, 44),
                        arrayOf(29, 47, 32, 34, 35, 36, 38, 39, 40),
                        arrayOf(74, 54, 52, 31, 50, 30, 42, 41, KeyEvent.KEYCODE_DEL),)
                    else -> {
                        KeyboardData.layoutQwertyCn[skbStyleMode]!!
                    }
                }
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
                keyBeans = lastRows(skbValue)
                rows.add(keyBeans)
            }
            InputModeSwitcherManager.MASK_SKB_LAYOUT_T9_PINYIN -> {  // 2000  T9键键
                var keyBeans: MutableList<SoftKey> = LinkedList()
                val keys = KeyboardData.layoutT9Cn[skbStyleMode]!!
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
                t9Key.last().apply {
                    widthF = 0.18f
                    stateId = 7
                }
                keyBeans.addAll(t9Key)
                rows.add(keyBeans)
                keyBeans = lastRows(skbValue)
                rows.add(keyBeans)

            }
            InputModeSwitcherManager.MASK_SKB_LAYOUT_HANDWRITING -> {// 3000 手写键盘
                var keyBeans: MutableList<SoftKey> = LinkedList()
                val keys = KeyboardData.layoutHandwritingCn[skbStyleMode]!!
                var handwritingKey = createHandwritingKey(keys[0][0]) // 符号站位
                handwritingKey.mLeftF = 0.815f
                handwritingKey.heightF = 0.50f
                keyBeans.add(handwritingKey)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                handwritingKey = createHandwritingKey(keys[1][0])
                handwritingKey.mLeftF = 0.815f
                keyBeans.add(handwritingKey)
                rows.add(keyBeans)
                keyBeans = lastRows(skbValue)
                rows.add(keyBeans)
            }
            InputModeSwitcherManager.MASK_SKB_LAYOUT_QWERTY_ABC -> {// 4000 英文全键
                var keyBeans: MutableList<SoftKey> = LinkedList()
                val keys = KeyboardData.layoutQwertyEn[skbStyleMode]!!
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
                val softKeyToggle = createKeyToggle(InputModeSwitcherManager.USER_DEF_KEYCODE_SHIFT_1)
                softKeyToggle.widthF = 0.147f
                softKeyToggle.setToggleStates(shiftToggleStates)
                keyBeans.add(softKeyToggle)
                keyBeans.addAll(createQwertyKeys(keys[2]))
                keyBeans.last().apply {
                    widthF = 0.147f
                }
                rows.add(keyBeans)
                keyBeans = lastRows(skbValue)
                keyBeans[keyBeans.size -2].stateId = 1
                rows.add(keyBeans)
            }
            InputModeSwitcherManager.MASK_SKB_LAYOUT_NUMBER -> {  // 5000 数字键盘
                var keyBeans: MutableList<SoftKey> = LinkedList()
                val keys =  KeyboardData.layoutT9Number[skbStyleMode]!!
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
                keyBeans = lastRows(skbValue)
                rows.add(keyBeans)
            }
            InputModeSwitcherManager.MASK_SKB_LAYOUT_LX17 -> {     // 6000 乱序17键盘
                var keyBeans: MutableList<SoftKey> = LinkedList()
                if(AppPrefs.getInstance().keyboardSetting.lx17WithLeftPrefix.getValue()) {
                    val keys = KeyboardData.layoutLX17CnWithLeftPrefix[skbStyleMode]!!
                    var lX17Keys = createLX17Keys(keys[0])
                    lX17Keys.first().apply {
                        widthF = 0.1457f
                        heightF = 0.75f
                    }
                    lX17Keys[1].mLeftF = 0.1457f
                    keyBeans.addAll(lX17Keys)
                    rows.add(keyBeans)
                    keyBeans = LinkedList()
                    lX17Keys = createLX17Keys(keys[1])
                    lX17Keys.first().mLeftF = 0.1457f
                    keyBeans.addAll(lX17Keys)
                    rows.add(keyBeans)
                    keyBeans = LinkedList()
                    lX17Keys = createLX17Keys(keys[2])
                    lX17Keys.first().mLeftF = 0.1457f
                    keyBeans.addAll(lX17Keys)
                    rows.add(keyBeans)
                    keyBeans = lastRows(skbValue)
                    rows.add(keyBeans)
                } else {
                    val keys =  KeyboardData.layoutLX17Cn[skbStyleMode]!!
                    var lX17Keys = createLX17Keys(keys[0], 0.165f)
                    keyBeans.addAll(lX17Keys)
                    rows.add(keyBeans)
                    keyBeans = LinkedList()
                    lX17Keys = createLX17Keys(keys[1], 0.165f)
                    keyBeans.addAll(lX17Keys)
                    rows.add(keyBeans)
                    keyBeans = LinkedList()
                    lX17Keys = createLX17Keys(keys[2], 0.165f)
                    keyBeans.addAll(lX17Keys)
                    rows.add(keyBeans)
                    keyBeans = lastRows(skbValue)
                    rows.add(keyBeans)
                }
            }
            InputModeSwitcherManager.MASK_SKB_LAYOUT_STROKE -> {  // 7000  笔画键盘
                var keyBeans: MutableList<SoftKey> = LinkedList()
                val keys =  KeyboardData.layoutStrokeCn[skbStyleMode]!!
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
                keyBeans = lastRows(skbValue)
                rows.add(keyBeans)
            }
            InputModeSwitcherManager.MASK_SKB_LAYOUT_TEXTEDIT -> {     // 8000 文本编辑键盘
                var keyBeans: MutableList<SoftKey> = LinkedList()
                val keys =  KeyboardData.layoutTextEdit[skbStyleMode]!!
                var editKeys = createTextEditKeys(keys[0])
                editKeys[0].heightF = 0.75f
                editKeys[2].heightF = 0.75f
                keyBeans.addAll(editKeys)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                editKeys = createTextEditKeys(keys[1])
                editKeys[0].mLeftF = 0.25f
                editKeys[1].mLeftF = 0.75f
                keyBeans.addAll(editKeys)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                editKeys = createTextEditKeys(keys[2])
                editKeys[0].mLeftF = 0.25f
                editKeys[1].mLeftF = 0.75f
                keyBeans.addAll(editKeys)
                rows.add(keyBeans)
                keyBeans = LinkedList()
                editKeys = createTextEditKeys(keys[3])
                editKeys[0].widthF = 0.33f
                editKeys[1].widthF = 0.33f
                editKeys[2].widthF = 0.33f
                keyBeans.addAll(editKeys)
                rows.add(keyBeans)
            }
        }
        val numberLineSkb = when(skbStyleMode){
            SkbStyleMode.Yuyan -> numberLine
            SkbStyleMode.Samsung -> numberLine
            SkbStyleMode.Google -> numberLine
        }
        softKeyboard = getSoftKeyboard(rows, numberLineSkb)
        mSoftKeyboardMap[skbValue] = softKeyboard
        return softKeyboard
    }

    // 键盘最后一行（各键盘统一，数字键盘稍微不同）
    private fun lastRows(skbValue: Int): MutableList<SoftKey> {
        val enterToggleStates = listOf(ToggleState("去往", 0), ToggleState("搜索", 1), ToggleState("发送", 2),
            ToggleState("下一个", 3), ToggleState("完成", 4))
        val softKeyToggle = createKeyToggle(KeyEvent.KEYCODE_ENTER)
        softKeyToggle.widthF = 0.18f
        softKeyToggle.stateId = 0
        softKeyToggle.setToggleStates(enterToggleStates)
        val keyBeans = mutableListOf<SoftKey>()
        val t9Keys = when(skbValue){
            InputModeSwitcherManager.MASK_SKB_LAYOUT_T9_PINYIN, InputModeSwitcherManager.MASK_SKB_LAYOUT_HANDWRITING, InputModeSwitcherManager.MASK_SKB_LAYOUT_STROKE ->{
                if(skbStyleMode == SkbStyleMode.Google){
                    createT9Keys(arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5, InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_8, InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2,
                        KeyEvent.KEYCODE_SPACE, InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14))
                } else if(skbStyleMode == SkbStyleMode.Samsung){
                    createT9Keys(arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3, InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2,
                        KeyEvent.KEYCODE_SPACE, InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5))
                } else {
                    createT9Keys(arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3, InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5,
                            KeyEvent.KEYCODE_SPACE, InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2))
                }
            }
            InputModeSwitcherManager.MASK_SKB_LAYOUT_QWERTY_ABC -> {
                if(skbStyleMode == SkbStyleMode.Google){
                    createQwertyKeys(arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5, InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_8, InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2,
                        KeyEvent.KEYCODE_SPACE, InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14))
                } else if (skbStyleMode == SkbStyleMode.Samsung) {
                    createQwertyKeys(arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3, InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2,
                        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13, KeyEvent.KEYCODE_SPACE, InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14, InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5))
                } else {
                    createQwertyKeys(arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3, InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5,
                            InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14, KeyEvent.KEYCODE_SPACE, InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2))
                }
            }
            InputModeSwitcherManager.MASK_SKB_LAYOUT_NUMBER -> {
                createT9NumberKeys(arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3, InputModeSwitcherManager.USER_DEF_KEYCODE_RETURN_6, 7, KeyEvent.KEYCODE_SPACE))
            }
            InputModeSwitcherManager.MASK_SKB_LAYOUT_LX17 -> {
                if(skbStyleMode == SkbStyleMode.Google){
                    createT9Keys(arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5, InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_8, InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2,
                        KeyEvent.KEYCODE_SPACE, InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14))
                } else if (skbStyleMode == SkbStyleMode.Samsung) {
                    createLX17Keys(arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3, InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2,
                        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13, KeyEvent.KEYCODE_SPACE, InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5))
                } else {
                    createLX17Keys(arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3, InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5,
                            InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13, KeyEvent.KEYCODE_SPACE, InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2))
                }
            }
            else -> { //0x1000 InputModeSwitcherManager.MASK_SKB_LAYOUT_QWERTY_PINYIN
                if(skbStyleMode == SkbStyleMode.Google){
                    createQwertyPYKeys(arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5, InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_8, InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2,
                        KeyEvent.KEYCODE_SPACE, InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14))
                } else if (skbStyleMode == SkbStyleMode.Samsung) {
                    createQwertyPYKeys(arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3, InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2,
                        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13, KeyEvent.KEYCODE_SPACE, InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14, InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5))
                } else {
                    createQwertyPYKeys(arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3, InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5,
                            InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13, KeyEvent.KEYCODE_SPACE, InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2))
                }
            }
        }
        if (skbStyleMode == SkbStyleMode.Google) {
            t9Keys[2].stateId = 2
            when (t9Keys.size) {
                6 -> {
                    t9Keys[0].widthF = 0.185f;t9Keys[1].widthF = 0.1f
                    t9Keys[2].widthF = 0.1f;t9Keys[3].widthF = 0.23f
                    t9Keys[4].widthF = 0.1f;t9Keys[5].widthF = 0.1f
                    softKeyToggle.widthF = 0.185f
                }
                5 -> {
                    t9Keys[0].widthF = 0.185f;t9Keys[1].widthF = 0.1f
                    t9Keys[2].widthF = 0.1f;t9Keys[3].widthF = 0.33f
                    t9Keys[4].widthF = 0.1f;softKeyToggle.widthF = 0.185f
                }
                else -> {
                    softKeyToggle.widthF = 0.18f
                    t9Keys[0].widthF = 0.18f;t9Keys[1].widthF = 0.21f
                    t9Keys[2].widthF = 0.21f;t9Keys[3].widthF = 0.21f
                }
            }
        } else if (skbStyleMode == SkbStyleMode.Samsung) {
            if(skbValue == 0x4000)t9Keys[1].stateId = 1
            when (t9Keys.size) {
                6 -> {
                    t9Keys[0].widthF = 0.1457f;t9Keys[1].widthF = 0.1457f
                    t9Keys[2].widthF = 0.099f;t9Keys[3].widthF = 0.2f
                    t9Keys[4].widthF = 0.099f;t9Keys[5].widthF = 0.1457f
                    softKeyToggle.widthF = 0.1457f
                }
                5 -> {
                    t9Keys[0].widthF = 0.16f;t9Keys[1].widthF = 0.099f
                    t9Keys[2].widthF = 0.099f;t9Keys[3].widthF = 0.38f
                    t9Keys[4].widthF = 0.099f;softKeyToggle.widthF = 0.16f
                }
                else -> {
                    softKeyToggle.widthF = 0.18f
                    t9Keys[0].widthF = 0.18f;t9Keys[1].widthF = 0.21f
                    t9Keys[2].widthF = 0.21f;t9Keys[3].widthF = 0.21f
                }
            }
        } else {
            if (t9Keys.size == 5) {
                softKeyToggle.widthF = 0.147f
                t9Keys[0].widthF = 0.147f;t9Keys[1].widthF = 0.099f
                t9Keys[2].widthF = 0.099f;t9Keys[3].widthF = 0.396f
                t9Keys[4].widthF = 0.099f
            } else {
                t9Keys[0].widthF = 0.18f;t9Keys[1].widthF = 0.147f
                t9Keys[2].widthF = 0.336f;t9Keys[3].widthF = 0.147f
            }
        }
        keyBeans.addAll(t9Keys)
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
    private fun getSoftKeyboard(rows: List<List<SoftKey>>, isNumberRow: Boolean): SoftKeyboard {
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
                        keyBean.setKeyDimensions(keyXPos, keyYPos/1.2f, keyHeight/1.2f)
                    } else {
                        keyBean.setKeyDimensions(keyXPos, keyYPos)
                    }
                }
                keyBean.setSkbCoreSize(EnvironmentSingleton.instance.skbWidth, EnvironmentSingleton.instance.skbHeight)
                lastKeyRight = keyXPos + keyWidth
                lastKeyTop = keyYPos
                lastKeyBottom = keyYPos + keyHeight
            }
        }
        return SoftKeyboard(rows)
    }

    private fun createT9Keys(codes: Array<Int>): Array<SoftKey> {
        val softKeys = mutableListOf<SoftKey>()
        val keyPreset =  if(mSkbValue == 0x7000) getKeyPreset("strokeKeyPreset") else getKeyPreset("t9PYKeyPreset")
        for(code in codes){
            val labels = keyPreset[code]
            softKeys.add(SoftKey(code = code, label = labels?.getOrNull(0) ?: "", labelSmall = labels?.getOrNull(1)?: "").apply {
                widthF = 0.21f
            })
        }
        return softKeys.toTypedArray()
    }

    private fun createT9NumberKeys(codes: Array<Int>): Array<SoftKey> {
        val softKeys = mutableListOf<SoftKey>()
        val keyPreset = getKeyPreset("t9NumberKeyPreset")
        for(code in codes){
            val labels = keyPreset[code]
            softKeys.add(SoftKey(code = code, label = labels?.getOrNull(0) ?: "", labelSmall = labels?.getOrNull(1) ?: "").apply {
                widthF = 0.21f
            })
        }
        return softKeys.toTypedArray()
    }

    private fun createQwertyPYKeys(codes: Array<Int>): Array<SoftKey> {
        val keyMnemonicPreset = when (rimeValue) {
            CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY + DoublePinyinSchemaMode.flypy -> doubleFlyMnemonicPreset
            CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY + DoublePinyinSchemaMode.abc -> doubleAbcMnemonicPreset
            CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY + DoublePinyinSchemaMode.mspy -> doubleMSMnemonicPreset
            CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY + DoublePinyinSchemaMode.natural -> doubleNaturalMnemonicPreset
            CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY + DoublePinyinSchemaMode.sogou -> doubleSogouMnemonicPreset
            CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY + DoublePinyinSchemaMode.ziguang -> doubleZiguangMnemonicPreset
            else -> emptyMap()
        }
        val softKeys = mutableListOf<SoftKey>()
        val keyPreset = if(numberLine)getKeyPreset("qwertyPYKeyPreset") else getKeyPreset("qwertyPYKeyNumberPreset")
        for(code in codes){
            val labels = keyPreset[code]
            softKeys.add(SoftKey(code = code, label = labels?.getOrNull(0) ?: "", labelSmall = labels?.getOrNull(1) ?: "", keyMnemonic = keyMnemonicPreset[code] ?: "").apply {
                widthF = 0.099f
            })
        }
        return softKeys.toTypedArray()
    }

    private fun createQwertyKeys(codes: Array<Int>): Array<SoftKey> {
        val softKeys = mutableListOf<SoftKey>()
        val keyPreset = if(numberLine)getKeyPreset("qwertyKeyPreset") else getKeyPreset("qwertyKeyNumberPreset")
        for(code in codes){
            val labels = keyPreset[code]
            softKeys.add(SoftKey(code = code, label = labels?.getOrNull(0) ?: "", labelSmall = labels?.getOrNull(1) ?: "", keyMnemonic = labels?.getOrNull(2) ?: "").apply {
                widthF = 0.099f
            })
        }
        return softKeys.toTypedArray()
    }

    private fun createHandwritingKey(code: Int): SoftKey {
        val keyPreset = if(numberLine)getKeyPreset("qwertyPYKeyPreset") else getKeyPreset("qwertyPYKeyNumberPreset")
        val labels = keyPreset[code]
        return SoftKey(code = code, label = labels?.getOrNull(0) ?: "", labelSmall = labels?.getOrNull(1) ?: "").apply {
            widthF = 0.18f
        }
    }

    private fun createNumberLineKeys(codes: Array<Int>): Array<SoftKey> {
        val softKeys = mutableListOf<SoftKey>()
        for(code in codes) {
            val softKey = SoftKey(label = code.toString()).apply {
                widthF = 0.099f
                heightF = 0.2f
            }
            softKeys.add(softKey)
        }
        return softKeys.toTypedArray()
    }

    private fun createLX17Keys(codes: Array<Int>, width: Float = 0.142f): Array<SoftKey> {
        val softKeys = mutableListOf<SoftKey>()
        val keyPreset = if(numberLine)getKeyPreset("lx17PYKeyPreset") else getKeyPreset("lx17PYKeyNumberPreset")
        for(code in codes){
            val labels = keyPreset[code]
            softKeys.add(SoftKey(code = code, label = labels?.getOrNull(0) ?: "", labelSmall = labels?.getOrNull(1) ?: "", keyMnemonic= lx17MnemonicPreset[code] ?: "").apply {
                widthF = width
            })
        }
        return softKeys.toTypedArray()
    }

    private fun createTextEditKeys(codes: Array<Int>): Array<SoftKey> {
        val softKeys = mutableListOf<SoftKey>()
        val keyPreset = getKeyPreset("textEditKeyPreset")
        for(code in codes){
            val labels = keyPreset[code]
            softKeys.add(SoftKey(code = code, label = labels?.getOrNull(0) ?: "").apply {
                widthF = 0.2475f
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

    fun getKeyPreset(key:String):Map<Int, Array<String>>{
        return KeyPreset.getKeyPreset(key)
    }
}