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
class CandidatesAdapter(context: Context?) :
    RecyclerView.Adapter<CandidatesAdapter.SymbolHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val textColor: Int = activeTheme.keyTextColor
    private var mOnItemClickListener: OnRecyclerItemClickListener? = null
    fun setOnItemClickLitener(mOnItemClickLitener: OnRecyclerItemClickListener?) {
        mOnItemClickListener = mOnItemClickLitener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolHolder {
        val view = inflater.inflate(R.layout.sdk_item_recyclerview_candidates, parent, false)
        return SymbolHolder(view)
    }

    override fun onBindViewHolder(holder: SymbolHolder, position: Int) {
        holder.textView.text = DecodingInfo.candidates[position].text
        holder.textView.setOnClickListener { view: View? ->
            mOnItemClickListener?.onItemClick(this@CandidatesAdapter, view, position)
        }
    }

    override fun getItemCount(): Int {
        return DecodingInfo.candidateSize
    }

    inner class SymbolHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView: EmojiTextView = view.findViewById(R.id.gv_candidates_item)

        init {
            textView.setTextColor(textColor)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, instance.candidateTextSize)
        }
    }
}
