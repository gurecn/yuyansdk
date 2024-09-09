package com.yuyan.imemodule.adapter

import android.content.Context
import android.graphics.drawable.VectorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.callback.OnRecyclerItemClickListener
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.entity.SkbFunItem
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton

/**
 * 候选词界面适配器
 */
class CandidatesMenuAdapter(context: Context?, var items: MutableList<SkbFunItem>) : RecyclerView.Adapter<CandidatesMenuAdapter.SymbolHolder>() {
    private val inflater: LayoutInflater
    private var mOnItemClickListener: OnRecyclerItemClickListener? = null
    private val textColor: Int = activeTheme.keyTextColor
    private val itemHeight: Int
    fun setOnItemClickLitener(mOnItemClickLitener: OnRecyclerItemClickListener?) {
        mOnItemClickListener = mOnItemClickLitener
    }

    init {
        inflater = LayoutInflater.from(context)
        itemHeight = EnvironmentSingleton.instance.heightForCandidates
    }

    inner class SymbolHolder(view: View) : RecyclerView.ViewHolder(view) {
        var entranceIconImageView: ImageView? = null
        init {
            entranceIconImageView = itemView.findViewById(R.id.candidates_menu_item)
            val layoutParams = itemView.layoutParams
            layoutParams.width = itemHeight
            layoutParams.height = itemHeight
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidatesMenuAdapter.SymbolHolder {
        val view = inflater.inflate(R.layout.sdk_item_recyclerview_candidates_menu, parent, false)
        return SymbolHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CandidatesMenuAdapter.SymbolHolder, position: Int) {
        val item = items[position]
        holder.entranceIconImageView?.setImageResource(item.funImgRecource)
        val vectorDrawableCompat = holder.entranceIconImageView?.getDrawable() as VectorDrawable
        vectorDrawableCompat.setTint(textColor)
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener { v: View? ->
                mOnItemClickListener!!.onItemClick(this, v, position)
            }
        }
    }
    fun setData(data: MutableList<SkbFunItem>) {
        this.items = data
    }

    fun getMenuMode(position: Int): SkbMenuMode {
        return items[position].skbMenuMode
    }
}