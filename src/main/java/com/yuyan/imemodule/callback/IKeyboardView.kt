package com.yuyan.imemodule.callback

import android.content.Context
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout

abstract class IKeyboardView(context: Context) : RelativeLayout(context){
    abstract fun onStartInputView(editorInfo: EditorInfo, restarting: Boolean)
    abstract fun processKeyDown(keyCode: Int, event: KeyEvent): Boolean
    abstract fun processKeyUp(event: KeyEvent): Boolean
    abstract fun onUpdateSelection(oldSelStart: Int, oldSelEnd: Int, newSelStart: Int, newSelEnd: Int,  candidatesEnd: Int)
    abstract  fun onWindowShown()
    abstract fun onWindowHidden()
    abstract fun updatePosition(anchor: FloatArray)
    abstract fun getKeyboardRect():IntArray
    abstract fun updateTheme()
    abstract fun showSymbols(symbols: Array<String>)
    abstract fun setConfiguration(newConfig: android.content.res.Configuration)
    abstract fun onCandidateChanged()


    /**
     * 判断按键是否属于输入视图需要处理的文本/候选/编辑导航按键。
     */
    open fun isKeyEventForIme(keyCode: Int, event: KeyEvent): Boolean {
        if (event.isPrintingKey || event.unicodeChar != 0) return true
        return when (keyCode) {
            in KeyEvent.KEYCODE_A..KeyEvent.KEYCODE_Z,
            in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9,
            in KeyEvent.KEYCODE_DPAD_UP..KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_DEL, KeyEvent.KEYCODE_FORWARD_DEL, KeyEvent.KEYCODE_TAB,
            KeyEvent.KEYCODE_APOSTROPHE, KeyEvent.KEYCODE_SEMICOLON, KeyEvent.KEYCODE_CLEAR,
            KeyEvent.KEYCODE_SYM, KeyEvent.KEYCODE_NUM, KeyEvent.KEYCODE_PICTSYMBOLS,
            KeyEvent.KEYCODE_LANGUAGE_SWITCH, KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> true
            else -> false
        }
    }

}