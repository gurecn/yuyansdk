package com.yuyan.imemodule.adapter

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.Launcher
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.database.entry.SideSymbol
import splitties.dimensions.dp
import splitties.views.dsl.core.add
import splitties.views.dsl.core.editText
import splitties.views.dsl.core.lParams
import splitties.views.dsl.core.matchParent

class PrefixSettingsAdapter ( private val mDatas: MutableList<SideSymbol>, type:String) : RecyclerView.Adapter<PrefixSettingsAdapter.PrefixSettingsHolder>() {
    private var  mType = "pinyin"
    init {
        mType = type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrefixSettingsHolder {
        val content = LinearLayout(Launcher.instance.context).apply {
            setPadding(0, dp(5), 0, dp(5))
            add(editText {
                gravity = Gravity.CENTER
                setTextColor(activeTheme.keyTextColor)
                id = R.id.et_prefix_setting_key
            }, lParams(width = 0, weight = 1f, height = matchParent){
                setMargins(dp(20), 0, dp(20), 0)
            })
            add(editText {
                gravity = Gravity.CENTER
                setTextColor(activeTheme.keyTextColor)
                id = R.id.et_prefix_setting_value
            }, lParams(width = 0, weight = 2f))

            add(ImageView(context).apply {
                setImageResource(R.drawable.ic_menu_menu)
                drawable.setTint(activeTheme.keyTextColor)
            }, lParams(width = 0, weight = 1f, gravity = Gravity.CENTER))
        }
        return PrefixSettingsHolder(content)
    }

    override fun onBindViewHolder(holder: PrefixSettingsHolder, position: Int) {
        if(position < mDatas.size) {
            holder.etPrefixKey.setText(mDatas[position].symbolKey)
            holder.etPrefixValue.setText(mDatas[position].symbolValue)
        }
        holder.etPrefixKey.doOnTextChanged { s, _, _, _ ->
            val key = s.toString()
            val bindPos = holder.bindingAdapterPosition
            if(bindPos < mDatas.size) {
                val data = mDatas[bindPos]
                data.symbolKey = key
                if(data.symbolKey == "" && data.symbolValue == ""){
                    mDatas.removeAt(bindPos)
                    notifyItemRemoved(bindPos)
                }
            }
            else {
                mDatas.add(SideSymbol(symbolKey = key, symbolValue = key, type = mType))
                holder.etPrefixValue.setText(key)
                notifyDataSetChanged()
            }
        }
        holder.etPrefixValue.doOnTextChanged { s, _, _, _ ->
            val value = s.toString()
            val bindPos = holder.bindingAdapterPosition
            if(bindPos < mDatas.size) {
                val data = mDatas[bindPos]
                data.symbolValue = value
                if(data.symbolKey == "" && data.symbolValue == ""){
                    mDatas.removeAt(bindPos)
                    notifyItemRemoved(bindPos)
                }
            }
            else {
                mDatas.add(SideSymbol(symbolKey = value, symbolValue = value, type = mType))
                holder.etPrefixKey.setText(value)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return mDatas.size + 1
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