package com.yuyan.imemodule.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.emoji2.widget.EmojiTextView
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.callback.OnRecyclerItemClickListener
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.service.DictDecoder
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance

/**
 * 候选词界面适配器
 */
class FloatCandidatesBarAdapter(context: Context?) :
    RecyclerView.Adapter<FloatCandidatesBarAdapter.SymbolHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var mOnItemClickListener: OnRecyclerItemClickListener? = null
    private var activeCandidatePosition = RecyclerView.NO_POSITION
    private var firstVisiblePosition = 0
    fun setOnItemClickLitener(mOnItemClickLitener: OnRecyclerItemClickListener?) {
        mOnItemClickListener = mOnItemClickLitener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolHolder {
        val view = inflater.inflate(R.layout.sdk_item_recyclerview_float_candidates_bar, parent, false)
        return SymbolHolder(view)
    }

    override fun onBindViewHolder(holder: SymbolHolder, position: Int) {
        if(DictDecoder.isCandidatesEmpty) return
        holder.textViewCandidate.text = DictDecoder.candidates[position].text
        bindCandidateAppearance(holder, position)
        if (mOnItemClickListener != null) {
            holder.textViewCandidate.setOnClickListener { view: View? ->
                mOnItemClickListener!!.onItemClick(this@FloatCandidatesBarAdapter, view, position)
            }
        }
    }

    fun updateVisibleIndices(recyclerView: RecyclerView) {
        for (i in 0 until recyclerView.childCount) {
            val holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i)) as? SymbolHolder ?: continue
            val position = holder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                bindCandidateAppearance(holder, position)
            }
        }
    }

    private fun bindCandidateAppearance(holder: SymbolHolder, position: Int) {
        val indexText = "${(position - firstVisiblePosition + 1).coerceAtLeast(1)}. "
        if (holder.textViewIndex.text.toString() != indexText) {
            holder.textViewIndex.text = indexText
        }
        val textColor = if (displayedActivePosition == position) {
            activeTheme.accentKeyBackgroundColor
        } else {
            activeTheme.keyTextColor
        }
        holder.textViewIndex.setTextColor(textColor)
        holder.textViewCandidate.setTextColor(textColor)
    }

    override fun getItemCount(): Int {
        return DictDecoder.candidateSize
    }

    fun updateCandidateState(firstVisiblePosition: Int, activeCandidatePosition: Int) {
        this.firstVisiblePosition = firstVisiblePosition
        this.activeCandidatePosition = activeCandidatePosition
    }

    private val displayedActivePosition: Int
        get() = if (activeCandidatePosition != RecyclerView.NO_POSITION) {
            activeCandidatePosition
        } else {
            firstVisiblePosition
        }

    fun notifyChanged() {
        notifyDataSetChanged()
    }

    class SymbolHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewIndex: TextView = view.findViewById(R.id.tv_candidates_index)
        var textViewCandidate: EmojiTextView = view.findViewById(R.id.gv_candidates_bar_item)
        init {
            textViewIndex.setTextColor(activeTheme.keyTextColor)
            textViewCandidate.setTextColor(activeTheme.keyTextColor)
            textViewIndex.setTextSize(TypedValue.COMPLEX_UNIT_DIP, instance.candidateTextSize * 0.7f)
            textViewCandidate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, instance.candidateTextSize * 0.7f)
        }
    }
}
