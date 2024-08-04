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
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import com.yuyan.imemodule.utils.KeyboardLoaderUtil
import splitties.dimensions.dp
import splitties.views.dsl.constraintlayout.endOfParent
import splitties.views.dsl.constraintlayout.lParams
import splitties.views.dsl.constraintlayout.startOfParent
import splitties.views.dsl.constraintlayout.topOfParent
import splitties.views.dsl.core.add

class KeyboardPreviewView(context: Context) : RelativeLayout(context) {
    var intrinsicWidth = 0
        private set
    var intrinsicHeight = 0
        private set
    private var qwerTextContainer: TextKeyboard? = null
    private fun initView() {
        removeAllViews()
        val mSkbRoot = LayoutInflater.from(context).inflate(R.layout.sdk_skb_container, this, false)
        val previewUi = mSkbRoot.findViewById<RelativeLayout>(R.id.skb_input_keyboard_view)
        qwerTextContainer = TextKeyboard(context)
        val softKeyboard = KeyboardLoaderUtil.instance.getSoftKeyboard(AppPrefs.getInstance().internal.inputDefaultMode.getValue()  and InputModeSwitcherManager.MASK_SKB_LAYOUT)
        qwerTextContainer!!.setSoftKeyboard(softKeyboard)
        previewUi.addView(qwerTextContainer, LayoutParams(instance.skbWidth, instance.skbHeight))
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
