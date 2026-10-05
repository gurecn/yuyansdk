package com.yuyan.imemodule.view

import android.view.View
import android.widget.RelativeLayout
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.database.entry.Phrase

/**
 * 常用语编辑器面板。
 */
class PhraseEditorPanel(private val container: RelativeLayout, private val anchor: View) {
    var isShowing = false
        private set

    private var editView: EditPhrasesView? = null

    val visibleView: EditPhrasesView?
        get() = editView?.takeIf { isShowing }

    /**
     * 显示编辑器并填充内容。
     */
    fun show(phrase: Phrase?): EditPhrasesView {
        val view = ensureView()
        isShowing = true
        view.visibility = View.VISIBLE
        if (phrase != null) view.setExtraData(phrase) else view.clearPhrasesContent()
        return view
    }

    /** 隐藏并保存当前内容 */
    fun hideAndSave() {
        val view = editView ?: return
        isShowing = false
        view.clearEditorFocus()
        view.visibility = View.GONE
        view.addPhrasesHandle()
    }

    /** 跟随键盘重排同步显隐 */
    fun syncVisibility() {
        editView?.visibility = if (isShowing) View.VISIBLE else View.GONE
    }

    fun updateTheme(theme: Theme) {
        editView?.updateTheme(theme)
    }

    private fun ensureView(): EditPhrasesView {
        editView?.let { return it }
        val view = EditPhrasesView(container.context).apply {
            visibility = View.GONE
            updateTheme(ThemeManager.activeTheme)
        }
        container.addView(view, RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ABOVE, anchor.id)
            addRule(RelativeLayout.ALIGN_LEFT, anchor.id)
        })
        editView = view
        return view
    }
}
