

package com.yuyan.imemodule.view.widget

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import splitties.views.bottomPadding

fun RecyclerView.applyNavBarInsetsBottomPadding() {
    clipToPadding = false
    ViewCompat.setOnApplyWindowInsetsListener(this) { _, windowInsets ->
        windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).also {
            bottomPadding = it.bottom
        }
        windowInsets
    }
}
