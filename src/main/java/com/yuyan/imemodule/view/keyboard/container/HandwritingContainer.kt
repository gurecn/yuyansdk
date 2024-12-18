package com.yuyan.imemodule.view.keyboard.container

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.PrefixAdapter
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.database.entry.SideSymbol
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.ui.utils.AppUtil
import com.yuyan.imemodule.utils.KeyboardLoaderUtil.Companion.instance
import com.yuyan.imemodule.view.keyboard.HandwritingKeyboard
import com.yuyan.imemodule.view.keyboard.InputView
import splitties.dimensions.dp
import splitties.views.dsl.core.margin

/**
 * 手写键盘容器
 *
 * 包含手写键盘[HandwritingKeyboard]及拼音选择界面。
 *
 * 与九宫格键盘容器[T9TextContainer]、九宫格候选词键盘容器[CandidatesContainer]不同的是，手写键盘容器拼音选择栏在键盘右侧。
 */
@SuppressLint("ViewConstructor")
class HandwritingContainer(context: Context?, inputView: InputView) : InputBaseContainer(context, inputView) {
    private val mSideSymbolsPinyin:List<SideSymbol>
    // 键盘界面上符号(T9左侧、手写右侧)
    private var mRVRightSymbols: SwipeRecyclerView = inflate(getContext(), R.layout.sdk_view_rv_prefix, null) as SwipeRecyclerView
    private val mLlAddSymbol : LinearLayout = LinearLayout(context).apply{
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT).apply { margin = (dp(20)) }
        gravity = Gravity.CENTER
    }
    init {
        val ivAddSymbol = ImageView(context).apply {
            setPadding(dp(5))
            setImageResource(R.drawable.ic_menu_setting)
            drawable.setTint(ThemeManager.activeTheme.keyTextColor)
        }
        ivAddSymbol.setOnClickListener { _:View ->
            val arguments = Bundle()
            arguments.putInt("type", 0)
            AppUtil.launchSettingsToPrefix(context!!, arguments)
        }
        mLlAddSymbol.addView(ivAddSymbol)
        mSideSymbolsPinyin = DataBaseKT.instance.sideSymbolDao().getAllSideSymbolPinyin()
    }

    /**
     * 更新软键盘布局
     */
    override fun updateSkbLayout() {
        if (null == mMajorView) {
            mMajorView = HandwritingKeyboard(context)
            val params: ViewGroup.LayoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            addView(mMajorView, params)
            (mMajorView as HandwritingKeyboard).setResponseKeyEvent(inputView)
        }
        val softKeyboard = instance.getSoftKeyboard(InputModeSwitcherManager.MASK_SKB_LAYOUT_HANDWRITING)
        mMajorView!!.setSoftKeyboard(softKeyboard)
        updateKeyboardView()
        mMajorView!!.invalidate()
    }

    // 更新键盘上侧边符号列表
    protected fun updateKeyboardView() {
        mRVRightSymbols.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false))
        if (mRVRightSymbols.parent != null) {
            val parent = mRVRightSymbols.parent as ViewGroup
            parent.removeView(mRVRightSymbols)
        }
        if (mRVRightSymbols.footerCount <= 0) {
            mRVRightSymbols.addFooterView(mLlAddSymbol)
        }
        addView(mRVRightSymbols, createLayoutParams())
        val strs = mSideSymbolsPinyin.map { it.symbolKey }.toTypedArray()
        val adapter = PrefixAdapter(context, strs)
        mRVRightSymbols.setAdapter(null)
        mRVRightSymbols.setOnItemClickListener{ _: View?, position: Int ->
            val symbol = mSideSymbolsPinyin.map { it.symbolValue }[position]
            val softKey = SoftKey(symbol)
            inputView.responseKeyEvent(softKey)
        }
        mRVRightSymbols.setAdapter(adapter)
    }

    private fun createLayoutParams(): LayoutParams {
        val softKeyboard = mMajorView?.getSoftKeyboard()
        val softKeySymbolHolder =
            softKeyboard?.getKeyByCode(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12)
        val prefixLayoutParams = LayoutParams(
            softKeySymbolHolder!!.width(), LayoutParams.MATCH_PARENT
        )
        prefixLayoutParams.setMargins(softKeyboard.keyXMargin, softKeySymbolHolder.mTop + softKeyboard.keyYMargin,
            softKeyboard.keyXMargin, softKeyboard.skbCoreHeight - softKeySymbolHolder.mBottom + softKeyboard.keyYMargin
        )
        prefixLayoutParams.addRule(ALIGN_PARENT_RIGHT)
        return prefixLayoutParams
    }
}