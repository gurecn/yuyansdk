package com.yuyan.imemodule.handwriting.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import com.yuyan.imemodule.utils.thread.ThreadPoolUtils
import java.util.Vector
import kotlin.math.acos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Author:gurecn@gmail.com
 * Time:2019/4/22
 * Description:This is DrawingStrokes
 */
class DrawingStrokes(strokeView: View, strokes: Strokes) {
    private val STROKES_MAX_WIDTH: Float = 50f   // 最大笔迹宽度
    var mPaint: Paint? = null
    var timePoints = Vector<TimePoint>()
    var strokesPath: Path? = null
    var lastLineX = 0f
    var lastLineY = 0f
    var mLastX = 0f
    var mLastY = 0f
    var mLLastX = 0f
    var mLLastY = 0f
    @JvmField
    var mPoint: Vector<TimePoint>
    var lastTop = TimePoint()
    var lastBottom = TimePoint()
    var isDown = false
    var isUp = false
    var mLastK = 0f
    var strokeView: View
    var strokes: Strokes? = null

    //抬笔和操作笔划时的绘图
    var canvasStroke: Canvas? = null
    var bitmapStroke: Bitmap? = null
    var splineCurveStrategy: SplineCurveStrategy? = null
    var mLastWidth = 0f
    private var width = 0f
    private var height = 0f
    private var maxWidth = 0f
    fun setSize(width: Float, height: Float, mPaint: Paint?) {
        if (canvasStroke != null) return
        this.width = width
        this.height = height
        initBitmapStroke()
        if (this.mPaint == null) {
            this.mPaint = mPaint
        }
    }

    private fun initBitmapStroke() {
        if (bitmapStroke == null) {
            bitmapStroke =
                Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
            canvasStroke = Canvas(bitmapStroke!!)
        }
    }

    fun strokeWidth(press: Float, widthDelta: Float): Float {
        val width = (min(
            maxWidth.toDouble(),
            (0.1f * (1 + press * (maxWidth * 10 - 1))).toDouble()
        ) * 0.9f + mLastWidth * 0.1f).toFloat()
        return if (width > mLastWidth) min(width.toDouble(), (mLastWidth + widthDelta).toDouble())
            .toFloat() else max(width.toDouble(), (mLastWidth - widthDelta).toDouble())
            .toFloat()
    }

    fun setMaxWidth(maxWidth: Float) {
        this.maxWidth = maxWidth
    }

    fun addPoint(timePoint: TimePoint, pressure: Float) {
        mPoint.add(timePoint)
        if (mPoint.size > 3) {
            val splineCurve = SplineCurve(
                mPoint[0],
                mPoint[1], mPoint[2], mPoint[3]
            )
            val velocity = splineCurve.point3.velocityFrom(splineCurve.point2)
            val widthDelta: Float
            if (velocity > 3) {
                splineCurve.steps = 4
                widthDelta = 3.0f
            } else if (velocity > 2) {
                splineCurve.steps = 3
                widthDelta = 2.0f
            } else if (velocity > 1) {
                splineCurve.steps = 3
                widthDelta = 1.0f
            } else if (velocity > 0.5) {
                splineCurve.steps = 2
                widthDelta = 0.8f
            } else if (velocity > 0.2) {
                splineCurve.steps = 2
                widthDelta = 0.6f
            } else if (velocity > 0.1) {
                splineCurve.steps = 1
                widthDelta = 0.3f
            } else {
                splineCurve.steps = 1
                widthDelta = 0.2f
            }
            var newWidth: Float = strokeWidth(pressure, widthDelta)
            newWidth = if (java.lang.Float.isNaN(newWidth)) mLastWidth else newWidth
            if (splineCurveStrategy == null) {
                splineCurveStrategy =
                    SplineCurveStrategy(splineCurve, mLastWidth, newWidth, canvasStroke!!, mPaint!!)
                splineCurveStrategy!!.initLastPoint(lastTop, lastBottom)
            } else {
                splineCurveStrategy!!.updateData(mLastWidth, newWidth, splineCurve)
            }
            splineCurveStrategy!!.drawPen(this) //钢笔
            mPoint.removeAt(0)
        }
    }

    fun calculateDegree(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float): Float {
        val b = sqrt(((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)).toDouble())
            .toFloat()
        val c = sqrt(
            ((x2 - x3) * (x2 - x3) +
                    (y2 - y3) * (y2 - y3)).toDouble()
        ).toFloat()
        val a = sqrt(
            ((x1 - x3) * (x1 - x3) +
                    (y1 - y3) * (y1 - y3)).toDouble()
        ).toFloat()
        if (c == 0f || b == 0f) return 0f
        val sum = (b * b + c * c - a * a) / (2 * b * c)
        var degree = acos(sum.toDouble()).toFloat() * 180 / Math.PI.toFloat()
        if (java.lang.Float.isNaN(degree)) degree = 0f
        return degree
    }

    var isLoopUpdate = false // 是否有线程执行刷新任务，确保只有一个线程刷新界面，防止闪烁。

    @SuppressLint("HandlerLeak")
    var onUpdatePathToCanvasHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            isLoopUpdate = false
            if (isUp) {
                if (strokes.pathsVectorSize > 0) {
                    updatePathToCanvasDelayed()
                } else {
                    strokeView.invalidate() // 清空笔迹
                }
            }
        }
    }

    fun updatePathToCanvasDelayed() {
        if (isLoopUpdate) return
        isLoopUpdate = true
        ThreadPoolUtils.executeSingleton {
            val interval: Long = 500
            checkPathTime()
            canvasStroke!!.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            strokes!!.draw(canvasStroke!!, mPaint!!)
            onUpdatePathToCanvasHandler.sendEmptyMessageDelayed(0, interval)
        }
    }

    var isChecking = false
    var lastTime: Long = 0
    var lastActionUpTime: Long = 0

    init {
        this.strokes = strokes
        this.strokeView = strokeView
        strokesPath = Path()
        mPoint = Vector()
    }

    fun checkPathTime() {
        if (isChecking || System.currentTimeMillis() - lastTime < 200) return
        isChecking = true
        val size = strokes!!.pathsVector.size
        if (size == 0) return
        val speedAlpha = 30 // 笔迹消失速度
        if (isUp && System.currentTimeMillis() - lastActionUpTime > 1000) {
            strokes!!.pathsVector.clear()
        }
        var itselfAlpha: Int
        val iterator = strokes!!.pathsVector.iterator()
        while (iterator.hasNext()) {
            val stroke = iterator.next()
            itselfAlpha = stroke.alpha - speedAlpha
            if (itselfAlpha <= 20) {
                iterator.remove()
            } else {
                stroke.alpha = itselfAlpha
            }
        }
        lastTime = System.currentTimeMillis()
        isChecking = false
    }

    fun actionDown(x: Float, y: Float, pressure: Float) {
        strokesPath!!.reset()
        timePoints.clear()
        mPoint.clear()
        isDown = true
        isUp = false
        mLLastX = x
        mLLastY = y
        mLastX = x
        mLastY = y
        mLastWidth = min(
            maxWidth.toDouble(),
            (0.1f * (1 + (pressure + 0.2f) * (maxWidth * 10 - 1))).toDouble()
        ).toFloat()
        mLastK = 0f
        strokes!!.addPathsVector()
        addPoint(TimePoint(x, y), pressure)
        addPoint(TimePoint(x, y), pressure)
    }

    fun actionMove(x: Float, y: Float, pressure: Float) {
        addPoint(TimePoint(x, y), pressure)
    }

    fun actionUp(x: Float, y: Float, pressure: Float) {
        addPoint(TimePoint(x, y), pressure)
        isUp = true
        lastActionUpTime = System.currentTimeMillis()
        addPoint(TimePoint(x, y), pressure)
        for (i in timePoints.indices.reversed()) {
            strokesPath!!.lineTo(timePoints.elementAt(i).x, timePoints.elementAt(i).y)
        }
        timePoints.clear()
        if (strokes!!.pathsVector.size > 0) {
            strokes!!.pathsVector.elementAt(strokes!!.pathsVectorSize - 1).setStroke(strokesPath)
        }
        updatePathToCanvasDelayed()
    }

    fun draw(canvas: Canvas, mPaint: Paint) {
        val paintWidth = getInstance().handwriting.handWritingWidth.getValue()
        setMaxWidth(STROKES_MAX_WIDTH * paintWidth / 100f)
        canvas.drawBitmap(bitmapStroke!!, 0f, 0f, mPaint)
    }

    fun clear() {
        strokes!!.pathsVector.clear()
        canvasStroke!!.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }
}
