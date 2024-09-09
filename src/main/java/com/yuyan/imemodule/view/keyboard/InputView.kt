package com.yuyan.imemodule.view.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.InputType
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.LauncherModel
import com.yuyan.imemodule.callback.CandidateViewListener
import com.yuyan.imemodule.callback.IResponseKeyEvent
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.data.theme.ThemeManager.prefs
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.manager.SymbolsManager
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.KeyboardOneHandedMod
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode
import com.yuyan.imemodule.service.DecodingInfo
import com.yuyan.imemodule.service.ImeService
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.ui.utils.AppUtil
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.utils.KeyboardLoaderUtil
import com.yuyan.imemodule.utils.StringUtils
import com.yuyan.imemodule.view.CandidatesBar
import com.yuyan.imemodule.view.ComposingView
import com.yuyan.imemodule.view.keyboard.container.CandidatesContainer
import com.yuyan.imemodule.view.keyboard.container.ClipBoardContainer
import com.yuyan.imemodule.view.keyboard.container.InputViewParent
import com.yuyan.imemodule.view.keyboard.container.SettingsContainer
import com.yuyan.imemodule.view.keyboard.container.SymbolContainer
import com.yuyan.imemodule.view.keyboard.container.T9TextContainer
import com.yuyan.imemodule.view.popup.PopupComponent
import com.yuyan.inputmethod.core.CandidateListItem
import com.yuyan.inputmethod.core.Kernel
import splitties.bitflags.hasFlag
import splitties.views.bottomPadding
import splitties.views.rightPadding

/**
 * 输入法主界面。
 *
 * 包含拼音显示、候选词栏、键盘界面等。
 *
 * 在 该类中处理界面绘制、、输入逻辑等为输入法核心处理类。
 *
 * 注: 所有键盘自定义 View禁用构造方法警告，且不创建含AttributeSet的构造方法。为了实现代码混淆效果。
 */

@SuppressLint("ViewConstructor")
class InputView(context: Context, service: ImeService) : RelativeLayout(context),
    IResponseKeyEvent {
    private var service: ImeService
    val mInputModeSwitcher = InputModeSwitcherManager()
    val mDecInfo = DecodingInfo() // 词库解码操作对象
    private var currentInputEditorInfo:EditorInfo? = null
    private var isSkipEngineMode = false //选择候选词栏时，为true则不进行引擎操作。当为切板模式或常用符号模式时为true。
    private var mImeState = ImeState.STATE_IDLE // 当前的输入法状态
    private var mChoiceNotifier = ChoiceNotifier()
    private lateinit var mComposingView: ComposingView // 组成字符串的View，用于显示输入的拼音。
    lateinit var mSkbRoot: RelativeLayout
    private lateinit var mSkbCandidatesBarView: CandidatesBar //候选词栏根View
    private lateinit var mHoderLayoutLeft: LinearLayout
    private lateinit var mHoderLayoutRight: LinearLayout
    private lateinit var mOnehandHoderLayout: LinearLayout
    private lateinit var mLlKeyboardBottomHolder: LinearLayout

    init {
        this.service = service
        initNavbarBackground(service)
        initView(context)
    }

    fun initView(context: Context) {
        if (!::mSkbRoot.isInitialized) {
            mSkbRoot = LayoutInflater.from(context).inflate(R.layout.sdk_skb_container, this, false) as RelativeLayout
            mSkbCandidatesBarView = mSkbRoot.findViewById(R.id.candidates_bar)
            mHoderLayoutLeft = mSkbRoot.findViewById(R.id.ll_skb_holder_layout_left)
            mHoderLayoutRight = mSkbRoot.findViewById(R.id.ll_skb_holder_layout_right)
            val mIvcSkbContainer:InputViewParent = mSkbRoot.findViewById(R.id.skb_input_keyboard_view)
            KeyboardManager.instance.setData(mIvcSkbContainer, this)
            mLlKeyboardBottomHolder =  mSkbRoot.findViewById(R.id.iv_keyboard_holder)
            addView(mSkbRoot)
            mComposingView = ComposingView(context)
            mComposingView.setPadding(DevicesUtils.dip2px(10), 0,DevicesUtils.dip2px(10),0)
            addView(mComposingView,  LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                addRule(ABOVE, mSkbRoot.id)
                addRule(ALIGN_LEFT, mSkbRoot.id)
            })
            val root = PopupComponent.get().root
            val viewParent = root.parent
            if (viewParent != null) {
                (viewParent as ViewGroup).removeView(root)
            }
            addView(root, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                addRule(ALIGN_BOTTOM, mSkbRoot.id)
                addRule(ALIGN_LEFT, mSkbRoot.id)
            })
        }
        mSkbCandidatesBarView.initialize(mChoiceNotifier, mDecInfo)
        val oneHandedModSwitch = prefs.oneHandedModSwitch.getValue()
        val oneHandedMod = prefs.oneHandedMod.getValue()
        if(::mOnehandHoderLayout.isInitialized)mOnehandHoderLayout.visibility = GONE
        if (oneHandedModSwitch) {
            mOnehandHoderLayout = when(oneHandedMod){
                KeyboardOneHandedMod.LEFT ->  mHoderLayoutRight
                else -> mHoderLayoutLeft
            }
            mOnehandHoderLayout.visibility = VISIBLE
            val mIbOneHandNone = mOnehandHoderLayout.findViewById<ImageButton>(R.id.ib_holder_one_hand_none)
            mIbOneHandNone.setOnClickListener { view: View -> onClick(view) }
            val mIbOneHand = mOnehandHoderLayout.findViewById<ImageButton>(R.id.ib_holder_one_hand_left)
            mIbOneHand.setOnClickListener { view: View -> onClick(view) }
            val layoutParamsHoder = mOnehandHoderLayout.layoutParams
            val margin = EnvironmentSingleton.instance.heightForCandidates
            layoutParamsHoder.width = EnvironmentSingleton.instance.holderWidth
            layoutParamsHoder.height = EnvironmentSingleton.instance.skbHeight + margin
        }
        if(EnvironmentSingleton.instance.isLandscape || prefs.keyboardModeFloat.getValue()){
            bottomPadding = (if(EnvironmentSingleton.instance.isLandscape) AppPrefs.getInstance().internal.keyboardBottomPaddingLandscapeFloat
                else AppPrefs.getInstance().internal.keyboardBottomPaddingFloat).getValue()
            rightPadding = (if(EnvironmentSingleton.instance.isLandscape) AppPrefs.getInstance().internal.keyboardRightPaddingLandscapeFloat
            else AppPrefs.getInstance().internal.keyboardRightPaddingFloat).getValue()
            mSkbRoot.bottomPadding = 0
            mSkbRoot.rightPadding = 0
            mLlKeyboardBottomHolder.minimumHeight = 0
        } else {
            bottomPadding = 0
            rightPadding = 0
            mSkbRoot.bottomPadding = AppPrefs.getInstance().internal.keyboardBottomPadding.getValue()
            mSkbRoot.rightPadding = AppPrefs.getInstance().internal.keyboardRightPadding.getValue()
            mLlKeyboardBottomHolder.minimumHeight = EnvironmentSingleton.instance.systemNavbarWindowsBottom
        }
        updateTheme()
    }

    // 刷新主题
    fun updateTheme() {
        setBackgroundResource(android.R.color.transparent)
        mSkbRoot.background = activeTheme.backgroundDrawable(prefs.keyBorder.getValue())
        mComposingView.updateTheme(activeTheme)
        mSkbCandidatesBarView.updateTheme(activeTheme.keyTextColor)
    }

    private fun onClick(view: View) {
        if (view.id == R.id.ib_holder_one_hand_none) {
            prefs.oneHandedModSwitch.setValue(!prefs.oneHandedModSwitch.getValue())
        } else {
            val oneHandedMod = prefs.oneHandedMod.getValue()
            prefs.oneHandedMod.setValue(if (oneHandedMod == KeyboardOneHandedMod.LEFT) KeyboardOneHandedMod.RIGHT else KeyboardOneHandedMod.LEFT)
        }
        EnvironmentSingleton.instance.initData()
        KeyboardLoaderUtil.instance.clearKeyboardMap()
        KeyboardManager.instance.clearKeyboard()
        KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
    }

    /**
     * 响应软键盘按键的处理函数。在软键盘集装箱SkbContainer中responseKeyEvent（）的调用。
     * 软键盘集装箱SkbContainer的responseKeyEvent（）在自身类中调用。
     */
    override fun responseKeyEvent(sKey: SoftKey) {
        // 播放按键声音和震动
        DevicesUtils.tryPlayKeyDown(sKey)
        DevicesUtils.tryVibrate(this)
        isSkipEngineMode = false
        val keyCode = sKey.keyCode
        if (sKey.isKeyCodeKey) {  // 系统的keycode,单独处理
            val keyEvent = KeyEvent(0, 0, KeyEvent.ACTION_UP, keyCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD)
            processKey(keyEvent)
        } else if (sKey.isUserDefKey) { // 是用户定义的keycode
            if (!mDecInfo.isAssociate && !mDecInfo.isCandidatesListEmpty) {
                if(mInputModeSwitcher.isChinese) {
                    chooseAndUpdate(0)
                } else if(mInputModeSwitcher.isEnglish){
                    val displayStr = mDecInfo.composingStrForCommit // 把输入的拼音字符串发送给EditText
                    commitDecInfoText(displayStr)
                    resetToIdleState()
                }
            }
            if (InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 == keyCode || InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4 == keyCode) {  // 点击标点、表情按钮
                val symbolType = if (keyCode == InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4) { 4 } else if(mInputModeSwitcher.isEnglish) { 1 } else if(mInputModeSwitcher.isNumberSkb) { 2 } else { 0 }
                val symbols = SymbolsManager.instance!!.getmSymbols(symbolType)
                showSymbols(symbols)
                KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.SYMBOL)
                (KeyboardManager.instance.currentContainer as SymbolContainer?)!!.setSymbolsView(
                    symbolType
                )
            } else {
                mInputModeSwitcher.switchModeForUserKey(keyCode)
                resetToIdleState()
            }
        } else if (sKey.isUniStrKey) {  // 字符按键
            if (!mDecInfo.isAssociate && !mDecInfo.isCandidatesListEmpty) {
                if(mInputModeSwitcher.isChinese) {
                    chooseAndUpdate(0)
                } else if(mInputModeSwitcher.isEnglish){
                    val displayStr = mDecInfo.composingStrForCommit // 把输入的拼音字符串发送给EditText
                    commitDecInfoText(displayStr)
                    resetToIdleState()
                }
            }
            val selectSymbol = sKey.keyLabel
            if(selectSymbol != null)commitText(selectSymbol)
        }
    }

    /**
     * 响应软键盘长按键的处理函数。在软键盘集装箱SkbContainer中responseKeyEvent（）的调用。
     * 软键盘集装箱SkbContainer的responseKeyEvent（）在自身类中调用。
     */
    override fun responseLongKeyEvent(showText: String?) {
        if (!mDecInfo.isAssociate && !mDecInfo.isCandidatesListEmpty) {
            if(mInputModeSwitcher.isChinese) {
                chooseAndUpdate(0)
            } else if(mInputModeSwitcher.isEnglish){
                val displayStr = mDecInfo.composingStrForCommit // 把输入的拼音字符串发送给EditText
                commitDecInfoText(displayStr)
                resetToIdleState()
            }
            mDecInfo.reset()
        }
        if (showText != null)commitText(showText)
    }

    override fun responseHandwritingResultEvent(words: ArrayList<CandidateListItem?>) {
        mDecInfo.isAssociate = false
        mDecInfo.cacheCandidates(words)
        changeToStateInput()
    }

    /**
     * 按键处理函数
     */
    fun processKey(event: KeyEvent): Boolean {
        // 功能键处理
        if (processFunctionKeys(event)) {
            return true
        }
        var result = false
        if (!mInputModeSwitcher.mInputTypePassword &&(mInputModeSwitcher.isEnglish || mInputModeSwitcher.isChinese)) { // 中文、英语输入模式
            result = when (mImeState) {
                ImeState.STATE_IDLE -> processStateIdle(event)
                ImeState.STATE_INPUT -> processStateInput(event)
                ImeState.STATE_PREDICT -> processStatePredict(event)
                ImeState.STATE_COMPOSING -> processStateEditComposing(event)
            }
        } else { // 数字、符号处理 && 英语、未开启智能英文
            val keyChar = event.unicodeChar
            if (0 != keyChar) {
                sendKeyChar(
                    if (mInputModeSwitcher.isEnglishLower) Character.toLowerCase(keyChar.toChar())
                    else  Character.toUpperCase(keyChar.toChar())
                )
                result = true
            }
        }
        return result
    }

    /**
     * 功能键处理函数
     */
    private fun processFunctionKeys(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (service.isInputViewShown) {
                requestHideSelf()
                return true
            }
        }
        if (keyCode == KeyEvent.KEYCODE_CLEAR) {
            resetToIdleState()
            return true
        }
        // 中文，智能英文输入单独处理（涉及引擎操作），不在这边处理。
        if (mInputModeSwitcher.mInputTypePassword || (!mInputModeSwitcher.isChinese && !mInputModeSwitcher.isEnglish)) {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                sendKeyEvent(keyCode)
                return true
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_SPACE) {
                sendKeyEvent(keyCode)
                return true
            }
        }
        return false
    }

    /**
     * 当 mImeState == ImeState.STATE_IDLE 或者 mImeState ==
     * ImeState.STATE_APP_COMPLETION 时的按键处理函数
     */
    private fun processStateIdle(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        val keyChar = event.unicodeChar
        if (keyChar in 'A'.code .. 'Z'.code || keyChar in 'a'.code .. 'z'.code || keyChar in  '0'.code .. '9'.code|| keyChar == '\''.code){
            mDecInfo.inputAction(keyCode, event)
            // 对输入的拼音进行查询
            updateCandidate()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_SPACE) {
            if (!mDecInfo.isCandidatesListEmpty && !mDecInfo.isAssociate) {
                chooseAndUpdate(0)
            } else {
                sendKeyEvent(keyCode)
            }
            return true
        } else if (keyCode != 0) {
            sendKeyEvent(keyCode)
            return true
        } else if (keyChar != 0) {
            sendKeyChar(keyChar.toChar())
        }
        return false
    }

    /**
     * 当 mImeState == ImeState.STATE_INPUT 时的按键处理函数
     */
    private fun processStateInput(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        val keyChar = event.unicodeChar
        if (keyChar in 'A'.code .. 'Z'.code || keyChar in 'a'.code .. 'z'.code || keyChar in  '0'.code .. '9'.code|| keyChar == '\''.code) {
            //判断如果是拼写模式下  点击英文键盘上的数字键和数字键盘 已添加字符的形式添加
            mDecInfo.inputAction(keyCode, KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            updateCandidate()
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (mDecInfo.composingStrForDisplay.isEmpty()) {
                sendKeyEvent(keyCode)
            } else {
                mDecInfo.deleteAction()
                updateCandidate()
            }
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            val displayStr = mDecInfo.composingStrForCommit // 把输入的拼音字符串发送给EditText
            commitDecInfoText(displayStr)
            resetToIdleState()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_SPACE) {
            // 选择高亮的候选词
            if (!mDecInfo.isCandidatesListEmpty && !mDecInfo.isAssociate) {
                chooseAndUpdate(0)
            }
            return true
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (service.isInputViewShown) {
                requestHideSelf()
                return true
            }
        }
        return false
    }

    /**
     * 当 mImeState == ImeState.STATE_PREDICT 时的按键处理函数
     */
    private fun processStatePredict(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        val keyChar = event.unicodeChar
        if (keyChar in 'A'.code .. 'Z'.code || keyChar in 'a'.code .. 'z'.code || keyChar in  '0'.code .. '9'.code|| keyChar == '\''.code){
            changeToStateInput()
            // 加一个字符进输入的拼音字符串中
            mDecInfo.inputAction(keyCode, event)
            // 对输入的拼音进行查询。
            updateCandidate()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (mDecInfo.isCandidatesListEmpty) {  //联想状态且无联想词时，点击删除执行删除操作
                sendKeyEvent(keyCode)
            }
            resetToIdleState()
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (service.isInputViewShown) {
                requestHideSelf()
                return true
            }
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            val retStr = mDecInfo.composingStrForCommit
            if (!TextUtils.isEmpty(retStr)) {
                // 发送文本给EditText
                commitDecInfoText(retStr)
            } else {
                // 发生ENTER键给EditText
                sendKeyEvent(keyCode)
            }
            resetToIdleState()
        } else if (keyCode == KeyEvent.KEYCODE_SPACE) {
            // 选择候选词
            if (!mDecInfo.isCandidatesListEmpty && !mDecInfo.isAssociate) {
                chooseAndUpdate(0)
            }
        }
        return false
    }

    /**
     * 当 mImeState == ImeState.STATE_COMPOSING 时的按键处理函数
     */
    private fun processStateEditComposing(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        val keyChar = event.unicodeChar
        if (keyChar in 'A'.code .. 'Z'.code || keyChar in 'a'.code .. 'z'.code || keyChar in  '0'.code .. '9'.code|| keyChar == '\''.code) {
            //判断如果是拼写模式下  点击英文键盘上的数字键和数字键盘 已添加字符的形式添加
            mDecInfo.inputAction(keyCode, KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            updateCandidate()
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (mDecInfo.composingStrForDisplay.isEmpty()) {
                sendKeyEvent(keyCode)
            } else {
                mDecInfo.deleteAction()
                updateCandidate()
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_SPACE) {
            if (!mDecInfo.isCandidatesListEmpty && !mDecInfo.isAssociate) {
                chooseAndUpdate(0)
            }
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // 获取原始的输入拼音的字符
            val composingStr = mDecInfo.composingStrForDisplay
            if (composingStr.isEmpty()) { // 发送 ENTER 键给 EditText
                sendKeyEvent(keyCode)
            } else { // 发送文本给EditText
                commitDecInfoText(composingStr.replace("'", ""))
                resetToIdleState()
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (service.isInputViewShown) {
                requestHideSelf()
                return true
            }
        }
        return false
    }

    /**
     * 设置输入法状态为 mImeState = ImeState.STATE_COMPOSING;
     */
    private fun changeToStateComposing() {
        mImeState = ImeState.STATE_COMPOSING
    }

    /**
     * 设置输入法状态为 mImeState = ImeState.STATE_INPUT;
     */
    private fun changeToStateInput() {
        mImeState = ImeState.STATE_INPUT
        updateCandidateBar()
    }

    /**
     * 重置到空闲状态
     */
    fun resetToIdleState() {
        if (mInputModeSwitcher.isEnglish) setComposingText("") // 清除预选词
        resetCandidateWindow()
        // 从候选词、符号界面切换到输入键盘
        KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
        val container = KeyboardManager.instance.currentContainer
        (container as? T9TextContainer)?.updateSymbolListView()
        mComposingView.setDecodingInfo(mDecInfo)
        mImeState = ImeState.STATE_IDLE
    }

    /**
     * 选择候选词，并根据条件是否进行下一步的预报。
     * @param candId 选择索引
     */
    private fun chooseAndUpdate(candId: Int) {
        // 剪贴板或候选词栏常用符号模式时，不调用引擎查询
        if (isSkipEngineMode) {
            val choice = mDecInfo.getCandidate(candId)
            if (choice != null && choice.text.isNotEmpty()) {
                commitDecInfoText(choice.text)
            }
            resetToIdleState()
        } else {
            mDecInfo.chooseDecodingCandidate(candId)
            // 选择的候选词上屏
            if (mDecInfo.isFinish) {
                var choice: String? = mDecInfo.fullSent
                if (TextUtils.isEmpty(choice)) choice = mDecInfo.getCandidate(candId)?.text
                commitDecInfoText(choice)
                resetToIdleState()
            } else {  // 不上屏，继续选择
                val composing = mDecInfo.composingStrForDisplay
                if (ImeState.STATE_IDLE == mImeState || composing.isNotEmpty()) {
                    if (mInputModeSwitcher.isEnglish) {
                        setComposingText(composing)
                    }
                    changeToStateComposing()
                    updateCandidateBar()
                    val container = KeyboardManager.instance.currentContainer
                    (container as? T9TextContainer)?.updateSymbolListView()
                } else {
                    resetToIdleState()
                }
            }
        }
    }

    /**
     * 刷新候选词，重新从词库进行获取。
     */
    private fun updateCandidate() {
        mDecInfo.chooseDecodingCandidate(-1)
        val composing = mDecInfo.composingStrForDisplay
        if (ImeState.STATE_IDLE == mImeState || composing.isNotEmpty()) {
            if (mInputModeSwitcher.isEnglish) {
                setComposingText(composing)
            }
            changeToStateComposing()
            updateCandidateBar()
            val container = KeyboardManager.instance.currentContainer
            (container as? T9TextContainer)?.updateSymbolListView()
        } else {
            resetToIdleState()
        }
    }

    /**
     * 显示候选词视图
     */
    fun updateCandidateBar() {
        mSkbCandidatesBarView.showCandidates()
        mComposingView.setDecodingInfo(mDecInfo)
    }

    /**
     * 重置候选词区域
     */
    private fun resetCandidateWindow() {
        isSkipEngineMode = false
        mDecInfo.reset()
        updateCandidateBar()
    }

    /**
     * 选择候选词后的处理函数。
     */
    fun onChoiceTouched(activeCandNo: Int) {
        if (mImeState == ImeState.STATE_COMPOSING || mImeState == ImeState.STATE_INPUT || mImeState == ImeState.STATE_PREDICT) {
            // 播放按键声音和震动
            DevicesUtils.tryPlayKeyDown()
            DevicesUtils.tryVibrate(this)
            // 选择候选词
            chooseAndUpdate(activeCandNo)
        } else {
            resetToIdleState()
        }
    }

    /**
     * 当用户选择了候选词或者在候选词视图滑动了手势时的通知输入法。实现了候选词视图的监听器CandidateViewListener，
     * 有选择候选词的处理函数、隐藏键盘的事件
     */
    inner class ChoiceNotifier internal constructor() : CandidateViewListener {
        override fun onClickChoice(choiceId: Int) {
            onChoiceTouched(choiceId)
        }

        override fun onClickMore(level: Int, position: Int) {
            if (ImeState.STATE_COMPOSING == mImeState) {
                changeToStateInput()
            }
            if (level == 0) {
                KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.CANDIDATES)
                val candidatesContainer = KeyboardManager.instance.currentContainer as CandidatesContainer?
                candidatesContainer?.showCandidatesView(position)
            } else {
                val container = KeyboardManager.instance.currentContainer
                (container as? T9TextContainer)?.updateSymbolListView()
                KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
            }
        }

        override fun onClickSetting() {
            if (KeyboardManager.instance.isInputKeyboard) {
                KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.SETTINGS)
                (KeyboardManager.instance.currentContainer as SettingsContainer?)?.showSettingsView()
            } else {
                KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
                updateCandidateBar()
            }
        }

        override fun onClickMenu(skbMenuMode: SkbMenuMode) {
            onSettingsMenuClick(skbMenuMode)
        }

        override fun onClickCloseKeyboard() {
            requestHideSelf()
        }

        override fun onClickClearCandidate() {
            resetToIdleState()
            KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
        }

        override fun onClickClearClipBoard() {
            LauncherModel.instance.mClipboardDao?.clearAllClipBoardContent()
            KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
            resetToIdleState()
        }
    }

    public fun onSettingsMenuClick(skbMenuMode: SkbMenuMode) {
        when (skbMenuMode) {
            SkbMenuMode.EmojiKeyboard -> {
                val symbols = SymbolsManager.instance!!.getmSymbols(CustomConstant.EMOJI_TYPR_FACE_DATA)
                showSymbols(symbols)
                KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.SYMBOL)
                (KeyboardManager.instance.currentContainer as SymbolContainer?)!!.setSymbolsView(
                    CustomConstant.EMOJI_TYPR_FACE_DATA)
            }
            SkbMenuMode.SwitchKeyboard -> (KeyboardManager.instance.currentContainer as SettingsContainer?)!!.showSkbSelelctModeView()
            SkbMenuMode.KeyboardHeight -> {
                KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
                KeyboardManager.instance.currentContainer!!.setKeyboardHeight()
            }
            SkbMenuMode.DarkTheme -> {
                val isDark = activeTheme.isDark
                val theme: Theme = if (isDark) {
                    prefs.lightModeTheme.getValue()
                } else {
                    prefs.darkModeTheme.getValue()
                }
                ThemeManager.setNormalModeTheme(theme)
                KeyboardManager.instance.clearKeyboard()
                KeyboardManager.instance.switchKeyboard(
                    mInputModeSwitcher.skbLayout
                )
            }
            SkbMenuMode.Feedback -> {
                AppUtil.launchSettingsToKeyboard(context)
            }
            SkbMenuMode.NumberRow -> {
                val abcNumberLine = prefs.abcNumberLine.getValue()
                prefs.abcNumberLine.setValue(!abcNumberLine)
                //更换键盘模式后 重亲加载键盘
                KeyboardLoaderUtil.instance.changeSKBNumberRow()
                KeyboardManager.instance.clearKeyboard()
                KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
            }
            SkbMenuMode.JianFan -> {
                val chineseFanTi = AppPrefs.getInstance().input.chineseFanTi.getValue()
                AppPrefs.getInstance().input.chineseFanTi.setValue(!chineseFanTi)
                Kernel.nativeUpdateImeOption()
                KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
            }
            SkbMenuMode.LockEnglish -> {
                val keyboardLockEnglish = prefs.keyboardLockEnglish.getValue()
                prefs.keyboardLockEnglish.setValue(!keyboardLockEnglish)
                KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
            }
            SkbMenuMode.SymbolShow -> {
                val keyboardSymbol = prefs.keyboardSymbol.getValue()
                prefs.keyboardSymbol.setValue(!keyboardSymbol)
                KeyboardManager.instance.clearKeyboard()
                KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
            }
            SkbMenuMode.Mnemonic -> {
                val keyboardMnemonic = prefs.keyboardMnemonic.getValue()
                prefs.keyboardMnemonic.setValue(!keyboardMnemonic)
                KeyboardManager.instance.clearKeyboard()
                KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
            }
            SkbMenuMode.EmojiInput -> {
                val emojiInput = AppPrefs.getInstance().input.emojiInput.getValue()
                AppPrefs.getInstance().input.emojiInput.setValue(!emojiInput)
                Kernel.nativeUpdateImeOption()
                KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
            }
            SkbMenuMode.Handwriting -> AppUtil.launchSettingsToHandwriting(context)
            SkbMenuMode.Settings -> AppUtil.launchSettings(context)
            SkbMenuMode.OneHanded -> {
                prefs.oneHandedModSwitch.setValue(!prefs.oneHandedModSwitch.getValue())
                EnvironmentSingleton.instance.initData()
                KeyboardLoaderUtil.instance.clearKeyboardMap()
                KeyboardManager.instance.clearKeyboard()
                KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
            }
            SkbMenuMode.FlowerTypeface -> {
                showFlowerTypeface()
                KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
            }
            SkbMenuMode.FloatKeyboard -> {
                if(!EnvironmentSingleton.instance.isLandscape) {  // 横屏强制悬浮键盘，暂不支持关闭
                    val keyboardModeFloat = prefs.keyboardModeFloat.getValue()
                    prefs.keyboardModeFloat.setValue(!keyboardModeFloat)
                    EnvironmentSingleton.instance.initData()
                    KeyboardLoaderUtil.instance.clearKeyboardMap()
                    KeyboardManager.instance.clearKeyboard()
                }
                KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
            }
            SkbMenuMode.ClipBoard -> {
                KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.ClipBoard)
                updateCandidateBar()
                (KeyboardManager.instance.currentContainer as ClipBoardContainer?)?.showClipBoardView()
            }
            else ->{}
        }
    }



    /**
     * 输入法状态
     * 空闲，输入，编辑，联想
     */
    enum class ImeState {
        STATE_IDLE, STATE_INPUT, STATE_COMPOSING, STATE_PREDICT
    }

    /**
     * 点击花漾字菜单
     */
    fun showFlowerTypeface() {
        mSkbCandidatesBarView.showFlowerTypeface()
    }

    /**
     * 选择拼音
     */
    fun selectPrefix(position: Int) {
        // 播放按键声音和震动
        DevicesUtils.tryPlayKeyDown()
        DevicesUtils.tryVibrate(this)
        mDecInfo.selectPrefix(position)
        updateCandidate()
    }

    //展示常用符号
    fun showSymbols(symbols: Array<String>) {
        //设置候选词
        val list = ArrayList<CandidateListItem?>()
        for (symbol in symbols) {
            list.add(CandidateListItem("", symbol))
        }
        mDecInfo.cacheCandidates(list)
        mDecInfo.isAssociate = true
        isSkipEngineMode = true
        mSkbCandidatesBarView.showCandidates()
        mImeState = ImeState.STATE_PREDICT
    }

    fun requestHideSelf() {
        resetToIdleState()
        service.requestHideSelf(InputMethodManager.HIDE_NOT_ALWAYS)
    }

    @SuppressLint("SimpleDateFormat")
    fun onStartInputView(editorInfo: EditorInfo) {
        resetToIdleState()
        currentInputEditorInfo = editorInfo
        mInputModeSwitcher.requestInputWithSkb(editorInfo)
        KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
        if(AppPrefs.getInstance().clipboard.clipboardSuggestion.getValue()){
            val lastClipboardTime = LauncherModel.instance.mLastClipboardTime
            val lastClipboardContent = LauncherModel.instance.mLastClipboardContent
            if(lastClipboardContent.isNotBlank()) {
                val clipboardItemTimeout = AppPrefs.getInstance().clipboard.clipboardItemTimeout.getValue()
                if (System.currentTimeMillis() - lastClipboardTime <= clipboardItemTimeout * 1000) {
                    showSymbols(arrayOf(lastClipboardContent))
                }
                LauncherModel.instance.mLastClipboardTime = 0L
                LauncherModel.instance.mLastClipboardContent = ""
            }

        }
    }

    /**
     * 模拟按键点击
     */
    fun sendKeyEvent(keyCode: Int) {
        if(keyCode != KeyEvent.KEYCODE_ENTER) {
            service.sendDownUpKeyEvents(keyCode)
        } else {
            val inputConnection = service.getCurrentInputConnection()
            currentInputEditorInfo?.run {
                if (inputType and InputType.TYPE_MASK_CLASS == InputType.TYPE_NULL || imeOptions.hasFlag(EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
                    service.sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)
                    return
                }
                if (!actionLabel.isNullOrEmpty() && actionId != EditorInfo.IME_ACTION_UNSPECIFIED) {
                    inputConnection.performEditorAction(actionId)
                    return
                }
                when (val action = imeOptions and EditorInfo.IME_MASK_ACTION) {
                    EditorInfo.IME_ACTION_UNSPECIFIED, EditorInfo.IME_ACTION_NONE -> service.sendDownUpKeyEvents(keyCode)
                    else -> inputConnection.performEditorAction(action)
                }
            }
        }
    }

    /**
     * 向输入框提交预选词
     */
    private fun setComposingText(text: CharSequence) {
        val inputConnection = service.getCurrentInputConnection()
        inputConnection?.setComposingText(text, 1)
    }

    /**
     * 发送字符串给编辑框
     */
    private fun commitText(resultText: String) {
        val inputConnection = service.getCurrentInputConnection()
        inputConnection.commitText(StringUtils.converted2FlowerTypeface(resultText), 1)
    }

    /**
     * 发送候选词字符串给编辑框
     */
    private fun commitDecInfoText(resultText: String?) {
        if(resultText == null) return
        val inputConnection = service.getCurrentInputConnection()
        inputConnection.commitText(StringUtils.converted2FlowerTypeface(resultText), 1)
        if (mInputModeSwitcher.isEnglish && mDecInfo.isFinish && AppPrefs.getInstance().input.abcSpaceAuto.getValue()) {
            inputConnection.commitText(" ", 1)
        }
    }

    private fun sendKeyChar(char: Char) {
        service.sendKeyChar(char)
    }

    private fun initNavbarBackground(service: ImeService) {
        service.window.window!!.also {
            WindowCompat.setDecorFitsSystemWindows(it, false)
            it.navigationBarColor = Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                it.isNavigationBarContrastEnforced = false
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
            EnvironmentSingleton.instance.systemNavbarWindowsBottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            mLlKeyboardBottomHolder.minimumHeight = if(EnvironmentSingleton.instance.isLandscape || prefs.keyboardModeFloat.getValue())  0 else EnvironmentSingleton.instance.systemNavbarWindowsBottom
            insets
        }
    }
}