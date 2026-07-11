package com.yuyan.imemodule.candidate

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yuyan.imemodule.R
import com.yuyan.imemodule.callback.CandidateViewListener
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.keyboard.KeyboardManager
import com.yuyan.imemodule.manager.InputModeSwitcher
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode
import com.yuyan.imemodule.service.DictDecoder
import com.yuyan.imemodule.service.ImeService
import com.yuyan.imemodule.service.InputDispatcher
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.utils.StringUtils
import com.yuyan.imemodule.view.widget.LifecycleRelativeLayout
import splitties.dimensions.dp
import splitties.views.bottomPadding
import splitties.views.leftPadding
import kotlin.math.max

/**
 * 物理键盘输入界面。
 * 包含拼音显示、候选词栏、键盘界面等。
 */
@SuppressLint("ViewConstructor")
class CandidateView(context: Context, private val service: ImeService) : LifecycleRelativeLayout(context) {

    private var mHorizontalCutoutWidth: Int = 0
    private var mFloatCandidateBarWidth: Int = 0
    private val appPrefs = getInstance()
    private val mChoiceNotifier = ChoiceNotifier()
    var mSkbRoot: RelativeLayout
    var mSkbCandidatesBarView: FloatCandidateBar

    init {
        InputModeSwitcher.reset()
        initDisplayCutout(service)
        mFloatCandidateBarWidth = (if(instance.isLandscape)instance.mScreenHeight else instance.mScreenWidth) - dp(40)
        mSkbRoot = LayoutInflater.from(context).inflate(R.layout.sdk_candidate_container, this, false) as RelativeLayout
        addView(mSkbRoot)
        mSkbCandidatesBarView = mSkbRoot.findViewById(R.id.candidates_bar)
        DictDecoder.candidatesLiveData.observe(this) {
            mSkbCandidatesBarView.showCandidates()
        }
        initView()
    }

    private fun initDisplayCutout(service: ImeService) {
        ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val lp: WindowManager.LayoutParams? = service.window.window?.attributes
                lp?.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                service.window.window?.setAttributes(lp)
            }
            val safeLeft = insets.getInsets(WindowInsetsCompat.Type.systemBars()).left
            val safeRight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).right
            val cutout = insets.displayCutout
            val displayCutoutWidth = if(cutout!=null) max(cutout.safeInsetLeft, cutout.safeInsetRight) else 0
            mHorizontalCutoutWidth = resources.displayMetrics.widthPixels - safeLeft - safeRight - displayCutoutWidth
            insets
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun initView() {
        mSkbCandidatesBarView.initialize(mChoiceNotifier)
        mSkbRoot.layoutParams.width = mFloatCandidateBarWidth
        updateTheme()
    }

    fun updateTheme() {
        setBackgroundResource(android.R.color.transparent)
        val activeTheme = ThemeManager.activeTheme
        val keyTextColor = activeTheme.keyTextColor
        mSkbCandidatesBarView.updateTheme(keyTextColor)
    }

    fun updatePosition(anchor: FloatArray) {
        val bottom = instance.mScreenHeight - anchor[1].toInt()
        val diffHight = (instance.heightForCandidatesArea * 1.5).toInt()
        leftPadding = if(!instance.isLandscape) 0
         else if(mHorizontalCutoutWidth - anchor[0] > mFloatCandidateBarWidth)anchor[0].toInt() - dp(20)
        else mHorizontalCutoutWidth - mFloatCandidateBarWidth
        bottomPadding = if(bottom > diffHight) bottom - diffHight else bottom + instance.heightForCandidatesArea
    }

    fun processKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if(InputModeSwitcher.isEnglish) return false
        // 字母、数字、符号、空格
        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) return true
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) return true
        if (keyCode == KeyEvent.KEYCODE_SPACE) return true
        if (keyCode == KeyEvent.KEYCODE_APOSTROPHE || keyCode == KeyEvent.KEYCODE_SEMICOLON) return true   // KEYCODE_SEMICOLON在双拼中使用
        // 编辑键
        if (keyCode == KeyEvent.KEYCODE_DEL) return true
        if (keyCode == KeyEvent.KEYCODE_ENTER) return true
        if (keyCode == KeyEvent.KEYCODE_TAB) return true
        // 方向键
        if (keyCode >= KeyEvent.KEYCODE_DPAD_UP && keyCode <= KeyEvent.KEYCODE_DPAD_RIGHT) return true
        return false
    }

    fun processKeyUp(event: KeyEvent): Boolean {
        InputModeSwitcher.resetCharCase()
        return if (processFunctionKeys(event)) true
        else if (InputModeSwitcher.isChinese) processInput(event)
        else  false
    }

    private fun processFunctionKeys(event: KeyEvent): Boolean {
        return when (val keyCode = event.keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_SPACE -> {
                if (DictDecoder.isCandidatesEmpty || (DictDecoder.isAssociate && !mSkbCandidatesBarView.isActiveCand())) {
                    sendKeyEvent(keyCode)
                    resetToIdleState()
                } else {
                    chooseAndUpdate()
                }
                if(keyCode == KeyEvent.KEYCODE_SPACE && event.isCtrlPressed ){
                    InputModeSwitcher.switchModeForUserKey(InputModeSwitcher.USER_KEYCODE_LANG)
                    resetToIdleState()
                    Toast.makeText(context, if(InputModeSwitcher.isEnglish)"语燕输入法-英文" else "语燕输入法-拼音", Toast.LENGTH_LONG).show()
                }
                true
            }
            KeyEvent.KEYCODE_CLEAR -> {
                resetToIdleState()
                true
            }
            KeyEvent.KEYCODE_ENTER -> {
                if (DictDecoder.isCandidatesEmpty || DictDecoder.isAssociate) sendKeyEvent(keyCode)
                else commitDecInfoText(DictDecoder.composingStrForCommit)
                resetToIdleState()
                true
            }
            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (!DictDecoder.isCandidatesEmpty) {
                    mSkbCandidatesBarView.updateActiveCandidateNo(keyCode)
                } else {
                    sendKeyEvent(keyCode)
                }
                true
            }
            else -> false
        }
    }

    private fun processInput(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        val keyChar = event.unicodeChar
        val label = keyChar.toChar().toString()
        return when {
            keyCode == KeyEvent.KEYCODE_DEL -> {
                if (DictDecoder.isCandidatesEmpty || DictDecoder.isAssociate) {
                    sendKeyEvent(keyCode)
                } else {
                    if(!DictDecoder.isEngineFinish){
                        InputDispatcher.deleteAction()
                    } else {
                        InputDispatcher.reset()
                    }
                    updateCandidate()
                }
                true
            }
            label.isDigitsOnly() ->{
                chooseAndUpdate(label.toInt() - 1)
                true
            }
            Character.isLetter(keyChar) || keyCode == KeyEvent.KEYCODE_APOSTROPHE || keyCode == KeyEvent.KEYCODE_SEMICOLON -> {
                InputDispatcher.inputKeyCode(event)
                updateCandidate()
                true
            }
            else -> {
                if (!DictDecoder.isCandidatesEmpty && !DictDecoder.isAssociate) chooseAndUpdate()
                false
            }
        }
    }

    fun resetToIdleState() {
        DictDecoder.reset()
        InputDispatcher.reset()
    }

    fun chooseAndUpdate(candId: Int = mSkbCandidatesBarView.getActiveCandNo()) {
        val choice = DictDecoder.chooseDecodingCandidate(candId)
        if (DictDecoder.isCandidatesEmpty || DictDecoder.isAssociate) {
            commitDecInfoText(choice)
            resetToIdleState()
        }
    }

    private fun updateCandidate() {
        DictDecoder.updateDecodingCandidate()
        if (DictDecoder.isCandidatesEmpty) resetToIdleState()
    }

    inner class ChoiceNotifier internal constructor() : CandidateViewListener {
        override fun onClickChoice(choiceId: Int) {
            DevicesUtils.tryPlayKeyDown()
            DevicesUtils.tryVibrate(KeyboardManager.instance.currentContainer)
            chooseAndUpdate(choiceId)
        }

        override fun onClickMore(level: Int) {}

        override fun onClickMenu(skbMenuMode: SkbMenuMode){}

        override fun onClickClearCandidate() {}

        override fun onClickClearClipBoard() {}
    }

    fun requestHideSelf() = service.requestHideSelf(0)

    private fun sendKeyEvent(keyCode: Int) {
        when (keyCode) {
            KeyEvent.KEYCODE_ENTER -> service.sendEnterKeyEvent()
            in KeyEvent.KEYCODE_DPAD_UP..KeyEvent.KEYCODE_DPAD_RIGHT -> {
                service.sendCombinationKeyEvents(keyCode)
            }
            else -> service.sendCombinationKeyEvents(keyCode)
        }
    }

    private fun commitDecInfoText(resultText: String?) {
        resultText ?: return
        service.commitText(StringUtils.converted2FlowerTypeface(resultText))
        if (InputModeSwitcher.isEnglish){
            service.finishComposingText()
            if(appPrefs.input.abcSpaceAuto.getValue()) service.commitText(" ")
            resetToIdleState()
        }
    }

    fun onStartInput(editorInfo: EditorInfo?, restarting: Boolean) {
        if(editorInfo != null)InputModeSwitcher.requestInputWithSkb(editorInfo)
        if (!restarting) resetToIdleState()
    }

}