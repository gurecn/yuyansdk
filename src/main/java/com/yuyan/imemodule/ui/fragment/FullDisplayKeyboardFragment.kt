package com.yuyan.imemodule.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.yuyan.imemodule.R
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = with(requireContext()) {

        val pinyinContainer = LinearLayout(context).apply {
            setPadding(dp(20))
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER }.apply {
            addView(ImageView(context).apply {
                setImageResource(R.drawable.keyboard_t9_full_display)
                setOnClickListener{ v: View? ->
                    v?.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_anim_reduce_90))
                }
            }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(100)))
            addView(textView {
                setText(R.string.keyboard_full_display_advanced_tip)
                gravity = Gravity.CENTER
                setTextColor(context.color(R.color.color_1488CC))
            })
            setBackgroundResource(R.drawable.shape_select_rectangle)
        }

        val numberContainer = LinearLayout(context).apply {
            setPadding(dp(20))
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER }.apply {
            addView(ImageView(context).apply {
                setImageResource(R.drawable.keyboard_t9_normal)
                setOnClickListener{ v: View? ->
                    v?.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_anim_reduce_90))
                }
            }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(100)))
            addView(textView {
                setText(R.string.keyboard_full_display_normal_tip)
                gravity = Gravity.CENTER
            })
        }
        val header = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL   }.apply {
            addView(pinyinContainer, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(dp(20)) })
            addView(numberContainer, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(dp(20)) })
        }

        val elevation = textView {
            setBackgroundResource(R.color.skb_shadow_icon_color)
        }

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
}