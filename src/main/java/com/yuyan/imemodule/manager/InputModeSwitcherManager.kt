package com.yuyan.imemodule.manager

import android.view.inputmethod.EditorInfo
import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import com.yuyan.imemodule.keyboard.KeyboardManager
import com.yuyan.inputmethod.core.Kernel

/**
 * 输入法模式转换器。设置输入法的软键盘。
 */
object InputModeSwitcherManager {

    /**
     * User defined key code, used by soft keyboard.
     * 用户定义的key的code，用于软键盘。shift键的code。
     */
    const val USER_DEF_KEYCODE_SHIFT_1 = -1

    /**
     * User defined key code, used by soft keyboard. 语言键的code,语言切换键。
     */
    const val USER_DEF_KEYCODE_LANG_2 = -2

    /**
     * User defined key code, used by soft keyboard. 语言键的code,符号键盘切换键。
     */
    const val USER_DEF_KEYCODE_SYMBOL_3 = -3

    /**
     * User defined key code, used by soft keyboard. 语言键的code,表情键盘切换键。
     */
    const val USER_DEF_KEYCODE_EMOJI_4 = -4

    /**
     * User defined key code, used by soft keyboard. 语言键的code,数字键盘切换键。
     */
    const val USER_DEF_KEYCODE_NUMBER_5 = -5

    /**
     * User defined key code, used by soft keyboard. 语言键的code,数字键盘返回按键。
     */
    const val USER_DEF_KEYCODE_RETURN_6 = -6

    /**
     * User defined key code, used by soft keyboard. 语言键的code,编辑键盘。
     */
    const val USER_DEF_KEYCODE_TEXTEDIT_7 = -7

    /**
     * User defined key code, used by soft keyboard. 表情逗号。
     */
    const val USER_DEF_KEYCODE_EMOJI_8 = -8

    /**
     * User defined key code, used by soft keyboard. 方向控制。或谷歌键盘的重输按钮
     */
    const val USER_DEF_KEYCODE_CURSOR_DIRECTION_9 = -9

    /**
     * User defined key code, used by soft keyboard. 语言键的code,九宫格、手写符号侧栏占位符。
     */
    const val USER_DEF_KEYCODE_LEFT_SYMBOL_12 = -12

    /**
     * User defined key code, used by soft keyboard. 语言键的code,逗号（中文：，;英文：,）。
     */
    const val USER_DEF_KEYCODE_LEFT_COMMA_13 = -13
    /**
     * User defined key code, used by soft keyboard. 语言键的code,句号（中文：。;英文：.）。
     */
    const val USER_DEF_KEYCODE_LEFT_PERIOD_14 = -14
    /**
     * User defined key code, used by soft keyboard. 语言键的code,星号（*）。
     */
    const val USER_DEF_KEYCODE_STAR_17 = -17

    /**
     * User defined key code, used by soft keyboard. 编辑键盘方向
     */
    const val USER_DEF_KEYCODE_SELECT_MODE = -18
    const val USER_DEF_KEYCODE_SELECT_ALL = -19
    const val USER_DEF_KEYCODE_CUT = -20
    const val USER_DEF_KEYCODE_COPY = -21
    const val USER_DEF_KEYCODE_PASTE = -22
    const val USER_DEF_KEYCODE_MOVE_START = -23
    const val USER_DEF_KEYCODE_MOVE_END = -24


    /**
     * Bits used to indicate soft keyboard layout. If none bit is set, the
     * current input mode does not require a soft keyboard.
     * 第8位指明软键盘的布局。如果最8位为0，那么就表明当前输入法模式不需要软键盘。
     */
    const val MASK_SKB_LAYOUT = 0xff00 //不同位代表意义:布局（2位）/语言/大小写/0/0/0/0

    /**
     * A kind of soft keyboard layout. An input mode should be anded with
     * [.MASK_SKB_LAYOUT] to get its soft keyboard layout. 指明标准的传统键盘,拼音
     */
    const val MASK_SKB_LAYOUT_QWERTY_PINYIN = 0x1000

    /**
     * A kind of soft keyboard layout. An input mode should be anded with
     * [.MASK_SKB_LAYOUT] to get its soft keyboard layout. 指明九宫格软键盘，拼音
     */
    const val MASK_SKB_LAYOUT_T9_PINYIN = 0x2000

    /**
     * A kind of soft keyboard layout. An input mode should be anded with
     * [.MASK_SKB_LAYOUT] to get its soft keyboard layout. 指明手写键
     */
    const val MASK_SKB_LAYOUT_HANDWRITING = 0x3000

    /**
     * A kind of soft keyboard layout. An input mode should be anded with
     * [.MASK_SKB_LAYOUT] to get its soft keyboard layout. 指明标准的传统键盘
     */
    const val MASK_SKB_LAYOUT_QWERTY_ABC = 0x4000

    /**
     * A kind of soft keyboard layout. An input mode should be anded with
     * [.MASK_SKB_LAYOUT] to get its soft keyboard layout. 指明数字键
     */
    const val MASK_SKB_LAYOUT_NUMBER = 0x5000

    /**
     * A kind of soft keyboard layout. An input mode should be anded with
     * [.MASK_SKB_LAYOUT] to get its soft keyboard layout. 指明乱序17
     */
    const val MASK_SKB_LAYOUT_LX17 = 0x6000

    /**
     * A kind of soft keyboard layout. An input mode should be anded with
     * [.MASK_SKB_LAYOUT] to get its soft keyboard layout. 指明笔画键盘
     */
    const val MASK_SKB_LAYOUT_STROKE = 0x7000

    /**
     * A kind of soft keyboard layout. An input mode should be anded with
     * [.MASK_SKB_LAYOUT] to get its soft keyboard layout. 指明文本编辑键盘
     */
    const val MASK_SKB_LAYOUT_TEXTEDIT= 0x8000

    /**
     * 第6位指明语言。
     */
    private const val MASK_LANGUAGE = 0x00f0

    /**
     * Used to indicate the current language. An input mode should be anded with
     * [.MASK_LANGUAGE] to get this information. 指明中文语言。
     */
    const val MASK_LANGUAGE_CN = 0x0010

    /**
     * Used to indicate the current language. An input mode should be anded with
     * [.MASK_LANGUAGE] to get this information. 指明英文语言。
     */
    private const val MASK_LANGUAGE_EN = 0x0020

    /**
     * 第5位指明软键盘当前的状态，比如高（大写），低（小写）。
     */
    private const val MASK_CASE = 0x000f

    /**
     * 指明软键盘状态为低（小写）。
     */
    private const val MASK_CASE_LOWER = 0x0000

    /**
     * 指明软键盘状态为高（大写）。
     */
    const val MASK_CASE_UPPER = 0x0001

    /**
     * 指明软键盘状态为高（大写）锁定状态。
     */
    private const val MASK_CASE_UPPER_LOCK = 0x0002

    /**
     * Mode for inputing Chinese with soft keyboard. 九宫格软键盘、中文模式
     */
    const val MODE_T9_CHINESE = MASK_SKB_LAYOUT_T9_PINYIN or MASK_LANGUAGE_CN

    /**
     * Mode for inputing Chinese with soft keyboard. 手写软键盘、中文模式
     */
    const val MODE_HANDWRITING_CHINESE = MASK_SKB_LAYOUT_HANDWRITING or MASK_LANGUAGE_CN

    /**
     * Mode for inputing English lower characters with soft keyboard. 标准软键盘、英文、小写模式
     */
    private const val MODE_SKB_ENGLISH_LOWER =
        MASK_SKB_LAYOUT_QWERTY_ABC or MASK_LANGUAGE_EN or MASK_CASE_LOWER

    /**
     * Mode for inputing English upper characters with soft keyboard. 标准软键盘、英文、大写软键盘模式
     */
    private const val MODE_SKB_ENGLISH_UPPER =
        MASK_SKB_LAYOUT_QWERTY_ABC or MASK_LANGUAGE_EN or MASK_CASE_UPPER

    /**
     * Mode for inputing English upper characters with soft keyboard. 标准软键盘、英文、大写锁定软键盘模式
     */
    private const val MODE_SKB_ENGLISH_UPPER_LOCK =
        MASK_SKB_LAYOUT_QWERTY_ABC or MASK_LANGUAGE_EN or MASK_CASE_UPPER_LOCK

    /**
     * Unset mode. 未设置输入法模式。
     */
    private const val MODE_UNSET = 0
    /**
     * The input mode for the current edit box. 当前输入法的模式
     */
    private var mInputMode = MODE_UNSET

    /**
     * Used to remember recent mode to input language. 最近的语言输入法模式
     */
    private var mRecentLauageInputMode = MODE_UNSET

    /**
     * Used to indicate required toggling operations.
     * 控制当前输入法模式软键盘布局要显示的按键切换状态和要显示的行ID。比如当前软键盘布局中
     * ，有一个按键有默认状态、和两个切换状态，ToggleStates中的mKeyStates[]保存的就是当前要显示的切换状态
     * ，如果按键的两个切换状态都没有在mKeyStates[]中
     * ，那么按键就显示默认状态，如果两个切换状态中有一个在mKeyStates[]中，就显示那个切换状态
     * 。注意：绝对不可能有一个按键的两个或两个以上的切换状态同时在mKeyStates
     * []中。ToggleStates不仅控制按键的显示状态，还可以直接控制一行按键的显示
     * 。mRowIdToEnable保存的就是可显示的行的ID（每行的ID不是唯一的 ，一个ID同时赋值给多行）。只有ID为
     * mRowIdToEnable 和 ALWAYS_SHOW_ROW_ID 的行才会被显示出来。
     */
	@JvmField
	val mToggleStates = ToggleStates()

    /**
     * 控制当前输入法模式软键盘布局要显示的按键切换状态和要显示的行ID的管理类。比如当前软键盘布局中
     * ，有一个按键有默认状态、和两个切换状态，ToggleStates中的mKeyStates[]保存的就是当前要显示的切换状态
     * ，如果按键的两个切换状态都没有在mKeyStates[]中
     * ，那么按键就显示默认状态，如果两个切换状态中有一个在mKeyStates[]中，就显示那个切换状态
     * 。注意：绝对不可能有一个按键的两个或两个以上的切换状态同时在mKeyStates
     * []中。ToggleStates不仅控制按键的显示状态，还可以直接控制一行按键的显示
     * 。mRowIdToEnable保存的就是可显示的行的ID（每行的ID不是唯一的 ，一个ID同时赋值给多行）。只有ID为
     * mRowIdToEnable 和 ALWAYS_SHOW_ROW_ID 的行才会被显示出来。
     */
    class ToggleStates {
        @JvmField
        var charCase = MASK_CASE_LOWER
        @JvmField
		var mStateEnter = 0
    }

    val skbImeLayout: Int
        /**
         * 更加软键盘：切换为语言键盘
         */
        get() = mRecentLauageInputMode and MASK_SKB_LAYOUT

    val skbLayout: Int
        /**
         * 更加软键盘 LAYOUT，切换回上次键盘
         */
        get() = mInputMode and MASK_SKB_LAYOUT

    // 记录SHIFT点击时间，作为双击判断
    private var lsatClickTime = 0L

    // 中文模式，临时切换为英文
    private var isChineseMode = true
    /**
     * 通过我们定义的软键盘的按键，切换输入法模式。
     */
    fun switchModeForUserKey(userKey: Int) {
        var newInputMode = MODE_UNSET
        if (USER_DEF_KEYCODE_SHIFT_1 == userKey) {
            if(isChinese && !isChineseMode)isChineseMode = true
            newInputMode = if(System.currentTimeMillis() - lsatClickTime < 300){
                MODE_SKB_ENGLISH_UPPER_LOCK
            } else if (MODE_SKB_ENGLISH_LOWER == mInputMode) {
                MODE_SKB_ENGLISH_UPPER
            } else if (MODE_SKB_ENGLISH_UPPER == mInputMode || MODE_SKB_ENGLISH_UPPER_LOCK == mInputMode){
                if(isChineseMode) getInstance().internal.inputMethodPinyinMode.getValue() else MODE_SKB_ENGLISH_LOWER
            } else {
                MODE_SKB_ENGLISH_LOWER
            }
            lsatClickTime = System.currentTimeMillis()
        } else if (USER_DEF_KEYCODE_LANG_2 == userKey) {
            newInputMode = if (isChinese) {
                isChineseMode = false
                MODE_SKB_ENGLISH_LOWER
            } else {
                getInstance().internal.inputMethodPinyinMode.getValue()
            }
        } else if (USER_DEF_KEYCODE_NUMBER_5 == userKey) {
            newInputMode = MASK_SKB_LAYOUT_NUMBER
        } else if (USER_DEF_KEYCODE_TEXTEDIT_7 == userKey) {
            newInputMode = MASK_SKB_LAYOUT_TEXTEDIT
        } else if (USER_DEF_KEYCODE_RETURN_6 == userKey) {
            newInputMode = if (mRecentLauageInputMode != 0) mRecentLauageInputMode else getInstance().internal.inputMethodPinyinMode.getValue()
        }
        // 保存新的输入法模式
        saveInputMode(newInputMode)
        KeyboardManager.instance.switchKeyboard()
    }

    /**
     * 根据编辑框的 EditorInfo 信息获取软键盘的输入法模式。
     */
    fun requestInputWithSkb(editorInfo: EditorInfo) {
        var newInputMode: Int
        when (editorInfo.inputType and EditorInfo.TYPE_MASK_CLASS) {
            EditorInfo.TYPE_CLASS_NUMBER, EditorInfo.TYPE_CLASS_PHONE, EditorInfo.TYPE_CLASS_DATETIME -> newInputMode = MASK_SKB_LAYOUT_NUMBER
            else -> {
                val v = editorInfo.inputType and EditorInfo.TYPE_MASK_VARIATION
                newInputMode = if (v == EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    || v == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
                    || v == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    || v == EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD) {
                        MODE_SKB_ENGLISH_LOWER
                    } else if(getInstance().keyboardSetting.keyboardLockEnglish.getValue()){
                        getInstance().internal.inputDefaultMode.getValue()
                    } else{
                        getInstance().internal.inputMethodPinyinMode.getValue()
                    }
            }
        }
        // 根据EditorInfo.imeOptions添加 要显示的按键的切换状态 ，以下只添加 Enter 键的切换状态。
        val action = editorInfo.imeOptions and (EditorInfo.IME_MASK_ACTION or EditorInfo.IME_FLAG_NO_ENTER_ACTION)
        var enterState = 0 //EditorInfo.IME_ACTION_GO
        when (action) {
            EditorInfo.IME_ACTION_SEARCH -> enterState = 1
            EditorInfo.IME_ACTION_SEND -> enterState = 2
            EditorInfo.IME_ACTION_NEXT -> {
                val f = editorInfo.inputType and EditorInfo.TYPE_MASK_FLAGS
                if (f != EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE) {
                    enterState = 3
                }
            }
            EditorInfo.IME_ACTION_DONE -> enterState = 4
        }
        mToggleStates.mStateEnter = enterState
        if (newInputMode != mInputMode && MODE_UNSET != newInputMode) {
            saveInputMode(newInputMode)
            KeyboardManager.instance.switchKeyboard()
        }
    }

    val isNumberSkb: Boolean
        /**
         * 是否是数字盘输入法模式
         */
        get() = mInputMode and MASK_SKB_LAYOUT == MASK_SKB_LAYOUT_NUMBER

    val isTextEditSkb: Boolean
        get() = mInputMode and MASK_SKB_LAYOUT == MASK_SKB_LAYOUT_TEXTEDIT

    val isChinese: Boolean
        /**
         * 是否是中文语言。
         */
        get() = mInputMode and MASK_LANGUAGE == MASK_LANGUAGE_CN
    val isChineseT9: Boolean
        /**
         * 是否的9宫格中文语言
         */
        get() = mInputMode and (MASK_SKB_LAYOUT or MASK_LANGUAGE) == MODE_T9_CHINESE

    val isQwert: Boolean
        /**
         * 是否的全键中文语言
         */
        get() = mInputMode and MASK_SKB_LAYOUT == MASK_SKB_LAYOUT_QWERTY_PINYIN || mInputMode and MASK_SKB_LAYOUT == MASK_SKB_LAYOUT_QWERTY_ABC

    val isChineseHandWriting: Boolean
        /**
         * 手写中文语言
         */
        get() = mInputMode and (MASK_SKB_LAYOUT or MASK_LANGUAGE) == MODE_HANDWRITING_CHINESE
    val isEnglish: Boolean
        /**
         * 是否是软件盘英语模式
         */
        get() = mInputMode and MASK_LANGUAGE == MASK_LANGUAGE_EN
    val isEnglishLower: Boolean
        /**
         * 是否是软键盘高（小写）模式
         */
        get() = mInputMode and (MASK_SKB_LAYOUT or MASK_LANGUAGE or MASK_CASE) == MODE_SKB_ENGLISH_LOWER
    val isEnglishUpperCase: Boolean
        /**
         * 是否是软键盘高（大写）模式
         */
        get() = mInputMode and (MASK_SKB_LAYOUT or MASK_LANGUAGE or MASK_CASE) == MODE_SKB_ENGLISH_UPPER


    val isEnglishUpperLockCase: Boolean
        /**
         * 是否是软键盘高（锁定大写）模式
         */
        get() = mInputMode and (MASK_SKB_LAYOUT or MASK_LANGUAGE or MASK_CASE) == MODE_SKB_ENGLISH_UPPER_LOCK

    /**
     * 保存新的输入法模式
     */
    fun saveInputMode(newInputMode: Int) {
        mInputMode = newInputMode // 设置新的输入法模式为当前的输入法模式
        // 语言键：显示中文或者英文、中符、英符的键
        if (isEnglish) {
            val charCase = mInputMode and MASK_CASE
            mToggleStates.charCase = charCase
            Kernel.initImeSchema(CustomConstant.SCHEMA_EN)
        } else {
            Kernel.initImeSchema(getInstance().internal.pinyinModeRime.getValue())
        }
        if (isChinese || isEnglish) {
            mRecentLauageInputMode = mInputMode
            getInstance().internal.inputDefaultMode.setValue(mInputMode)
        }
    }

}
