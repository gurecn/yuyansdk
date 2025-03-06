package com.yuyan.imemodule.view.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.handwriting.HdManager.Companion.instance
import com.yuyan.imemodule.handwriting.view.DrawingStrokes
import com.yuyan.imemodule.handwriting.view.Strokes
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import java.util.LinkedList
import kotlin.math.sqrt

/**
 * 手写键盘
 *
 * 继承输入键盘[TextKeyboard]，在输入键盘上层覆盖手写板[Canvas]绘制笔迹效果。
 */
@SuppressLint("ViewConstructor")
class HandwritingKeyboard(context: Context?) : TextKeyboard(context) {
    private val mHandWritingPaint: Paint //绘制笔迹的画笔
    private val mDrawing: DrawingStrokes
    private var mLastUpTime: Long = 0 //记录上次手写抬手时间，与本次按下时间对比。
    private val mSBPoint: MutableList<Short?> = LinkedList()
    private var isHandleHandwriting = false //判断是否正在手写

    init {
        //笔划
        val strokes = Strokes()
        mDrawing = DrawingStrokes(this, strokes)
        mHandWritingPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    /**
     * 重置主题
     */
    override fun setTheme(theme: Theme) {
        super.setTheme(theme)
        mHandWritingPaint.setColor(mActiveTheme.keyTextColor)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mDrawing.setSize(width.toFloat(), height.toFloat(), mHandWritingPaint)
        mDrawing.draw(canvas, mHandWritingPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(me: MotionEvent): Boolean {
        val x = me.x.toInt()
        val y = me.y.toInt()
        val softKey = getKeyIndices(x, y)
        if (softKey == null && me.actionMasked == MotionEvent.ACTION_DOWN || isHandleHandwriting) {
            handleHandwriting(me)
            return true
        }
        return super.onTouchEvent(me)
    }

    //处理手写事件
    private fun handleHandwriting(event: MotionEvent) {
        mSBPoint.add(event.x.toInt().toShort())
        mSBPoint.add(event.y.toInt().toShort())
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                isHandleHandwriting = true
                val times = 1200 - getInstance().handwriting.handWritingSpeed.getValue()
                //默认1.3s
                if (mLastUpTime != 0L && System.currentTimeMillis() - mLastUpTime > times) {
                    val key = SoftKey()
                    mService!!.responseKeyEvent(key)
                    mSBPoint.clear()
                    mDrawing.clear()
                }
                mDrawing.actionDown(event.x, event.y, event.pressure)
            }

            MotionEvent.ACTION_MOVE -> {
                val historySize = event.historySize
                for (i in 0 until historySize) {
                    val historicalX = event.getHistoricalX(i)
                    val historicalY = event.getHistoricalY(i)
                    //判断两点之间的距离是否太短
                    val distance = sqrt(
                        ((historicalX - mDrawing.mPoint[mDrawing.mPoint.size - 1].x)
                                * (historicalX - mDrawing.mPoint[mDrawing.mPoint.size - 1].x)
                                + (historicalY - mDrawing.mPoint[mDrawing.mPoint.size - 1].y)
                                * (historicalY - mDrawing.mPoint[mDrawing.mPoint.size - 1].y)).toDouble()
                    )
                    if (mDrawing.mPoint.size > 0 && distance > 0.2) {
                        mDrawing.actionMove(
                            historicalX,
                            historicalY,
                            event.getHistoricalPressure(i)
                        )
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_POINTER_UP -> {
                isHandleHandwriting = false
                mLastUpTime = System.currentTimeMillis()
                mDrawing.actionUp(event.x, event.y, event.pressure)
                mSBPoint.add(-1)
                mSBPoint.add(0)
                recognitionData()
            }

            else -> {}
        }
        invalidate()
    }
    
    private fun recognitionData() {
        instance!!.recognitionData(mSBPoint) {
            item -> mService?.postDelayed({ mService!!.responseHandwritingResultEvent(item) }, 20)
        }
    }
}