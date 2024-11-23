package com.yuyan.imemodule.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.prefs.AppPrefs
import splitties.dimensions.dp
import splitties.resources.color
import splitties.views.dsl.constraintlayout.below
import splitties.views.dsl.constraintlayout.constraintLayout
import splitties.views.dsl.constraintlayout.lParams
import splitties.views.dsl.constraintlayout.startOfParent
import splitties.views.dsl.constraintlayout.topOfParent
import splitties.views.dsl.core.add
import splitties.views.dsl.core.matchParent
import splitties.views.dsl.core.textView
import splitties.views.dsl.core.wrapContent


class FullDisplayKeyboardFragment: Fragment(){

    private lateinit var advancedContainer: LinearLayout
    private lateinit var advancedTip: TextView
    private lateinit var normalContainer: LinearLayout
    private lateinit var normalTip: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = with(requireContext()) {
        advancedTip = textView {
            setText(R.string.keyboard_full_display_advanced_tip)
            gravity = Gravity.CENTER
        }
        advancedContainer = LinearLayout(context).apply {
            setPadding(dp(20))
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER }.apply {
            addView(ImageView(context).apply {
                setImageResource(R.drawable.keyboard_t9_full_display)
                setOnClickListener{ v: View? ->
                    v?.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_anim_reduce_90))
                    AppPrefs.getInstance().internal.fullDisplayKeyboardEnable.setValue(true)
                    updateView()
                }
            }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(100)))
            addView(advancedTip)
        }
        normalTip = textView {
            setText(R.string.keyboard_full_display_normal_tip)
            gravity = Gravity.CENTER
        }
        normalContainer = LinearLayout(context).apply {
            setPadding(dp(20))
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER }.apply {
            addView(ImageView(context).apply {
                setImageResource(R.drawable.keyboard_t9_normal)
                setOnClickListener{ v: View? ->
                    v?.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_anim_reduce_90))
                    AppPrefs.getInstance().internal.fullDisplayKeyboardEnable.setValue(false)
                    updateView()
                }
            }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(100)))
            addView(normalTip)
        }
        val header = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL   }.apply {
            addView(advancedContainer, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(dp(20)) })
            addView(normalContainer, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(dp(20)) })
        }

        val elevation = textView {
            setBackgroundResource(R.color.skb_shadow_icon_color)
        }
        updateView()
        constraintLayout {
            add(header, lParams(width = matchParent, height = wrapContent) {
                topOfParent()
                startOfParent()
                setMargins(dp(20))
            })
            add(elevation, lParams(width = matchParent,height = dp(1)) {
                below(header)
                setMargins(dp(40),dp(20),dp(40), 0)
            })
        }
    }

    private fun updateView(){
        val fullDisplayKeyboardEnable = AppPrefs.getInstance().internal.fullDisplayKeyboardEnable.getValue()
        if(fullDisplayKeyboardEnable){
            advancedContainer.setBackgroundResource(R.drawable.shape_select_rectangle)
            advancedTip.setTextColor(ImeSdkApplication.context.color(R.color.color_1488CC))
            normalContainer.background = null
            normalTip.setTextColor(ImeSdkApplication.context.color(R.color.skb_key_text_color))
        } else {
            normalContainer.setBackgroundResource(R.drawable.shape_select_rectangle)
            normalTip.setTextColor(ImeSdkApplication.context.color(R.color.color_1488CC))
            advancedContainer.background = null
            advancedTip.setTextColor(ImeSdkApplication.context.color(R.color.skb_key_text_color))
        }
    }
}