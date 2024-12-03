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
import com.yuyan.imemodule.db.entry.SideSymbol
import splitties.dimensions.dp
import splitties.views.dsl.core.add
import splitties.views.dsl.core.editText
import splitties.views.dsl.core.lParams

class PrefixSettingsAdapter ( private val mDatas: List<SideSymbol>) : RecyclerView.Adapter<PrefixSettingsAdapter.PrefixSettingsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrefixSettingsHolder {
        val content = LinearLayout(context).apply {
            setPadding(0, dp(5), 0, dp(5))
            add(editText {
                gravity = Gravity.CENTER
                background = null
                id = R.id.et_prefix_setting_key
            }, lParams(width = 0, weight = 2f))

            add(editText {
                gravity = Gravity.CENTER
                background = null
                id = R.id.et_prefix_setting_value
            }, lParams(width = 0, weight = 2f))

            add(ImageView(context).apply {
                setImageResource(R.drawable.sdk_vector_vertical_scale)
            }, lParams(width = 0, weight = 1f, gravity = Gravity.CENTER))
        }
        return PrefixSettingsHolder(content)
    }

    override fun onBindViewHolder(holder: PrefixSettingsHolder, position: Int) {
        holder.etPrefixKey.setText(mDatas[position].symbolKey)
        holder.etPrefixValue.setText(mDatas[position].symbolValue)
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    inner class PrefixSettingsHolder(view: View) : RecyclerView.ViewHolder(view) {
        var etPrefixKey: EditText
        var etPrefixValue: EditText
        init {
            view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_VERTICAL
            }
            etPrefixKey = view.findViewById(R.id.et_prefix_setting_key)
            etPrefixValue = view.findViewById(R.id.et_prefix_setting_value)
        }
    }
}