package com.yuyan.imemodule.view.keyboard

import android.annotation.SuppressLint
import android.content.Context
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
import com.yuyan.imemodule.R
import com.yuyan.imemodule.callback.CandidateViewListener
import com.yuyan.imemodule.callback.IResponseKeyEvent
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.data.theme.ThemeManager.prefs
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.manager.SymbolsManager
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import com.yuyan.imemodule.prefs.behavior.KeyboardOneHandedMod
import com.yuyan.imemodule.service.DecodingInfo
import com.yuyan.imemodule.service.ImeService
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.KeyboardLoaderUtil
import com.yuyan.imemodule.utils.LogUtil
import com.yuyan.imemodule.utils.StringUtils
import com.yuyan.imemodule.view.CandidatesBar
import com.yuyan.imemodule.view.ComposingView
import com.yuyan.imemodule.view.keyboard.container.CandidatesContainer
import com.yuyan.imemodule.view.keyboard.container.InputViewParent
import com.yuyan.imemodule.view.keyboard.container.SettingsContainer
import com.yuyan.imemodule.view.keyboard.container.SymbolContainer
import com.yuyan.imemodule.view.keyboard.container.T9TextContainer
import com.yuyan.imemodule.view.popup.PopupComponent.Companion.get
import com.yuyan.inputmethod.core.CandidateListItem

@SuppressLint("ViewConstructor") // 禁用构造方法警告，不创建含AttributeSet的构造方法，为了实现代码混淆效果

class InputView(context: Context, service: ImeService) : RelativeLayout(context),
    IResponseKeyEvent {
    private var service: ImeService
    val mInputModeSwitcher = InputModeSwitcherManager()
    // 词库解码操作对象
    val mDecInfo = DecodingInfo()
    private var isSkipEngineMode = false //选择候选词栏时，为true则不进行引擎操作。当为切板模式或常用符号模式时为true。
    // 当前的输入法状态
    private var mImeState = ImeState.STATE_IDLE
    private var mChoiceNotifier = ChoiceNotifier()
    private var mComposingView: ComposingView? = null // 组成字符串的View，用于显示输入的拼音。
    private var mSkbCandidatesBarView: CandidatesBar? = null //候选词栏根View
    private var mIbOneHand: ImageButton? = null
    private var mIbOneHandNone: ImageButton? = null
    var mSkbRoot: LinearLayout? = null
    private var mHoderLayoutLeft: LinearLayout? = null
    private var mHoderLayoutRight: LinearLayout? = null
    private var mHoderLayout: LinearLayout? = null

    init {
        this.service = service
        initView(context)
    }

    fun initView(context: Context?) {
        LogUtil.d(TAG, "initView")
        if (mSkbRoot == null) {
            mSkbRoot = LayoutInflater.from(context).inflate(R.layout.sdk_skb_container, this, false) as LinearLayout
            mComposingView = mSkbRoot?.findViewById(R.id.cmv_container)
            mSkbCandidatesBarView = mSkbRoot?.findViewById(R.id.candidates_bar)
            mHoderLayoutLeft = mSkbRoot?.findViewById(R.id.ll_skb_holder_layout_left)
            mHoderLayoutRight = mSkbRoot?.findViewById(R.id.ll_skb_holder_layout_right)
            val mIvcSkbContainer:InputViewParent? = mSkbRoot?.findViewById(R.id.skb_input_keyboard_view)
            KeyboardManager.instance.setData(mIvcSkbContainer, this)
            var layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            addView(mSkbRoot, layoutParams)
            val popupComponent = get()
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            val viewParent = popupComponent.root.parent
            if (viewParent != null) {
                (viewParent as ViewGroup).removeView(popupComponent.root)
            }
            addView(popupComponent.root, layoutParams)
        }
        mSkbCandidatesBarView?.initialize(mChoiceNotifier, mDecInfo)
        val oneHandedMod = prefs.oneHandedMod.getValue()
        mHoderLayout?.visibility = GONE
        mHoderLayout = when(oneHandedMod){
            KeyboardOneHandedMod.LEFT -> mHoderLayoutLeft
            KeyboardOneHandedMod.RIGHT ->  mHoderLayoutRight
            else -> null
        }
        if (oneHandedMod != KeyboardOneHandedMod.None) {
            mHoderLayout?.visibility = VISIBLE
            mIbOneHandNone = mHoderLayout?.findViewById(R.id.ib_holder_one_hand_none)
            mIbOneHandNone?.setOnClickListener { view: View -> onClick(view) }
            mIbOneHand = mHoderLayout?.findViewById(R.id.ib_holder_one_hand_left)
            mIbOneHand?.setOnClickListener { view: View -> onClick(view) }
            val layoutParamsHoder = mHoderLayout?.layoutParams
            val margin = EnvironmentSingleton.instance.heightForCandidates + EnvironmentSingleton.instance.heightForComposingView
            layoutParamsHoder?.width = EnvironmentSingleton.instance.holderWidth
            layoutParamsHoder?.height = EnvironmentSingleton.instance.skbHeight + margin
        }
        if(EnvironmentSingleton.instance.isLandscape){
            val layoutParams = mSkbRoot?.layoutParams as LayoutParams
            layoutParams.addRule(ALIGN_PARENT_BOTTOM, 0)
            layoutParams.addRule(CENTER_VERTICAL, TRUE)
            layoutParams.addRule(ALIGN_PARENT_RIGHT, TRUE)
        } else {
            val layoutParams = mSkbRoot?.layoutParams as LayoutParams
            layoutParams.addRule(ALIGN_PARENT_BOTTOM, TRUE)
            layoutParams.addRule(CENTER_VERTICAL, 0)
            layoutParams.addRule(ALIGN_PARENT_RIGHT, TRUE)
        }
        updateTheme()
    }

    // 刷新主题
    fun updateTheme() {
        setBackgroundResource(android.R.color.transparent)
        val isKeyBorder = prefs.keyBorder.getValue()
        mSkbRoot?.background = activeTheme.backgroundDrawable(isKeyBorder)
        mComposingView?.updateTheme(activeTheme.keyTextColor)
        mSkbCandidatesBarView?.updateTheme(activeTheme.keyTextColor)
        mIbOneHandNone?.getDrawable()?.setTint(activeTheme.keyTextColor)
        mIbOneHand?.getDrawable()?.setTint(activeTheme.keyTextColor)
    }

    private fun onClick(view: View) {
        if (view.id == R.id.ib_holder_one_hand_none) {
            prefs.oneHandedMod.setValue(KeyboardOneHandedMod.None)
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
        LogUtil.d(TAG, "responseKeyEvent sKey：" + sKey.keyCode)
        isSkipEngineMode = false
        val keyCode = sKey.keyCode
        if (sKey.isKeyCodeKey) {  // 系统的keycode,单独处理
            val keyEvent = KeyEvent(0, 0, KeyEvent.ACTION_UP, keyCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD)
            processKey(keyEvent)
        } else if (sKey.isUserDefKey) { // 是用户定义的keycode
            if (InputModeSwitcherManager.USERDEF_KEYCODE_SYMBOL_ZH_3 == keyCode || InputModeSwitcherManager.USERDEF_KEYCODE_EMOJI_6 == keyCode) {  // 点击标点、表情按钮
                if (!mDecInfo.isAssociate && !mDecInfo.isFinish) {
                    chooseAndUpdate(0)
                }
                val symbolType = if (keyCode == InputModeSwitcherManager.USERDEF_KEYCODE_EMOJI_6) { 4 } else if(mInputModeSwitcher.isEnglish) { 1 } else if(mInputModeSwitcher.isNumberSkb) { 2 } else { 0 }
                val symbols = SymbolsManager.instance!!.getmSymbols(symbolType)
                showSymbols(symbols)
                KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.SYMBOL)
                (KeyboardManager.instance.currentContainer as SymbolContainer?)!!.setSymbolsView(
                    symbolType
                )
            } else {
                if (!mDecInfo.isCandidatesListEmpty && !mDecInfo.isAssociate) {
                    if (keyCode == InputModeSwitcherManager.USERDEF_KEYCODE_NUMBER_7 || keyCode == InputModeSwitcherManager.USERDEF_KEYCODE_LANG_2) {  //数字键盘、中英切换，有候选词的情况 上屏候选词
                        chooseAndUpdate(0)
                    }
                }
                mInputModeSwitcher.switchModeForUserKey(keyCode)
                resetToIdleState()
            }
        } else if (sKey.isUniStrKey) {  // 字符按键
            if (!mDecInfo.isCandidatesListEmpty && !mDecInfo.isAssociate) {
                chooseAndUpdate(0)
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
        if (!mDecInfo.isCandidatesListEmpty) { // 上屏候选词后上屏符号
            if (!mDecInfo.isAssociate) {
                chooseAndUpdate(0)
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
        if (mInputModeSwitcher.isEnglish || mInputModeSwitcher.isChinese) { // 中文、英语输入模式
            result = when (mImeState) {
                ImeState.STATE_IDLE -> processStateIdle(event)
                ImeState.STATE_INPUT -> processStateInput(event)
                ImeState.STATE_PREDICT -> processStatePredict(event)
                ImeState.STATE_COMPOSING -> processStateEditComposing(event)
            }
            // 临时代码:判断英语、未锁定大写、大写键盘时，键盘切换为小写
            if (mInputModeSwitcher.isEnglish && mInputModeSwitcher.isEnglishUpperCase) { // 英语、未开启智能英文
                val keyCode = event.keyCode
                if (keyCode in KeyEvent.KEYCODE_A .. KeyEvent.KEYCODE_Z) {
                    //切换为英文小写键盘
                    mInputModeSwitcher.saveInputMode(InputModeSwitcherManager.MODE_SKB_ENGLISH_LOWER)
                    KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
                }
            }
        } else { // 数字、符号处理 && 英语、未开启智能英文
            val keyChar = event.unicodeChar
            if (0 != keyChar) {
                sendKeyChar(keyChar.toChar()) // 发送文本给EditText
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
        if (mInputModeSwitcher.isChinese || mInputModeSwitcher.isEnglish) {
            return false
        }
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            sendKeyEvent(keyCode)
            return true
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_SPACE) {
            val keyChar = event.unicodeChar
            sendKeyChar(keyChar.toChar())
            return true
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
            val spaceSelectAssociation = getInstance().input.spaceSelectAssociation.getValue()
            if (!mDecInfo.isCandidatesListEmpty && (!mDecInfo.isAssociate || spaceSelectAssociation)) {
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
            val displayStr = mDecInfo.composingStrForDisplay.replace("'".toRegex(), "")
            // 把输入的拼音字符串发送给EditText
            commitDecInfoText(displayStr)
            resetToIdleState()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_SPACE) {
            // 选择高亮的候选词
            val spaceSelectAssociation = getInstance().input.spaceSelectAssociation.getValue()
            if (!mDecInfo.isCandidatesListEmpty && (!mDecInfo.isAssociate || spaceSelectAssociation)) {
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
            val retStr = mDecInfo.composingStrForDisplay.replace("'".toRegex(), "")
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
            val spaceSelectAssociation = getInstance().input.spaceSelectAssociation.getValue()
            if (!mDecInfo.isCandidatesListEmpty && (!mDecInfo.isAssociate || spaceSelectAssociation)) {
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
            val spaceSelectAssociation = getInstance().input.spaceSelectAssociation.getValue()
            if (!mDecInfo.isCandidatesListEmpty && (!mDecInfo.isAssociate || spaceSelectAssociation)) {
                chooseAndUpdate(0)
            }
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // 获取原始的输入拼音的字符
            val composingStr = mDecInfo.composingStrForDisplay
            if (composingStr.isEmpty()) { // 发送 ENTER 键给 EditText
                sendKeyEvent(KeyEvent.KEYCODE_ENTER)
            } else { // 发送文本给EditText
                commitDecInfoText(composingStr.replace("'".toRegex(), ""))
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
        LogUtil.d(TAG, "resetToIdleState")
        if (mInputModeSwitcher.isEnglish) setComposingText("") // 清除预选词
        resetCandidateWindow()
        // 从候选词、符号界面切换到输入键盘
        KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
        val container = KeyboardManager.instance.currentContainer
        (container as? T9TextContainer)?.updateSymbolListView()
        mComposingView?.setDecodingInfo(mDecInfo)
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
    private fun updateCandidateBar() {
        mSkbCandidatesBarView?.showCandidates()
        mComposingView?.setDecodingInfo(mDecInfo)
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
            }
        }

        override fun onClickCloseKeyboard() {
            requestHideSelf()
        }

        override fun onClickClearCandidate() {
            resetToIdleState()
            KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
        }
    }

    /**
     * 输入法状态
     * 空闲，输入，编辑，联想
     */
    enum class ImeState {
        STATE_IDLE,
        STATE_INPUT,
        STATE_COMPOSING,
        STATE_PREDICT
    }

    /**
     * 点击花漾字菜单
     */
    fun showFlowerTypeface() {
        mSkbCandidatesBarView?.showFlowerTypeface()
    }

    /**
     * 选择拼音
     */
    fun selectPrefix(position: Int) {
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
        mSkbCandidatesBarView?.showCandidates()
        mImeState = ImeState.STATE_PREDICT
    }

    fun requestHideSelf() {
        LogUtil.d(TAG, "requestHideSelf")
        resetToIdleState()
        service.requestHideSelf(InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun onStartInputView(editorInfo: EditorInfo) {
        LogUtil.d(TAG, "onStartInputView")
        mInputModeSwitcher.requestInputWithSkb(editorInfo)
        KeyboardManager.instance.switchKeyboard(mInputModeSwitcher.skbLayout)
    }

    /**
     * 模拟按键点击
     */
    fun sendKeyEvent(keyCode: Int) {
        val inputConnection = service.getCurrentInputConnection()
        if (null != inputConnection) {
            inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
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
        if (mInputModeSwitcher.isEnglish && mDecInfo.isFinish) {
            inputConnection.commitText(" ", 1)
        }
    }

    private fun sendKeyChar(char: Char) {
        service.sendKeyChar(char)
    }

    companion object {
        private val TAG = InputView::class.java.getSimpleName()
    }
}