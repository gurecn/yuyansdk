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
import com.yuyan.imemodule.utils.StringUtils.sbc2dbcCase

/**
 * 拼音选择
 * User:Gaolei  gurecn@gmail.com
 * Date:2017/9/19
 * I'm glad to share my knowledge with you all.
 */
class PrefixAdapter(context: Context?, private val mDatas: Array<String>) :
    RecyclerView.Adapter<PrefixAdapter.SymbolTypeHolder>() {
    private val inflater: LayoutInflater
    private var mOnItemClickListener: OnRecyclerItemClickListener? = null
    private val textColor: Int = activeTheme.keyTextColor
    fun setOnItemClickLitener(mOnItemClickLitener: OnRecyclerItemClickListener?) {
        mOnItemClickListener = mOnItemClickLitener
    }

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolTypeHolder {
        val view = inflater.inflate(R.layout.sdk_item_list_alpha_symbol_noraml, parent, false)
        return SymbolTypeHolder(view)
    }

    override fun onBindViewHolder(holder: SymbolTypeHolder, position: Int) {
        holder.tvSymbolType.text = sbc2dbcCase(mDatas[position])
        if (mOnItemClickListener != null) {
            holder.tvSymbolType.setOnClickListener { v: View? ->
                mOnItemClickListener!!.onItemClick(this@PrefixAdapter, v, position)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    inner class SymbolTypeHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvSymbolType: TextView

        init {
            tvSymbolType = view.findViewById(android.R.id.text1)
            tvSymbolType.setTextColor(textColor)
        }
    }

    fun getSymbolData(position: Int): String {
        return mDatas[position]
    }
}
