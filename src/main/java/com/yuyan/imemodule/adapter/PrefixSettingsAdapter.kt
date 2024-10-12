package com.yuyan.imemodule.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.ImeSdkApplication.Companion.context
import splitties.views.dsl.core.editText


class PrefixSettingsAdapter ( private val mDatas: MutableList<String>) : RecyclerView.Adapter<PrefixSettingsAdapter.PrefixSettingsHolder>() {

    private var mOnEditTextTextChangedListener: OnEditTextTextChangedListener? = null
    fun setOnEditTextTextChangedListener(onEditTextTextChangedListener: OnEditTextTextChangedListener?) {
        mOnEditTextTextChangedListener = onEditTextTextChangedListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrefixSettingsHolder {
        val header = LinearLayout(context).apply {
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }.apply {
            addView(editText {
                    filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
                    gravity = Gravity.CENTER
                    id = R.id.et_prefix_setting_content
                }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            )
        }
        return PrefixSettingsHolder(header)
    }

    override fun onBindViewHolder(holder: PrefixSettingsHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.etPrefixContent.setText(mDatas[position])
        holder.etPrefixContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(content: Editable) {
                mOnEditTextTextChangedListener?.onTextChanged(content.toString(), position)
            }
        })

    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    inner class PrefixSettingsHolder(view: View) : RecyclerView.ViewHolder(view) {
        var etPrefixContent: EditText

        init {
            etPrefixContent = view.findViewById(R.id.et_prefix_setting_content)
        }
    }
}

fun interface OnEditTextTextChangedListener {
    fun onTextChanged(content: String, position: Int)
}