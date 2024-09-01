package com.yuyan.imemodule.view.keyboard.container

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.PrefixAdapter
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.utils.KeyboardLoaderUtil.Companion.instance
import com.yuyan.imemodule.view.keyboard.HandwritingKeyboard
import com.yuyan.imemodule.view.keyboard.InputView

/**
 * 手写键盘容器
 *
 * 包含手写键盘[HandwritingKeyboard]及拼音选择界面。
 *
 * 与九宫格键盘容器[T9TextContainer]、九宫格候选词键盘容器[CandidatesContainer]不同的是，手写键盘容器拼音选择栏在键盘右侧。
 */
@SuppressLint("ViewConstructor")
class HandwritingContainer(context: Context?, inputView: InputView) : InputBaseContainer(context, inputView) {
    // 键盘界面上符号(T9左侧、手写右侧)
    private var mRVRightSymbols: RecyclerView = inflate(getContext(), R.layout.sdk_view_rv_prefix, null) as RecyclerView

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
        addView(mRVRightSymbols, createLayoutParams())
        val strs = resources.getStringArray(R.array.SymbolRealNine)
        val adapter = PrefixAdapter(context, strs)
        adapter.setOnItemClickLitener { _, _, position ->
            val softKey = SoftKey(strs[position])
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