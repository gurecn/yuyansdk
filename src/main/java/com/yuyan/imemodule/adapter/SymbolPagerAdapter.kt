package com.yuyan.imemodule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.JustifyContent
import com.yuyan.imemodule.R
import com.yuyan.imemodule.data.emojicon.EmojiconData
import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.prefs.behavior.SymbolMode
import com.yuyan.imemodule.view.keyboard.manager.CustomFlexboxLayoutManager

/**
 * 表情或符号界面适配器
 */
class SymbolPagerAdapter(context: Context, private val mDatas: Map<EmojiconData.Category, List<String>>, val viewType: SymbolMode, private val onClickSymbol: (String, Int) -> Unit) :
    RecyclerView.Adapter<SymbolPagerAdapter.ViewHolder>() {
    private val mContext: Context

    init {
        mContext = context
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val emojiGroupRv: RecyclerView = view.findViewById(R.id.emojiGroupRv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.sdk_item_pager_symbols_emoji, parent, false))
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val key = mDatas.keys.toList()[position]

        val item = when (key.label) {
            "🕝" -> { DataBaseKT.instance.usedSymbolDao().getAllSymbolEmoji().map{ it.symbol }.takeIf { it.isNotEmpty() }?:mDatas[mDatas.keys.toList()[1]]}
            "最近" -> { DataBaseKT.instance.usedSymbolDao().getAllUsedSymbol().map{ it.symbol }.takeIf { it.isNotEmpty() }?:mDatas[mDatas.keys.toList()[1]] }
            else -> mDatas[key]
        }
        val manager = CustomFlexboxLayoutManager(mContext)
        manager.flexDirection = FlexDirection.ROW
        manager.flexWrap = FlexWrap.WRAP
        manager.justifyContent = JustifyContent.SPACE_AROUND
        holder.emojiGroupRv.layoutManager = manager
        val mSymbolAdapter = SymbolAdapter(mContext, viewType, position, onClickSymbol)
        mSymbolAdapter.mDatas = item
        holder.emojiGroupRv.adapter = mSymbolAdapter
    }
}
