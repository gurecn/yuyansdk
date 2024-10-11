package com.yuyan.imemodule.adapter

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.ImeSdkApplication.Companion.context
import splitties.dimensions.dp
import splitties.views.dsl.core.editText
import splitties.views.dsl.core.margin
import splitties.views.dsl.core.textView

class PrefixSettingsAdapter ( private val mDatas: Array<String>) : RecyclerView.Adapter<PrefixSettingsAdapter.PrefixSettingsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrefixSettingsHolder {
        val header = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_VERTICAL
            }
        }.apply {
            addView(textView {}, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            addView(editText {
                    gravity = Gravity.CENTER
                    id = R.id.et_prefix_setting_content
                }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { margin = dp(5) }
            )
            addView(textView {}, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3f))
            addView(ImageView(context).apply {
                    setImageResource(R.drawable.sdk_vector_vertical_scale)
                }, LinearLayout.LayoutParams(dp(20), dp(20)).apply { gravity = Gravity.CENTER}
            )
            addView(textView {}, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        }
        return PrefixSettingsHolder(header)
    }

    override fun onBindViewHolder(holder: PrefixSettingsHolder, position: Int) {
        holder.etPrefixContent.setText(mDatas[position])
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

    fun getSymbolData(position: Int): String {
        return mDatas[position]
    }
}