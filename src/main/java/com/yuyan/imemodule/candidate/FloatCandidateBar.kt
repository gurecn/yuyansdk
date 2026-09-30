package com.yuyan.imemodule.candidate

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.yuyan.imemodule.adapter.FloatCandidatesBarAdapter
import com.yuyan.imemodule.callback.CandidateViewListener
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.keyboard.KeyboardManager
import com.yuyan.imemodule.keyboard.container.CandidatesContainer
import com.yuyan.imemodule.manager.layout.CustomLinearLayoutManager
import com.yuyan.imemodule.service.DictDecoder
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import splitties.dimensions.dp

/**
 * 物理键盘候选词集装箱
 */
class FloatCandidateBar(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private var mFloatCandidateBarWidth: Int = 0

    private lateinit var mCvListener: CandidateViewListener // 候选词视图监听器
    private lateinit var mCandidatesDataContainer: LinearLayout //候选词视图
    private lateinit var mComposingView: TextView // 组成字符串的View，用于显示输入的拼音。
    private lateinit var mRVCandidates: RecyclerView    //候选词列表
    private lateinit var mCandidatesAdapter: FloatCandidatesBarAdapter
    private lateinit var candidatesData: LinearLayout //候选词视图
    private var activeCandidatePosition = RecyclerView.NO_POSITION
    private var firstVisiblePosition = 0
    private var displayedCandidates = DictDecoder.candidates

    fun initialize(cvListener: CandidateViewListener) {
        mCvListener = cvListener
        mFloatCandidateBarWidth = (if(instance.isLandscape)instance.mScreenHeight else instance.mScreenWidth) - dp(40)
        initCandidateView()
    }

    // 初始化候选词界面
    private fun initCandidateView() {
        if(!::mCandidatesDataContainer.isInitialized) {
            mCandidatesDataContainer = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
            }
            mComposingView = TextView(context).apply {
                includeFontPadding = false
                setPadding(dp(10), 0, dp(10), 0)
            }
            candidatesData = LinearLayout(context).apply {
                gravity = Gravity.CENTER_VERTICAL
            }
            mRVCandidates = RecyclerView(context).apply {
                setItemAnimator(null)
                layoutParams = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)
                layoutManager =
                    CustomLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
            mCandidatesAdapter = FloatCandidatesBarAdapter(context)
            mCandidatesAdapter.setOnItemClickLitener { _: RecyclerView.Adapter<*>?, _: View?, position: Int ->
                mCvListener.onClickChoice(position)
            }
            mRVCandidates.setAdapter(mCandidatesAdapter)
            mRVCandidates.addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    refreshVisibleCandidates()
                }
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        DictDecoder.activeCandidateBar =
                            layoutManager.findLastVisibleItemPosition()
                        val itemCount = recyclerView.adapter?.itemCount
                        if (KeyboardManager.instance.currentContainer !is CandidatesContainer && itemCount != null && itemCount > 0 && DictDecoder.activeCandidateBar >= itemCount - 1) {
                            DictDecoder.nextPageCandidates
                        }
                    }
                }
            })
            mRVCandidates.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                refreshVisibleCandidates()
            }
            mCandidatesDataContainer.addView(mComposingView)
            mCandidatesDataContainer.addView(candidatesData)
            this.addView(mCandidatesDataContainer, LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        } else {
            (mRVCandidates.parent as ViewGroup).removeView(mRVCandidates)
        }
        val candidatesHeight = instance.heightForCandidates
        mComposingView.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, instance.heightForcomposing)
        candidatesData.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, candidatesHeight)
        candidatesData.addView(mRVCandidates, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, candidatesHeight, 1f))
        mComposingView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, instance.composingTextSize)
        mCandidatesAdapter.notifyChanged()
    }

    /**
     * 显示候选词
     */
    fun showCandidates() {
        val candidates = DictDecoder.candidates
        val composing = DictDecoder.composingStrForDisplay
        val isAppend = DictDecoder.activeCandidateBar > 0 &&
            displayedCandidates.isNotEmpty() && candidates.size > displayedCandidates.size &&
            composing == mComposingView.text.toString() &&
            candidates.subList(0, displayedCandidates.size) == displayedCandidates
        displayedCandidates = candidates
        mComposingView.text = composing
        if (!isAppend) {
            activeCandidatePosition = RecyclerView.NO_POSITION
            firstVisiblePosition = if (candidates.isEmpty()) 0 else {
                DictDecoder.activeCandidateBar.coerceIn(0, candidates.lastIndex)
            }
            pageStarts.clear()
            (mRVCandidates.layoutManager as? LinearLayoutManager)
                ?.scrollToPositionWithOffset(firstVisiblePosition, 0)
        }
        this.visibility = if (candidates.isEmpty()) GONE else VISIBLE
        mCandidatesAdapter.updateCandidateState(firstVisiblePosition, activeCandidatePosition)
        mCandidatesAdapter.notifyChanged()
    }

    private fun refreshVisibleCandidates() {
        val lm = mRVCandidates.layoutManager as? LinearLayoutManager ?: return
        val first = lm.findFirstVisibleItemPosition()
        if (first != RecyclerView.NO_POSITION) firstVisiblePosition = first
        mCandidatesAdapter.updateCandidateState(firstVisiblePosition, activeCandidatePosition)
        mCandidatesAdapter.updateVisibleIndices(mRVCandidates)
    }

    private fun selectCandidate(position: Int) {
        activeCandidatePosition = position
        mCandidatesAdapter.updateCandidateState(firstVisiblePosition, activeCandidatePosition)
        mCandidatesAdapter.updateVisibleIndices(mRVCandidates)
    }

    private val displayedActivePosition: Int
        get() = if (isActiveCand()) activeCandidatePosition else firstVisiblePosition

    /**
     * 更新激活的候选词
     */
    fun updateActiveCandidateNo(keyCode: Int) {
        if (DictDecoder.isCandidatesEmpty) return
        val step = when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> -1
            KeyEvent.KEYCODE_DPAD_RIGHT -> 1
            else -> return
        }
        val target = (displayedActivePosition + step).coerceIn(0, DictDecoder.candidateSize - 1)
        selectCandidate(target)
        mRVCandidates.layoutManager?.scrollToPosition(target)
    }

    fun getVisibleCandPosition(visibleNo: Int): Int {
        if (visibleNo < 1 || DictDecoder.isCandidatesEmpty) return -1
        val position = firstVisiblePosition + visibleNo - 1
        return if (position in 0 until DictDecoder.candidateSize) position else -1
    }

    /**
     * 获取激活的候选词
     */
    fun getActiveCandNo(): Int {
        if (DictDecoder.isCandidatesEmpty) return 0
        return displayedActivePosition.coerceIn(0, DictDecoder.candidateSize - 1)
    }

    private val pageStarts = ArrayDeque<Int>()   // 每页起点的历史记录
    /**
     * 翻页功能
     */
    fun pageChange(keyCode: Int) {
        if (DictDecoder.isCandidatesEmpty) return
        val lm = mRVCandidates.layoutManager as? LinearLayoutManager ?: return
        val target = when (keyCode) {
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                val first = lm.findFirstVisibleItemPosition()
                if (first == RecyclerView.NO_POSITION) return
                val last = lm.findLastCompletelyVisibleItemPosition().let {
                    if (it == RecyclerView.NO_POSITION) first else it
                }
                val next = last + 1
                if (next >= mCandidatesAdapter.itemCount) return
                pageStarts.addLast(first)
                next
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                if (pageStarts.isEmpty()) return
                pageStarts.removeLast()
            }
            else -> return
        }
        firstVisiblePosition = target
        selectCandidate(target)
        lm.scrollToPositionWithOffset(target, 0)
    }

    /**
     * 是否操作选词
     */
    fun isActiveCand(): Boolean {
        return activeCandidatePosition in 0 until DictDecoder.candidateSize
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMeasure = MeasureSpec.makeMeasureSpec(instance.heightForCandidatesArea, MeasureSpec.EXACTLY)
        val widthMeasure = MeasureSpec.makeMeasureSpec(mFloatCandidateBarWidth, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasure, heightMeasure)
    }

    // 刷新主题
    fun updateTheme(textColor: Int) {
        initCandidateView()
        mComposingView.setTextColor(textColor)
        val drawable = GradientDrawable()
        drawable.setShape(GradientDrawable.RECTANGLE)
        drawable.setColor(ThemeManager.activeTheme.keyboardColor)
        val cornerRadiusInPx = 20f
        drawable.setCornerRadius(cornerRadiusInPx)
        background = drawable
        mCandidatesAdapter.notifyChanged()
    }
}
