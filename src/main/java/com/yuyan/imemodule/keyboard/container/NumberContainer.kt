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
import com.yuyan.imemodule.libs.recyclerview.SwipeRecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.PrefixAdapter
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.database.entry.SideSymbol
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.AppUtil
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.utils.KeyboardLoaderUtil.Companion.instance
import com.yuyan.imemodule.keyboard.InputView
import com.yuyan.imemodule.keyboard.TextKeyboard
import com.yuyan.imemodule.keyboard.HandwritingKeyboard
import splitties.dimensions.dp
import splitties.views.dsl.core.margin

/**
 * 数字键盘容器
 *
 * 包含输入键盘[TextKeyboard]及拼音选择界面。
 *
 * 与九宫格键盘容器[T9TextContainer]类似。
 */
@SuppressLint("ViewConstructor")
class NumberContainer(context: Context?, inputView: InputView) : InputBaseContainer(context, inputView) {
    private val mSideSymbolsNumber:List<SideSymbol>
    // 键盘、候选词界面上符号(T9左侧、手写右侧)、候选拼音ListView
    private var mRVLeftPrefix : SwipeRecyclerView = inflate(getContext(), R.layout.sdk_view_rv_prefix, null) as SwipeRecyclerView
    private val mLlAddSymbol : LinearLayout = LinearLayout(context).apply{
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
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
            arguments.putInt("type", 1)
            AppUtil.launchSettingsToPrefix(context!!, arguments)
        }
        mLlAddSymbol.addView(ivAddSymbol)
        mSideSymbolsNumber = DataBaseKT.instance.sideSymbolDao().getAllSideSymbolNumber()
    }

    /**
     * 更新软键盘布局
     */
    override fun updateSkbLayout() {
        if (null == mMajorView) {
            mMajorView = HandwritingKeyboard(context)
            val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            addView(mMajorView, params)
            mMajorView!!.setResponseKeyEvent(inputView)
        }
        val softKeyboard = instance.getSoftKeyboard(InputModeSwitcherManager.MASK_SKB_LAYOUT_NUMBER)
        mMajorView!!.setSoftKeyboard(softKeyboard)
        updateKeyboardView()
        mMajorView!!.invalidate()
    }

    // 更新键盘上侧边符号列表
    private fun updateKeyboardView() {
        val prefixLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mRVLeftPrefix.setLayoutManager(prefixLayoutManager)
        if (mRVLeftPrefix.parent != null) {
            val parent = mRVLeftPrefix.parent as ViewGroup
            parent.removeView(mRVLeftPrefix)
        }
        if (mRVLeftPrefix.footerCount <= 0) {
            mRVLeftPrefix.addFooterView(mLlAddSymbol)
        }
        addView(mRVLeftPrefix, createLayoutParams())
        val strs  = mSideSymbolsNumber.map { it.symbolKey }.toTypedArray()
        val adapter = PrefixAdapter(context, strs)
        mRVLeftPrefix.setAdapter(null)
        mRVLeftPrefix.setOnItemClickListener{ _: View?, position: Int ->
            val symbol = mSideSymbolsNumber.map { it.symbolValue }[position]
            val softKey = SoftKey(label = symbol)
            // 播放按键声音和震动
            DevicesUtils.tryPlayKeyDown()
            DevicesUtils.tryVibrate(this)
            inputView.responseKeyEvent(softKey)
        }
        mRVLeftPrefix.setAdapter(adapter)
    }

    private fun createLayoutParams(): LayoutParams {
        val softKeyboard = mMajorView!!.getSoftKeyboard()
        val softKeySymbolHolder =
            softKeyboard.getKeyByCode(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12)
        val prefixLayoutParams = LayoutParams(
            softKeySymbolHolder!!.width(), LayoutParams.MATCH_PARENT
        )
        prefixLayoutParams.setMargins(softKeyboard.keyXMargin,
                softKeySymbolHolder.mTop + softKeyboard.keyYMargin,
                softKeyboard.keyXMargin,
                EnvironmentSingleton.instance.skbHeight - softKeySymbolHolder.mBottom + softKeyboard.keyYMargin)
        return prefixLayoutParams
    }
}
