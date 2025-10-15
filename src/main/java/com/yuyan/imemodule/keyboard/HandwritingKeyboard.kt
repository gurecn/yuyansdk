package com.yuyan.imemodule.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import androidx.core.graphics.createBitmap
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.entity.handwriting.Bezier
import com.yuyan.imemodule.entity.handwriting.ControlTimedPoints
import com.yuyan.imemodule.entity.handwriting.TimedPoint
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import com.yuyan.inputmethod.HWEngine
import java.util.LinkedList
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sqrt

class HandwritingKeyboard(context: Context?) : TextKeyboard(context) {

    private val mPointsCache: MutableList<TimedPoint> = ArrayList<TimedPoint>()
    private val mControlTimedPointsCached = ControlTimedPoints()
    private var mLastUpTime: Long = 0 //记录上次手写抬手时间，与本次按下时间对比。
    private val mSBPoint: MutableList<Short?> = LinkedList()
    private var mPoints: MutableList<TimedPoint> =  ArrayList<TimedPoint>()
    private var mLastVelocity = 0f
    private var mLastWidth = 0f
    private var mMaxWidth: Int = convertDpToPx(12f)
    private var mMinWidth: Int = mMaxWidth/2
    private var mVelocityFilterWeight = 0.7f
    private val mPaint = Paint()
    private var mSignatureBitmap: Bitmap? = null
    private var mSignatureBitmapCanvas: Canvas? = null
    private val times = 1200L - getInstance().handwriting.handWritingSpeed.getValue()  // 默认1.3s

    init {
        this.mPaint.setColor(MEASURED_STATE_MASK)
        mPaint.setAntiAlias(true)
        mPaint.setStyle(Paint.Style.STROKE)
        mPaint.setStrokeCap(Paint.Cap.ROUND)
        mPaint.setStrokeJoin(Paint.Join.ROUND)
        clear()
    }

    override fun setTheme(theme: Theme) {
        super.setTheme(theme)
        mPaint.setColor(mActiveTheme.keyTextColor)
    }

    fun clear() {
        mPoints.clear()
        mLastVelocity = 0f
        mLastWidth = mMinWidth.toFloat()
        if (mSignatureBitmap != null) {
            mSignatureBitmap = null
            ensureSignatureBitmap()
        }
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(me: MotionEvent): Boolean {
        if(!isEnabled || !InputModeSwitcherManager.isChineseHandWriting) {
            return super.onTouchEvent(me)
        }
        val eventX = me.x
        val eventY = me.y
        val softKey = onKeyPressHandwriting(eventX.toInt(), eventY.toInt())
        if (softKey != null) {
            return super.onTouchEvent(me)
        }
        mSBPoint.add(me.x.toInt().toShort())
        mSBPoint.add(me.y.toInt().toShort())
        when (me.action) {
            MotionEvent.ACTION_DOWN -> {
                val paintWidthMax = getInstance().handwriting.handWritingWidth.getValue() * 4 / 10f
                mMaxWidth = convertDpToPx(paintWidthMax)
                mMinWidth = convertDpToPx(paintWidthMax/2f)
                parent.requestDisallowInterceptTouchEvent(true)
                mPoints.clear()
                addPoint(getNewPoint(eventX, eventY))
                if (mLastUpTime != 0L && System.currentTimeMillis() - mLastUpTime > times) {
                    val key = SoftKey()
                    mService!!.responseKeyEvent(key)
                    mSBPoint.clear()
                    clear()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                addPoint(getNewPoint(eventX, eventY))
                updatePathDelayed()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                addPoint(getNewPoint(eventX, eventY))
                parent.requestDisallowInterceptTouchEvent(true)
                mLastUpTime = System.currentTimeMillis()
                mSBPoint.add(-1)
                mSBPoint.add(0)
                recognitionData()
                updatePathDelayed()
            }
            else -> return false
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mSignatureBitmap != null) {
            canvas.drawBitmap(mSignatureBitmap!!, 0f, 0f, mPaint)
        }
    }

    private fun getNewPoint(x: Float, y: Float): TimedPoint? {
        val mCacheSize = mPointsCache.size
        val timedPoint = if (mCacheSize == 0) TimedPoint() else  mPointsCache.removeAt(mCacheSize - 1)
        return timedPoint.set(x, y)
    }

    private fun recyclePoint(point: TimedPoint?) {
        mPointsCache.add(point!!)
    }

    private fun addPoint(newPoint: TimedPoint?) {
        mPoints.add(newPoint!!)
        val pointsCount = mPoints.size
        if (pointsCount > 3) {
            var tmp = calculateCurveControlPoints(mPoints[0], mPoints[1], mPoints[2])
            val c2 = tmp.c2!!
            recyclePoint(tmp.c1)
            tmp = calculateCurveControlPoints(mPoints[1], mPoints[2], mPoints[3])
            val c3 = tmp.c1!!
            recyclePoint(tmp.c2)
            val curve = Bezier(mPoints[1], c2, c3, mPoints[2])
            val startPoint = curve.startPoint
            val endPoint = curve.endPoint
            var velocity = endPoint.velocityFrom(startPoint)
            velocity = if (velocity.isNaN()) 0.0f else velocity
            velocity = (mVelocityFilterWeight * velocity + (1 - mVelocityFilterWeight) * mLastVelocity)
            val newWidth = strokeWidth(velocity)
            addBezier(curve, mLastWidth, newWidth)
            mLastVelocity = velocity
            mLastWidth = newWidth
            recyclePoint(mPoints.removeAt(0))
            recyclePoint(c2)
            recyclePoint(c3)
        } else if (pointsCount == 1) {
            val firstPoint = mPoints[0]
            mPoints.add(getNewPoint(firstPoint.x, firstPoint.y)!!)
        }
    }

    private fun addBezier(curve: Bezier, startWidth: Float, endWidth: Float) {
        ensureSignatureBitmap()
        val originalWidth = mPaint.strokeWidth
        val widthDelta = endWidth - startWidth
        val drawSteps = ceil(curve.length().toDouble()).toFloat()
        var i = 0
        while (i < drawSteps) {
            val t = (i.toFloat()) / drawSteps
            val tt = t * t
            val ttt = tt * t
            val u = 1 - t
            val uu = u * u
            val uuu = uu * u
            var x = uuu * curve.startPoint.x
            x += 3 * uu * t * curve.control1.x
            x += 3 * u * tt * curve.control2.x
            x += ttt * curve.endPoint.x
            var y = uuu * curve.startPoint.y
            y += 3 * uu * t * curve.control1.y
            y += 3 * u * tt * curve.control2.y
            y += ttt * curve.endPoint.y
            mPaint.setStrokeWidth(startWidth + ttt * widthDelta)
            mSignatureBitmapCanvas!!.drawPoint(x, y, mPaint)
            i++
        }
        mPaint.setStrokeWidth(originalWidth)
    }

    private fun calculateCurveControlPoints(s1: TimedPoint, s2: TimedPoint, s3: TimedPoint): ControlTimedPoints {
        val dx1 = s1.x - s2.x
        val dy1 = s1.y - s2.y
        val dx2 = s2.x - s3.x
        val dy2 = s2.y - s3.y
        val m1X = (s1.x + s2.x) / 2.0f
        val m1Y = (s1.y + s2.y) / 2.0f
        val m2X = (s2.x + s3.x) / 2.0f
        val m2Y = (s2.y + s3.y) / 2.0f
        val l1 = sqrt((dx1 * dx1 + dy1 * dy1).toDouble()).toFloat()
        val l2 = sqrt((dx2 * dx2 + dy2 * dy2).toDouble()).toFloat()
        val dxm = (m1X - m2X)
        val dym = (m1Y - m2Y)
        var k = l2 / (l1 + l2)
        if (k.isNaN()) k = 0.0f
        val cmX = m2X + dxm * k
        val cmY = m2Y + dym * k
        val tx = s2.x - cmX
        val ty = s2.y - cmY
        return mControlTimedPointsCached.set(
            getNewPoint(m1X + tx, m1Y + ty),
            getNewPoint(m2X + tx, m2Y + ty)
        )
    }

    private fun strokeWidth(velocity: Float): Float {
        return max((mMaxWidth / (velocity + 1)).toDouble(), mMinWidth.toDouble()).toFloat()
    }

    private fun ensureSignatureBitmap() {
        if (mSignatureBitmap == null) {
            mSignatureBitmap = createBitmap(width, height)
            mSignatureBitmapCanvas = Canvas(mSignatureBitmap!!)
        }
    }

    private fun convertDpToPx(dp: Float): Int {
        return (context.resources.displayMetrics.density * dp).roundToInt()
    }

    /**
     * 手写键盘获取按下按键
     */
    private fun onKeyPressHandwriting(x: Int, y: Int): SoftKey? {
        return mSoftKeyboard!!.mapToKey(x, y)
    }

    private fun recognitionData() {
        HWEngine.recognitionData(mSBPoint) {
                item -> mService?.postDelayed({ mService!!.responseHandwritingResultEvent(item) }, 20)
        }
    }

    fun updatePathDelayed() {
        runnable.let { handler?.removeCallbacks(it) }
        handler?.postDelayed(runnable, times)
    }

    // 清空笔迹
    var runnable = Runnable {
        clear()
    }
}