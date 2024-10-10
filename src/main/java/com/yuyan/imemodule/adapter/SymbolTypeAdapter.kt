package com.yuyan.imemodule.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.emoji.widget.EmojiAppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.callback.OnRecyclerItemClickListener
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.data.emojicon.EmojiconData

/**
 * 符号，表情底部导航栏Adapter
 */
class SymbolTypeAdapter(context: Context?, private val mDatas: List<EmojiconData.Category>, showType: Int) :
    RecyclerView.Adapter<SymbolTypeAdapter.SymbolTypeHolder>() {
    private val inflater: LayoutInflater
    private val keyBackground: GradientDrawable
    private val pressKeyBackground: GradientDrawable
    private var mOnItemClickListener: OnRecyclerItemClickListener? = null
    private var isClicks = 0
    private val mTheme: Theme

    init {
        inflater = LayoutInflater.from(context)
        isClicks = showType
        mTheme = activeTheme
        val isKeyBorder = ThemeManager.prefs.keyBorder.getValue()
        keyBackground = GradientDrawable()
        pressKeyBackground = GradientDrawable()
        if (isKeyBorder) {
            val mActiveTheme = activeTheme
            val keyRadius = ThemeManager.prefs.keyRadius.getValue()
            keyBackground.setColor(mActiveTheme.keyBackgroundColor)
            keyBackground.setShape(GradientDrawable.RECTANGLE)
            keyBackground.setCornerRadius(keyRadius.toFloat()) // 设置圆角半径
            pressKeyBackground.setColor(mActiveTheme.genericActiveBackgroundColor)
            pressKeyBackground.setShape(GradientDrawable.RECTANGLE)
            pressKeyBackground.setCornerRadius(keyRadius.toFloat()) // 设置圆角半径
        }
    }

    fun setOnItemClickLitener(mOnItemClickLitener: OnRecyclerItemClickListener?) {
        mOnItemClickListener = mOnItemClickLitener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolTypeHolder {
        val view = inflater.inflate(R.layout.sdk_item_recycler_symbol_type, parent, false) as EmojiAppCompatTextView
        return SymbolTypeHolder(view)
    }

    override fun onBindViewHolder(holder: SymbolTypeHolder, position: Int) {
        val tvSymbolType = holder.itemView as EmojiAppCompatTextView
        tvSymbolType.setTextColor(mTheme.keyTextColor)
        tvSymbolType.text = mDatas[position].name
        tvSymbolType.background = if (isClicks == position) pressKeyBackground else keyBackground
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener { v: View? ->
                isClicks = holder.getBindingAdapterPosition()
                notifyDataSetChanged()
                mOnItemClickListener!!.onItemClick(this@SymbolTypeAdapter, v, isClicks) //索引即符号在SymbolsManager中的key
            }
        }
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    class SymbolTypeHolder(view: EmojiAppCompatTextView?) : RecyclerView.ViewHolder(
        view!!
    )
}
