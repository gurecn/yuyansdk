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
import com.yuyan.imemodule.service.DecodingInfo
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.AppUtil
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.utils.KeyboardLoaderUtil.Companion.instance
import com.yuyan.imemodule.keyboard.InputView
import com.yuyan.imemodule.keyboard.TextKeyboard
import com.yuyan.imemodule.keyboard.HandwritingKeyboard
import com.yuyan.imemodule.libs.recyclerview.SwipeRecyclerView
import splitties.dimensions.dp
import splitties.views.dsl.core.margin

/**
 * 九宫格键盘容器
 *
 * 包含输入键盘键盘[TextKeyboard]及拼音选择界面两层。
 *
 * 其中：
 *  输入键盘占据全部空间，左上角由拼音选择栏占位按键[InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12]站位。
 *
 *  拼音选择栏（无拼音时显示中文符号）位于键盘左上角，拼音选择栏占位按键正上方。
 *
 * 与数字键盘容器[NumberContainer]类似。
 */
@SuppressLint("ViewConstructor")
open class T9TextContainer(context: Context?, inputView: InputView, skbValue: Int = 0) : InputBaseContainer(context, inputView) {
    private var mSkbValue: Int = 0
    private val mSideSymbolsPinyin:List<SideSymbol>
    // 键盘、候选词界面上符号(T9左侧、手写右侧)、候选拼音ListView
    private val mRVLeftPrefix : SwipeRecyclerView = inflate(getContext(), R.layout.sdk_view_rv_prefix, null) as SwipeRecyclerView
    private val mLlAddSymbol : LinearLayout = LinearLayout(context).apply{
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT).apply { margin = (dp(20)) }
        gravity = Gravity.CENTER
    }

    init {
        mSkbValue = skbValue
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
            mMajorView!!.setResponseKeyEvent(inputView)
        }
        val softKeyboard = instance.getSoftKeyboard(mSkbValue)
        mMajorView!!.setSoftKeyboard(softKeyboard)
        updateKeyboardView()
        mMajorView!!.invalidate()
    }

    // 更新键盘上侧边符号列表
    private fun updateKeyboardView() {
        val softKeyboard = mMajorView!!.getSoftKeyboard()
        val softKeySymbolHolder = softKeyboard.getKeyByCode(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12) ?: return
        val prefixLayoutParams = LayoutParams(softKeySymbolHolder.width(), LayoutParams.MATCH_PARENT)
        prefixLayoutParams.setMargins(
            softKeyboard.keyXMargin,
            softKeySymbolHolder.mTop + softKeyboard.keyYMargin,
            softKeyboard.keyXMargin,
            EnvironmentSingleton.instance.skbHeight - softKeySymbolHolder.mBottom + softKeyboard.keyYMargin
        )
        val prefixLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mRVLeftPrefix.setLayoutManager(prefixLayoutManager)
        if (mRVLeftPrefix.parent != null) {
            val parent = mRVLeftPrefix.parent as ViewGroup
            parent.removeView(mRVLeftPrefix)
        }
        addView(mRVLeftPrefix, prefixLayoutParams)
        updateSymbolListView()
    }

    //更新符号显示,九宫格左侧符号栏
    fun updateSymbolListView() {
        var prefixs = DecodingInfo.prefixs
        val isPrefixs = prefixs.isNotEmpty()
        if (!isPrefixs) { // 有候选拼音显示候选拼音
            prefixs = mSideSymbolsPinyin.map { it.symbolKey }.toTypedArray()
            if (mRVLeftPrefix.footerCount <= 0) {
                mRVLeftPrefix.addFooterView(mLlAddSymbol)
            }
        } else {
            if (mRVLeftPrefix.footerCount > 0) {
                mRVLeftPrefix.removeFooterView(mLlAddSymbol)
            }
        }
        val adapter = PrefixAdapter(context, prefixs)
        mRVLeftPrefix.setAdapter(null)
        mRVLeftPrefix.setOnItemClickListener{ _: View?, position: Int ->
            if (!isPrefixs) {
                val symbol = mSideSymbolsPinyin.map { it.symbolValue }[position]
                val softKey = SoftKey(label = symbol)
                // 播放按键声音和震动
                DevicesUtils.tryPlayKeyDown()
                DevicesUtils.tryVibrate(this)
                inputView.responseKeyEvent(softKey)
            } else {
                inputView.selectPrefix(position)
            }
        }
        mRVLeftPrefix.setAdapter(adapter)
    }
}
