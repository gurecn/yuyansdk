package com.yuyan.imemodule.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.CandidatesBarAdapter
import com.yuyan.imemodule.application.LauncherModel
import com.yuyan.imemodule.callback.CandidateViewListener
import com.yuyan.imemodule.data.flower.FlowerTypefaceMode
import com.yuyan.imemodule.data.theme.ThemeManager.prefs
import com.yuyan.imemodule.prefs.behavior.KeyboardOneHandedMod
import com.yuyan.imemodule.service.DecodingInfo
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import com.yuyan.imemodule.utils.DevicesUtils.dip2px
import com.yuyan.imemodule.view.keyboard.KeyboardManager
import com.yuyan.imemodule.view.keyboard.container.CandidatesContainer

/**
 * 候选词集装箱
 */
class CandidatesBar(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    private lateinit var mCvListener: CandidateViewListener // 候选词视图监听器
    private lateinit var mRightArrowBtn: ImageView // 右边箭头按钮
    private lateinit var mDecInfo: DecodingInfo  // 词库解码对象
    private lateinit var mCandidatesDataContainer: LinearLayout //候选词视图
    private lateinit var mCandidatesMenuContainer: LinearLayout //控制菜单视图
    private lateinit var mRVCandidates: RecyclerView    //候选词列表
    private lateinit var mIvMenuCloseSKB: ImageView
    private lateinit var mCandidatesAdapter: CandidatesBarAdapter
    private lateinit var mLLContainerMenu:LinearLayout

    fun initialize(cvListener: CandidateViewListener, decInfo: DecodingInfo) {
        mDecInfo = decInfo
        mCvListener = cvListener
        initMenuView()
        initCandidateView()
    }

    // 初始化候选词界面
    private fun initCandidateView() {
        if(!::mCandidatesDataContainer.isInitialized) {
            mCandidatesDataContainer = LinearLayout(context).apply {
                gravity = Gravity.CENTER_VERTICAL
                visibility = GONE
                this.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
            val candidatesAreaHeight = instance.heightForCandidates
            mRightArrowBtn = ImageView(context).apply {
                isClickable = true
                setEnabled(true)
                setImageResource(R.drawable.sdk_level_list_candidates_display)
                val margin = dip2px(10f)
                val size = (candidatesAreaHeight * 0.2).toInt()
                setPadding(margin, size, margin, size)
                this.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 0f)
            }
            mRightArrowBtn.setOnClickListener { v: View ->
                val level = (v as ImageView).getDrawable().level
                if (level == 3) { //关闭键盘修改为重置候选词
                    mCvListener.onClickClearCandidate()
                } else if(level == 0) {
                    var lastItemPosition = 0
                    val layoutManager = mRVCandidates.layoutManager
                    if (layoutManager is LinearLayoutManager) {
                        lastItemPosition = layoutManager.findLastVisibleItemPosition()
                    }
                    if(mDecInfo.mCandidatesList.size > lastItemPosition + 1) {
                        mCvListener.onClickMore(level, lastItemPosition)
                        v.getDrawable().setLevel(1)
                    }
                } else {
                    mCvListener.onClickMore(level, 0)
                    v.getDrawable().setLevel(0)
                }
            }
            mRVCandidates = RecyclerView(context)
            mRVCandidates.layoutManager =  object : LinearLayoutManager(context, HORIZONTAL, false) {
                override fun canScrollHorizontally(): Boolean { return false }
            }
            mRVCandidates.layoutParams = LinearLayout.LayoutParams(0, candidatesAreaHeight, 1f)
            mCandidatesAdapter = CandidatesBarAdapter(context, mDecInfo.mCandidatesList)
            mCandidatesAdapter.setOnItemClickLitener { _: RecyclerView.Adapter<*>?, _: View?, position: Int ->
                mCvListener.onClickChoice(position)
            }
            mRVCandidates.setAdapter(mCandidatesAdapter)
            this.addView(mCandidatesDataContainer)
        } else {
            (mRightArrowBtn.parent as ViewGroup).removeView(mRightArrowBtn)
            (mRVCandidates.parent as ViewGroup).removeView(mRVCandidates)
        }
        val oneHandedMod = prefs.oneHandedMod.getValue()
        if (oneHandedMod == KeyboardOneHandedMod.LEFT) {
            mCandidatesDataContainer.addView(mRightArrowBtn)
            mCandidatesDataContainer.addView(mRVCandidates)
        } else {
            mCandidatesDataContainer.addView(mRVCandidates)
            mCandidatesDataContainer.addView(mRightArrowBtn)
        }
    }

    //初始化标题栏
    private fun initMenuView() {
        if(!::mCandidatesMenuContainer.isInitialized) {
            mCandidatesMenuContainer = LinearLayout(context).apply {
                gravity = Gravity.CENTER_VERTICAL
                this.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                    setMargins(dip2px(10f), 0, dip2px(10f) , 0)
                }
            }
            val ivMenuSetting = ImageView(context).apply {
                setImageResource(R.drawable.app_icon)
                isClickable = true
                setEnabled(true)
                setOnClickListener{mCvListener.onClickSetting()}
            }
            ivMenuSetting.layoutParams = LinearLayout.LayoutParams(instance.heightForCandidates, ViewGroup.LayoutParams.MATCH_PARENT, 0f)
            mLLContainerMenu = LinearLayout(context).apply {
                gravity = Gravity.CENTER
                this.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            }
            mIvMenuCloseSKB = ImageView(context).apply {
                setImageResource(R.drawable.sdk_skb_close_icon)
                isClickable = true
                setEnabled(true)
                this.layoutParams = LinearLayout.LayoutParams(instance.heightForCandidates, ViewGroup.LayoutParams.MATCH_PARENT, 0f)
                setOnClickListener { mCvListener.onClickCloseKeyboard() }
            }
            mCandidatesMenuContainer.addView(ivMenuSetting)
            mCandidatesMenuContainer.addView(mLLContainerMenu)
            mCandidatesMenuContainer.addView(mIvMenuCloseSKB)
            this.addView(mCandidatesMenuContainer)
        }
    }

    /**
     * 显示候选词
     */
    fun showCandidates() {
        if (mDecInfo.isCandidatesListEmpty) {
            showViewVisibility(mCandidatesMenuContainer)
        } else {
            showViewVisibility(mCandidatesDataContainer)
            if (mDecInfo.isAssociate) {
                mRightArrowBtn.getDrawable().setLevel(3)
            } else {
                val container = KeyboardManager.instance.currentContainer
                if (container is CandidatesContainer) {
                    mRightArrowBtn.getDrawable().setLevel(0)
                    var lastItemPosition = 0
                    val layoutManager = mRVCandidates.layoutManager
                    if (layoutManager is LinearLayoutManager) {
                        lastItemPosition = layoutManager.findLastVisibleItemPosition()
                    }
                    mCvListener.onClickMore(0, lastItemPosition)
                } else {
                    mRightArrowBtn.getDrawable().setLevel(0)
                }
            }
            mCandidatesAdapter.notifyDataSetChanged()
        }
    }

    /**
     * 选择花漾字
     */
    fun showFlowerTypeface() {
        showViewVisibility(mCandidatesMenuContainer)
        if(LauncherModel.instance.flowerTypeface == FlowerTypefaceMode.Disabled) {
            val spinner = Spinner(context)
            spinner.layoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            val flowerTypefaces = arrayOf(FlowerTypefaceMode.Mars, FlowerTypefaceMode.FlowerVine, FlowerTypefaceMode.Messy, FlowerTypefaceMode.Germinate,
                FlowerTypefaceMode.Fog,FlowerTypefaceMode.ProhibitAccess, FlowerTypefaceMode.Grass, FlowerTypefaceMode.Wind, FlowerTypefaceMode.Disabled)
            val flowerTypefacesName = resources.getStringArray(R.array.FlowerTypeface)
            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, flowerTypefacesName)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object:AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val select = flowerTypefaces[position]
                    LauncherModel.instance.flowerTypeface = select
                    if(select == FlowerTypefaceMode.Disabled){
                        mLLContainerMenu.removeAllViews()
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    LauncherModel.instance.flowerTypeface = FlowerTypefaceMode.Disabled
                }
            }
            LauncherModel.instance.flowerTypeface = FlowerTypefaceMode.Mars
            mLLContainerMenu.addView(spinner)
        } else {
            mLLContainerMenu.removeAllViews()
            LauncherModel.instance.flowerTypeface = FlowerTypefaceMode.Disabled
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMeasure = MeasureSpec.makeMeasureSpec(instance.heightForCandidates, MeasureSpec.EXACTLY)
        val widthMeasure = MeasureSpec.makeMeasureSpec(instance.skbWidth, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasure, heightMeasure)
    }

    private fun showViewVisibility(candidatesContainer: View) {
        mCandidatesMenuContainer.visibility = GONE
        mCandidatesDataContainer.visibility = GONE
        candidatesContainer.visibility = VISIBLE
    }

    // 刷新主题
    fun updateTheme(textColor: Int) {
        mIvMenuCloseSKB.getDrawable().setTint(textColor)
        mRightArrowBtn.getDrawable().setTint(textColor)
        mCandidatesAdapter.updateTextColor(textColor)
    }
}
