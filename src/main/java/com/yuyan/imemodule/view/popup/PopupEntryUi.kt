
package com.yuyan.imemodule.view.popup

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.ViewOutlineProvider
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import splitties.dimensions.dp
import splitties.views.dsl.core.Ui
import splitties.views.dsl.core.add
import splitties.views.dsl.core.frameLayout
import splitties.views.dsl.core.lParams
import splitties.views.dsl.core.view
import splitties.views.gravityCenter

class PopupEntryUi(override val ctx: Context) : Ui {

    var lastShowTime = -1L

    val textView = view(::AutoScaleTextView) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, EnvironmentSingleton.instance.keyTextSize * 1.2f)
        scaleMode = AutoScaleTextView.Mode.Proportional
    }

    override val root = frameLayout {
        outlineProvider = ViewOutlineProvider.BACKGROUND
        elevation = dp(2f)
        add(textView, lParams {
            gravity = gravityCenter
        })
    }

    fun setText(text: String) {
        textView.text = text
    }
    fun setBackground( theme: Theme, radius: Float) {
        root.background = GradientDrawable().apply {
            cornerRadius = radius
            setColor(theme.popupBackgroundColor)
        }
        textView.setTextColor(theme.keyTextColor)
    }
}
