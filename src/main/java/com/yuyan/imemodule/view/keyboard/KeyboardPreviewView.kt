package com.yuyan.imemodule.view.keyboard

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.yuyan.imemodule.R
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager.prefs
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import com.yuyan.imemodule.view.keyboard.container.InputBaseContainer
import com.yuyan.imemodule.view.keyboard.container.QwertyTextContainer

class KeyboardPreviewView(context: Context) : RelativeLayout(context) {
    var intrinsicWidth = 0
        private set
    var intrinsicHeight = 0
        private set
    private var qwerTextContainer: InputBaseContainer? = null
    private fun initView() {
        removeAllViews()
        val mSkbRoot = LayoutInflater.from(context).inflate(R.layout.sdk_skb_container, this, false)
        val previewUi = mSkbRoot.findViewById<RelativeLayout>(R.id.skb_input_keyboard_view)
        qwerTextContainer = QwertyTextContainer(context, null, InputModeSwitcherManager.MASK_SKB_LAYOUT_QWERTY_PINYIN)
        qwerTextContainer?.updateSkbLayout()
        previewUi.addView(qwerTextContainer)
        addView(mSkbRoot)
        intrinsicWidth = instance.skbWidth
        intrinsicHeight = instance.skbHeight
        previewUi.requestLayout()
    }

    fun setTheme(theme: Theme, background: Drawable) {
        qwerTextContainer?.setTheme(theme)
        setBackground(background)
    }

    fun setTheme(theme: Theme) {
        initView()
        qwerTextContainer?.setTheme(theme)
        background = theme.backgroundGradientDrawable(prefs.keyBorder.getValue())
        layoutParams?.width = instance.skbWidth
        layoutParams?.height = instance.inputAreaHeight
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (onSizeMeasured != null) onSizeMeasured!!.invoke(intrinsicWidth, intrinsicHeight)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (onSizeMeasured != null) onSizeMeasured!!.invoke(intrinsicWidth, intrinsicHeight)
    }

    var onSizeMeasured: Function2<Int, Int, Unit>? = null

    init {
        initView()
    }
}
