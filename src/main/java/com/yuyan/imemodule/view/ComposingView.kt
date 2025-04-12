package com.yuyan.imemodule.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.service.DecodingInfo
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import splitties.dimensions.dp

/**
 * 拼音字符串View，用于显示输入的拼音。
 */
class ComposingView(context: Context) : View(context) {
    /**
     * Used to draw composing string. When drawing the active and idle part of
     * the spelling(Pinyin) string, the color may be changed.
     */
    private val mPaint: Paint = Paint() // 显示拼音字符串

    /**
     * Used to estimate dimensions to show the string .
     */
    private val mFmi: FontMetricsInt

    private var mComposingDisplay:String = ""  //显示拼音

    init {
        mPaint.setColor(ThemeManager.activeTheme.keyTextColor)
        mPaint.isAntiAlias = true
        mPaint.textSize = dp(16).toFloat()
        mFmi = mPaint.getFontMetricsInt()
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setPadding(dp(10), 0, dp(10),0)
    }

    /**
     * 重置拼音字符串View状态
     */
    fun reset() {
        invalidate()
    }

    /**
     * 设置 解码操作对象，然后刷新View。
     */
    fun setDecodingInfo() {
        mComposingDisplay = DecodingInfo.composingStrForDisplay
        if (mComposingDisplay.isBlank()) {
            visibility = INVISIBLE
        }  else {
            visibility = VISIBLE
            requestLayout()
            invalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width: Float
        val height = instance.heightForComposingView
        if (mComposingDisplay.isBlank()) {
            width = 0f
        } else {
            width = (getPaddingLeft() + getPaddingRight() + LEFT_RIGHT_MARGIN * 2).toFloat()
            if (mComposingDisplay.length > COMPOSING_STR_LENGTH) {
                mComposingDisplay = mComposingDisplay.substring(mComposingDisplay.length - COMPOSING_STR_LENGTH)
            }
            width += mPaint.measureText(mComposingDisplay, 0, mComposingDisplay.length)
        }
        val widthMeasure = MeasureSpec.makeMeasureSpec(width.toInt(), MeasureSpec.EXACTLY)
        val heightMeasure = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasure, heightMeasure)
    }

    override fun onDraw(canvas: Canvas) {
        drawForPinyin(canvas)
    }

    /**
     * 画拼音字符串
     */
    private fun drawForPinyin(canvas: Canvas) {
        val x: Float = (paddingLeft + LEFT_RIGHT_MARGIN).toFloat()
        val y: Float = (paddingTop - mFmi.top).toFloat()
        if (mComposingDisplay.length > COMPOSING_STR_LENGTH) {
            mComposingDisplay = mComposingDisplay.substring(mComposingDisplay.length - COMPOSING_STR_LENGTH + 3)
            canvas.drawText(SUSPENSION_POINTS + mComposingDisplay, 0, (SUSPENSION_POINTS + mComposingDisplay).length, x, y, mPaint)
        } else if (!TextUtils.isEmpty(mComposingDisplay)){
            canvas.drawText(mComposingDisplay, 0, mComposingDisplay.length, x, y, mPaint)
        }
    }

    fun updateTheme(theme: Theme) {
        mPaint.setColor(theme.keyTextColor)
        setBackgroundColor(theme.barColor)
    }

    companion object {
        private const val SUSPENSION_POINTS = "…"
        private const val LEFT_RIGHT_MARGIN = 5 //左右间隔
        private const val COMPOSING_STR_LENGTH = 50 //字符串最大长度
    }
}
