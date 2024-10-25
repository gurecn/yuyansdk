package com.yuyan.imemodule.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.emoji2.widget.EmojiTextView
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.callback.OnRecyclerItemClickListener
import com.yuyan.imemodule.data.emojicon.EmojiconData
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme

/**
 * 符号，表情底部导航栏Adapter
 */
class SymbolTypeAdapter(context: Context?, private val mDatas: List<EmojiconData.Category>, showType: Int) :
    RecyclerView.Adapter<SymbolTypeAdapter.SymbolTypeHolder>() {
    private val inflater: LayoutInflater
    private val pressKeyBackground: GradientDrawable
    private var mOnItemClickListener: OnRecyclerItemClickListener? = null
    private var isClicks = 0
    private val mTheme: Theme

    init {
        inflater = LayoutInflater.from(context)
        isClicks = showType
        mTheme = activeTheme
        val isKeyBorder = ThemeManager.prefs.keyBorder.getValue()
        pressKeyBackground = GradientDrawable()
        if (isKeyBorder) {
            val mActiveTheme = activeTheme
            val keyRadius = ThemeManager.prefs.keyRadius.getValue()
            pressKeyBackground.setColor(mActiveTheme.genericActiveBackgroundColor)
            pressKeyBackground.setShape(GradientDrawable.RECTANGLE)
            pressKeyBackground.setCornerRadius(keyRadius.toFloat()) // 设置圆角半径
        }
    }

    fun setOnItemClickLitener(mOnItemClickLitener: OnRecyclerItemClickListener?) {
        mOnItemClickListener = mOnItemClickLitener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolTypeHolder {
        val view = inflater.inflate(R.layout.sdk_item_recycler_symbol_type, parent, false)
        return SymbolTypeHolder(view)
    }

    override fun onBindViewHolder(holder: SymbolTypeHolder, position: Int) {
        val category = mDatas[position]
        if(category.icon != 0){
            holder.imageView.setImageResource(category.icon )
            holder.imageView.visibility = View.VISIBLE
            holder.textView.visibility = View.GONE
        } else {
            holder.textView.text = category.label
            holder.textView.visibility = View.VISIBLE
            holder.imageView.visibility = View.GONE
        }
        holder.itemView.background = if (isClicks == position) pressKeyBackground else null
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

    inner class SymbolTypeHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView: EmojiTextView
        var imageView: ImageView
        init {
            textView = view.findViewById(R.id.tv_recycler_symbol_type)
            imageView = view.findViewById(R.id.iv_recycler_symbol_type)
            textView.setTextColor(mTheme.keyTextColor)
            imageView.drawable.setTint(mTheme.keyTextColor)
        }
    }
}
