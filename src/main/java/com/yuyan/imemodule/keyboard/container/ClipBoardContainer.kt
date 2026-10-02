package com.yuyan.imemodule.keyboard.container

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.ClipBoardAdapter
import com.yuyan.imemodule.adapter.SegmentWordsAdapter
import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.application.Launcher
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.database.entry.Clipboard
import com.yuyan.imemodule.libs.recyclerview.SwipeMenu
import com.yuyan.imemodule.libs.recyclerview.SwipeMenuBridge
import com.yuyan.imemodule.libs.recyclerview.SwipeMenuItem
import com.yuyan.imemodule.libs.recyclerview.SwipeRecyclerView
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.ClipboardLayoutMode
import com.yuyan.imemodule.prefs.behavior.PopupMenuMode
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.keyboard.InputView
import com.yuyan.imemodule.keyboard.KeyboardManager
import com.yuyan.imemodule.manager.layout.CustomGridLayoutManager
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import com.yuyan.imemodule.utils.segmentText
import com.yuyan.imemodule.utils.mergeTokens
import com.yuyan.imemodule.utils.clipboardManager
import com.yuyan.imemodule.utils.toast
import splitties.dimensions.dp
import splitties.views.textResource
import kotlin.math.ceil

/**
 * 粘贴板列表键盘容器
 *
 * 使用RecyclerView实现垂直ListView列表布局。
 */
@SuppressLint("ViewConstructor")
class ClipBoardContainer(context: Context, inputView: InputView) : BaseContainer(context, inputView) {
    private val mPaint : Paint = Paint() // 测量字符串长度
    private val mRVSymbolsView: SwipeRecyclerView = SwipeRecyclerView(context)
    private var mTVLable: TextView? = null
    private var itemMode:SkbMenuMode? = null
    private var segmentTokens: List<String> = emptyList()
    private var segmentAdapter: SegmentWordsAdapter? = null

    init {
        mPaint.textSize = dp(22f)
        initView(context)
    }

    private fun initView(context: Context) {
        mTVLable = TextView(context).apply {
            textResource = R.string.clipboard_empty_ltip
            gravity = Gravity.CENTER
            setTextColor(activeTheme.keyTextColor)
            textSize = instance.candidateTextSize
        }
        mRVSymbolsView.setItemAnimator(null)
        val layoutParams2 = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        mRVSymbolsView.layoutParams = layoutParams2
        this.addView(mRVSymbolsView)
    }

    /**
     * 显示候选词界面 , 点击候选词时执行
     */
    fun showClipBoardView(item: SkbMenuMode) {
        CustomConstant.lockClipBoardEnable = false
        itemMode = item
        segmentTokens = emptyList()
        segmentAdapter = null
        mTVLable?.textResource = R.string.clipboard_empty_ltip
        mRVSymbolsView.setHasFixedSize(true)
        val copyContents : MutableList<Clipboard> =
            if(itemMode == SkbMenuMode.ClipBoard) {
                DataBaseKT.instance.clipboardDao().getAll().toMutableList()
            } else {
                DataBaseKT.instance.phraseDao().getAll().map { line -> Clipboard(line.content) }.toMutableList()
            }
        val manager =  when (AppPrefs.getInstance().clipboard.clipboardLayoutCompact.getValue()){
            ClipboardLayoutMode.ListView ->  LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            ClipboardLayoutMode.GridView -> CustomGridLayoutManager(context, 2)
            ClipboardLayoutMode.FlexboxView -> {
                calculateColumn(copyContents.map { it.content })
                CustomGridLayoutManager(context, 6).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(i: Int) = mHashMapSymbols[i] ?: 1
                    }
                }
            }
        }
        mRVSymbolsView.setLayoutManager(manager)
        val viewParent = mTVLable?.parent
        if (viewParent != null) {
            (viewParent as ViewGroup).removeView(mTVLable)
        }
        if(copyContents.isEmpty()){
            this.addView(mTVLable, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        }
        val adapter = ClipBoardAdapter(context, copyContents)
        mRVSymbolsView.setAdapter(null)
        mRVSymbolsView.setOnItemClickListener{ _: View?, position: Int ->
            inputView.responseLongKeyEvent(Pair(PopupMenuMode.Text, copyContents[position].content))
            if(!CustomConstant.lockClipBoardEnable)KeyboardManager.instance.switchKeyboard()
        }
        mRVSymbolsView.setSwipeMenuCreator{ _: SwipeMenu, rightMenu: SwipeMenu, position: Int ->
            val topItem = SwipeMenuItem(mContext).apply {
                setImage(if(itemMode == SkbMenuMode.ClipBoard) {
                    if(copyContents[position].isKeep == 1)R.drawable.ic_baseline_untop_circle_32 else R.drawable.ic_baseline_top_circle_32 }
                else R.drawable.ic_menu_edit)
                image.setTint(activeTheme.keyTextColor)
            }
            rightMenu.addMenuItem(topItem)
            val deleteItem = SwipeMenuItem(mContext).apply {
                setImage(R.drawable.ic_menu_delete)
                image.setTint(activeTheme.keyTextColor)
            }
            rightMenu.addMenuItem(deleteItem)
            if (itemMode == SkbMenuMode.ClipBoard) {
                val segmentItem = SwipeMenuItem(mContext).apply {
                    setImage(R.drawable.ic_menu_segment)
                    image.setTint(activeTheme.keyTextColor)
                }
                rightMenu.addMenuItem(segmentItem)
            }
        }
        mRVSymbolsView.setOnItemMenuClickListener { menuBridge: SwipeMenuBridge, position: Int ->
            menuBridge.closeMenu()
            if(itemMode == SkbMenuMode.ClipBoard){
                if(menuBridge.position == 0) {
                    val data: Clipboard = copyContents[position]
                    data.isKeep = 1 - data.isKeep
                    DataBaseKT.instance.clipboardDao().update(data)
                    showClipBoardView(SkbMenuMode.ClipBoard)
                } else if(menuBridge.position == 1){
                    val data: Clipboard = copyContents.removeAt(position)
                    DataBaseKT.instance.clipboardDao().deleteByContent(data.content)
                    mRVSymbolsView.adapter?.notifyItemRemoved(position)
                } else if (menuBridge.position == 2) {
                    showSegmentView(copyContents[position].content)
                }
            } else {
                val content = copyContents[position].content
                if(menuBridge.position == 0) {
                    inputView.onSettingsMenuClick(SkbMenuMode.AddPhrases, DataBaseKT.instance.phraseDao().queryByContent(content))
                } else if(menuBridge.position == 1){
                    DataBaseKT.instance.phraseDao().deleteByContent(content)
                    showClipBoardView(SkbMenuMode.Phrases)
                }
            }
        }
        mRVSymbolsView.setAdapter(adapter)
    }

    private val mHashMapSymbols = HashMap<Int, Int>() //候选词索引列数对应表
    private fun calculateColumn(data : List<String>) {
        mHashMapSymbols.clear()
        val itemWidth = instance.skbWidth/6 - dp(10)
        var mCurrentColumn = 0
        val contents = data
        contents.forEachIndexed { position, candidate ->
            var count = getSymbolsCount(candidate, itemWidth)
            var nextCount = 0
            if (contents.size > position + 1) {
                val nextCandidate = contents[position + 1]
                nextCount = getSymbolsCount(nextCandidate, itemWidth)
            }
            mCurrentColumn = if (mCurrentColumn + count + nextCount > 6) {
                count = 6 - mCurrentColumn
                0
            } else  (mCurrentColumn + count) % 6
            mHashMapSymbols[position] = count
        }
    }

    private fun getSymbolsCount(data: String, itemWidth:Int): Int {
        return if (!TextUtils.isEmpty(data)) ceil(mPaint.measureText(data).div(itemWidth)).toInt() else 0
    }

    fun showSegmentView(content: String) {
        CustomConstant.lockClipBoardEnable = false
        itemMode = SkbMenuMode.Segment
        segmentTokens = segmentText(content)
        mRVSymbolsView.setHasFixedSize(true)

        // Empty state label
        mTVLable?.textResource = R.string.segment_empty
        val viewParent = mTVLable?.parent
        if (viewParent != null) {
            (viewParent as ViewGroup).removeView(mTVLable)
        }
        if (segmentTokens.isEmpty()) {
            this.addView(mTVLable, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        }

        // Flexbox layout (same as clipboard flexbox mode)
        calculateColumn(segmentTokens)
        val manager = CustomGridLayoutManager(context, 6).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(i: Int) = mHashMapSymbols[i] ?: 1
            }
        }
        mRVSymbolsView.setLayoutManager(manager)

        segmentAdapter = SegmentWordsAdapter(context, segmentTokens)
        // MUST setAdapter(null) first: clears mAdapterWrapper so setSwipeMenuCreator
        // /setOnItemMenuClickListener don't throw IllegalStateException (B1 fix).
        mRVSymbolsView.setAdapter(null)
        // Now safe: install no-op swipe creator/listener to prevent stale closures
        // from showClipBoardView causing IndexOutOfBoundsException (H1 fix).
        mRVSymbolsView.setSwipeMenuCreator { _, _, _ -> }
        mRVSymbolsView.setOnItemMenuClickListener { _, _ -> }
        mRVSymbolsView.setOnItemClickListener { _, position: Int ->
            segmentAdapter?.toggleSelection(position)
        }
        mRVSymbolsView.setAdapter(segmentAdapter)
        inputView.updateCandidateBar()
    }

    fun copySelectedSegments() {
        val selected = segmentAdapter?.getSelectedPositions() ?: emptySet()
        if (selected.isEmpty()) {
            mContext.toast(R.string.segment_no_selection)
            return
        }
        val merged = mergeTokens(segmentTokens, selected)
        Launcher.instance.context.clipboardManager.setPrimaryClip(
            android.content.ClipData.newPlainText("segment", merged)
        )
        mContext.toast(R.string.segment_copied)
        segmentAdapter?.clearSelection()
        // 与点击剪贴板条目行为一致：未锁定时返回键盘，锁定键可保持面板
        if(!CustomConstant.lockClipBoardEnable) KeyboardManager.instance.switchKeyboard()
    }

    fun getMenuMode():SkbMenuMode? {
       return itemMode
    }
}
