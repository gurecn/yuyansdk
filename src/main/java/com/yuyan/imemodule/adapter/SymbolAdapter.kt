package com.yuyan.imemodule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.emoji2.widget.EmojiTextView
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.HalfWidthSymbolsMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.utils.StringUtils

/**
 * 表情或符号界面适配器
 */
class SymbolAdapter(context: Context?, val viewType: Int, private val pagerIndex: Int, private val onClickSymbol: (String, Int) -> Unit) :
    RecyclerView.Adapter<SymbolAdapter.SymbolHolder>() {
    private val inflater: LayoutInflater
    var mDatas: List<String>? = null
    private val halfWidthSymbolsMode: HalfWidthSymbolsMode = AppPrefs.getInstance().keyboardSetting.halfWidthSymbolsMode.getValue()

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolHolder {
        return SymbolHolder(inflater.inflate(R.layout.sdk_item_recyclerview_symbols_emoji, parent, false))
    }

    override fun onBindViewHolder(holder: SymbolHolder, position: Int) {
        val data = mDatas!![position]
        holder.textView.text = data
        holder.tVSdb.visibility = if(viewType >= CustomConstant.EMOJI_TYPR_FACE_DATA) View.GONE else {
            when (halfWidthSymbolsMode) {
                HalfWidthSymbolsMode.All -> {
                    if (StringUtils.isDBCSymbol(data)) View.VISIBLE else View.GONE
                }
                HalfWidthSymbolsMode.OnlyUsed -> {
                    if (pagerIndex == 0 && StringUtils.isDBCSymbol(data)) View.VISIBLE else View.GONE
                }
                HalfWidthSymbolsMode.None -> { View.GONE }
            }
        }
        holder.textView.setOnClickListener { _: View? ->
            onClickSymbol(mDatas!![position], position)
        }
    }

    override fun getItemCount(): Int {
        return mDatas?.size?:0
    }

    inner class SymbolHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView: EmojiTextView
        var tVSdb: TextView
        init {
            textView = view.findViewById(R.id.gv_item)
            textView.setTextColor(activeTheme.keyTextColor)
            textView.textSize = DevicesUtils.px2dip(EnvironmentSingleton.instance.candidateTextSize) * 0.8f
            tVSdb = view.findViewById(R.id.tv_Sdb)
            tVSdb.setTextColor(activeTheme.keyTextColor)
        }
    }
}
