package com.yuyan.imemodule.view.keyboard.container

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.CandidatesAdapter
import com.yuyan.imemodule.adapter.PrefixAdapter
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.service.DecodingInfo
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import com.yuyan.imemodule.ui.utils.AppUtil
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.utils.DevicesUtils.dip2px
import com.yuyan.imemodule.utils.StringUtils.isLetter
import com.yuyan.imemodule.utils.thread.ThreadPoolUtils
import com.yuyan.imemodule.view.keyboard.InputView
import com.yuyan.imemodule.view.keyboard.manager.CustomGridLayoutManager
import splitties.dimensions.dp
import splitties.views.dsl.core.margin

/**
 * 候选词键盘容器
 *
 * 选择候选词界面分两种效果：九宫格（[com.yuyan.imemodule.manager.InputModeSwitcherManager.isChineseT9])情况下显示左侧拼音选择栏；全键、手写等情况下不显示拼音选择栏。
 * @see com.yuyan.imemodule.manager.InputModeSwitcherManager.isChineseT9
 */
@SuppressLint("ViewConstructor")
class CandidatesContainer(context: Context, inputView: InputView) : BaseContainer(context, inputView) {
    private lateinit var mRVSymbolsView: RecyclerView
    private lateinit var mCandidatesAdapter: CandidatesAdapter
    private var mRVLeftPrefix = inflate(getContext(), R.layout.sdk_view_rv_prefix, null) as SwipeRecyclerView
    private var activeCandidate = 0
    private var isLoadingMore = false // 正在加载更多
    private val mLlAddSymbol : LinearLayout = LinearLayout(context).apply{
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT).apply { margin = (dp(20)) }
        gravity = Gravity.CENTER
    }
    init {
        initView(context)
        val ivAddSymbol = ImageView(context).apply {
            setImageResource(R.drawable.baseline_add_circle_24)
        }
        ivAddSymbol.setOnClickListener { _:View ->
            val arguments = Bundle()
            arguments.putInt("type", 0)
            AppUtil.launchSettingsToPrefix(context, arguments)
        }
        mLlAddSymbol.addView(ivAddSymbol)
    }

    private fun initView(context: Context) {
        mRVSymbolsView = RecyclerView(context)
        mRVSymbolsView.setHasFixedSize(true)
        mRVSymbolsView.setItemAnimator(null)
        val prefixLayoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
        mRVLeftPrefix.setLayoutManager(prefixLayoutManager)
        val skbWidth = instance.skbWidth
        val skbHeight = instance.skbHeight
        val prefixLayoutParams = LayoutParams((skbWidth * 0.18).toInt(), LayoutParams.MATCH_PARENT)
        prefixLayoutParams.setMargins(0, (skbHeight * 0.01).toInt(), 0, (skbHeight * 0.01).toInt())
        addView(mRVLeftPrefix, prefixLayoutParams)
        mRVLeftPrefix.visibility = GONE
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        layoutParams.addRule(RIGHT_OF, mRVLeftPrefix.id)
        mRVSymbolsView.setLayoutParams(layoutParams)
        this.addView(mRVSymbolsView)
        val ivDelete = getIvDelete()
        this.addView(ivDelete)
        mCandidatesAdapter = CandidatesAdapter(context)
        mCandidatesAdapter.setOnItemClickLitener { _: RecyclerView.Adapter<*>?, _: View?, position: Int ->
            inputView.onChoiceTouched(position)
            mRVSymbolsView.scrollToPosition(0)
        }
        val layoutManager = CustomGridLayoutManager(context, 60)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(pos: Int): Int {
                if(DecodingInfo.candidateSize <= pos) return  12
                return DecodingInfo.candidates[pos].spanSize ?: 12
            }
        }
        mRVSymbolsView.setLayoutManager(layoutManager)
        mRVSymbolsView.setAdapter(mCandidatesAdapter)
        mRVSymbolsView.addOnScrollListener(RecyclerViewScrollListener())
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun getIvDelete(): ImageView {
        val ivDelete = ImageView(context)
        ivDelete.setImageResource(R.drawable.sdk_skb_key_delete_icon)
        val paddingBorder = dip2px(10f)
        ivDelete.setPadding(paddingBorder, paddingBorder, paddingBorder, paddingBorder)
        val isKeyBorder = ThemeManager.prefs.keyBorder.getValue()
        if (isKeyBorder) {
            val mActiveTheme = activeTheme
            val keyRadius = ThemeManager.prefs.keyRadius.getValue()
            val bg = GradientDrawable()
            bg.setColor(mActiveTheme.keyBackgroundColor)
            bg.shape = GradientDrawable.RECTANGLE
            bg.cornerRadius = keyRadius.toFloat() // 设置圆角半径
            ivDelete.background = bg
        }
        ivDelete.isClickable = true
        ivDelete.setEnabled(true)
        ivDelete.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 播放按键声音和震动
                    DevicesUtils.tryPlayKeyDown(SoftKey(KeyEvent.KEYCODE_DEL))
                    DevicesUtils.tryVibrate(this)
                }
                MotionEvent.ACTION_MOVE -> { }
                MotionEvent.ACTION_UP -> {
                    inputView.responseKeyEvent(SoftKey(KeyEvent.KEYCODE_DEL))
                }
            }
            true
        }
        val layoutParams3 = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParams3.setMargins(paddingBorder, paddingBorder, paddingBorder, paddingBorder)
        layoutParams3.addRule(ALIGN_PARENT_END, TRUE)
        layoutParams3.addRule(ALIGN_PARENT_BOTTOM, TRUE)
        ivDelete.layoutParams = layoutParams3
        return ivDelete
    }

    private inner class RecyclerViewScrollListener : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                ThreadPoolUtils.executeSingleton {
                    if (!isLoadingMore) {
                        isLoadingMore = true
                        val lastItem = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                        activeCandidate = lastItem
                        if (DecodingInfo.candidateSize - lastItem <= 20) { // 未加载中、未加载完、向下滑动、还有30个数据滑动到底
                            DecodingInfo.nextPageCandidates
                        }
                        isLoadingMore = false
                    }
                }
            }
        }
    }

    /**
     * 显示候选词界面 , 点击候选词时执行
     */
    fun showCandidatesView() {
        if (DecodingInfo.isCandidatesListEmpty) {
            activeCandidate = 0
            mRVSymbolsView.scrollToPosition(0)
            return
        }
        if(DecodingInfo.candidateSize == 10){
           DecodingInfo.nextPageCandidates
        }
        mCandidatesAdapter.notifyDataSetChanged()
        if (InputModeSwitcherManager.isChineseT9) {
            mRVLeftPrefix.visibility = VISIBLE
            updatePrefixsView()
        }
    }

    //更新左侧拼音显示
    private fun updatePrefixsView() {
        var prefixs =DecodingInfo.prefixs
        val isPrefixs = prefixs.isNotEmpty()
        if (!isPrefixs) { // 有候选拼音显示候选拼音
            prefixs = CustomConstant.PREFIXS_PINYIN
            if (mRVLeftPrefix.footerCount <= 0) {
                mRVLeftPrefix.addFooterView(mLlAddSymbol)
            }
        } else{
            if (mRVLeftPrefix.footerCount > 0) {
                mRVLeftPrefix.removeFooterView(mLlAddSymbol)
            }
        }
        val adapter = PrefixAdapter(context, prefixs)
        mRVLeftPrefix.setAdapter(null)
        mRVLeftPrefix.setOnItemClickListener{ _: View?, position: Int ->
            val s = prefixs[position]
            if (isPrefixs) {
                if (isLetter(s)) {
                    mRVSymbolsView.scrollToPosition(0)
                    inputView.selectPrefix(position)
                }
            } else {
                val softKey = SoftKey(s)
                // 播放按键声音和震动
                DevicesUtils.tryPlayKeyDown(softKey)
                DevicesUtils.tryVibrate(this)
                inputView.responseKeyEvent(softKey)
            }
        }
        mRVLeftPrefix.setAdapter(adapter)
    }
}
