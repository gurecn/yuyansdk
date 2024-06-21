package com.yuyan.imemodule.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.callback.OnRecyclerItemClickListener
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.entity.ClipBoardDataBean
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import com.yuyan.imemodule.utils.DevicesUtils.dip2px
import com.yuyan.imemodule.utils.DevicesUtils.px2dip
import com.yuyan.imemodule.utils.LogUtil.d
import splitties.views.dsl.core.margin

/**
 * 剪切板界面适配器
 */
class ClipBoardAdapter(context: Context, datas: List<ClipBoardDataBean>) :
    RecyclerView.Adapter<ClipBoardAdapter.SymbolHolder>() {
    private var mDatas: List<ClipBoardDataBean>
    private val mContext: Context
    private var textColor: Int
    private var mOnItemClickListener: OnRecyclerItemClickListener? = null
    fun setOnItemClickLitener(mOnItemClickLitener: OnRecyclerItemClickListener?) {
        mOnItemClickListener = mOnItemClickLitener
    }

    init {
        d("CandidatesAdapter", "CandidatesAdapter")
        mDatas = datas
        val theme = activeTheme
        textColor = theme.keyTextColor
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolHolder {
        val view = TextView(mContext)
        val marginValue = dip2px(3)
        view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            margin = marginValue
        }
        val paddingValue = dip2px(5)
        view.setPadding(paddingValue, paddingValue, paddingValue ,paddingValue)
        view.background = GradientDrawable().apply {
            setColor(activeTheme.keyBackgroundColor)
            setShape(GradientDrawable.RECTANGLE)
            setCornerRadius(ThemeManager.prefs.keyRadius.getValue().toFloat()) // 设置圆角半径
        }
        return SymbolHolder(view)
    }

    override fun onBindViewHolder(holder: SymbolHolder, position: Int) {
        holder.textView.text = mDatas[position].copyContent
        if (mOnItemClickListener != null) {
            holder.textView.setOnClickListener { view: View? ->
                mOnItemClickListener!!.onItemClick(
                    this@ClipBoardAdapter,
                    view,
                    position
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    inner class SymbolHolder(view: TextView) : RecyclerView.ViewHolder(view) {
        var textView: TextView

        init {
            textView = view
            textView.setTextColor(textColor)
            textView.textSize = px2dip(instance.candidateTextSize.toFloat()).toFloat()
        }
    }
}
