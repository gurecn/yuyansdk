package com.yuyan.imemodule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.callback.OnRecyclerItemClickListener
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import com.yuyan.imemodule.utils.DevicesUtils.px2dip
import com.yuyan.inputmethod.core.CandidateListItem

/**
 * 候选词界面适配器
 */
class CandidatesBarAdapter(context: Context?, datas: List<CandidateListItem?>) :
    RecyclerView.Adapter<CandidatesBarAdapter.SymbolHolder>() {
//    private var mCandidateTextSize = 0
    private var mDatas: List<CandidateListItem?>
    private val inflater: LayoutInflater
    private var textColor: Int
    private var mOnItemClickListener: OnRecyclerItemClickListener? = null
    fun setOnItemClickLitener(mOnItemClickLitener: OnRecyclerItemClickListener?) {
        mOnItemClickListener = mOnItemClickLitener
    }

    fun updateTextColor(textColor: Int) {
        this.textColor = textColor
    }

    init {
        mDatas = datas
        val theme = activeTheme
        textColor = theme.keyTextColor
        inflater = LayoutInflater.from(context)
//        mCandidateTextSize = instance.candidateTextSize
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolHolder {
        val view = inflater.inflate(R.layout.sdk_item_recyclerview_candidates_bar, parent, false)
        return SymbolHolder(view)
    }

    override fun onBindViewHolder(holder: SymbolHolder, position: Int) {
        holder.textView.text = mDatas[position]?.text
        if (mOnItemClickListener != null) {
            holder.textView.setOnClickListener { view: View? ->
                mOnItemClickListener!!.onItemClick(
                    this@CandidatesBarAdapter,
                    view,
                    position
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    inner class SymbolHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView: TextView

        init {
            textView = view.findViewById(R.id.gv_item)
            textView.setTextColor(textColor)
            textView.textSize = px2dip(instance.candidateTextSize.toFloat()).toFloat()
        }
    }
}
