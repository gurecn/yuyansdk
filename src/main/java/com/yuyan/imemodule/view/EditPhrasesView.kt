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
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.view.keyboard.KeyboardManager
import com.yuyan.imemodule.view.widget.ImeEditText
import com.yuyan.inputmethod.util.T9PinYinUtils
import splitties.dimensions.dp
import splitties.views.dsl.core.add
import splitties.views.dsl.core.lParams
import splitties.views.dsl.core.matchParent
import splitties.views.dsl.core.wrapContent

class EditPhrasesView(context: Context) : LinearLayout(context){

    private var mEtAddPhrasesContent: ImeEditText
    private var tvAddPhrasesTips:TextView
    init {
        orientation = VERTICAL
        tvAddPhrasesTips = TextView(context).apply {
            setText(R.string.add_phrases_input_tips)
            setPadding(dp(5))
            textSize = 12f
        }
        mEtAddPhrasesContent = ImeEditText(context).apply {
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
        add(tvAddPhrasesTips, lParams(width = wrapContent,height = wrapContent, weight = 0f))
        add(mEtAddPhrasesContent, lParams(width = matchParent,height = wrapContent, weight = 1f))
    }

    fun handleAddPhrasesView() {
        mEtAddPhrasesContent.requestFocus()
        val tips = "快捷输入为拼音首字母前4位:"
        mEtAddPhrasesContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                tvAddPhrasesTips.text = tips.plus(com.yuyan.imemodule.libs.pinyin4j.PinyinHelper.getPinYinHeadChar(editable.toString()))
            }
        })
    }

    fun clearPhrasesContent() {
        mEtAddPhrasesContent.setText("")
    }

    fun addPhrasesHandle() {
        val content = mEtAddPhrasesContent.text.toString()
        if(content.isNotBlank()) {
            val pinYinHeadChar = com.yuyan.imemodule.libs.pinyin4j.PinyinHelper.getPinYinHeadChar(content)
            val pinYinHeadT9 = pinYinHeadChar.map { T9PinYinUtils.pinyin2T9Key(it)}.joinToString("")
            val phrase =  Phrase(content = content, t9 = pinYinHeadT9, qwerty = pinYinHeadChar, lx17 = "")
            DataBaseKT.instance.phraseDao().insert(phrase)
            KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
        }
    }

    fun setExtraData(extra:String) {
        mEtAddPhrasesContent.setText(extra)
        mEtAddPhrasesContent.setSelection(extra.length)
    }

    fun commitText(text:String) {
        mEtAddPhrasesContent.commitText(text)
    }

    fun sendKeyEvent(keyCode: Int) {
        when(keyCode){
            KeyEvent.KEYCODE_DEL ->{
                mEtAddPhrasesContent.onKeyDown(keyCode, KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
                mEtAddPhrasesContent.onKeyUp(keyCode, KeyEvent(KeyEvent.ACTION_UP, keyCode))
            }
            KeyEvent.KEYCODE_ENTER ->{
                addPhrasesHandle()
            }
            else -> {
                val unicodeChar: Char = KeyEvent(KeyEvent.ACTION_DOWN, keyCode).unicodeChar.toChar()
                if (unicodeChar != Character.MIN_VALUE) {
                    mEtAddPhrasesContent.commitText(unicodeChar.toString())
                }
            }
        }
    }

    fun updateTheme(theme: Theme) {
        val keyTextColor = ThemeManager.activeTheme.keyTextColor
        setBackgroundColor(theme.barColor)
        mEtAddPhrasesContent.background = GradientDrawable().apply {
            setColor(ThemeManager.activeTheme.keyBackgroundColor)
            shape = GradientDrawable.RECTANGLE
            cornerRadius = ThemeManager.prefs.keyRadius.getValue().toFloat()
        }
        mEtAddPhrasesContent.setTextColor(keyTextColor)
        mEtAddPhrasesContent.setHintTextColor(keyTextColor)
        tvAddPhrasesTips.setTextColor(keyTextColor)
    }
}