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
    private val inflater: LayoutInflater
    private var mOnItemClickListener: OnRecyclerItemClickListener? = null
    private var mActiveCandNo:Int = 0
    fun setOnItemClickLitener(mOnItemClickLitener: OnRecyclerItemClickListener?) {
        mOnItemClickListener = mOnItemClickLitener
    }

    init {
        mActiveCandNo = 0
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolHolder {
        val view = inflater.inflate(R.layout.sdk_item_recyclerview_float_candidates_bar, parent, false)
        return SymbolHolder(view)
    }

    override fun onBindViewHolder(holder: SymbolHolder, position: Int) {
        if(DictDecoder.isCandidatesEmpty) return
        holder.textViewCandidate.text = DictDecoder.candidates[position].text
        holder.textViewIndex.text = buildString {
            append(position+1)
            append(". ")
        }
        holder.textViewIndex.setTextColor(if(mActiveCandNo-1 == position) activeTheme.accentKeyBackgroundColor else activeTheme.keyTextColor)
        holder.textViewCandidate.setTextColor(if(mActiveCandNo-1 == position) activeTheme.accentKeyBackgroundColor else activeTheme.keyTextColor)
        if (mOnItemClickListener != null) {
            holder.textViewCandidate.setOnClickListener { view: View? ->
                mOnItemClickListener!!.onItemClick(this@FloatCandidatesBarAdapter, view, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return DictDecoder.candidateSize
    }

    fun activeCandidates(activeCandNo:Int) {
        mActiveCandNo = activeCandNo
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
