package com.yuyan.imemodule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.emoji2.widget.EmojiTextView
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.callback.OnRecyclerItemClickListener
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.service.DecodingInfo
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.DevicesUtils

/**
 * 候选词界面适配器
 */
class CandidatesAdapter(context: Context?, candidatesStart: Int) :
    RecyclerView.Adapter<CandidatesAdapter.SymbolHolder>() {
    private val candidatesStart: Int
    private var mCandidateTextSize = 0
    private val inflater: LayoutInflater
    private val textColor: Int
    private var mOnItemClickListener: OnRecyclerItemClickListener? = null
    fun setOnItemClickLitener(mOnItemClickLitener: OnRecyclerItemClickListener?) {
        mOnItemClickListener = mOnItemClickLitener
    }

    private fun updateCandidateTextSize() {
        mCandidateTextSize = EnvironmentSingleton.instance.candidateTextSize
    }

    init {
        this.candidatesStart = candidatesStart
        textColor = activeTheme.keyTextColor
        inflater = LayoutInflater.from(context)
        updateCandidateTextSize()
    }

    fun updateData(num: Int) {
        val startIndex = itemCount - num - 1
        notifyItemRangeInserted(startIndex, num)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolHolder {
        val view = inflater.inflate(R.layout.sdk_item_recyclerview_candidates, parent, false)
        return SymbolHolder(view)
    }

    override fun onBindViewHolder(holder: SymbolHolder, position: Int) {
        val realPos = position + candidatesStart
        if (realPos < DecodingInfo.candidateSize) {
            holder.textView.text = DecodingInfo.candidates[realPos]?.text
        } else {
            holder.textView.text = "          "
        }
        holder.textView.setOnClickListener { view: View? ->
            mOnItemClickListener?.onItemClick(this@CandidatesAdapter, view, realPos)
        }
    }

    override fun getItemCount(): Int {
        return DecodingInfo.candidateSize + 1 - candidatesStart
    }

    inner class SymbolHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView: EmojiTextView
        init {
            textView = view.findViewById(R.id.gv_item)
            textView.setTextColor(textColor)
            textView.textSize = DevicesUtils.px2dip(mCandidateTextSize.toFloat()).toFloat()
        }
    }

    fun getItem(position: Int): Int {
        return position
    }
}
