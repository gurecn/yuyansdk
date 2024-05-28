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
        // enter键状态
        val enterToggleStates = LinkedList<ToggleState>()
        enterToggleStates.add(ToggleState("去往", 0))
        enterToggleStates.add(ToggleState("搜索", 1))
        enterToggleStates.add(ToggleState("发送", 2))
        enterToggleStates.add(ToggleState("下一个", 3))
        enterToggleStates.add(ToggleState("完成", 4))
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
                val qwertyKey = createNumberLineKey("1")
                qwertyKey.mLeftF = 0.005f
                skbNumberRow.add(qwertyKey)
                skbNumberRow.add(createNumberLineKey("2"))
                skbNumberRow.add(createNumberLineKey("3"))
                skbNumberRow.add(createNumberLineKey("4"))
                skbNumberRow.add(createNumberLineKey("5"))
                skbNumberRow.add(createNumberLineKey("6"))
                skbNumberRow.add(createNumberLineKey("7"))
                skbNumberRow.add(createNumberLineKey("8"))
                skbNumberRow.add(createNumberLineKey("9"))
                skbNumberRow.add(createNumberLineKey("0"))
            }
        }
        val rows: MutableList<List<SoftKey>> = LinkedList()
        if (numberLine) {
            rows.add(skbNumberRow)
        }
        // 1000  拼音全键
        if (skbValue == 0x1000) {
            var keyBeans: MutableList<SoftKey> = LinkedList()
            var qwertyKey = createQwertyKey(45, "Q", "[")
            qwertyKey.mLeftF = 0.005f
            keyBeans.add(qwertyKey)
            keyBeans.add(createQwertyKey(51, "W", "]"))
            keyBeans.add(createQwertyKey(33, "E", "<"))
            keyBeans.add(createQwertyKey(46, "R", ">"))
            keyBeans.add(createQwertyKey(48, "T", "（"))
            keyBeans.add(createQwertyKey(53, "Y", "）"))
            keyBeans.add(createQwertyKey(49, "U", "{"))
            keyBeans.add(createQwertyKey(37, "I", "}"))
            keyBeans.add(createQwertyKey(43, "O", "|"))
            keyBeans.add(createQwertyKey(44, "P", "-"))
            rows.add(keyBeans)
            keyBeans = LinkedList()
            qwertyKey = createQwertyKey(29, "A", "-")
            qwertyKey.mLeftF = 0.06f
            keyBeans.add(qwertyKey)
            keyBeans.add(createQwertyKey(47, "S", "/"))
            keyBeans.add(createQwertyKey(32, "D", ":"))
            keyBeans.add(createQwertyKey(34, "F", ";"))
            keyBeans.add(createQwertyKey(35, "G", "_"))
            keyBeans.add(createQwertyKey(36, "H", "#"))
            keyBeans.add(createQwertyKey(38, "J", "~"))
            keyBeans.add(createQwertyKey(39, "K", "'"))
            keyBeans.add(createQwertyKey(40, "L", "\""))
            rows.add(keyBeans)
            keyBeans = LinkedList()
            qwertyKey = createQwertyKey(75, "分词", "")
            qwertyKey.mLeftF = 0.005f
            qwertyKey.widthF = 0.147f
            keyBeans.add(qwertyKey)
            keyBeans.add(createQwertyKey(54, "Z", "@"))
            keyBeans.add(createQwertyKey(52, "X", "，"))
            keyBeans.add(createQwertyKey(31, "C", "。"))
            keyBeans.add(createQwertyKey(50, "V", "&"))
            keyBeans.add(createQwertyKey(30, "B", "?"))
            keyBeans.add(createQwertyKey(42, "N", "!"))
            keyBeans.add(createQwertyKey(41, "M", "..."))
            qwertyKey = createQwertyKey(KeyEvent.KEYCODE_DEL, "", "")
            qwertyKey.widthF = 0.147f
            qwertyKey.mRepeat = true
            keyBeans.add(qwertyKey)
            rows.add(keyBeans)
            keyBeans = LinkedList()
            if (!numberLine) {
                qwertyKey = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_SYMBOL_ZH_3, "符")
                qwertyKey.mLeftF = 0.005f
                qwertyKey.widthF = 0.09f
                keyBeans.add(qwertyKey)
                qwertyKey = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_EMOJI_6)
                qwertyKey.widthF = 0.09f
                keyBeans.add(qwertyKey)
                qwertyKey = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_NUMBER_7, "123")
                qwertyKey.widthF = 0.147f
                keyBeans.add(qwertyKey)
            } else {
                qwertyKey = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_SYMBOL_ZH_3, "符")
                qwertyKey.mLeftF = 0.005f
                qwertyKey.widthF = 0.18f
                keyBeans.add(qwertyKey)
                qwertyKey = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_EMOJI_6)
                qwertyKey.widthF = 0.147f
                keyBeans.add(qwertyKey)
            }
            qwertyKey = createT9Key(KeyEvent.KEYCODE_SPACE)
            qwertyKey.widthF = 0.336f
            keyBeans.add(qwertyKey)
            qwertyKey = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_LANG_2, "中")
            qwertyKey.widthF = 0.147f
            qwertyKey.stateId = 0
            keyBeans.add(qwertyKey)
            val softKeyToggle = createT9KeyToggle(KeyEvent.KEYCODE_ENTER)
            softKeyToggle.widthF = 0.18f
            softKeyToggle.stateId = 0
            softKeyToggle.setToggleStates(enterToggleStates)
            keyBeans.add(softKeyToggle)
            rows.add(keyBeans)
        }

        // 2000  T9键键
        if (skbValue == 0x2000) {
            var keyBeans: MutableList<SoftKey> = LinkedList()
            var t9Key = createT9Key(75, "分词", "1")
            t9Key.mLeftF = 0.185f
            keyBeans.add(t9Key)
            keyBeans.add(createT9Key(9, "ABC", "2"))
            keyBeans.add(createT9Key(10, "DEF", "3"))
            t9Key = createT9Key(0, "@")
            t9Key.widthF = 0.18f
            keyBeans.add(t9Key)
            rows.add(keyBeans)
            keyBeans = LinkedList()
            t9Key = createT9Key(11, "GHI", "4")
            t9Key.mLeftF = 0.185f
            keyBeans.add(t9Key)
            keyBeans.add(createT9Key(12, "JKL", "5"))
            keyBeans.add(createT9Key(13, "MNO", "6"))
            t9Key = createT9Key(KeyEvent.KEYCODE_CLEAR, "重输")
            t9Key.widthF = 0.18f
            keyBeans.add(t9Key)
            rows.add(keyBeans)
            keyBeans = LinkedList()
            t9Key = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_LEFT_SYMBOL_12)
            t9Key.mLeftF = 0.005f
            t9Key.mTopF = 0f
            t9Key.widthF = 0.18f
            t9Key.heightF = 0.75f
            keyBeans.add(t9Key)
            keyBeans.add(createT9Key(14, "PQRS", "7"))
            keyBeans.add(createT9Key(15, "TUV", "8"))
            keyBeans.add(createT9Key(16, "WXYZ", "9"))
            t9Key = createT9Key(KeyEvent.KEYCODE_DEL)
            t9Key.widthF = 0.18f
            t9Key.mRepeat = true
            keyBeans.add(t9Key)
            rows.add(keyBeans)
            keyBeans = LinkedList()
            if (!numberLine) {
                t9Key = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_SYMBOL_ZH_3, "符")
                t9Key.mLeftF = 0.005f
                t9Key.widthF = 0.09f
                keyBeans.add(t9Key)
                t9Key = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_EMOJI_6)
                t9Key.widthF = 0.09f
                keyBeans.add(t9Key)
                t9Key = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_NUMBER_7, "123")
                t9Key.widthF = 0.147f
                keyBeans.add(t9Key)
            } else {
                t9Key = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_SYMBOL_ZH_3, "符")
                t9Key.mLeftF = 0.005f
                t9Key.widthF = 0.18f
                keyBeans.add(t9Key)
                t9Key = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_EMOJI_6)
                t9Key.widthF = 0.147f
                keyBeans.add(t9Key)
            }
            t9Key = createT9Key(KeyEvent.KEYCODE_SPACE, "空格", "0")
            t9Key.widthF = 0.336f
            keyBeans.add(t9Key)
            t9Key = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_LANG_2, "中")
            t9Key.widthF = 0.147f
            t9Key.stateId = 0
            keyBeans.add(t9Key)
            val softKeyToggle = createT9KeyToggle(KeyEvent.KEYCODE_ENTER)
            softKeyToggle.widthF = 0.18f
            softKeyToggle.stateId = 0
            softKeyToggle.setToggleStates(enterToggleStates)
            keyBeans.add(softKeyToggle)
            rows.add(keyBeans)
        }

        // 3000 手写键盘
        if (skbValue == 0x3000) {
            var keyBeans: MutableList<SoftKey> = LinkedList()
            var handwritingKey = createHandwritingKey(InputModeSwitcherManager.USERDEF_KEYCODE_LEFT_SYMBOL_12) // 符号站位
            handwritingKey.mLeftF = 0.815f
            handwritingKey.heightF = 0.63f
            keyBeans.add(handwritingKey)
            rows.add(keyBeans)
            keyBeans = LinkedList()
            handwritingKey = createHandwritingKey(KeyEvent.KEYCODE_DEL)
            handwritingKey.mLeftF = 0.815f
            handwritingKey.mRepeat = true
            keyBeans.add(handwritingKey)
            rows.add(keyBeans)
            keyBeans = LinkedList()
            if (!numberLine) {
                handwritingKey = createHandwritingKey(InputModeSwitcherManager.USERDEF_KEYCODE_SYMBOL_ZH_3, "符")
                handwritingKey.mLeftF = 0.005f
                handwritingKey.widthF = 0.09f
                keyBeans.add(handwritingKey)
                handwritingKey = createHandwritingKey(InputModeSwitcherManager.USERDEF_KEYCODE_EMOJI_6)
                handwritingKey.widthF = 0.09f
                keyBeans.add(handwritingKey)
                handwritingKey = createHandwritingKey(InputModeSwitcherManager.USERDEF_KEYCODE_NUMBER_7, "123")
                handwritingKey.widthF = 0.147f
                keyBeans.add(handwritingKey)
            } else {
                handwritingKey = createHandwritingKey(InputModeSwitcherManager.USERDEF_KEYCODE_SYMBOL_ZH_3, "符")
                handwritingKey.mLeftF = 0.005f
                handwritingKey.widthF = 0.18f
                keyBeans.add(handwritingKey)
                handwritingKey = createHandwritingKey(InputModeSwitcherManager.USERDEF_KEYCODE_EMOJI_6)
                handwritingKey.widthF = 0.147f
                keyBeans.add(handwritingKey)
            }
            handwritingKey = createHandwritingKey(KeyEvent.KEYCODE_SPACE)
            handwritingKey.widthF = 0.336f
            keyBeans.add(handwritingKey)
            handwritingKey = createHandwritingKey(InputModeSwitcherManager.USERDEF_KEYCODE_LANG_2)
            handwritingKey.widthF = 0.147f
            keyBeans.add(handwritingKey)
            val handwritingKeyToggle = createHandwritingKeyToggle(KeyEvent.KEYCODE_ENTER)
            handwritingKeyToggle.widthF = 0.18f
            handwritingKeyToggle.stateId = 0
            handwritingKeyToggle.setToggleStates(enterToggleStates)
            keyBeans.add(handwritingKeyToggle)
            rows.add(keyBeans)
        }

        // 4000  英文全键
        if (skbValue == 0x4000) {
            var keyBeans: MutableList<SoftKey> = LinkedList()
            var qwertyKey = createQwertyKey(45, "Q", "[")
            qwertyKey.mLeftF = 0.005f
            keyBeans.add(qwertyKey)
            keyBeans.add(createQwertyKey(51, "W", "]"))
            keyBeans.add(createQwertyKey(33, "E", "<"))
            keyBeans.add(createQwertyKey(46, "R", ">"))
            keyBeans.add(createQwertyKey(48, "T", "（"))
            keyBeans.add(createQwertyKey(53, "Y", "）"))
            keyBeans.add(createQwertyKey(49, "U", "{"))
            keyBeans.add(createQwertyKey(37, "I", "}"))
            keyBeans.add(createQwertyKey(43, "O", "|"))
            keyBeans.add(createQwertyKey(44, "P", "-"))
            rows.add(keyBeans)
            keyBeans = LinkedList()
            qwertyKey = createQwertyKey(29, "A", "-")
            qwertyKey.mLeftF = 0.06f
            keyBeans.add(qwertyKey)
            keyBeans.add(createQwertyKey(47, "S", "/"))
            keyBeans.add(createQwertyKey(32, "D", ":"))
            keyBeans.add(createQwertyKey(34, "F", ";"))
            keyBeans.add(createQwertyKey(35, "G", "_"))
            keyBeans.add(createQwertyKey(36, "H", "#"))
            keyBeans.add(createQwertyKey(38, "J", "~"))
            keyBeans.add(createQwertyKey(39, "K", "'"))
            keyBeans.add(createQwertyKey(40, "L", "\""))
            rows.add(keyBeans)
            keyBeans = LinkedList()
            var softKeyToggle = createQwertyKeyToggle(-1)
            softKeyToggle.mLeftF = 0.005f
            softKeyToggle.widthF = 0.147f
            softKeyToggle.setToggleStates(shiftToggleStates)
            keyBeans.add(softKeyToggle)
            keyBeans.add(createQwertyKey(54, "Z", "@"))
            keyBeans.add(createQwertyKey(52, "X", ","))
            keyBeans.add(createQwertyKey(31, "C", "."))
            keyBeans.add(createQwertyKey(50, "V", "&"))
            keyBeans.add(createQwertyKey(30, "B", "?"))
            keyBeans.add(createQwertyKey(42, "N", "!"))
            keyBeans.add(createQwertyKey(41, "M", "..."))
            qwertyKey = createQwertyKey(KeyEvent.KEYCODE_DEL)
            qwertyKey.widthF = 0.147f
            qwertyKey.mRepeat = true
            keyBeans.add(qwertyKey)
            rows.add(keyBeans)
            keyBeans = LinkedList()
            if (!numberLine) {
                qwertyKey = createQwertyKey(InputModeSwitcherManager.USERDEF_KEYCODE_SYMBOL_EN_4, "符")
                qwertyKey.mLeftF = 0.005f
                qwertyKey.widthF = 0.09f
                keyBeans.add(qwertyKey)
                qwertyKey = createQwertyKey(InputModeSwitcherManager.USERDEF_KEYCODE_EMOJI_6)
                qwertyKey.widthF = 0.09f
                keyBeans.add(qwertyKey)
                qwertyKey = createQwertyKey(InputModeSwitcherManager.USERDEF_KEYCODE_NUMBER_7, "123")
                qwertyKey.widthF = 0.147f
                keyBeans.add(qwertyKey)
            } else {
                qwertyKey = createQwertyKey(InputModeSwitcherManager.USERDEF_KEYCODE_SYMBOL_EN_4, "符")
                qwertyKey.mLeftF = 0.005f
                qwertyKey.widthF = 0.18f
                keyBeans.add(qwertyKey)
                qwertyKey = createQwertyKey(InputModeSwitcherManager.USERDEF_KEYCODE_EMOJI_6)
                qwertyKey.widthF = 0.147f
                keyBeans.add(qwertyKey)
            }
            qwertyKey = createQwertyKey(KeyEvent.KEYCODE_SPACE)
            qwertyKey.widthF = 0.336f
            keyBeans.add(qwertyKey)
            qwertyKey = createQwertyKey(InputModeSwitcherManager.USERDEF_KEYCODE_LANG_2, "英")
            qwertyKey.widthF = 0.147f
            qwertyKey.stateId = 1
            keyBeans.add(qwertyKey)
            softKeyToggle = createQwertyKeyToggle(KeyEvent.KEYCODE_ENTER)
            softKeyToggle.widthF = 0.18f
            softKeyToggle.stateId = 0
            softKeyToggle.setToggleStates(enterToggleStates)
            keyBeans.add(softKeyToggle)
            rows.add(keyBeans)
        }

        // 5000 数字键盘
        if (skbValue == 0x5000) {
            var keyBeans: MutableList<SoftKey> = LinkedList()
            var t9Key = createT9Key(8, "1")
            t9Key.mLeftF = 0.185f
            keyBeans.add(t9Key)
            keyBeans.add(createT9Key(9, "2"))
            keyBeans.add(createT9Key(10, "3"))
            t9Key = createT9Key(0, "@")
            t9Key.widthF = 0.18f
            keyBeans.add(t9Key)
            rows.add(keyBeans)
            keyBeans = LinkedList()
            t9Key = createT9Key(11, "4")
            t9Key.mLeftF = 0.185f
            keyBeans.add(t9Key)
            keyBeans.add(createT9Key(12, "5"))
            keyBeans.add(createT9Key(13, "6"))
            t9Key = createT9Key(0, ".")
            t9Key.widthF = 0.18f
            keyBeans.add(t9Key)
            rows.add(keyBeans)
            keyBeans = LinkedList()
            t9Key = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_LEFT_SYMBOL_12)
            t9Key.mLeftF = 0.005f
            t9Key.mTopF = 0f
            t9Key.widthF = 0.18f
            t9Key.heightF = 0.75f
            keyBeans.add(t9Key)
            keyBeans.add(createT9Key(14, "7"))
            keyBeans.add(createT9Key(15, "8"))
            keyBeans.add(createT9Key(16, "9"))
            t9Key = createT9Key(KeyEvent.KEYCODE_DEL)
            t9Key.widthF = 0.18f
            t9Key.mRepeat = true
            keyBeans.add(t9Key)
            rows.add(keyBeans)
            keyBeans = LinkedList()
            t9Key = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_SYMBOL_NUM_5, "符")
            t9Key.mLeftF = 0.005f
            t9Key.widthF = 0.09f
            keyBeans.add(t9Key)
            t9Key = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_EMOJI_6)
            t9Key.widthF = 0.09f
            keyBeans.add(t9Key)
            t9Key = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_RETURN_8, "返回")
            t9Key.widthF = 0.147f
            keyBeans.add(t9Key)
            t9Key = createT9Key(7, "0")
            t9Key.widthF = 0.336f
            keyBeans.add(t9Key)
            t9Key = createT9Key(KeyEvent.KEYCODE_SPACE)
            t9Key.widthF = 0.147f
            keyBeans.add(t9Key)
            val softKeyToggle = createT9KeyToggle(KeyEvent.KEYCODE_ENTER)
            softKeyToggle.widthF = 0.18f
            softKeyToggle.stateId = 0
            softKeyToggle.setToggleStates(enterToggleStates)
            keyBeans.add(softKeyToggle)
            rows.add(keyBeans)
        }

        // 6000 乱序17键盘
        if (skbValue == 0x6000) {
            var keyBeans: MutableList<SoftKey> = LinkedList()
            var lX17Key = createLX17Key(36, "HP", "-", "a ia ua")
            lX17Key.mLeftF = 0.005f
            keyBeans.add(lX17Key)
            keyBeans.add(createLX17Key(47, "Sh", "/", "en in"))
            keyBeans.add(createLX17Key(54, "Zh", ";", "ang iao"))
            keyBeans.add(createLX17Key(30, "B", "?", "ao iong"))
            keyBeans.add(createLX17Key(52, "oXv", "#", "uai uan"))
            keyBeans.add(createLX17Key(41, "MS", "~", "ie uo"))
            rows.add(keyBeans)
            keyBeans = LinkedList()
            lX17Key = createLX17Key(40, "L", "\"", "ai ue")
            lX17Key.mLeftF = 0.005f
            keyBeans.add(lX17Key)
            keyBeans.add(createLX17Key(32, "D", ":", "u"))
            keyBeans.add(createLX17Key(53, "Y", ")", "eng ing"))
            keyBeans.add(createLX17Key(51, "WZ", "@", "e"))
            keyBeans.add(createLX17Key(38, "JK", "&", "i"))
            keyBeans.add(createLX17Key(42, "NR", "!", "an"))
            rows.add(keyBeans)
            keyBeans = LinkedList()
            lX17Key = createLX17Key(31, "Ch", "，", "iang ui")
            lX17Key.mLeftF = 0.005f
            keyBeans.add(lX17Key)
            keyBeans.add(createLX17Key(45, "Q~", "。", "ian uang"))
            keyBeans.add(createLX17Key(35, "G", "_", "ei un"))
            keyBeans.add(createLX17Key(34, "FC", "…", "iu ou"))
            keyBeans.add(createLX17Key(48, "T", "(", "er ong"))
            val softKey = createLX17Key(KeyEvent.KEYCODE_DEL)
            softKey.mRepeat = true
            keyBeans.add(softKey)
            rows.add(keyBeans)
            keyBeans = LinkedList()
            if (!numberLine) {
                lX17Key = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_SYMBOL_ZH_3, "符")
                lX17Key.mLeftF = 0.005f
                lX17Key.widthF = 0.09f
                keyBeans.add(lX17Key)
                lX17Key = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_EMOJI_6)
                lX17Key.widthF = 0.09f
                keyBeans.add(lX17Key)
                lX17Key = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_NUMBER_7, "123")
                lX17Key.widthF = 0.147f
                keyBeans.add(lX17Key)
            } else {
                lX17Key = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_SYMBOL_ZH_3, "符")
                lX17Key.mLeftF = 0.005f
                lX17Key.widthF = 0.18f
                keyBeans.add(lX17Key)
                lX17Key = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_EMOJI_6)
                lX17Key.widthF = 0.147f
                keyBeans.add(lX17Key)
            }
            lX17Key = createT9Key(KeyEvent.KEYCODE_SPACE)
            lX17Key.widthF = 0.336f
            keyBeans.add(lX17Key)
            lX17Key = createT9Key(InputModeSwitcherManager.USERDEF_KEYCODE_LANG_2, "中")
            lX17Key.widthF = 0.147f
            lX17Key.stateId = 0
            keyBeans.add(lX17Key)
            val softKeyToggle = createT9KeyToggle(KeyEvent.KEYCODE_ENTER)
            softKeyToggle.widthF = 0.18f
            softKeyToggle.stateId = 0
            softKeyToggle.setToggleStates(enterToggleStates)
            keyBeans.add(softKeyToggle)
            rows.add(keyBeans)
        }
        softKeyboard = getSoftKeyboard(skbValue, rows, numberLine)
        mSoftKeyboardMap[skbValue] = softKeyboard
        d(TAG, "loadBaseSkb finish:$skbValue")
        return softKeyboard
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
    private fun getSoftKeyboard(
        skbValue: Int,
        rows: List<List<SoftKey>>,
        isNumberRow: Boolean
    ): SoftKeyboard {
        val skbWidth = EnvironmentSingleton.instance!!.skbWidth
        val softKeyboardHeight = EnvironmentSingleton.instance!!.skbHeight
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

    /**
     * 创建固定T9按键
     */
    private fun createT9Key(code: Int, label: String = "", labelSmall: String = ""): SoftKey {
        val softKey = SoftKey(code, label, labelSmall)
        softKey.widthF = 0.21f
        softKey.heightF = 0.25f
        return softKey
    }

    /**
     * 创建固定标准按键
     */
    private fun createQwertyKey(code: Int, label: String = "", labelSmall: String = ""): SoftKey {
        val softKey = SoftKey(code, label, labelSmall)
        softKey.widthF = 0.099f
        softKey.heightF = 0.25f
        return softKey
    }

    /**
     * 创建固定手写按键
     */
    private fun createHandwritingKey(code: Int, label: String = "", labelSmall: String = ""): SoftKey {
        val softKey = SoftKey(code, label, labelSmall)
        softKey.widthF = 0.18f
        softKey.heightF = 0.18f
        return softKey
    }

    /**
     * 创建固定标准按键
     */
    private fun createNumberLineKey(label: String): SoftKey {
        val softKey = SoftKey(label)
        softKey.widthF = 0.099f
        softKey.heightF = 0.15f
        return softKey
    }

    /**
     * 创建固定T9按键
     */
    private fun createLX17Key(code: Int, label: String = "", labelSmall: String = "", keyMnemonic: String = ""): SoftKey {
        val softKey = SoftKey(code, label, labelSmall, keyMnemonic)
        softKey.widthF = 0.165f
        softKey.heightF = 0.25f
        return softKey
    }

    /**
     * 创建状态可变按键
     */
    private fun createT9KeyToggle(code: Int): SoftKeyToggle {
        val softKey = SoftKeyToggle(code)
        softKey.widthF = 0.23f
        softKey.heightF = 0.25f
        return softKey
    }

    private fun createQwertyKeyToggle(code: Int): SoftKeyToggle {
        val softKey = SoftKeyToggle(code)
        softKey.widthF = 0.098f
        softKey.heightF = 0.25f
        return softKey
    }

    private fun createHandwritingKeyToggle(code: Int): SoftKeyToggle {
        val softKey = SoftKeyToggle(code)
        softKey.widthF = 0.15f
        softKey.heightF = 0.18f
        return softKey
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
