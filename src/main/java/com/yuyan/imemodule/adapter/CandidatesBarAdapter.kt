package com.yuyan.imemodule.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.emoji2.widget.EmojiTextView
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.callback.OnRecyclerItemClickListener
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.service.DecodingInfo
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance

/**
 * 候选词界面适配器
 */
class CandidatesBarAdapter(context: Context?) :
    RecyclerView.Adapter<CandidatesBarAdapter.SymbolHolder>() {
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
        val view = inflater.inflate(R.layout.sdk_item_recyclerview_candidates_bar, parent, false)
        return SymbolHolder(view)
    }

    override fun onBindViewHolder(holder: SymbolHolder, position: Int) {
        if(DecodingInfo.isCandidatesListEmpty) return
        holder.textView.text = DecodingInfo.candidates[position].text
        holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, instance.candidateTextSize)
        holder.textView.setTextColor(if(mActiveCandNo-1 == position) activeTheme.accentKeyBackgroundColor else activeTheme.keyTextColor)
        if (mOnItemClickListener != null) {
            holder.textView.setOnClickListener { view: View? ->
                mOnItemClickListener!!.onItemClick(this@CandidatesBarAdapter, view, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return DecodingInfo.candidateSize
    }

    fun activeCandidates(activeCandNo:Int) {
        mActiveCandNo = activeCandNo
    }

    fun notifyChanged() {
        notifyDataSetChanged()
    }

    inner class SymbolHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView: EmojiTextView = view.findViewById(R.id.gv_candidates_bar_item)
        init {
            textView.setTextColor(activeTheme.keyTextColor)
        }
    }
}
