package com.yuyan.imemodule.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.view.keyboard.KeyboardManager
import splitties.views.dsl.core.editText

class PrefixSettingsFragment(pos:Int) : Fragment(){
    private var  positon = 0
    private var  onWaitSave = false
    private var prefix:String

    init {
        positon = pos
        prefix = (if(positon == 0)CustomConstant.PREFIXS_PINYIN else CustomConstant.PREFIXS_NUMBER).joinToString ("\n")
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = with(requireContext()) {

        var oldText = ""
        editText {
            setText(prefix)
            requestFocus()
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            doBeforeTextChanged { text, _, _, _ ->
                oldText = text?.toString()?:""
            }
            doOnTextChanged { text, start, _, _ ->
                var onChanged = false
                val prefixs = text.toString().split("\n".toRegex()).toMutableList()
                if(prefixs.size > 15){
                    prefixs.clear()
                    prefixs.addAll(oldText.split("\n".toRegex()))
                    onChanged = true
                    onWaitSave = true
                }
                prefixs.forEachIndexed{ index, item ->
                    if(item.length > 4) {
                        prefixs[index] = item.substring(0, 4)
                        onChanged = true
                        onWaitSave = true
                    }
                }
                if(onChanged){
                    val content = prefixs.joinToString ("\n")
                    this@editText.setText(content)
                    this@editText.setSelection(if(start > content.length)content.length else start)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if(onWaitSave) {
            if (positon == 0) {
                AppPrefs.getInstance().internal.keyboardPrefixsPinyin.setValue(prefix.replace("\n", " "))
                CustomConstant.PREFIXS_PINYIN = prefix.split("\n".toRegex()).toTypedArray()
            } else {
                AppPrefs.getInstance().internal.keyboardPrefixsNumber.setValue(prefix.replace("\n", " "))
                CustomConstant.PREFIXS_NUMBER = prefix.split("\n".toRegex()).toTypedArray()
            }
            KeyboardManager.instance.clearKeyboard()
        }
    }
}