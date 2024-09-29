package com.yuyan.imemodule.view.keyboard.container

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.PrefixAdapter
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.utils.KeyboardLoaderUtil.Companion.instance
import com.yuyan.imemodule.utils.StringUtils.isLetter
import com.yuyan.imemodule.view.keyboard.InputView
import com.yuyan.imemodule.view.keyboard.TextKeyboard

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
class T9TextContainer(context: Context?, inputView: InputView) : InputBaseContainer(context, inputView) {
    // 键盘、候选词界面上符号(T9左侧、手写右侧)、候选拼音ListView
    private var mRVLeftPrefix : RecyclerView = inflate(getContext(), R.layout.sdk_view_rv_prefix, null) as RecyclerView

    /**
     * 更新软键盘布局
     */
    override fun updateSkbLayout() {
        if (null == mMajorView) {
            mMajorView = TextKeyboard(context)
            val params: ViewGroup.LayoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            addView(mMajorView, params)
            mMajorView!!.setResponseKeyEvent(inputView)
        }
        val softKeyboard = instance.getSoftKeyboard(InputModeSwitcherManager.MASK_SKB_LAYOUT_T9_PINYIN)
        mMajorView!!.setSoftKeyboard(softKeyboard)
        updateKeyboardView()
        mMajorView!!.invalidate()
    }

    // 更新键盘上侧边符号列表
    protected fun updateKeyboardView() {
        val prefixLayoutParams = createLayoutParams()
        val prefixLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mRVLeftPrefix.setLayoutManager(prefixLayoutManager)
        if (mRVLeftPrefix.parent != null) {
            val parent = mRVLeftPrefix.parent as ViewGroup
            parent.removeView(mRVLeftPrefix)
        }
        addView(mRVLeftPrefix, prefixLayoutParams)
        updateSymbolListView()
    }

    private fun createLayoutParams(): LayoutParams {
        val softKeyboard = mMajorView!!.getSoftKeyboard()
        val softKeySymbolHolder =
            softKeyboard.getKeyByCode(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12)
        val prefixLayoutParams = LayoutParams(
            softKeySymbolHolder!!.width(), LayoutParams.MATCH_PARENT
        )
        prefixLayoutParams.setMargins(
            softKeyboard.keyXMargin,
            softKeySymbolHolder.mTop + softKeyboard.keyYMargin,
            softKeyboard.keyXMargin,
            softKeyboard.skbCoreHeight - softKeySymbolHolder.mBottom + softKeyboard.keyYMargin
        )
        return prefixLayoutParams
    }

    //更新符号显示,九宫格左侧符号栏
    fun updateSymbolListView() {
        var prefixs = mDecInfo!!.prefixs
        val isPrefixs: Boolean
        if (prefixs.isEmpty()) { // 有候选拼音显示候选拼音
            prefixs = resources.getStringArray(R.array.SymbolRealNine)
            isPrefixs = false
        } else {
            isPrefixs = true
        }
        val adapter = PrefixAdapter(context, prefixs)
        adapter.setOnItemClickLitener { parent: RecyclerView.Adapter<*>?, _: View?, position: Int ->
            val symbol = (parent as PrefixAdapter?)!!.getSymbolData(position)
            if (isPrefixs) {
                if (isLetter(symbol)) {
                    inputView.selectPrefix(position)
                }
            } else {
                val softKey = SoftKey(symbol)
                // 播放按键声音和震动
                DevicesUtils.tryPlayKeyDown(softKey)
                DevicesUtils.tryVibrate(this)
                inputView.responseKeyEvent(softKey)
            }
        }
        mRVLeftPrefix.setAdapter(adapter)
    }
}
