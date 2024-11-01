package com.yuyan.imemodule.view.widget

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

open class LifecycleRelativeLayout(context: Context)  : RelativeLayout(context), LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        initParentViewModelIfNeeded()
    }

    open fun initParentViewModelIfNeeded() {}

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        if (visibility == VISIBLE) {
            lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        } else {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }
    }

    override val lifecycle: Lifecycle = lifecycleRegistry
}