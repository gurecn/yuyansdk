package com.yuyan.imemodule.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.emoji2.widget.EmojiTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import com.yuyan.imemodule.utils.DevicesUtils.dip2px
import splitties.views.dsl.core.margin

/**
 * 分词界面适配器
 *
 * 展示分词后的词语片段，支持点击多选。
 */
class SegmentWordsAdapter(
    private val context: Context,
    private val tokens: List<String>
) : RecyclerView.Adapter<SegmentWordsAdapter.WordHolder>() {

    private val selectedPositions = mutableSetOf<Int>()
    private val textColor = activeTheme.keyTextColor
    private val selectedTextColor = activeTheme.keyBackgroundColor // contrast against accent bg
    private val marginValue = dip2px(3)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordHolder {
        val container = RelativeLayout(context).apply {
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = GridLayoutManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(marginValue, marginValue, marginValue, marginValue)
            }
        }
        val textView = EmojiTextView(context).apply {
            id = R.id.clipboard_adapter_content
            // 单行显示；长 token（URL）用中间省略，头尾可见便于辨认
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.MIDDLE
            gravity = Gravity.CENTER
            textSize = instance.candidateTextSize.toFloat()
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                margin = marginValue
                addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            }
        }
        container.addView(textView)
        return WordHolder(container)
    }

    override fun onBindViewHolder(holder: WordHolder, position: Int) {
        val isSelected = selectedPositions.contains(position)
        holder.textView.text = tokens[position]
        holder.textView.setTextColor(if (isSelected) selectedTextColor else textColor)
        holder.container.background = GradientDrawable().apply {
            setColor(if (isSelected) activeTheme.accentKeyBackgroundColor else activeTheme.keyBackgroundColor)
            shape = GradientDrawable.RECTANGLE
            cornerRadius = ThemeManager.prefs.keyRadius.getValue().toFloat()
        }
    }

    override fun getItemCount(): Int = tokens.size

    fun toggleSelection(position: Int) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(position)
        } else {
            selectedPositions.add(position)
        }
        notifyItemChanged(position)
    }

    fun getSelectedPositions(): Set<Int> = selectedPositions.toSet()

    fun clearSelection() {
        val previous = selectedPositions.toList()
        selectedPositions.clear()
        previous.forEach { notifyItemChanged(it) }
    }

    inner class WordHolder(val container: RelativeLayout) : RecyclerView.ViewHolder(container) {
        val textView: EmojiTextView = container.findViewById(R.id.clipboard_adapter_content)
    }
}
