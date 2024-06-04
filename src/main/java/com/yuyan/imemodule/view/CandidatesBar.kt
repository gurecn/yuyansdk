package com.yuyan.imemodule.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.LevelListDrawable
import android.graphics.drawable.VectorDrawable
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
import com.yuyan.imemodule.callback.CandidateViewListener
import com.yuyan.imemodule.constant.CustomConstant
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
    // 候选词视图监听器
    private var mCvListener: CandidateViewListener? = null
    // 右边箭头按钮
    private var mRightArrowBtn: ImageView? = null
    // 词库解码对象
    private var mDecInfo: DecodingInfo? = null
    private var mCandidatesDataContainer: LinearLayout? = null //候选词视图
    private var mCandidatesMenuContainer: LinearLayout? = null //控制菜单视图
    private var mRVCandidates: RecyclerView? = null
    private var mIvMenuCloseSKB: ImageView? = null
    private var mCandidatesAdapter: CandidatesBarAdapter? = null
    private var mLLContainerMenu:LinearLayout?= null
    @SuppressLint("ClickableViewAccessibility")
    fun initialize(cvListener: CandidateViewListener?, decInfo: DecodingInfo?) {
        mDecInfo = decInfo
        mCvListener = cvListener
        initMenuView()
        initCandidateView()
    }

    /**
     * 初始化候选词界面
     */
    private fun initCandidateView() {
        if(mCandidatesDataContainer == null) {
            mCandidatesDataContainer = LinearLayout(context)
            mCandidatesDataContainer!!.gravity = Gravity.CENTER_VERTICAL
            mRightArrowBtn = ImageView(context)
            mRightArrowBtn!!.isClickable = true
            mRightArrowBtn!!.setEnabled(true)
            mRightArrowBtn!!.setOnClickListener { v: View ->
                if ((v as ImageView).getDrawable().level == 3) { //关闭键盘修改为重置候选词
                    mCvListener!!.onClickClearCandidate()
                } else {
                    val level = v.getDrawable().level
                    if(level == 0) {
                        var lastItemPosition = 0
                        val layoutManager = mRVCandidates!!.layoutManager
                        if (layoutManager is LinearLayoutManager) {
                            lastItemPosition = layoutManager.findLastVisibleItemPosition()
                        }
                        if(mDecInfo!!.mCandidatesList.size > lastItemPosition + 1) {
                            mCvListener!!.onClickMore(level, lastItemPosition)
                            v.getDrawable().setLevel(1)
                        }
                    } else {
                        mCvListener!!.onClickMore(level, 0)
                        v.getDrawable().setLevel(0)
                    }
                }
            }
            mRightArrowBtn!!.setImageResource(R.drawable.sdk_level_list_candidates_display)
            val candidatesAreaHeight = instance.heightForCandidates
            val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 0f)
            val margin = dip2px(10f)
            val size = (candidatesAreaHeight * 0.2).toInt()
            mRightArrowBtn!!.setPadding(margin, size, margin, size)
            mRightArrowBtn!!.setLayoutParams(layoutParams)
            mRVCandidates = RecyclerView(context)
            val layoutManager: LinearLayoutManager =
                object : LinearLayoutManager(context, HORIZONTAL, false) {
                    override fun canScrollHorizontally(): Boolean {
                        return false
                    }
                }
            mRVCandidates!!.setLayoutManager(layoutManager)
            val layoutParamRV = LinearLayout.LayoutParams(0, candidatesAreaHeight, 1f)
            mRVCandidates!!.setLayoutParams(layoutParamRV)
            if (mDecInfo != null) {
                mCandidatesAdapter = CandidatesBarAdapter(context, mDecInfo!!.mCandidatesList)
                mCandidatesAdapter!!.setOnItemClickLitener { _: RecyclerView.Adapter<*>?, _: View?, position: Int ->
                    mCvListener!!.onClickChoice(position)
                }
                mRVCandidates!!.setAdapter(mCandidatesAdapter)
            }
            mCandidatesDataContainer!!.visibility = GONE
            val layoutParam = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            mCandidatesDataContainer!!.setLayoutParams(layoutParam)
            this.addView(mCandidatesDataContainer)
        } else {
            (mRightArrowBtn?.parent as ViewGroup).removeView(mRightArrowBtn)
            (mRVCandidates?.parent as ViewGroup).removeView(mRVCandidates)
        }
        val oneHandedMod = prefs.oneHandedMod.getValue()
        if (oneHandedMod == KeyboardOneHandedMod.LEFT) {
            mCandidatesDataContainer!!.addView(mRightArrowBtn)
            mCandidatesDataContainer!!.addView(mRVCandidates)
        } else {
            mCandidatesDataContainer!!.addView(mRVCandidates)
            mCandidatesDataContainer!!.addView(mRightArrowBtn)
        }
    }

    //初始化标题栏
    private fun initMenuView() {
        if(mCandidatesMenuContainer == null) {
            mCandidatesMenuContainer = LinearLayout(context)
            mCandidatesMenuContainer!!.gravity = Gravity.CENTER_VERTICAL
            val layoutParam = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            mCandidatesMenuContainer!!.setLayoutParams(layoutParam)
            val ivMenuSetting = ImageView(context)
            ivMenuSetting.setImageResource(R.drawable.app_icon)
            ivMenuSetting.isClickable = true
            ivMenuSetting.setEnabled(true)
            val layoutParams = LinearLayout.LayoutParams(
                instance.heightForCandidates,
                ViewGroup.LayoutParams.MATCH_PARENT,
                0f
            )
            layoutParam.leftMargin = dip2px(10f)
            layoutParam.rightMargin = dip2px(10f)
            ivMenuSetting.setLayoutParams(layoutParams)
            ivMenuSetting.setOnClickListener { mCvListener!!.onClickSetting() }
            mCandidatesMenuContainer!!.addView(ivMenuSetting)
            val layoutParamsLL =
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            mLLContainerMenu = LinearLayout(context)
            mLLContainerMenu?.layoutParams = layoutParamsLL
            mCandidatesMenuContainer!!.addView(mLLContainerMenu)
            mIvMenuCloseSKB = ImageView(context)
            mIvMenuCloseSKB?.setImageResource(R.drawable.sdk_skb_close_icon)
            mIvMenuCloseSKB?.isClickable = true
            mIvMenuCloseSKB?.setEnabled(true)
            mIvMenuCloseSKB?.setLayoutParams(layoutParams)
            mIvMenuCloseSKB?.setOnClickListener { mCvListener!!.onClickCloseKeyboard() }
            mCandidatesMenuContainer!!.addView(mIvMenuCloseSKB)
            this.addView(mCandidatesMenuContainer)
        }
    }

    /**
     * 显示候选词
     */
    fun showCandidates() {
        if (null == mDecInfo || mDecInfo!!.isCandidatesListEmpty) {
            showViewVisibility(mCandidatesMenuContainer)
            return
        }
        showViewVisibility(mCandidatesDataContainer)
        if (mDecInfo!!.isAssociate) {
            mRightArrowBtn!!.getDrawable().setLevel(3)
        } else {
            val container = KeyboardManager.instance.currentContainer
            if (container is CandidatesContainer) {
                mRightArrowBtn?.getDrawable()?.setLevel(0)
                var lastItemPosition = 0
                val layoutManager1 = mRVCandidates!!.layoutManager
                if (layoutManager1 is LinearLayoutManager) {
                    lastItemPosition = layoutManager1.findLastVisibleItemPosition()
                }
                mCvListener?.onClickMore(0, lastItemPosition)
            } else {
                mRightArrowBtn?.getDrawable()?.setLevel(0)
            }
        }
        mCandidatesAdapter?.notifyDataSetChanged()
    }

    /**
     * 选择花漾字
     */
    fun showFlowerTypeface() {
        showViewVisibility(mCandidatesMenuContainer)
        if(CustomConstant.flowerTypeface == FlowerTypefaceMode.Disabled) {
            val spinner = Spinner(context)
            val layoutParam = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            spinner.layoutParams = layoutParam
            val flowerTypefaces = arrayOf(FlowerTypefaceMode.Mars, FlowerTypefaceMode.FlowerVine, FlowerTypefaceMode.Messy, FlowerTypefaceMode.Germinate,
                FlowerTypefaceMode.Fog,FlowerTypefaceMode.ProhibitAccess, FlowerTypefaceMode.Grass, FlowerTypefaceMode.Wind, FlowerTypefaceMode.Disabled)
            val flowerTypefacesName = resources.getStringArray(R.array.FlowerTypeface)
            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, flowerTypefacesName)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object:AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val select = flowerTypefaces[position]
                    CustomConstant.flowerTypeface = select
                    if(select == FlowerTypefaceMode.Disabled){
                        mLLContainerMenu?.removeAllViews()
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    CustomConstant.flowerTypeface = FlowerTypefaceMode.Disabled
                }
            }
            mLLContainerMenu?.gravity = Gravity.CENTER
            CustomConstant.flowerTypeface = FlowerTypefaceMode.Mars
            mLLContainerMenu?.addView(spinner)
        } else {
            mLLContainerMenu?.removeAllViews()
            CustomConstant.flowerTypeface = FlowerTypefaceMode.Disabled
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measuredHeight = instance.heightForCandidates
        val skbWidth = instance.skbWidth
        val heightMeasure = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
        val widthMeasure = MeasureSpec.makeMeasureSpec(skbWidth, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasure, heightMeasure)
    }

    private fun showViewVisibility(candidatesContainer: View?) {
        mCandidatesMenuContainer?.visibility = GONE
        mCandidatesDataContainer?.visibility = GONE
        candidatesContainer!!.visibility = VISIBLE
    }

    fun updateTheme(textColor: Int) {
        // 刷新主题
        val vectorDrawableCompat = mIvMenuCloseSKB!!.getDrawable() as VectorDrawable
        vectorDrawableCompat.setTint(textColor)
        val drawable = mRightArrowBtn!!.getDrawable() as LevelListDrawable
        drawable.setTint(textColor)
        mCandidatesAdapter?.updateTextColor(textColor)
    }
}
