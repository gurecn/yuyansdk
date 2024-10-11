package com.yuyan.imemodule.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.PrefixSettingsAdapter
import com.yuyan.imemodule.constant.CustomConstant
import splitties.dimensions.dp
import splitties.views.dsl.constraintlayout.below
import splitties.views.dsl.constraintlayout.bottomOfParent
import splitties.views.dsl.constraintlayout.constraintLayout
import splitties.views.dsl.constraintlayout.endOfParent
import splitties.views.dsl.constraintlayout.lParams
import splitties.views.dsl.constraintlayout.startOfParent
import splitties.views.dsl.constraintlayout.topOfParent
import splitties.views.dsl.core.add
import splitties.views.dsl.core.matchParent
import splitties.views.dsl.core.textView
import splitties.views.dsl.core.wrapContent


class PrefixSettingsFragment : Fragment(){
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.setting_ime_prefixs)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = with(requireContext()) {

        val pinyinContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER }.apply {
                addView(ImageView(context).apply {
                    setImageResource(R.mipmap.skb_prefix_pinyin)
                }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(100)))
                addView(textView {
                    setText(R.string.skb_prefix_pinyin_tips)
                    gravity = Gravity.CENTER
                })
        }

        val numberContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER }.apply {
                addView(ImageView(context).apply {
                    setImageResource(R.mipmap.skb_prefix_number)
                }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(100)))
                addView(textView {
                    setText(R.string.skb_prefix_number_tips)
                    gravity = Gravity.CENTER
                })
        }
        val header = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL   }.apply {
            addView(pinyinContainer, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            addView(numberContainer, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        }

        val mRVSymbolsView = SwipeRecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
        }
        val adapter = PrefixSettingsAdapter(CustomConstant.PREFIXS_PINYIN)
        mRVSymbolsView.setAdapter(adapter)
        val elevation = textView {
            setBackgroundResource(R.color.skb_shadow_icon_color)
        }

        mRVSymbolsView.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                mRVSymbolsView.post {
                    if (adapter.itemCount > 0) {
                        mRVSymbolsView.smoothScrollToPosition(adapter.itemCount - 1)
                    }
                }
            }
        }

        constraintLayout {
            add(header, lParams(height = wrapContent) {
                topOfParent()
                startOfParent()
                setMargins(dp(20))
            })
            add(elevation, lParams(width = matchParent,height = dp(1)) {
                below(header)
                setMargins(dp(40),dp(20),dp(40), 0)
            })
            add(mRVSymbolsView, lParams {
                below(elevation)
                startOfParent()
                endOfParent()
                bottomOfParent()
            })
        }
    }
}