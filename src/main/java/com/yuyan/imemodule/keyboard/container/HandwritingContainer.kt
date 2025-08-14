package com.yuyan.imemodule.keyboard.container

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
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.PrefixAdapter
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.database.entry.SideSymbol
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.AppUtil
import com.yuyan.imemodule.utils.KeyboardLoaderUtil.Companion.instance
import com.yuyan.imemodule.keyboard.InputView
import com.yuyan.imemodule.keyboard.HandwritingKeyboard
import com.yuyan.imemodule.libs.recyclerview.SwipeRecyclerView
import splitties.dimensions.dp
import splitties.views.dsl.constraintlayout.endOfParent
import splitties.views.dsl.constraintlayout.lParams
import splitties.views.dsl.core.add
import splitties.views.dsl.core.margin
import splitties.views.dsl.core.matchParent

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
            val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
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

        val softKeyboard = mMajorView?.getSoftKeyboard()
        val softKeySymbolHolder = softKeyboard?.getKeyByCode(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12)
        add(mRVRightSymbols, lParams (width =softKeySymbolHolder!!.width(), height = matchParent).apply{
            endOfParent(0)
            setMargins(softKeyboard.keyXMargin, softKeySymbolHolder.mTop + softKeyboard.keyYMargin,
                    softKeyboard.keyXMargin, EnvironmentSingleton.instance.skbHeight - softKeySymbolHolder.mBottom + softKeyboard.keyYMargin)
        })
        val strs = mSideSymbolsPinyin.map { it.symbolKey }.toTypedArray()
        val adapter = PrefixAdapter(context, strs)
        mRVRightSymbols.setAdapter(null)
        mRVRightSymbols.setOnItemClickListener{ _: View?, position: Int ->
            val symbol = mSideSymbolsPinyin.map { it.symbolValue }[position]
            val softKey = SoftKey(label = symbol)
            inputView.responseKeyEvent(softKey)
        }
        mRVRightSymbols.setAdapter(adapter)
    }
}