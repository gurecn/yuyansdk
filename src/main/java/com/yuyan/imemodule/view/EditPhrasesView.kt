package com.yuyan.imemodule.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.yuyan.imemodule.R
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.database.entry.Phrase
import com.yuyan.imemodule.libs.pinyin4j.PinyinHelper
import com.yuyan.imemodule.manager.InputModeSwitcher
import com.yuyan.imemodule.keyboard.KeyboardManager
import com.yuyan.imemodule.view.widget.ImeEditText
import com.yuyan.inputmethod.util.LX17PinYinUtils
import com.yuyan.inputmethod.util.T9PinYinUtils
import splitties.dimensions.dp
import splitties.views.dsl.core.add
import splitties.views.dsl.core.lParams
import splitties.views.dsl.core.matchParent
import splitties.views.dsl.core.wrapContent

class EditPhrasesView(context: Context) : LinearLayout(context){

    private var mEtPhrases: ImeEditText
    private var mEtPhrasesQuickCode:ImeEditText
    private var mEtPhrasesTips:TextView
    init {
        orientation = VERTICAL
        mEtPhrasesTips = TextView(context).apply {
            setText(R.string.add_phrases_input_tips)
            setPadding(dp(5))
            textSize = 12f
        }
        mEtPhrasesQuickCode = ImeEditText(context).apply {
            setText(R.string.add_phrases_input_tips)
            setPadding(dp(5))
            background = null
            isCursorVisible = true
            isFocusable = true
            isFocusableInTouchMode = true
            inputType
            textSize = 12f
        }
       val llTipContainer = LinearLayout(context)
        llTipContainer.add(mEtPhrasesTips, lParams(weight = 0f))
        llTipContainer.add(mEtPhrasesQuickCode, lParams(weight = 1f))
        mEtPhrases = ImeEditText(context).apply {
            background = null
            isCursorVisible = true
            isFocusable = true
            isFocusableInTouchMode = true
            gravity = Gravity.CENTER_VERTICAL
            textSize = 16f
            minLines = 3
            setPadding(dp(10))
            setHint(R.string.add_phrases_input_hint)
        }
        add(llTipContainer, lParams(width = matchParent,height = wrapContent, weight = 0f))
        add(mEtPhrases, lParams(width = matchParent,height = wrapContent, weight = 1f))
        mEtPhrases.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                val pinYinHeadChar = PinyinHelper.getPinYinHeadChar(editable.toString().take(4))
                if(pinYinHeadChar.isNotBlank()) {
                    mEtPhrasesQuickCode.setText(pinYinHeadChar)
                    mEtPhrasesQuickCode.setSelection(pinYinHeadChar.length)
                }
            }
        })
    }

    fun requestEditorFocus() {
        mEtPhrases.requestFocus()
    }

    fun clearEditorFocus() {
        if (mEtPhrases.isFocused) mEtPhrases.clearFocus()
        if (mEtPhrasesQuickCode.isFocused) mEtPhrasesQuickCode.clearFocus()
    }

    fun clearPhrasesContent() {
        mEtPhrases.setText("")
        mEtPhrasesQuickCode.setText("")
    }

    fun addPhrasesHandle() {
        val content = mEtPhrases.text.toString()
        if(content.isNotBlank()) {
            val pinYinHeadChar  = mEtPhrasesQuickCode.text.toString()
            val pinYinHeadT9 = pinYinHeadChar.map { T9PinYinUtils.pinyin2T9Key(it)}.joinToString("")
            val pinYinHeadLX17 = pinYinHeadChar.map { LX17PinYinUtils.pinyin2Lx17Key(it)}.joinToString("")
            val phrase =  Phrase(content = content, t9 = pinYinHeadT9, qwerty = pinYinHeadChar, lx17 = pinYinHeadLX17)
            DataBaseKT.instance.phraseDao().insert(phrase)
            KeyboardManager.instance.switchKeyboard(InputModeSwitcher.skbImeLayout)
        }
    }

    fun setExtraData(extra:Phrase) {
        val content = extra.content
        val qwerty = extra.qwerty
        mEtPhrases.setText(content)
        mEtPhrases.setSelection(content.length)
        mEtPhrasesQuickCode.setText(qwerty)
        mEtPhrasesQuickCode.setSelection(qwerty.length)
    }

    private val focusedEditor: ImeEditText
        get() = if (mEtPhrasesQuickCode.isFocused) mEtPhrasesQuickCode else mEtPhrases

    fun commitText(text: String) {
        focusedEditor.commitText(text)
    }

    fun deleteBackward() {
        val editor = focusedEditor
        editor.onKeyDown(KeyEvent.KEYCODE_DEL, KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
        editor.onKeyUp(KeyEvent.KEYCODE_DEL, KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
    }

    fun deleteBackward(count: Int) {
        val editor = focusedEditor
        val editable = editor.text ?: return
        val selStart = editor.selectionStart.coerceAtLeast(0)
        val selEnd = editor.selectionEnd.coerceAtLeast(0)
        val from = if (selStart != selEnd) minOf(selStart, selEnd)
        else (minOf(selStart, selEnd) - count).coerceAtLeast(0)
        val to = maxOf(selStart, selEnd)
        if (from < to) editable.delete(from, to)
    }

    fun moveCursor(delta: Int) {
        val editor = focusedEditor
        val length = editor.text?.length ?: 0
        editor.setSelection((editor.selectionStart + delta).coerceIn(0, length))
    }

    fun insertKeyChar(keyCode: Int) {
        val unicodeChar = KeyEvent(KeyEvent.ACTION_DOWN, keyCode).unicodeChar.toChar()
        if (unicodeChar != Character.MIN_VALUE) focusedEditor.commitText(unicodeChar.toString())
    }

    fun updateTheme(theme: Theme) {
        val keyTextColor = ThemeManager.activeTheme.keyTextColor
        setBackgroundColor(theme.barColor)
        mEtPhrases.background = GradientDrawable().apply {
            setColor(ThemeManager.activeTheme.keyBackgroundColor)
            shape = GradientDrawable.RECTANGLE
            cornerRadius = ThemeManager.prefs.keyRadius.getValue().toFloat()
        }
        mEtPhrases.setTextColor(keyTextColor)
        mEtPhrases.setHintTextColor(keyTextColor)
        mEtPhrasesTips.setTextColor(keyTextColor)
        mEtPhrasesQuickCode.setTextColor(keyTextColor)
    }
}