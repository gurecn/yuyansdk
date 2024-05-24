package com.yuyan.imemodule.view.skbitem.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View

/**
 * Author: Gaolei
 * @describe: 指示器
 */
class IndicatorView(context: Context?) : View(context, null, 0) {
    private var indicatorColor = 0
    private var indicatorColorSelected = 0
    private var indicatorWidth = 0
    private var mPaint: Paint? = null
    private var indicatorCount = 0
    private var currentIndicator = 0

    @SuppressLint("HandlerLeak")
    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == 0x12) {
                invalidate()
            }
        }
    }
    private var mRectF: RectF? = null

    init {
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        indicatorColor = Color.rgb(0, 0, 0)
        indicatorColorSelected = Color.rgb(0, 0, 0)
        mRectF = RectF()
    }

    override fun onDraw(canvas: Canvas) {
        val viewWidth = width
        val viewHeight = height
        val totalWidth = indicatorWidth * (2 * indicatorCount - 1)
        if (indicatorCount > 0) {
            for (i in 0 until indicatorCount) {
                if (i == currentIndicator) {
                    mPaint!!.setColor(indicatorColorSelected)
                } else {
                    mPaint!!.setColor(indicatorColor)
                }
                val left = (viewWidth - totalWidth) / 2 + i * 2 * indicatorWidth
                val top = (viewHeight - indicatorWidth) / 2
                val right = left + indicatorWidth
                val bottom = top + indicatorWidth
                mRectF!![left.toFloat(), top.toFloat(), right.toFloat()] = bottom.toFloat()
                canvas.drawOval(mRectF!!, mPaint!!)
            }
        }
    }

    /**
     * 设置指示器数量
     * @param indicatorCount  指示器数量
     */
    fun setIndicatorCount(indicatorCount: Int) {
        this.indicatorCount = indicatorCount
    }

    /**
     * 设置当前选中指示器
     * @param currentIndicator  选中指示器下标
     */
    fun setCurrentIndicator(currentIndicator: Int) {
        this.currentIndicator = currentIndicator
        handler.sendEmptyMessage(0x12)
    }

    /**
     * 设置指示器颜色
     * @param indicatorColor  未选中指示器颜色
     */
    fun setIndicatorColor(indicatorColor: Int) {
        this.indicatorColor = indicatorColor
    }

    /**
     * 设置选中指示器颜色
     * @param indicatorColorSelected  选中指示器颜色
     */
    fun setIndicatorColorSelected(indicatorColorSelected: Int) {
        this.indicatorColorSelected = indicatorColorSelected
    }

    /**
     * 设置指示器大小
     * @param indicatorWidth  指示器直径
     */
    fun setIndicatorWidth(indicatorWidth: Int) {
        this.indicatorWidth = indicatorWidth
    }
}
