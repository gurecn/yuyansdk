package com.yuyan.imemodule.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import splitties.dimensions.dp
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.emoji2.widget.EmojiTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.yuyan.imemodule.R
import com.yuyan.imemodule.data.emojicon.YuyanEmojiCompat
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.HalfWidthSymbolsMode
import com.yuyan.imemodule.prefs.behavior.SymbolMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import com.yuyan.imemodule.utils.StringUtils

/**
 * 表情或符号界面适配器
 */
class SymbolAdapter(context: Context?, val viewType: SymbolMode, private val pagerIndex: Int, private val onClickSymbol: (String, Int) -> Unit) :
    RecyclerView.Adapter<SymbolAdapter.SymbolHolder>() {
    private val inflater: LayoutInflater
    var mDatas: List<String>? = null
    private val halfWidthSymbolsMode = AppPrefs.getInstance().keyboardSetting.halfWidthSymbolsMode.getValue()

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolHolder {
        return SymbolHolder(inflater.inflate(R.layout.sdk_item_recyclerview_symbols_emoji, parent, false))
    }

    override fun onBindViewHolder(holder: SymbolHolder, position: Int) {
        val data = mDatas!![position]
        holder.textView.text = data
        holder.tVSdb.visibility = if(viewType != SymbolMode.Symbol) View.GONE else {
            when (halfWidthSymbolsMode) {
                HalfWidthSymbolsMode.All -> if (StringUtils.isDBCSymbol(data)) View.VISIBLE else View.GONE
                HalfWidthSymbolsMode.OnlyUsed -> if (pagerIndex == 0 && StringUtils.isDBCSymbol(data)) View.VISIBLE else View.GONE
                HalfWidthSymbolsMode.None -> View.GONE
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
        var textView: EmojiTextView = view.findViewById(R.id.gv_symbols_item)
        var tVSdb: TextView
        init {
            textView.setTextColor(activeTheme.keyTextColor)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, instance.candidateTextSize * if(viewType != SymbolMode.Emojicon)1f else 0.9f)
            tVSdb = view.findViewById(R.id.tv_sdb_symbols_item)
            tVSdb.setTextColor(activeTheme.keyTextColor)
            if(viewType == SymbolMode.Emojicon && pagerIndex == 1 && YuyanEmojiCompat.isWeChatInput){
                (view.layoutParams as FlexboxLayoutManager.LayoutParams) .apply {
                    width = (EnvironmentSingleton.instance.skbWidth - view.dp(18)) / 3
                   setMargins(view.dp(3))
                }
               val paddingStart =  view.dp(5)
               val paddingTop =  view.dp(10)
                textView.setPadding(paddingStart, paddingTop, paddingStart, paddingTop)
                view.setBackgroundResource(R.drawable.shape_emojicon_background)
            }

        }
    }
}
