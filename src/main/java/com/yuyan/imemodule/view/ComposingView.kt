package com.yuyan.imemodule.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.yuyan.imemodule.R
import com.yuyan.imemodule.service.DecodingInfo
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance

/**
 * 拼音字符串View，用于显示输入的拼音。
 */
class ComposingView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    /**
     * Used to draw composing string. When drawing the active and idle part of
     * the spelling(Pinyin) string, the color may be changed.
     */
    private val mPaint // 显示拼音字符串
            : Paint

    /**
     * Used to estimate dimensions to show the string .
     */
    private val mFmi: FontMetricsInt

    /**
     * 解码操作对象
     */
    var mDecInfo: DecodingInfo? = null

    init {
        val r = context.resources
        // 字符串普通颜色
        val textColor = ContextCompat.getColor(context, android.R.color.tab_indicator_text)
        val mFontSize = r.getDimensionPixelSize(R.dimen.composing_height)
        mPaint = Paint()
        mPaint.setColor(textColor)
        mPaint.isAntiAlias = true
        mPaint.textSize = mFontSize.toFloat()
        mFmi = mPaint.getFontMetricsInt()
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
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
    fun setDecodingInfo(decInfo: DecodingInfo?) {
        mDecInfo = decInfo
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width: Float
        val height = instance!!.heightForComposingView
        if (null == mDecInfo || mDecInfo!!.isFinish) {
            width = 0f
        } else {
            width = (getPaddingLeft() + getPaddingRight() + LEFT_RIGHT_MARGIN * 2).toFloat()
            var str = mDecInfo!!.composingStrForDisplay
            if (!TextUtils.isEmpty(str) && str.length > COMPOSING_STR_LENGTH) {
                str = str.substring(str.length - COMPOSING_STR_LENGTH)
            }
            width += mPaint.measureText(str, 0, str.length)
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
        val x: Float = (getPaddingLeft() + LEFT_RIGHT_MARGIN).toFloat()
        val y: Float = (-mFmi.top + paddingTop).toFloat()
        if (mDecInfo == null) return
        var cmpsStr = mDecInfo!!.composingStrForDisplay
        if (!TextUtils.isEmpty(cmpsStr) && cmpsStr.length > COMPOSING_STR_LENGTH) {
            cmpsStr = cmpsStr.substring(cmpsStr.length - COMPOSING_STR_LENGTH + 3)
            canvas.drawText(
                SUSPENSION_POINTS + cmpsStr,
                0,
                (SUSPENSION_POINTS + cmpsStr).length,
                x,
                y,
                mPaint
            )
        } else {
            canvas.drawText(cmpsStr, 0, cmpsStr.length, x, y, mPaint)
        }
    }

    fun updateTheme(textColor: Int) {
        // 刷新主题
        mPaint.setColor(textColor)
    }

    companion object {
        /**
         * Suspension points used to display long items. 省略号
         */
        private const val SUSPENSION_POINTS = "…"
        private const val LEFT_RIGHT_MARGIN = 5 //左右间隔
        private const val COMPOSING_STR_LENGTH = 50 //字符串最大长度
    }
}
