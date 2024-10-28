package com.yuyan.imemodule.adapter

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
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
class SymbolPagerAdapter(con: Context?, private val mDatas: Map<EmojiconData.Category, List<String>>?, val viewType: Int, private val onClickEmoji: (String, Int) -> Unit) :
    RecyclerView.Adapter<SymbolPagerAdapter.ViewHolder>() {
    private var mPaint : Paint
    private val inflater: LayoutInflater
    private val context: Context?

    init {
        inflater = LayoutInflater.from(con)
        context = con

        mPaint = Paint()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mSymbolAdapter = SymbolAdapter(context, viewType, onClickEmoji)
        val emojiGroupRv: RecyclerView = view.findViewById<RecyclerView>(R.id.emojiGroupRv).apply {
            mPaint.textSize = dp(20f)
            adapter = mSymbolAdapter
        }
    }

    private val mHashMapSymbols = HashMap<Int, Int>() //候选词索引列数对应表
    /**
     * 计算符号列表实际所占列数
     */
    private fun calculateColumn(data: List<String>) {
        mHashMapSymbols.clear()
        val itemWidth = EnvironmentSingleton.instance.skbWidth/16
        var mCurrentColumn = 0
        for (position in data.indices) {
            val candidate = data[position]
            var count = getSymbolsCount(candidate, itemWidth)
            var nextCount = 0
            if (data.size > position + 1) {
                val nextCandidate = data[position + 1]
                nextCount = getSymbolsCount(nextCandidate, itemWidth)
            }
            if (mCurrentColumn + count + nextCount > 8) {
                count = 8 - mCurrentColumn
                mCurrentColumn = 0
            } else {
                mCurrentColumn = (mCurrentColumn + count) % 8
            }
            mHashMapSymbols[position] = count
        }
    }

    /**
     * 根据词长计算当前候选词需占的列数
     */
    private fun getSymbolsCount(data: String, itemWidth:Int): Int {
        return if (!TextUtils.isEmpty(data)) {
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
        return ViewHolder(inflater.inflate(R.layout.sdk_item_pager_symbols_emoji, parent, false))
    }

    override fun getItemCount(): Int {
        return mDatas?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mDatas?.get(mDatas.keys.toList()[position])
        holder.apply {
            (emojiGroupRv.layoutManager as GridLayoutManager).apply {
                spanCount = 8
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(i: Int): Int {
                        return mHashMapSymbols[i] ?: 1
                    }
                }
            }
            mSymbolAdapter.apply {
                calculateColumn(item!!)
                mDatas = item
                notifyDataSetChanged()
            }
        }
    }
}
