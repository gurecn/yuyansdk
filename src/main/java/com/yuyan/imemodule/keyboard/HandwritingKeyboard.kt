package com.yuyan.imemodule.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.DisplayMetrics
import android.view.MotionEvent
import androidx.core.graphics.createBitmap
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import com.yuyan.imemodule.entity.handwriting.Bezier
import com.yuyan.imemodule.entity.handwriting.ControlTimedPoints
import com.yuyan.imemodule.entity.handwriting.TimedPoint
import com.yuyan.inputmethod.core.HandWriting
import java.util.LinkedList
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

class HandwritingKeyboard(context: Context?) : TextKeyboard(context) {

    private var mLastUpTime: Long = 0 //记录上次手写抬手时间，与本次按下时间对比。
    private val mSBPoint: MutableList<Short?> = LinkedList()
    private var mPoints: MutableList<TimedPoint> =  ArrayList<TimedPoint>()
    private var mLastTouchX = 0f
    private var mLastTouchY = 0f
    private var mLastVelocity = 0f
    private var mLastWidth = 0f
    private val mRect: RectF
    private var mMaxWidth: Int
    private var mMinWidth = convertDpToPx(6.0f)
    private var mVelocityFilterWeight = 0.7f
    private val mPaint = Paint()
    private val mPath = Path()
    private var mSignatureBitmap: Bitmap? = null
    private var mSignatureBitmapCanvas: Canvas? = null
    private val times = 1200L - getInstance().handwriting.handWritingSpeed.getValue()  // 默认1.3s

    init {
        this.mPaint.setColor(MEASURED_STATE_MASK)
        mPaint.setAntiAlias(true)
        mPaint.setStyle(Paint.Style.STROKE)
        mPaint.setStrokeCap(Paint.Cap.ROUND)
        mPaint.setStrokeJoin(Paint.Join.ROUND)
        val paintWidth = getInstance().handwriting.handWritingWidth.getValue()
        mMaxWidth = convertDpToPx(40f * paintWidth / 100f)
        mRect = RectF()
        clear()
    }

    override fun setTheme(theme: Theme) {
        super.setTheme(theme)
        mPaint.setColor(mActiveTheme.keyTextColor)
    }


    fun clear() {
        mPoints.clear()
        mLastVelocity = 0f
        mLastWidth = ((mMinWidth + mMaxWidth) / 2).toFloat()
        mPath.reset()
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
                parent.requestDisallowInterceptTouchEvent(true)
                mPoints.clear()
                mPath.moveTo(eventX, eventY)
                mLastTouchX = eventX
                mLastTouchY = eventY
                addPoint(TimedPoint(eventX, eventY))
                resetDirtyRect(eventX, eventY)
                addPoint(TimedPoint(eventX, eventY))
                if (mLastUpTime != 0L && System.currentTimeMillis() - mLastUpTime > times) {
                    val key = SoftKey()
                    mService!!.responseKeyEvent(key)
                    mSBPoint.clear()
                    clear()
                }
            }

            MotionEvent.ACTION_MOVE -> {
                resetDirtyRect(eventX, eventY)
                addPoint(TimedPoint(eventX, eventY))
                updatePathDelayed()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                resetDirtyRect(eventX, eventY)
                addPoint(TimedPoint(eventX, eventY))
                parent.requestDisallowInterceptTouchEvent(true)
                mLastUpTime = System.currentTimeMillis()
                mSBPoint.add(-1)
                mSBPoint.add(0)
                recognitionData()
                updatePathDelayed()
            }
            else -> return false
        }
        invalidate((mRect.left - mMaxWidth).toInt(), (mRect.top - mMaxWidth).toInt(),
            (mRect.right + mMaxWidth).toInt(), (mRect.bottom + mMaxWidth).toInt())
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mSignatureBitmap != null) {
            canvas.drawBitmap(mSignatureBitmap!!, 0f, 0f, mPaint)
        }
    }

    private fun addPoint(newPoint: TimedPoint) {
        mPoints.add(newPoint)
        if (mPoints.size > 2) {
            if (mPoints.size == 3) mPoints.add(0, mPoints[0])
            var tmp = calculateCurveControlPoints(mPoints[0], mPoints[1], mPoints[2])
            val c2 = tmp.c2
            tmp = calculateCurveControlPoints(mPoints[1], mPoints[2], mPoints[3])
            val c3 = tmp.c1
            val curve = Bezier(mPoints[1], c2, c3, mPoints[2])
            val startPoint = curve.startPoint
            val endPoint = curve.endPoint
            var velocity = endPoint.velocityFrom(startPoint)
            velocity = if (velocity.isNaN()) 0.0f else velocity
            velocity = mVelocityFilterWeight * velocity + (1 - mVelocityFilterWeight) * mLastVelocity
            val newWidth = strokeWidth(velocity)
            addBezier(curve, mLastWidth, newWidth)
            mLastVelocity = velocity
            mLastWidth = newWidth
            mPoints.removeAt(0)
        }
    }

    private fun addBezier(curve: Bezier, startWidth: Float, endWidth: Float) {
        ensureSignatureBitmap()
        val originalWidth = mPaint.strokeWidth
        val widthDelta = endWidth - startWidth
        val drawSteps = floor(curve.length().toDouble()).toFloat()
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
            expandDirtyRect(x, y)
            i++
        }
        mPaint.setStrokeWidth(originalWidth)
    }

    private fun calculateCurveControlPoints(s1: TimedPoint, s2: TimedPoint, s3: TimedPoint): ControlTimedPoints {
        val dx1 = s1.x - s2.x
        val dy1 = s1.y - s2.y
        val dx2 = s2.x - s3.x
        val dy2 = s2.y - s3.y
        val m1 = TimedPoint((s1.x + s2.x) / 2.0f, (s1.y + s2.y) / 2.0f)
        val m2 = TimedPoint((s2.x + s3.x) / 2.0f, (s2.y + s3.y) / 2.0f)
        val l1 = sqrt((dx1 * dx1 + dy1 * dy1).toDouble()).toFloat()
        val l2 = sqrt((dx2 * dx2 + dy2 * dy2).toDouble()).toFloat()
        val dxm = (m1.x - m2.x)
        val dym = (m1.y - m2.y)
        val k = l2 / (l1 + l2)
        val cm = TimedPoint(m2.x + dxm * k, m2.y + dym * k)
        val tx = s2.x - cm.x
        val ty = s2.y - cm.y
        return ControlTimedPoints(TimedPoint(m1.x + tx, m1.y + ty), TimedPoint(m2.x + tx, m2.y + ty))
    }

    private fun strokeWidth(velocity: Float): Float {
        return if(velocity < 0.2f) velocity else max((mMaxWidth / (velocity + 1f)), mMinWidth.toFloat())
    }

    /**
     * Called when replaying history to ensure the dirty region includes all
     * mPoints.
     */
    private fun expandDirtyRect(historicalX: Float, historicalY: Float) {
        if (historicalX < mRect.left) {
            mRect.left = historicalX
        } else if (historicalX > mRect.right) {
            mRect.right = historicalX
        }
        if (historicalY < mRect.top) {
            mRect.top = historicalY
        } else if (historicalY > mRect.bottom) {
            mRect.bottom = historicalY
        }
    }

    /**
     * Resets the dirty region when the motion event occurs.
     */
    private fun resetDirtyRect(eventX: Float, eventY: Float) {
        mRect.left = min(mLastTouchX.toDouble(), eventX.toDouble()).toFloat()
        mRect.right = max(mLastTouchX.toDouble(), eventX.toDouble()).toFloat()
        mRect.top = min(mLastTouchY.toDouble(), eventY.toDouble()).toFloat()
        mRect.bottom = max(mLastTouchY.toDouble(), eventY.toDouble()).toFloat()
    }

    private fun ensureSignatureBitmap() {
        if (mSignatureBitmap == null) {
            mSignatureBitmap = createBitmap(width, height)
            mSignatureBitmapCanvas = Canvas(mSignatureBitmap!!)
        }
    }

    private fun convertDpToPx(dp: Float): Int {
        return (dp * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    /**
     * 手写键盘获取按下按键
     */
    private fun onKeyPressHandwriting(x: Int, y: Int): SoftKey? {
        return mSoftKeyboard!!.mapToKey(x, y)
    }

    private fun recognitionData() {
        HandWriting.recognitionData(mSBPoint) {
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