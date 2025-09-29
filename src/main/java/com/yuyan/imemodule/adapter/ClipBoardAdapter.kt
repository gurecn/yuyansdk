package com.yuyan.imemodule.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.emoji2.widget.EmojiTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.database.entry.Clipboard
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.ClipboardLayoutMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import com.yuyan.imemodule.utils.DevicesUtils.dip2px
import splitties.views.dsl.core.margin

/**
 * 剪切板界面适配器
 */
class ClipBoardAdapter(context: Context, datas: MutableList<Clipboard>) :
    RecyclerView.Adapter<ClipBoardAdapter.SymbolHolder>() {
    private var mDatas : MutableList<Clipboard> = datas
    private val mContext: Context
    private var textColor: Int
    private var clipboardLayoutCompact: ClipboardLayoutMode

    init {
        val theme = activeTheme
        textColor = theme.keyTextColor
        mContext = context
        clipboardLayoutCompact = AppPrefs.getInstance().clipboard.clipboardLayoutCompact.getValue()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolHolder {
        val mContainer = RelativeLayout(mContext)
        mContainer.gravity = Gravity.CENTER_VERTICAL
        val marginValue = dip2px(3)
        when (clipboardLayoutCompact){
            ClipboardLayoutMode.ListView -> {
                mContainer.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(marginValue*2, marginValue, marginValue*2, marginValue)
                }
            }
            else -> {
                mContainer.layoutParams = GridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(marginValue, marginValue, marginValue, marginValue)
                }
            }
        }
        mContainer.background = GradientDrawable().apply {
            setColor(activeTheme.keyBackgroundColor)
            setShape(GradientDrawable.RECTANGLE)
            setCornerRadius(ThemeManager.prefs.keyRadius.getValue().toFloat()) // 设置圆角半径
        }
        val viewContext = EmojiTextView(mContext).apply {
            id = R.id.clipboard_adapter_content
            maxLines = 3
            ellipsize = TextUtils.TruncateAt.END
            gravity = Gravity.CENTER
            layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT).apply {
                margin = marginValue
                addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            }
        }
        val viewIvYopTips = ImageView(mContext).apply {
            id = R.id.clipboard_adapter_top_tips
            setImageResource(R.drawable.ic_baseline_top_tips_32)
            layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT).apply {
                addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
                addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
            }
        }
        mContainer.addView(viewContext)
        mContainer.addView(viewIvYopTips)
        return SymbolHolder(mContainer)
    }

    override fun onBindViewHolder(holder: SymbolHolder, position: Int) {
        val data = mDatas[position]
        holder.textView.text = data.content.replace("\n", "\\n")
        holder.ivTopTips.visibility = if(data.isKeep == 1)View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    inner class SymbolHolder(view: RelativeLayout) : RecyclerView.ViewHolder(view) {
        var textView: TextView = view.findViewById(R.id.clipboard_adapter_content)
        var ivTopTips: ImageView
        init {
            textView.setTextColor(textColor)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, instance.candidateTextSize)
            ivTopTips = view.findViewById(R.id.clipboard_adapter_top_tips)
        }
    }
}
