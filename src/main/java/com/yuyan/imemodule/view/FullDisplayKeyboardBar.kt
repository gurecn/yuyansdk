package com.yuyan.imemodule.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.yuyan.imemodule.R
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.FullDisplayCenterMode
import com.yuyan.imemodule.prefs.behavior.FullDisplayKeyMode
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode
import com.yuyan.imemodule.utils.InputMethodUtil
import com.yuyan.imemodule.keyboard.InputView
import com.yuyan.imemodule.keyboard.KeyboardManager
import com.yuyan.imemodule.keyboard.container.ClipBoardContainer
import splitties.dimensions.dp
import splitties.views.dsl.core.add
import splitties.views.dsl.core.lParams
import splitties.views.dsl.core.wrapContent
import kotlin.math.abs

/**
 * 全面屏键盘优化
 */
@SuppressLint("ViewConstructor")
class FullDisplayKeyboardBar(context: Context?, inputView: InputView) : LinearLayout(context) {

    private val mInputView: InputView
    private val mIVKeyLeft: ImageView
    private val mLLCenter: LinearLayout
    private val mIVKeyRight: ImageView
    private val mCenterModeMove: Boolean
    init {
        mInputView = inputView
        setPadding(dp(20), dp(10), dp(20), dp(20))
        val fullDisplayKeyLeft = AppPrefs.getInstance().internal.fullDisplayKeyModeLeft.getValue()
        val fullDisplayKeyRight = AppPrefs.getInstance().internal.fullDisplayKeyModeRight.getValue()
        val centerMode = FullDisplayCenterMode.decode(AppPrefs.getInstance().internal.fullDisplayCenterMode.getValue())
        mCenterModeMove = FullDisplayCenterMode.MoveCursor == centerMode
        gravity = Gravity.CENTER
        mIVKeyLeft = ImageView(context).apply {
            setImageResource(getIcon(FullDisplayKeyMode.decode(fullDisplayKeyLeft)))
            isClickable = true
            isEnabled = true
            setOnClickListener { _: View? ->
                onClick(FullDisplayKeyMode.decode(AppPrefs.getInstance().internal.fullDisplayKeyModeLeft.getValue()))
            }
        }
        mLLCenter = LinearLayout(context)
        mIVKeyRight = ImageView(context).apply {
            val iconId = getIcon(FullDisplayKeyMode.decode(fullDisplayKeyRight))
            if(iconId != 0)setImageResource(iconId)
            isClickable = true
            isEnabled = true
            setOnClickListener { _: View? ->
                onClick(FullDisplayKeyMode.decode(AppPrefs.getInstance().internal.fullDisplayKeyModeRight.getValue()))
            }
        }
        add(mIVKeyLeft, lParams(width = wrapContent,height = wrapContent, weight = 0f))
        add(mLLCenter, lParams(width = 0,height = wrapContent, weight = 1f))
        add(mIVKeyRight, lParams(width = wrapContent,height = wrapContent, weight = 0f))
        isClickable = true
        isEnabled = true
    }

    private fun getIcon(keyMode:FullDisplayKeyMode):Int{
       return when(keyMode){
            FullDisplayKeyMode.SwitchIme -> R.drawable.ic_menu_language
            FullDisplayKeyMode.SwitchLanguage -> R.drawable.ic_menu_keyboard_switcher
           FullDisplayKeyMode.Clipboard -> R.drawable.ic_menu_clipboard
           FullDisplayKeyMode.Phrases -> R.drawable.ic_menu_phrases
           else -> 0
       }
    }
    private fun onClick(keyMode:FullDisplayKeyMode){
        when(keyMode){
            FullDisplayKeyMode.SwitchIme -> InputMethodUtil.showPicker()
            FullDisplayKeyMode.SwitchLanguage -> InputModeSwitcherManager.switchModeForUserKey(InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2)
            FullDisplayKeyMode.Clipboard, FullDisplayKeyMode.Phrases -> {
                if(KeyboardManager.instance.currentContainer is ClipBoardContainer){
                    KeyboardManager.instance.switchKeyboard()
                } else {
                    KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.ClipBoard)
                    (KeyboardManager.instance.currentContainer as ClipBoardContainer?)?.showClipBoardView(
                        if (keyMode == FullDisplayKeyMode.Clipboard) SkbMenuMode.ClipBoard else SkbMenuMode.Phrases
                    )
                    mInputView.updateCandidateBar()
                }
            }
            else -> {}
        }
    }

    private var lastEventX:Float = -1f
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(me: MotionEvent): Boolean {
        var result = false
        val currentX = me.x
        if (me.action == MotionEvent.ACTION_DOWN){
            lastEventX = currentX
            result = true
        } else {
            val relDiffX = abs(currentX - lastEventX)
            val spaceSwipeMoveCursorSpeed = AppPrefs.getInstance().keyboardSetting.spaceSwipeMoveCursorSpeed.getValue()
            if (relDiffX > spaceSwipeMoveCursorSpeed && mCenterModeMove) {  // 左右滑动
                val key = SoftKey()
                key.code = if (currentX < lastEventX) KeyEvent.KEYCODE_DPAD_LEFT else KeyEvent.KEYCODE_DPAD_RIGHT
                mInputView.responseKeyEvent(key)
                lastEventX = currentX
                result = true
            }
        }
        return result
    }


    // 刷新主题
    fun updateTheme(textColor: Int) {
        mIVKeyLeft.drawable?.setTint(textColor)
        mIVKeyRight.drawable?.setTint(textColor)
    }
}
