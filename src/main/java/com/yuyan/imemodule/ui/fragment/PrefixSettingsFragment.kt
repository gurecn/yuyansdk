package com.yuyan.imemodule.ui.fragment

import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.LineHeightSpan
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.view.keyboard.KeyboardManager
import splitties.dimensions.dp
import splitties.views.dsl.core.editText

class PrefixSettingsFragment(pos:Int) : Fragment(){
    private var  positon = 0
    private var  onWaitSave = false
    private lateinit var editText:EditText

    init {
        positon = pos
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = with(requireContext()) {

        var oldText = ""
        editText = editText {
            val spannableString = SpannableString((if(positon == 0)CustomConstant.PREFIXS_PINYIN else CustomConstant.PREFIXS_NUMBER).joinToString ("\n"))
            spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            spannableString.setSpan( LineHeightSpanCustom(dp(5)), 0, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            setText(spannableString)
            requestFocus()
            gravity = Gravity.CENTER_HORIZONTAL
            paintFlags = this.paintFlags or Paint.UNDERLINE_TEXT_FLAG;
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            doBeforeTextChanged { text, _, _, _ ->
                oldText = text?.toString()?:""
            }
            doOnTextChanged { text, start, _, _ ->
                var onChanged = false
                val prefixs = text.toString().split(Regex("\n")).toMutableList()
                if(prefixs.size > 15){
                    prefixs.clear()
                    prefixs.addAll(oldText.split(Regex("\n")))
                    onChanged = true
                }
                prefixs.forEachIndexed{ index, item ->
                    if(item.length > 4) {
                        prefixs[index] = item.substring(0, 4)
                        onChanged = true
                    }
                }
                val content = prefixs.joinToString ("\n")
                if(onChanged){
                    this@editText.setText(content)
                    this@editText.setSelection(if(start > content.length)content.length else start)
                }
                onWaitSave = true
            }
        }
        editText
    }

    override fun onStop() {
        super.onStop()
        if(onWaitSave) {
            val prefixs = editText.text.toString().split(Regex("\n")).filterNot { it.isEmpty() }
            val prefix = prefixs.joinToString ("\n")
            if (positon == 0) {
                AppPrefs.getInstance().internal.keyboardPrefixsPinyin.setValue(prefix)
                CustomConstant.PREFIXS_PINYIN = prefixs.toTypedArray()
            } else {
                AppPrefs.getInstance().internal.keyboardPrefixsNumber.setValue(prefix)
                CustomConstant.PREFIXS_NUMBER = prefixs.toTypedArray()
            }
            KeyboardManager.instance.clearKeyboard()
        }
    }
}

class LineHeightSpanCustom(private val lineHeight: Int) : LineHeightSpan {
    override fun chooseHeight(text: CharSequence, start: Int, end: Int, spanstartv: Int, v: Int, fm: FontMetricsInt) {
        fm.bottom += lineHeight
        fm.descent += lineHeight
    }
}