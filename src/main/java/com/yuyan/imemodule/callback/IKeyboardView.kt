package com.yuyan.imemodule.callback

import android.content.Context
import android.content.res.Configuration
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import com.yuyan.imemodule.view.widget.LifecycleRelativeLayout

abstract class IKeyboardView(context: Context) : LifecycleRelativeLayout(context){
    abstract fun onStartInputView(editorInfo: EditorInfo, restarting: Boolean)
    abstract fun processKeyDown(keyCode: Int, event: KeyEvent): Boolean
    abstract fun processKeyUp(event: KeyEvent): Boolean
    abstract fun onUpdateSelection(oldSelStart: Int, oldSelEnd: Int, newSelStart: Int, newSelEnd: Int,  candidatesEnd: Int)
    abstract  fun onWindowShown()
    abstract fun onWindowHidden()
    abstract fun updatePosition(anchor: FloatArray)
    abstract fun getKeyboardRect():IntArray
//    abstract fun getKeyboardWidth():Int
//    abstract fun getKeyboardHeight():Int
    abstract fun updateTheme()
    abstract fun showSymbols(symbols: Array<String>)
    abstract fun setConfiguration(newConfig: android.content.res.Configuration)
}