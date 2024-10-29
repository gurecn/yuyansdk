package com.yuyan.imemodule.adapter

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.LauncherModel
import com.yuyan.imemodule.data.emojicon.EmojiconData
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import splitties.dimensions.dp
import kotlin.math.ceil

/**
 * 表情或符号界面适配器
 * User:Gaolei  gurecn@gmail.com
 * Date:2017/7/20
 * I'm glad to share my knowledge with you all.
 */
class SymbolPagerAdapter(context: Context?, private val mDatas: Map<EmojiconData.Category, List<String>>, val viewType: Int, private val onClickSymbol: (String, Int) -> Unit) :
    RecyclerView.Adapter<SymbolPagerAdapter.ViewHolder>() {
    private var mPaint : Paint
    private val mContext: Context?

    init {
        mContext = context
        mPaint = Paint()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mSymbolAdapter = SymbolAdapter(mContext, viewType, onClickSymbol)
        val emojiGroupRv: RecyclerView = view.findViewById<RecyclerView>(R.id.emojiGroupRv).apply {
            mPaint.textSize = dp(20f)
            adapter = mSymbolAdapter
        }
    }

    private val mHashMapSymbols = HashMap<Int, List<Int>>() //候选符号列数对应表
    /**
     * 计算符号列表实际所占列数
     */
    private fun calculateColumn(index: Int, data: List<String>) {
        val columns = mutableListOf<Int>()
        val itemWidth = EnvironmentSingleton.instance.skbWidth/16
        var mCurrentColumn = 0
        for (position in data.indices) {
            var count = getSymbolsCount(data[position], itemWidth)
            var nextCount = 0
            if (data.size > position + 1) {
                nextCount = getSymbolsCount(data[position + 1], itemWidth)
            }
            if (mCurrentColumn + count + nextCount > 8) {
                count = 8 - mCurrentColumn
                mCurrentColumn = 0
            } else {
                mCurrentColumn = (mCurrentColumn + count) % 8
            }
            columns.add(count)
        }
        mHashMapSymbols[index] = columns
    }

    /**
     * 根据词长计算当前候选词需占的列数
     */
    private fun getSymbolsCount(data: String, itemWidth:Int): Int {
        return if (data.isNotBlank()) {
            val bounds = Rect()
            mPaint.getTextBounds(data, 0, data.length, bounds)
            val x = ceil(bounds.width().toFloat().div(itemWidth)).toInt()
            if(x >= 8) 8
            else if(x >= 4) 4
            else if(x > 1) 2
            else  1
        } else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.sdk_item_pager_symbols_emoji, parent, false))
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val key = mDatas.keys.toList()[position]
        val item = if(key.label == "🕝") { LauncherModel.instance.usedEmojiDao!!.allUsedEmoji} else mDatas[key]
        holder.apply {
            (emojiGroupRv.layoutManager as GridLayoutManager).apply {
                spanCount = 8
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(pos: Int): Int {
                        return mHashMapSymbols[holder.getBindingAdapterPosition()]!![pos]
                    }
                }
            }
            mSymbolAdapter.apply {
                calculateColumn(position, item!!)
                mDatas = item
                notifyDataSetChanged()
            }
        }
    }
}
