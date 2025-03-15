package com.yuyan.imemodule.view.keyboard.handwriting

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.DisplayMetrics
import android.view.MotionEvent
import androidx.core.view.ViewCompat
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.view.keyboard.TextKeyboard
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import androidx.core.graphics.createBitmap
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.handwriting.HdManager.Companion.instance
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import com.yuyan.imemodule.utils.LogUtil
import java.util.LinkedList

class SignatureView(context: Context?) : TextKeyboard(context) {

    private var mLastUpTime: Long = 0 //记录上次手写抬手时间，与本次按下时间对比。
    private val mSBPoint: MutableList<Short?> = LinkedList()
    // View state
    private var mPoints: MutableList<TimedPoint?>? = null
    private var mLastTouchX = 0f
    private var mLastTouchY = 0f
    private var mLastVelocity = 0f
    private var mLastWidth = 0f
    private val mRect: RectF

    private var mMaxWidth = convertDpToPx(12.0f)
    private var mMinWidth = convertDpToPx(6.0f)
    private var mVelocityFilterWeight = 0.7f

    private val mPaint = Paint()
    private val mPath = Path()
    private var mSignatureBitmap: Bitmap? = null
    private var mSignatureBitmapCanvas: Canvas? = null

    init {
        this.mPaint.setColor(ViewCompat.MEASURED_STATE_MASK)
        mPaint.setAntiAlias(true)
        mPaint.setStyle(Paint.Style.STROKE)
        mPaint.setStrokeCap(Paint.Cap.ROUND)
        mPaint.setStrokeJoin(Paint.Join.ROUND)
        mRect = RectF()
        clear()
    }

    override fun setTheme(theme: Theme) {
        super.setTheme(theme)
        mPaint.setColor(mActiveTheme.keyTextColor)
    }

    /**
     * Set the minimum width of the stroke in pixel.
     *
     * @param minWidth
     * the width in dp.
     */
    fun setMinWidth(minWidth: Float) {
        mMinWidth = convertDpToPx(minWidth)
    }

    /**
     * Set the maximum width of the stroke in pixel.
     *
     * @param maxWidth
     * the width in dp.
     */
    fun setMaxWidth(maxWidth: Float) {
        mMaxWidth = convertDpToPx(maxWidth)
    }

    /**
     * Set the velocity filter weight.
     *
     * @param velocityFilterWeight
     * the weight.
     */
    fun setVelocityFilterWeight(velocityFilterWeight: Float) {
        mVelocityFilterWeight = velocityFilterWeight
    }

    fun clear() {
        mPoints = ArrayList<TimedPoint?>()
        mLastVelocity = 0f
        mLastWidth = ((mMinWidth + mMaxWidth) / 2).toFloat()
        mPath.reset()

        if (mSignatureBitmap != null) {
            mSignatureBitmap = null
            ensureSignatureBitmap()
        }
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(!isEnabled || !InputModeSwitcherManager.isChineseHandWriting) {
            return super.onTouchEvent(event)
        }
        val eventX = event.getX()
        val eventY = event.getY()
        val softKey = onKeyPressHandwriting(eventX.toInt(), eventY.toInt())
        if (softKey != null) {
            return super.onTouchEvent(event)
        }
        mSBPoint.add(event.x.toInt().toShort())
        mSBPoint.add(event.y.toInt().toShort())
        when (event.getAction()) {
            MotionEvent.ACTION_DOWN -> {
                getParent().requestDisallowInterceptTouchEvent(true)
                mPoints!!.clear()
                mPath.moveTo(eventX, eventY)
                mLastTouchX = eventX
                mLastTouchY = eventY
                addPoint(TimedPoint(eventX, eventY))

                resetDirtyRect(eventX, eventY)
                addPoint(TimedPoint(eventX, eventY))

                val times = 1200 - getInstance().handwriting.handWritingSpeed.getValue()
                //默认1.3s
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
            }

            MotionEvent.ACTION_UP -> {
                resetDirtyRect(eventX, eventY)
                addPoint(TimedPoint(eventX, eventY))
                getParent().requestDisallowInterceptTouchEvent(true)

                mLastUpTime = System.currentTimeMillis()
                mSBPoint.add(-1)
                mSBPoint.add(0)
                recognitionData()
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

    var signatureBitmap: Bitmap
        get() {
            val originalBitmap = this.transparentSignatureBitmap
            val whiteBgBitmap = createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight())
            val canvas = Canvas(whiteBgBitmap)
            canvas.drawColor(Color.WHITE)
            canvas.drawBitmap(originalBitmap, 0f, 0f, null)
            return whiteBgBitmap
        }
        set(signature) {
            clear()
            ensureSignatureBitmap()

            val tempSrc = RectF()
            val tempDst = RectF()

            val dWidth: Int = signature.getWidth()
            val dHeight: Int = signature.getHeight()
            val vWidth = getWidth()
            val vHeight = getHeight()

            // Generate the required transform.
            tempSrc.set(0f, 0f, dWidth.toFloat(), dHeight.toFloat())
            tempDst.set(0f, 0f, vWidth.toFloat(), vHeight.toFloat())

            val drawMatrix = Matrix()
            drawMatrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER)

            val canvas = Canvas(mSignatureBitmap!!)
            canvas.drawBitmap(signature, drawMatrix, null)
            invalidate()
        }

    val transparentSignatureBitmap: Bitmap
        get() {
            ensureSignatureBitmap()
            return mSignatureBitmap!!
        }

    fun getTransparentSignatureBitmap(trimBlankSpace: Boolean): Bitmap? {
        if (!trimBlankSpace) {
            return this.transparentSignatureBitmap
        }

        ensureSignatureBitmap()

        val imgHeight = mSignatureBitmap!!.getHeight()
        val imgWidth = mSignatureBitmap!!.getWidth()

        val backgroundColor = Color.TRANSPARENT

        var xMin = Int.Companion.MAX_VALUE
        var xMax = Int.Companion.MIN_VALUE
        var yMin = Int.Companion.MAX_VALUE
        var yMax = Int.Companion.MIN_VALUE

        var foundPixel = false

        // Find xMin
        for (x in 0..<imgWidth) {
            var stop = false
            for (y in 0..<imgHeight) {
                if (mSignatureBitmap!!.getPixel(x, y) != backgroundColor) {
                    xMin = x
                    stop = true
                    foundPixel = true
                    break
                }
            }
            if (stop) break
        }

        // Image is empty...
        if (!foundPixel) return null

        // Find yMin
        for (y in 0..<imgHeight) {
            var stop = false
            for (x in xMin..<imgWidth) {
                if (mSignatureBitmap!!.getPixel(x, y) != backgroundColor) {
                    yMin = y
                    stop = true
                    break
                }
            }
            if (stop) break
        }

        // Find xMax
        for (x in imgWidth - 1 downTo xMin) {
            var stop = false
            for (y in yMin..<imgHeight) {
                if (mSignatureBitmap!!.getPixel(x, y) != backgroundColor) {
                    xMax = x
                    stop = true
                    break
                }
            }
            if (stop) break
        }

        // Find yMax
        for (y in imgHeight - 1 downTo yMin) {
            var stop = false
            for (x in xMin..xMax) {
                if (mSignatureBitmap!!.getPixel(x, y) != backgroundColor) {
                    yMax = y
                    stop = true
                    break
                }
            }
            if (stop) break
        }

        return Bitmap.createBitmap(mSignatureBitmap!!, xMin, yMin, xMax - xMin, yMax - yMin)
    }

    private fun addPoint(newPoint: TimedPoint?) {
        mPoints!!.add(newPoint)
        if (mPoints!!.size > 2) {
            // To reduce the initial lag make it work with 3 mPoints
            // by copying the first point to the beginning.
            if (mPoints!!.size == 3) mPoints!!.add(0, mPoints!!.get(0))

            var tmp = calculateCurveControlPoints(
                mPoints!!.get(0)!!,
                mPoints!!.get(1)!!,
                mPoints!!.get(2)!!
            )
            val c2 = tmp.c2
            tmp = calculateCurveControlPoints(
                mPoints!!.get(1)!!,
                mPoints!!.get(2)!!,
                mPoints!!.get(3)!!
            )
            val c3 = tmp.c1
            val curve = Bezier(mPoints!!.get(1), c2, c3, mPoints!!.get(2))

            val startPoint = curve.startPoint
            val endPoint = curve.endPoint

            var velocity = endPoint.velocityFrom(startPoint)
            velocity = if (velocity.isNaN()) 0.0f else velocity

            velocity =
                mVelocityFilterWeight * velocity + (1 - mVelocityFilterWeight) * mLastVelocity

            // The new width is a function of the velocity. Higher velocities
            // correspond to thinner strokes.
            val newWidth = strokeWidth(velocity)

            // The Bezier's width starts out as last curve's final width, and
            // gradually changes to the stroke width just calculated. The new
            // width calculation is based on the velocity between the Bezier's
            // start and end mPoints.
            addBezier(curve, mLastWidth, newWidth)

            mLastVelocity = velocity
            mLastWidth = newWidth

            // Remove the first element from the list,
            // so that we always have no more than 4 mPoints in mPoints array.
            mPoints!!.removeAt(0)
        }
    }

    private fun addBezier(curve: Bezier, startWidth: kotlin.Float, endWidth: kotlin.Float) {
        ensureSignatureBitmap()
        val originalWidth = mPaint.getStrokeWidth()
        val widthDelta = endWidth - startWidth
        val drawSteps = floor(curve.length().toDouble()).toFloat()

        var i = 0
        while (i < drawSteps) {
            // Calculate the Bezier (x, y) coordinate for this step.
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

            // Set the incremental stroke width and draw.
            mPaint.setStrokeWidth(startWidth + ttt * widthDelta)
            mSignatureBitmapCanvas!!.drawPoint(x, y, mPaint)
            expandDirtyRect(x, y)
            i++
        }

        mPaint.setStrokeWidth(originalWidth)
    }

    private fun calculateCurveControlPoints(
        s1: TimedPoint,
        s2: TimedPoint,
        s3: TimedPoint
    ): ControlTimedPoints {
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

        return ControlTimedPoints(
            TimedPoint(m1.x + tx, m1.y + ty),
            TimedPoint(m2.x + tx, m2.y + ty)
        )
    }

    private fun strokeWidth(velocity: kotlin.Float): kotlin.Float {
        return max((mMaxWidth / (velocity + 1)).toDouble(), mMinWidth.toDouble()).toFloat()
    }

    /**
     * Called when replaying history to ensure the dirty region includes all
     * mPoints.
     *
     * @param historicalX
     * the previous x coordinate.
     * @param historicalY
     * the previous y coordinate.
     */
    private fun expandDirtyRect(historicalX: kotlin.Float, historicalY: kotlin.Float) {
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
     *
     * @param eventX
     * the event x coordinate.
     * @param eventY
     * the event y coordinate.
     */
    private fun resetDirtyRect(eventX: kotlin.Float, eventY: kotlin.Float) {
        // The mLastTouchX and mLastTouchY were set when the ACTION_DOWN motion
        // event occurred.

        mRect.left = min(mLastTouchX.toDouble(), eventX.toDouble()).toFloat()
        mRect.right = max(mLastTouchX.toDouble(), eventX.toDouble()).toFloat()
        mRect.top = min(mLastTouchY.toDouble(), eventY.toDouble()).toFloat()
        mRect.bottom = max(mLastTouchY.toDouble(), eventY.toDouble()).toFloat()
    }

    private fun ensureSignatureBitmap() {
        if (mSignatureBitmap == null) {
            mSignatureBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888)
            mSignatureBitmapCanvas = Canvas(mSignatureBitmap!!)
        }
    }

    private fun convertDpToPx(dp: kotlin.Float): Int {
        return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    /**
     * 手写键盘获取按下按键
     *
     * @param x 按下坐标x值
     * @param y 按下坐标y值
     * @return 返回按下的按键
     */
    private fun onKeyPressHandwriting(x: Int, y: Int): SoftKey? {
        return mSoftKeyboard!!.mapToKey(x, y)
    }

    private fun recognitionData() {

        LogUtil.d("11111111111", " recognitionData mSBPoint:${mSBPoint.size}   mService:$mService ")
        instance!!.recognitionData(mSBPoint) {
                item -> mService?.postDelayed({ mService!!.responseHandwritingResultEvent(item) }, 20)
        }
    }

}
