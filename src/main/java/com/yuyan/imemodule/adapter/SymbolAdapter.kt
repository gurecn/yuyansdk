package com.yuyan.imemodule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.emoji2.widget.EmojiTextView
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.DevicesUtils

/**
 * 表情或符号界面适配器
 */
class SymbolAdapter(context: Context?,  val viewType: Int, private val onClickSymbol: (String, Int) -> Unit) :
    RecyclerView.Adapter<SymbolAdapter.SymbolHolder>() {
    private val textColor: Int
    private val inflater: LayoutInflater
    var mDatas: List<String>? = null

    init {
        val theme = activeTheme
        textColor = theme.keyTextColor
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolHolder {
        return SymbolHolder(inflater.inflate(R.layout.sdk_item_recyclerview_symbols_emoji, parent, false))
    }

    override fun onBindViewHolder(holder: SymbolHolder, position: Int) {
        holder.textView.text = mDatas!![position]
        holder.textView.setOnClickListener { _: View? ->
            onClickSymbol(mDatas!![position], position)
        }
    }

    override fun getItemCount(): Int {
        return mDatas?.size?:0
    }

    inner class SymbolHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView: EmojiTextView
        init {
            textView = view.findViewById(R.id.gv_item)
            textView.setTextColor(textColor)
            textView.textSize = DevicesUtils.px2dip(EnvironmentSingleton.instance.candidateTextSize) * 0.8f
        }
    }
}
