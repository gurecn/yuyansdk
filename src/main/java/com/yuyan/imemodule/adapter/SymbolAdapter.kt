package com.yuyan.imemodule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.callback.OnRecyclerItemClickListener
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.utils.StringUtils.sbc2dbcCase

/**
 * 表情或符号界面适配器
 * User:Gaolei  gurecn@gmail.com
 * Date:2017/7/20
 * I'm glad to share my knowledge with you all.
 */
class SymbolAdapter(context: Context?, private val mDatas: Array<String>, val viewType: Int) :
    RecyclerView.Adapter<SymbolAdapter.SymbolHolder>() {
    private val textColor: Int
    private val inflater: LayoutInflater
    private var mOnItemClickListener: OnRecyclerItemClickListener? = null
    fun setOnItemClickLitener(mOnItemClickLitener: OnRecyclerItemClickListener?) {
        mOnItemClickListener = mOnItemClickLitener
    }

    init {
        val theme = activeTheme
        textColor = theme.keyTextColor
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolHolder {
        val view = inflater.inflate(R.layout.sdk_item_recyclerview_symbols_emoji, parent, false)
        return SymbolHolder(view)
    }

    override fun onBindViewHolder(holder: SymbolHolder, position: Int) {
        if (viewType == 0) {
            holder.textView.text = sbc2dbcCase(getItem(position)) // 中文符号显示半角
        } else {
            holder.textView.text = getItem(position)
        }
        if (mOnItemClickListener != null) {
            holder.textView.setOnClickListener { view: View? ->
                mOnItemClickListener!!.onItemClick(
                    this@SymbolAdapter,
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
            if (viewType == CustomConstant.EMOJI_TYPR_FACE_DATA) {
                textView.setPadding(0, 0, 0, 0)
            }
        }
    }

    fun getItem(position: Int): String {
        return mDatas[position]
    }
}
