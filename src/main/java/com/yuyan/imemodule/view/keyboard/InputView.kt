package com.yuyan.imemodule.view.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.callback.CandidateViewListener
import com.yuyan.imemodule.callback.IResponseKeyEvent
import com.yuyan.imemodule.data.emojicon.EmojiconData.SymbolPreset
import com.yuyan.imemodule.data.flower.FlowerTypefaceMode
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.database.entry.Phrase
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import com.yuyan.imemodule.prefs.behavior.KeyboardOneHandedMod
import com.yuyan.imemodule.prefs.behavior.PopupMenuMode
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode
import com.yuyan.imemodule.service.DecodingInfo
import com.yuyan.imemodule.service.ImeService
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.ui.utils.AppUtil
import com.yuyan.imemodule.ui.utils.InputMethodUtil
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.utils.KeyboardLoaderUtil
import com.yuyan.imemodule.utils.StringUtils
import com.yuyan.imemodule.utils.exp4j.ExpressionBuilder
import com.yuyan.imemodule.utils.pinyin4j.PinyinHelper
import com.yuyan.imemodule.view.CandidatesBar
import com.yuyan.imemodule.view.ComposingView
import com.yuyan.imemodule.view.FullDisplayKeyboardBar
import com.yuyan.imemodule.view.keyboard.container.CandidatesContainer
import com.yuyan.imemodule.view.keyboard.container.ClipBoardContainer
import com.yuyan.imemodule.view.keyboard.container.InputBaseContainer
import com.yuyan.imemodule.view.keyboard.container.InputViewParent
import com.yuyan.imemodule.view.keyboard.container.SettingsContainer
import com.yuyan.imemodule.view.keyboard.container.SymbolContainer
import com.yuyan.imemodule.view.keyboard.container.T9TextContainer
import com.yuyan.imemodule.view.popup.PopupComponent
import com.yuyan.imemodule.view.preference.ManagedPreference
import com.yuyan.imemodule.view.widget.ImeEditText
import com.yuyan.imemodule.view.widget.LifecycleRelativeLayout
import com.yuyan.inputmethod.core.CandidateListItem
import com.yuyan.inputmethod.core.Kernel
import com.yuyan.inputmethod.util.T9PinYinUtils
import splitties.bitflags.hasFlag
import splitties.views.bottomPadding
import splitties.views.rightPadding
import kotlin.math.absoluteValue

/**
 * 输入法主界面。
 * 包含拼音显示、候选词栏、键盘界面等。
 * 在 该类中处理界面绘制、、输入逻辑等为输入法核心处理类。
 * 注: 所有键盘自定义 View禁用构造方法警告，且不创建含AttributeSet的构造方法。为了实现代码混淆效果。
 */

@SuppressLint("ViewConstructor")
class InputView(context: Context, service: ImeService) : LifecycleRelativeLayout(context), IResponseKeyEvent {
    var isAddPhrases = false
    private var chinesePrediction = true
    private var oldAddPhrases = ""
    private var mEtAddPhrasesContent: ImeEditText? = null
    private var tvAddPhrasesTips:TextView? = null
    private var service: ImeService
    private var currentInputEditorInfo:EditorInfo? = null
    private var mImeState = ImeState.STATE_IDLE // 当前的输入法状态
    private var mChoiceNotifier = ChoiceNotifier()
    private lateinit var mComposingView: ComposingView // 组成字符串的View，用于显示输入的拼音。
    lateinit var mSkbRoot: RelativeLayout
    private lateinit var mSkbCandidatesBarView: CandidatesBar //候选词栏根View
    private lateinit var mHoderLayoutLeft: LinearLayout
    private lateinit var mHoderLayoutRight: LinearLayout
    private lateinit var mOnehandHoderLayout: LinearLayout
    lateinit var mAddPhrasesLayout: RelativeLayout
    private lateinit var mLlKeyboardBottomHolder: LinearLayout
    private lateinit var mRightPaddingKey: ManagedPreference.PInt
    private lateinit var mBottomPaddingKey: ManagedPreference.PInt
    private var mFullDisplayKeyboardBar:FullDisplayKeyboardBar? = null

    init {
        this.service = service
        initNavbarBackground(service)
        initView(context)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun initView(context: Context) {
        if (!::mSkbRoot.isInitialized) {
            mSkbRoot = LayoutInflater.from(context).inflate(R.layout.sdk_skb_container, this, false) as RelativeLayout
            addView(mSkbRoot)
            mSkbCandidatesBarView = mSkbRoot.findViewById(R.id.candidates_bar)
            mHoderLayoutLeft = mSkbRoot.findViewById(R.id.ll_skb_holder_layout_left)
            mHoderLayoutRight = mSkbRoot.findViewById(R.id.ll_skb_holder_layout_right)
            mAddPhrasesLayout = LayoutInflater.from(context).inflate(R.layout.skb_add_phrases_container, mSkbRoot, false) as RelativeLayout
            addView(mAddPhrasesLayout,  LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                addRule(ABOVE, mSkbRoot.id)
                addRule(ALIGN_LEFT, mSkbRoot.id)
            })
            val mIvcSkbContainer:InputViewParent = mSkbRoot.findViewById(R.id.skb_input_keyboard_view)
            KeyboardManager.instance.setData(mIvcSkbContainer, this)
            mLlKeyboardBottomHolder =  mSkbRoot.findViewById(R.id.iv_keyboard_holder)
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
        if(isAddPhrases){
            mAddPhrasesLayout.visibility = View.VISIBLE
            handleAddPhrasesView()
        } else {
            mAddPhrasesLayout.visibility = View.GONE
        }
        mSkbCandidatesBarView.initialize(mChoiceNotifier)
        val oneHandedModSwitch = getInstance().keyboardSetting.oneHandedModSwitch.getValue()
        val oneHandedMod = getInstance().keyboardSetting.oneHandedMod.getValue()
        if(::mOnehandHoderLayout.isInitialized)mOnehandHoderLayout.visibility = GONE
        if (oneHandedModSwitch) {
            mOnehandHoderLayout = when(oneHandedMod){
                KeyboardOneHandedMod.LEFT ->  mHoderLayoutRight
                else -> mHoderLayoutLeft
            }
            mOnehandHoderLayout.visibility = VISIBLE
            mOnehandHoderLayout[0].setOnClickListener { view: View -> onClick(view) }
            mOnehandHoderLayout[1].setOnClickListener { view: View -> onClick(view) }
            (mOnehandHoderLayout[1] as ImageButton).setImageResource(if (oneHandedMod == KeyboardOneHandedMod.LEFT) R.drawable.ic_menu_one_hand_right else R.drawable.ic_menu_one_hand)
            val layoutParamsHoder = mOnehandHoderLayout.layoutParams
            layoutParamsHoder.width = EnvironmentSingleton.instance.holderWidth
            layoutParamsHoder.height = EnvironmentSingleton.instance.skbHeight
        }
        mLlKeyboardBottomHolder.removeAllViews()
        mLlKeyboardBottomHolder.layoutParams.width = EnvironmentSingleton.instance.skbWidth
        if(EnvironmentSingleton.instance.keyboardModeFloat){
            mBottomPaddingKey = (if(EnvironmentSingleton.instance.isLandscape) getInstance().internal.keyboardBottomPaddingLandscapeFloat
                else getInstance().internal.keyboardBottomPaddingFloat)
            mRightPaddingKey = (if(EnvironmentSingleton.instance.isLandscape) getInstance().internal.keyboardRightPaddingLandscapeFloat
            else getInstance().internal.keyboardRightPaddingFloat)
            bottomPadding = mBottomPaddingKey.getValue()
            rightPadding = mRightPaddingKey.getValue()
            mSkbRoot.bottomPadding = 0
            mSkbRoot.rightPadding = 0
            mLlKeyboardBottomHolder.minimumHeight = EnvironmentSingleton.instance.heightForKeyboardMove
            val mIvKeyboardMove = ImageView(context).apply {
                setImageResource(R.drawable.ic_horizontal_line)
                isClickable = true
                isEnabled = true
            }
            mLlKeyboardBottomHolder.addView(mIvKeyboardMove)
            mIvKeyboardMove.setOnTouchListener { _, event -> onMoveKeyboardEvent(event) }
        } else {
            val fullDisplayKeyboardEnable = getInstance().internal.fullDisplayKeyboardEnable.getValue()
            if(fullDisplayKeyboardEnable){
                mFullDisplayKeyboardBar = FullDisplayKeyboardBar(context, this)
                mLlKeyboardBottomHolder.addView(mFullDisplayKeyboardBar)
                mLlKeyboardBottomHolder.minimumHeight = EnvironmentSingleton.instance.heightForFullDisplayBar + EnvironmentSingleton.instance.systemNavbarWindowsBottom
            } else {
                mLlKeyboardBottomHolder.minimumHeight = EnvironmentSingleton.instance.systemNavbarWindowsBottom
            }
            bottomPadding = 0
            rightPadding = 0
            mBottomPaddingKey =  getInstance().internal.keyboardBottomPadding
            mRightPaddingKey =  getInstance().internal.keyboardRightPadding
            mSkbRoot.bottomPadding = mBottomPaddingKey.getValue()
            mSkbRoot.rightPadding = mRightPaddingKey.getValue()
        }
        updateTheme()
        DecodingInfo.candidatesLiveData.observe( this){ _ ->
            updateCandidateBar()
            (KeyboardManager.instance.currentContainer as? CandidatesContainer)?.showCandidatesView()
        }
    }

    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var rightPaddingValue = 0  // 右侧边距
    private var bottomPaddingValue = 0  // 底部边距
    private fun onMoveKeyboardEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                bottomPaddingValue = mBottomPaddingKey.getValue()
                rightPaddingValue = mRightPaddingKey.getValue()
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx: Float = event.rawX - initialTouchX
                val dy: Float = event.rawY - initialTouchY
                if(dx.absoluteValue > 10) {
                    rightPaddingValue -= dx.toInt()
                    rightPaddingValue = if(rightPaddingValue < 0) 0
                    else if(rightPaddingValue > EnvironmentSingleton.instance.mScreenWidth - mSkbRoot.width) {
                        EnvironmentSingleton.instance.mScreenWidth - mSkbRoot.width
                    } else rightPaddingValue
                    initialTouchX = event.rawX
                    if(EnvironmentSingleton.instance.keyboardModeFloat) {
                        rightPadding = rightPaddingValue
                    } else {
                        mSkbRoot.rightPadding = rightPaddingValue
                    }
                }
                if(dy.absoluteValue > 10 ) {
                    bottomPaddingValue -= dy.toInt()
                    bottomPaddingValue = if(bottomPaddingValue < 0) 0
                    else if(bottomPaddingValue > EnvironmentSingleton.instance.mScreenHeight - mSkbRoot.height) {
                        EnvironmentSingleton.instance.mScreenHeight - mSkbRoot.height
                    } else bottomPaddingValue
                    initialTouchY = event.rawY
                    if(EnvironmentSingleton.instance.keyboardModeFloat) {
                        bottomPadding = bottomPaddingValue
                    } else {
                        mSkbRoot.bottomPadding = bottomPaddingValue
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mRightPaddingKey.setValue(rightPaddingValue)
                mBottomPaddingKey.setValue(bottomPaddingValue)
            }
        }
        return false
    }

    // 刷新主题
    fun updateTheme() {
        setBackgroundResource(android.R.color.transparent)
        mSkbRoot.background = ThemeManager.activeTheme.backgroundDrawable(ThemeManager.prefs.keyBorder.getValue())
        mComposingView.updateTheme(ThemeManager.activeTheme)
        mSkbCandidatesBarView.updateTheme(ThemeManager.activeTheme.keyTextColor)
        if(::mOnehandHoderLayout.isInitialized) {
            (mOnehandHoderLayout[0] as ImageButton).drawable?.setTint(ThemeManager.activeTheme.keyTextColor)
            (mOnehandHoderLayout[1] as ImageButton).drawable?.setTint(ThemeManager.activeTheme.keyTextColor)
        }
        mFullDisplayKeyboardBar?.updateTheme(ThemeManager.activeTheme.keyTextColor)
        mAddPhrasesLayout.setBackgroundColor(ThemeManager.activeTheme.barColor)
        mEtAddPhrasesContent?.background = GradientDrawable().apply {
            setColor(ThemeManager.activeTheme.keyBackgroundColor)
            shape = GradientDrawable.RECTANGLE
            cornerRadius = ThemeManager.prefs.keyRadius.getValue().toFloat()
        }
        mEtAddPhrasesContent?.setTextColor(ThemeManager.activeTheme.keyTextColor)
        mEtAddPhrasesContent?.setHintTextColor(ThemeManager.activeTheme.keyTextColor)
        tvAddPhrasesTips?.setTextColor(ThemeManager.activeTheme.keyTextColor)
    }

    private fun onClick(view: View) {
        if (view.id == R.id.ib_holder_one_hand_none) {
            getInstance().keyboardSetting.oneHandedModSwitch.setValue(!getInstance().keyboardSetting.oneHandedModSwitch.getValue())
        } else {
            val oneHandedMod = getInstance().keyboardSetting.oneHandedMod.getValue()
            getInstance().keyboardSetting.oneHandedMod.setValue(if (oneHandedMod == KeyboardOneHandedMod.LEFT) KeyboardOneHandedMod.RIGHT else KeyboardOneHandedMod.LEFT)
        }
        EnvironmentSingleton.instance.initData()
        KeyboardLoaderUtil.instance.clearKeyboardMap()
        KeyboardManager.instance.clearKeyboard()
        KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
    }

    /**
     * 响应软键盘按键的处理函数。在软键盘集装箱SkbContainer中responseKeyEvent（）的调用。
     * 软键盘集装箱SkbContainer的responseKeyEvent（）在自身类中调用。
     */
    override fun responseKeyEvent(sKey: SoftKey, isFeedback:Boolean) {
        val keyCode = sKey.keyCode
        if (sKey.isKeyCodeKey) {  // 系统的keycode,单独处理
            val keyEvent = KeyEvent(0, 0, KeyEvent.ACTION_UP, keyCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD)
            processKey(keyEvent)
        } else if (sKey.isUserDefKey || sKey.isUniStrKey) { // 是用户定义的keycode
            if (!DecodingInfo.isAssociate && !DecodingInfo.isCandidatesListEmpty) {
                if(InputModeSwitcherManager.isChinese) {
                    chooseAndUpdate()
                } else if(InputModeSwitcherManager.isEnglish){
                    commitDecInfoText(DecodingInfo.composingStrForCommit)  // 把输入的拼音字符串发送给EditText
                }
            }
            if (InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 == keyCode) {  // 点击标点按钮
                KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.SYMBOL)
                (KeyboardManager.instance.currentContainer as SymbolContainer?)!!.setSymbolsView(0)
            } else  if (InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4 == keyCode) {  // 点击表情按钮
                KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.SYMBOL)
                mSkbCandidatesBarView.showCandidates(CustomConstant.EMOJI_TYPR_FACE_DATA)
                (KeyboardManager.instance.currentContainer as SymbolContainer?)!!.setSymbolsView(CustomConstant.EMOJI_TYPR_FACE_DATA)
            } else if ( keyCode in InputModeSwitcherManager.USER_DEF_KEYCODE_RETURN_6 .. InputModeSwitcherManager.USER_DEF_KEYCODE_SHIFT_1) {
                InputModeSwitcherManager.switchModeForUserKey(keyCode)
            }else if(sKey.keyLabel.isNotBlank()){
                if(SymbolPreset.containsKey(sKey.keyLabel))commitPairSymbol(sKey.keyLabel)
                else commitText(sKey.keyLabel)
            }
            mImeState = ImeState.STATE_IDLE
            resetCandidateWindow()
        }
    }


    private var textBeforeCursor:String = ""

    /**
     * 响应软键盘长按键的处理函数。在软键盘集装箱SkbContainer中responseKeyEvent（）的调用。
     * 软键盘集装箱SkbContainer的responseKeyEvent（）在自身类中调用。
     */
    override fun responseLongKeyEvent(result:Pair<PopupMenuMode, String>) {
        if (!DecodingInfo.isAssociate && !DecodingInfo.isCandidatesListEmpty) {
            if(InputModeSwitcherManager.isChinese) {
                chooseAndUpdate()
            } else if(InputModeSwitcherManager.isEnglish){
                val displayStr = DecodingInfo.composingStrForCommit // 把输入的拼音字符串发送给EditText
                commitDecInfoText(displayStr)
            }
        }
        when(result.first){
            PopupMenuMode.Text -> {
                if(SymbolPreset.containsKey(result.second))commitPairSymbol(result.second)
                else commitText(result.second)
            }
            PopupMenuMode.SwitchIME -> {
                InputMethodUtil.showPicker()
            }
            PopupMenuMode.EnglishCell -> {
                val abcSearchEnglishCell = getInstance().input.abcSearchEnglishCell.getValue()
                getInstance().input.abcSearchEnglishCell.setValue(!abcSearchEnglishCell)
            }
            PopupMenuMode.Clear -> {
                if(isAddPhrases) mEtAddPhrasesContent?.setText("")
                else {
                    val inputConnection = service.getCurrentInputConnection()
                    val clearText = inputConnection.getTextBeforeCursor(1000, InputConnection.GET_TEXT_WITH_STYLES).toString()
                    if(clearText.isNotEmpty()){
                        textBeforeCursor = clearText
                        inputConnection.deleteSurroundingText(1000, 0)
                    }
                }
            }
            PopupMenuMode.Revertl -> {
                commitText(textBeforeCursor)
                textBeforeCursor = ""
            }
            PopupMenuMode.Enter -> {  // 长按回车键
                commitText("\n")
            }
            else -> {}
        }
        resetToIdleState()
    }

    override fun responseHandwritingResultEvent(words: Array<CandidateListItem>) {
        DecodingInfo.isAssociate = false
        DecodingInfo.cacheCandidates(words)
        mImeState = ImeState.STATE_INPUT
        updateCandidateBar()
    }

    /**
     * 按键处理函数
     */
    fun processKey(event: KeyEvent): Boolean {
        // 功能键处理
        if (processFunctionKeys(event)) return true
        val englishCellDisable = InputModeSwitcherManager.isEnglish && !getInstance().input.abcSearchEnglishCell.getValue()
        val result = if(englishCellDisable){
            processEnglishKey(event)
        } else if (!InputModeSwitcherManager.mInputTypePassword &&(InputModeSwitcherManager.isEnglish || InputModeSwitcherManager.isChinese)) { // 中文、英语输入模式
            processInput(event)
        } else { // 数字、符号处理
            processEnglishKey(event)
        }
        return result
    }

    /**
     * 英文非智能输入处理函数
     */
    private fun processEnglishKey(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        var keyChar = event.unicodeChar
        val lable = keyChar.toChar().toString()
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            sendKeyEvent(keyCode)
            return true
        } else if(keyCode in (KeyEvent.KEYCODE_A .. KeyEvent.KEYCODE_Z) ){
            if (!InputModeSwitcherManager.isEnglishLower) keyChar = keyChar - 'a'.code + 'A'.code
            commitText(keyChar.toChar().toString())
            return true
        } else if (keyCode != 0) {
            sendKeyEvent(keyCode)
            return true
        } else if (lable.isNotEmpty()) {
            if(SymbolPreset.containsKey(lable))commitPairSymbol(lable)
            else commitText(lable)
            return true
        }
        return false
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
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_SPACE) {
            if (DecodingInfo.isFinish || DecodingInfo.isAssociate) {
                sendKeyEvent(keyCode)
                resetToIdleState()
            } else {
                chooseAndUpdate()
            }
            return true
        } else if (keyCode == KeyEvent.KEYCODE_CLEAR) {
            resetToIdleState()
            return true
        }  else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (DecodingInfo.isFinish || DecodingInfo.isAssociate) {
                sendKeyEvent(keyCode)
            } else {
                commitDecInfoText(DecodingInfo.composingStrForCommit)
            }
            resetToIdleState()
            return true
        }else if (keyCode == KeyEvent.KEYCODE_DEL && (InputModeSwitcherManager.mInputTypePassword || InputModeSwitcherManager.isNumberSkb)) {
            sendKeyEvent(keyCode)
            resetToIdleState()
            return true
        }
        return false
    }

    /**
     * 按键处理函数
     */
    private fun processInput(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        val keyChar = event.unicodeChar
        val lable = keyChar.toChar().toString()
        if (keyChar in 'A'.code .. 'Z'.code || keyChar in 'a'.code .. 'z'.code || keyChar in  '0'.code .. '9'.code|| keyCode == KeyEvent.KEYCODE_APOSTROPHE || keyCode == KeyEvent.KEYCODE_SEMICOLON){
            mImeState = ImeState.STATE_INPUT
            DecodingInfo.inputAction(keyCode)
            updateCandidate()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (DecodingInfo.isFinish) {
                sendKeyEvent(keyCode)
                resetToIdleState()
            } else {
                DecodingInfo.deleteAction()
                updateCandidate()
            }
            return true
        } else if (keyCode != 0) {
            if (!DecodingInfo.isCandidatesListEmpty && !DecodingInfo.isAssociate) {
                chooseAndUpdate()
            }
            sendKeyEvent(keyCode)
            resetToIdleState()
            return true
        } else if(lable.isNotEmpty()) {
            if (!DecodingInfo.isCandidatesListEmpty && !DecodingInfo.isAssociate) {
                chooseAndUpdate()
            }
            if(SymbolPreset.containsKey(lable))commitPairSymbol(lable)
            else commitText(lable)
            resetToIdleState()
            return true
        }
        return false
    }

    /**
     * 重置到空闲状态
     */
    fun resetToIdleState() {
        resetCandidateWindow()
        // 从候选词、符号界面切换到输入键盘
        KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
        (KeyboardManager.instance.currentContainer as? T9TextContainer)?.updateSymbolListView()
        mComposingView.setDecodingInfo()
        mImeState = ImeState.STATE_IDLE
    }

    /**
     * 切换到联想状态
     */
    private fun resetToPredictState() {
        resetCandidateWindow()
        // 从候选词、符号界面切换到输入键盘
        KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
        (KeyboardManager.instance.currentContainer as? T9TextContainer)?.updateSymbolListView()
        mComposingView.setDecodingInfo()
        mImeState = ImeState.STATE_PREDICT
    }

    /**
     * 选择候选词，并根据条件是否进行下一步的预报。
     * @param candId 选择索引
     */
    private fun chooseAndUpdate(candId: Int = 0) {
        val candidate = DecodingInfo.getCandidate(candId)
        if(candidate?.comment == "📋"){  // 处理剪贴板或常用语
            commitDecInfoText(candidate.text)
            resetToPredictState()
        } else {
            val choice = DecodingInfo.chooseDecodingCandidate(candId)
            if (DecodingInfo.isEngineFinish || DecodingInfo.isAssociate) {  // 选择的候选词上屏
                commitDecInfoText(choice)
                resetToPredictState()
            } else {  // 不上屏，继续选择
                if (!DecodingInfo.isFinish) {
                    val composing = DecodingInfo.composingStrForDisplay
                    if (InputModeSwitcherManager.isEnglish) setComposingText(composing)
                    updateCandidateBar()
                    (KeyboardManager.instance.currentContainer as? T9TextContainer)?.updateSymbolListView()
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
        DecodingInfo.updateDecodingCandidate()
        if (!DecodingInfo.isFinish) {
            val composing = DecodingInfo.composingStrForDisplay
            if (InputModeSwitcherManager.isEnglish) {
                setComposingText(composing)
            }
            updateCandidateBar()
            (KeyboardManager.instance.currentContainer as? T9TextContainer)?.updateSymbolListView()
        } else {
            resetToIdleState()
        }
    }

    /**
     * 显示候选词视图
     */
    fun updateCandidateBar() {
        mSkbCandidatesBarView.showCandidates()
        mComposingView.setDecodingInfo()
    }

    /**
     * 重置候选词区域
     */
    private fun resetCandidateWindow() {
        DecodingInfo.reset()
        updateCandidateBar()
    }

    /**
     * 选择候选词后的处理函数。
     */
    fun onChoiceTouched(activeCandNo: Int) {
        // 播放按键声音和震动
        DevicesUtils.tryPlayKeyDown()
        DevicesUtils.tryVibrate(this)
        // 选择候选词
        chooseAndUpdate(activeCandNo)
    }

    /**
     * 当用户选择了候选词或者在候选词视图滑动了手势时的通知输入法。实现了候选词视图的监听器CandidateViewListener，
     * 有选择候选词的处理函数、隐藏键盘的事件
     */
    inner class ChoiceNotifier internal constructor() : CandidateViewListener {
        override fun onClickChoice(choiceId: Int) {
            onChoiceTouched(choiceId)
        }

        override fun onClickMore(level: Int) {
            if (level == 0) {
                KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.CANDIDATES)
                (KeyboardManager.instance.currentContainer as? CandidatesContainer)?.showCandidatesView()
            } else {
                KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
                (KeyboardManager.instance.currentContainer as? T9TextContainer)?.updateSymbolListView()
            }
        }

        override fun onClickSetting() {
            if (KeyboardManager.instance.isInputKeyboard) {
                KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.SETTINGS)
                (KeyboardManager.instance.currentContainer as SettingsContainer?)?.showSettingsView()
                updateCandidateBar()
            } else {
                KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
            }
        }

        override fun onClickMenu(skbMenuMode: SkbMenuMode) {
            onSettingsMenuClick(skbMenuMode)
        }

        override fun onClickClearCandidate() {
            resetToIdleState()
            KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
        }

        override fun onClickClearClipBoard() {
            DataBaseKT.instance.clipboardDao().deleteAll()
            (KeyboardManager.instance.currentContainer as ClipBoardContainer?)?.showClipBoardView(SkbMenuMode.ClipBoard)
        }
    }

    fun onSettingsMenuClick(skbMenuMode: SkbMenuMode, extra:String = "") {
        when (skbMenuMode) {
            SkbMenuMode.EmojiKeyboard -> {
                KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.SYMBOL)
                mSkbCandidatesBarView.showCandidates(CustomConstant.EMOJI_TYPR_FACE_DATA)
                (KeyboardManager.instance.currentContainer as SymbolContainer?)!!.setSymbolsView(CustomConstant.EMOJI_TYPR_FACE_DATA)
            }
            SkbMenuMode.Emoticons -> {
                KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.SYMBOL)
                mSkbCandidatesBarView.showCandidates(CustomConstant.EMOJI_TYPR_SMILE_TEXT)
                (KeyboardManager.instance.currentContainer as SymbolContainer?)!!.setSymbolsView(CustomConstant.EMOJI_TYPR_SMILE_TEXT)
            }
            SkbMenuMode.SwitchKeyboard -> {
                KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.SETTINGS)
                (KeyboardManager.instance.currentContainer as SettingsContainer?)?.showSkbSelelctModeView()
                updateCandidateBar()
            }
            SkbMenuMode.KeyboardHeight -> {
                KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
                KeyboardManager.instance.currentContainer!!.setKeyboardHeight()
            }
            SkbMenuMode.DarkTheme -> {
                val theme = (if (ThemeManager.activeTheme.isDark) ThemeManager.prefs.lightModeTheme else ThemeManager.prefs.darkModeTheme).getValue()
                ThemeManager.setNormalModeTheme(theme)
                KeyboardManager.instance.clearKeyboard()
                KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
            }
            SkbMenuMode.Feedback -> {
                AppUtil.launchSettingsToKeyboard(context)
            }
            SkbMenuMode.NumberRow -> {
                val abcNumberLine = getInstance().keyboardSetting.abcNumberLine.getValue()
                getInstance().keyboardSetting.abcNumberLine.setValue(!abcNumberLine)
                //更换键盘模式后 重亲加载键盘
                KeyboardLoaderUtil.instance.changeSKBNumberRow()
                KeyboardManager.instance.clearKeyboard()
                KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
            }
            SkbMenuMode.JianFan -> {
                val chineseFanTi = getInstance().input.chineseFanTi.getValue()
                getInstance().input.chineseFanTi.setValue(!chineseFanTi)
                Kernel.nativeUpdateImeOption()
                KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
            }
            SkbMenuMode.LockEnglish -> {
                val keyboardLockEnglish = getInstance().keyboardSetting.keyboardLockEnglish.getValue()
                getInstance().keyboardSetting.keyboardLockEnglish.setValue(!keyboardLockEnglish)
                KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
            }
            SkbMenuMode.SymbolShow -> {
                val keyboardSymbol = ThemeManager.prefs.keyboardSymbol.getValue()
                ThemeManager.prefs.keyboardSymbol.setValue(!keyboardSymbol)
                KeyboardManager.instance.clearKeyboard()
                KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
            }
            SkbMenuMode.Mnemonic -> {
                val keyboardMnemonic = ThemeManager.prefs.keyboardMnemonic.getValue()
                ThemeManager.prefs.keyboardMnemonic.setValue(!keyboardMnemonic)
                KeyboardLoaderUtil.instance.clearKeyboardMap()
                KeyboardManager.instance.clearKeyboard()
                KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
            }
            SkbMenuMode.EmojiInput -> {
                val emojiInput = getInstance().input.emojiInput.getValue()
                getInstance().input.emojiInput.setValue(!emojiInput)
                Kernel.nativeUpdateImeOption()
                KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
            }
            SkbMenuMode.Handwriting -> AppUtil.launchSettingsToHandwriting(context)
            SkbMenuMode.Settings -> AppUtil.launchSettings(context)
            SkbMenuMode.OneHanded -> {
                getInstance().keyboardSetting.oneHandedModSwitch.setValue(!getInstance().keyboardSetting.oneHandedModSwitch.getValue())
                EnvironmentSingleton.instance.initData()
                KeyboardLoaderUtil.instance.clearKeyboardMap()
                KeyboardManager.instance.clearKeyboard()
                KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
            }
            SkbMenuMode.FlowerTypeface -> {
                CustomConstant.flowerTypeface = if(CustomConstant.flowerTypeface == FlowerTypefaceMode.Disabled) FlowerTypefaceMode.Mars else FlowerTypefaceMode.Disabled
                showFlowerTypeface()
                KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
            }
            SkbMenuMode.FloatKeyboard -> {
                val keyboardModeFloat = EnvironmentSingleton.instance.keyboardModeFloat
                EnvironmentSingleton.instance.keyboardModeFloat = !keyboardModeFloat
                EnvironmentSingleton.instance.initData()
                KeyboardLoaderUtil.instance.clearKeyboardMap()
                KeyboardManager.instance.clearKeyboard()
                KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
            }
            SkbMenuMode.ClipBoard,SkbMenuMode.Phrases -> {
                KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.ClipBoard)
                (KeyboardManager.instance.currentContainer as ClipBoardContainer?)?.showClipBoardView(skbMenuMode)
                updateCandidateBar()
            }
            SkbMenuMode.Custom -> {
                KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.SETTINGS)
                (KeyboardManager.instance.currentContainer as SettingsContainer?)?.enableDragItem(true)
                updateCandidateBar()
            }
            SkbMenuMode.CloseSKB -> {
                requestHideSelf()
            }
            SkbMenuMode.AddPhrases -> {
                isAddPhrases = true
                oldAddPhrases = extra
                KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
                (KeyboardManager.instance.currentContainer as InputBaseContainer?)?.updateStates()
                initView(context)
                mEtAddPhrasesContent?.setText(extra)
                mEtAddPhrasesContent?.setSelection(extra.length)
            }
            else ->{}
        }
        mSkbCandidatesBarView.initMenuView()
    }

    private fun handleAddPhrasesView() {
        mEtAddPhrasesContent =  mAddPhrasesLayout.findViewById(R.id.et_add_phrases_content)
        mEtAddPhrasesContent?.requestFocus()
        tvAddPhrasesTips =  mAddPhrasesLayout.findViewById(R.id.tv_add_phrases_tips)
        val tips = "快捷输入为拼音首字母前4位:"
        mEtAddPhrasesContent?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                tvAddPhrasesTips?.text = tips.plus(PinyinHelper.getPinYinHeadChar(editable.toString()))
            }
        })
    }

    private fun addPhrasesHandle() {
        val content = mEtAddPhrasesContent?.text.toString()
        DataBaseKT.instance.phraseDao().deleteByContent(oldAddPhrases)
        if(content.isNotBlank()) {
            val pinYinHeadChar = PinyinHelper.getPinYinHeadChar(content)
            val pinYinHeadT9 = pinYinHeadChar.map { T9PinYinUtils.pinyin2T9Key(it)}.joinToString("")
            val phrase =  Phrase(content = content, t9 = pinYinHeadT9, qwerty = pinYinHeadChar, lx17 = "")
            DataBaseKT.instance.phraseDao().insert(phrase)
            KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
        }
    }

    /**
     * 输入法状态
     * 空闲，输入，联想
     */
    enum class ImeState {
        STATE_IDLE, STATE_INPUT, STATE_PREDICT
    }

    /**
     * 点击花漾字菜单
     */
    private fun showFlowerTypeface() {
        mSkbCandidatesBarView.showFlowerTypeface()
    }

    /**
     * 选择拼音
     */
    fun selectPrefix(position: Int) {
        // 播放按键声音和震动
        DevicesUtils.tryPlayKeyDown()
        DevicesUtils.tryVibrate(this)
        DecodingInfo.selectPrefix(position)
        updateCandidate()
    }

    //常用符号、剪切板
    fun showSymbols(symbols: Array<String>) {
        mImeState = ImeState.STATE_INPUT
        val list = symbols.map { symbol-> CandidateListItem("📋", symbol) }.toTypedArray()
        DecodingInfo.cacheCandidates(list)
        DecodingInfo.isAssociate = true
        updateCandidateBar()
    }

    private fun requestHideSelf() {
        if(isAddPhrases){
            isAddPhrases = false
            addPhrasesHandle()
            initView(context)
        } else {
            service.requestHideSelf(0)
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun onStartInputView(editorInfo: EditorInfo) {
        resetToIdleState()
        currentInputEditorInfo = editorInfo
        InputModeSwitcherManager.requestInputWithSkb(editorInfo)
        KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
    }

    /**
     * 模拟按键点击
     */
    private fun sendKeyEvent(keyCode: Int) {
        if(isAddPhrases){
            when(keyCode){
                KeyEvent.KEYCODE_DEL ->{
                    mEtAddPhrasesContent?.onKeyDown(keyCode, KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
                    mEtAddPhrasesContent?.onKeyUp(keyCode, KeyEvent(KeyEvent.ACTION_UP, keyCode))
                }
                KeyEvent.KEYCODE_ENTER ->{
                    isAddPhrases = false
                    addPhrasesHandle()
                    initView(context)
                    onSettingsMenuClick(SkbMenuMode.Phrases)
                }
                else -> {
                    val unicodeChar: Char = KeyEvent(KeyEvent.ACTION_DOWN, keyCode).unicodeChar.toChar()
                    if (unicodeChar != Character.MIN_VALUE) {
                        mEtAddPhrasesContent?.commitText(unicodeChar.toString())
                    }
                }
            }
        } else {
            if (keyCode != KeyEvent.KEYCODE_ENTER) {
                service.sendDownUpKeyEvents(keyCode)
            } else {
                val inputConnection = service.getCurrentInputConnection()
                currentInputEditorInfo?.run {
                    if (inputType and InputType.TYPE_MASK_CLASS == InputType.TYPE_NULL || imeOptions.hasFlag(EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
                        service.sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)
                    } else if (!actionLabel.isNullOrEmpty() && actionId != EditorInfo.IME_ACTION_UNSPECIFIED) {
                        inputConnection.performEditorAction(actionId)
                    } else when (val action = imeOptions and EditorInfo.IME_MASK_ACTION) {
                        EditorInfo.IME_ACTION_UNSPECIFIED, EditorInfo.IME_ACTION_NONE -> service.sendDownUpKeyEvents(keyCode)
                        else -> inputConnection.performEditorAction(action)
                    }
                }
            }
        }
    }

    /**
     * 向输入框提交预选词
     */
    private fun setComposingText(text: CharSequence) {
        if(!isAddPhrases)service.getCurrentInputConnection()?.setComposingText(text, 1)
    }

    /**
     * 发送字符串给编辑框
     */
    private fun commitText(text: String) {
        if(isAddPhrases) mEtAddPhrasesContent?.commitText(text)
        else service.getCurrentInputConnection()?.commitText(StringUtils.converted2FlowerTypeface(text), 1)
    }

    /**
     * 发送成对符号给编辑框
     */
    private fun commitPairSymbol(text: String) {
        if(isAddPhrases) {
            mEtAddPhrasesContent?.commitText(text)
        } else {
            val ic = service.getCurrentInputConnection()
            ic?.commitText(text, 1)
            ic?.commitText(SymbolPreset[text]!!, 1)
            ic.commitText("", -1)
        }
    }

    /**
     * 发送候选词字符串给编辑框
     */
    private fun commitDecInfoText(resultText: String?) {
        if(resultText == null) return
        if(isAddPhrases){
            mEtAddPhrasesContent?.commitText(resultText)
        } else {
            val inputConnection = service.getCurrentInputConnection()
            inputConnection.commitText(StringUtils.converted2FlowerTypeface(resultText), 1)
            if (InputModeSwitcherManager.isEnglish && DecodingInfo.isFinish && getInstance().input.abcSpaceAuto.getValue()) {
                inputConnection.commitText(" ", 1)
            }
        }
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
            val fullDisplayKeyboardEnable = getInstance().internal.fullDisplayKeyboardEnable.getValue()
            mLlKeyboardBottomHolder.minimumHeight = if(EnvironmentSingleton.instance.keyboardModeFloat)  0
            else if(fullDisplayKeyboardEnable) EnvironmentSingleton.instance.heightForFullDisplayBar + EnvironmentSingleton.instance.systemNavbarWindowsBottom
            else  EnvironmentSingleton.instance.systemNavbarWindowsBottom
            insets
        }
    }

    fun onWindowShown() {
        chinesePrediction = getInstance().input.chinesePrediction.getValue()
    }

    fun onWindowHidden() {
        if(isAddPhrases){
            isAddPhrases = false
            addPhrasesHandle()
            initView(context)
        }
    }

    fun onUpdateSelection(newSelStart: Int, newSelEnd: Int) {
        if(chinesePrediction && newSelStart == newSelEnd) {
            if (mImeState == ImeState.STATE_PREDICT || InputModeSwitcherManager.isNumberSkb) {
                val inputConnection = service.getCurrentInputConnection()
                val text = inputConnection.getTextBeforeCursor(10, 0).toString()
                if (text.isNotBlank()) {
                    val expressionEnd = StringUtils.getExpressionEnd(text)
                    if(!expressionEnd.isNullOrEmpty()){
                        if(expressionEnd.length > 1){
                            try {
                                val evaluate = ExpressionBuilder(expressionEnd).build().evaluate()
                                val  resultFloat = evaluate.toFloat()
                                showSymbols(if(text.endsWith("="))arrayOf(resultFloat.toString()) else  arrayOf(resultFloat.toString(), "=".plus(resultFloat)) )
                            } catch (_:Exception){ }
                        }
                    } else if (StringUtils.isChineseEnd(text)) {
                        DecodingInfo.isAssociate = true
                        DecodingInfo.getAssociateWord(text)
                        updateCandidate()
                        updateCandidateBar()
                    }
                }
            }
        }
    }
}