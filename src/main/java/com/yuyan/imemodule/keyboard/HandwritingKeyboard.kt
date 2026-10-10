package com.yuyan.imemodule.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode
import android.view.MotionEvent
import androidx.core.graphics.createBitmap
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.manager.InputModeSwitcher
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import com.yuyan.inputmethod.HWEngine
import java.util.LinkedList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * 手写键盘。
 */
class HandwritingKeyboard(context: Context?) : TextKeyboard(context) {
    private val density = getContext().resources.displayMetrics.density
    private var mInkBitmap: Bitmap? = null
    private var mInkCanvas: Canvas? = null
    private var mStampBitmap: Bitmap? = null
    private var mStampCanvas: Canvas? = null
    private val mStampPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
    }
    private val mStampPath = Path()
    private val mInkCoverPaint = Paint(Paint.FILTER_BITMAP_FLAG).apply {
        colorFilter = PorterDuffColorFilter(ThemeManager.activeTheme.keyTextColor, PorterDuff.Mode.SRC_IN)
    }
    private val mInkX = FloatList()
    private val mInkY = FloatList()
    private val mInkWidth = FloatList()
    private val mSBPoint: MutableList<Short?> = LinkedList()
    private val mPrevPoint = InputPoint()
    private val mLastPoint = InputPoint()
    private var mPointCount = 0
    private val mRawX = FloatArray(RAW_BUFFER_SIZE)
    private val mRawY = FloatArray(RAW_BUFFER_SIZE)
    private val mRawTime = LongArray(RAW_BUFFER_SIZE)
    private var mRawHead = 0
    private var mRawSize = 0
    private var mDrawing = false
    private var mDelegated = false
    private var mActivePointerId = MotionEvent.INVALID_POINTER_ID
    private var mLastUpTime = 0L
    private var mMaxWidth = 0f
    private var mMinWidth = 0f
    private var mCurrentWidth = 0f  // 当前实际笔宽，受渐变速率限制
    private var mLastInkWidth = 0f
    private var mLastVelocity = 0f
    private var mStrokeLength = 0f
    private var mVelocityReady = false  // 起笔预热阶段是否已结束
    private val mSampleStep = max(1.5f * density, 2f)
    private val mWidthRampLength = 20f * density   // 笔宽从最细变到最粗所需的行进距离
    private val mWarmupDistance = 6f * density     // 起笔预热距离，这段不按速度定笔宽
    private val mEndTaperLength = 6f * density     // 收笔出锋长度
    private val mTailClampLength = 14f * density   // 收笔前限制笔宽回涨的长度
    private val mMinPointDistance = 0.7f * density
    private val mAutoClearDelay = max(100L, 1200L - getInstance().handwriting.handWritingSpeed.getValue())

    init {
        clear()
    }

    override fun setTheme(theme: Theme) {
        super.setTheme(theme)
        mInkCoverPaint.colorFilter =
            PorterDuffColorFilter(mActiveTheme.keyTextColor, PorterDuff.Mode.SRC_IN)
    }

    fun clear() {
        mDrawing = false
        mPointCount = 0
        mInkX.clear()
        mInkY.clear()
        mInkWidth.clear()
        mInkBitmap?.eraseColor(Color.TRANSPARENT)
        mStampBitmap?.eraseColor(Color.TRANSPARENT)
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mInkBitmap = null
        mInkCanvas = null
        mStampBitmap = null
        mStampCanvas = null
        clear()
    }

    override fun closing() {
        super.closing()
        handler?.removeCallbacks(mAutoClearRunnable)
        mDrawing = false
        mPointCount = 0
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(me: MotionEvent): Boolean {
        val action = me.actionMasked
        if (mDelegated) {
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                mDelegated = false
            }
            return super.onTouchEvent(me)
        }
        if (!isEnabled || !InputModeSwitcher.isChineseHandWriting || mLongPressKey) {
            return super.onTouchEvent(me)
        }
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                if (onKeyPressHandwriting(me.x.toInt(), me.y.toInt()) != null) {
                    val consumed = super.onTouchEvent(me)
                    mDelegated = consumed
                    return consumed
                }
                if (mLastUpTime != 0L && System.currentTimeMillis() - mLastUpTime > mAutoClearDelay) {
                    mService?.responseKeyEvent(SoftKey(InputModeSwitcher.USER_KEYCODE_SELECTED))
                    mSBPoint.clear()
                    clear()
                }
                parent?.requestDisallowInterceptTouchEvent(true)
                handler?.removeCallbacks(mAutoClearRunnable)
                mActivePointerId = me.getPointerId(0)
                beginStroke()
                addPoint(me.x, me.y, me.eventTime)
                recordRecognitionPoint(me.x, me.y)
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!mDrawing) return true
                val index = me.findPointerIndex(mActivePointerId)
                if (index < 0) return true
                var changed = false
                for (h in 0 until me.historySize) {
                    if (addPoint(me.getHistoricalX(index, h), me.getHistoricalY(index, h), me.getHistoricalEventTime(h))) {
                        changed = true
                    }
                }
                if (addPoint(me.getX(index), me.getY(index), me.eventTime)) {
                    changed = true
                }
                recordRecognitionPoint(me.getX(index), me.getY(index))
                if (changed) invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (!mDrawing) return true
                val index = me.findPointerIndex(mActivePointerId).let { if (it < 0) 0 else it }
                endStroke(me.getX(index), me.getY(index), me.eventTime)
                return true
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                return true
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val index = me.actionIndex
                if (mDrawing && me.getPointerId(index) == mActivePointerId) {
                    endStroke(me.getX(index), me.getY(index), me.eventTime)
                }
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                if (mDrawing) {
                    endStroke(mLastPoint.x, mLastPoint.y, me.eventTime)
                }
                return true
            }
            else -> return super.onTouchEvent(me)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mInkBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
        if (mDrawing) {
            renderStroke()
            mStampBitmap?.let { canvas.drawBitmap(it, 0f, 0f, mInkCoverPaint) }
        }
    }

    private fun beginStroke() {
        mDrawing = true
        mPointCount = 0
        mRawHead = 0
        mRawSize = 0
        mInkX.clear()
        mInkY.clear()
        mInkWidth.clear()
        mStampBitmap?.eraseColor(Color.TRANSPARENT)
        mLastVelocity = 0f
        mLastInkWidth = 0f
        mStrokeLength = 0f
        mVelocityReady = false
        val widthDp = getInstance().handwriting.handWritingWidth.getValue() * 0.4f
        mMaxWidth = max(convertDpToPx(widthDp).toFloat(), 2f)
        mMinWidth = mMaxWidth * MIN_WIDTH_RATIO
        mCurrentWidth = mMinWidth
    }

    private fun addPoint(x: Float, y: Float, time: Long): Boolean {
        if (mRawSize > 0) {
            val last = (mRawHead + mRawSize - 1) % RAW_BUFFER_SIZE
            val dx = x - mRawX[last]
            val dy = y - mRawY[last]
            if (dx * dx + dy * dy < mMinPointDistance * mMinPointDistance) return false
        }
        if (mRawSize < RAW_BUFFER_SIZE) {
            val index = (mRawHead + mRawSize) % RAW_BUFFER_SIZE
            mRawX[index] = x
            mRawY[index] = y
            mRawTime[index] = time
            mRawSize++
        } else {
            mRawX[mRawHead] = x
            mRawY[mRawHead] = y
            mRawTime[mRawHead] = time
            mRawHead = (mRawHead + 1) % RAW_BUFFER_SIZE
        }
        when (mRawSize) {
            1 ->
                emitPoint(x, y, time)
            RAW_BUFFER_SIZE -> {
                val a = mRawHead
                val b = (mRawHead + 1) % RAW_BUFFER_SIZE
                val c = (mRawHead + 2) % RAW_BUFFER_SIZE
                emitPoint(
                    mRawX[a] * SMOOTH_WEIGHT + mRawX[b] * (1 - 2 * SMOOTH_WEIGHT) + mRawX[c] * SMOOTH_WEIGHT,
                    mRawY[a] * SMOOTH_WEIGHT + mRawY[b] * (1 - 2 * SMOOTH_WEIGHT) + mRawY[c] * SMOOTH_WEIGHT,
                    mRawTime[b]
                )
            }
        }
        return true
    }

    private fun flushPoint() {
        if (mRawSize < 2) {
            mRawSize = 0
            return
        }
        val last = (mRawHead + mRawSize - 1) % RAW_BUFFER_SIZE
        emitPoint(mRawX[last], mRawY[last], mRawTime[last])
        mRawSize = 0
    }

    private fun emitPoint(x: Float, y: Float, time: Long) {
        if (mPointCount > 0 && distance(mLastPoint.x, mLastPoint.y, x, y) < MIN_EMIT_DISTANCE) return
        if (mPointCount > 0) {
            val velocity = distance(mLastPoint.x, mLastPoint.y, x, y) /
                max(1L, time - mLastPoint.time).toFloat() / density
            if (mVelocityReady) {
                mLastVelocity = VELOCITY_FILTER_WEIGHT * velocity + (1 - VELOCITY_FILTER_WEIGHT) * mLastVelocity
            } else if (mStrokeLength >= mWarmupDistance) {
                mLastVelocity = velocity * VELOCITY_START_COMPENSATION
                mVelocityReady = true
            }
        }
        val width = if (mVelocityReady) widthFor(mLastVelocity) else mMinWidth

        when (mPointCount) {
            0 -> appendSample(x, y, mMinWidth)
            1 -> appendQuad(mLastPoint.x, mLastPoint.y, mLastPoint.x, mLastPoint.y, (mLastPoint.x + x) * 0.5f, (mLastPoint.y + y) * 0.5f, width)
            else -> appendQuad(
                (mPrevPoint.x + mLastPoint.x) * 0.5f, (mPrevPoint.y + mLastPoint.y) * 0.5f,
                mLastPoint.x, mLastPoint.y,
                (mLastPoint.x + x) * 0.5f, (mLastPoint.y + y) * 0.5f, width
            )
        }
        mPrevPoint.set(mLastPoint)
        mLastPoint.set(x, y, time)
        mPointCount++
    }

    private fun appendQuad(x0: Float, y0: Float, cx: Float, cy: Float, x1: Float, y1: Float, endWidth: Float) {
        val startWidth = mLastInkWidth
        val length = distance(x0, y0, cx, cy) + distance(cx, cy, x1, y1)
        val steps = max(1, ceil(length / mSampleStep).toInt())
        for (i in 1..steps) {
            val t = i.toFloat() / steps
            val u = 1f - t
            val x = u * u * x0 + 2f * u * t * cx + t * t * x1
            val y = u * u * y0 + 2f * u * t * cy + t * t * y1
            val smooth = t * t * (3f - 2f * t)
            appendSample(x, y, startWidth + (endWidth - startWidth) * smooth)
        }
    }

    private fun appendSample(x: Float, y: Float, width: Float) {
        val count = mInkX.size
        var travel = 0f
        if (count > 0) {
            travel = distance(mInkX[count - 1], mInkY[count - 1], x, y)
            mStrokeLength += travel
        }
        val finalWidth = if (count == 0) {
            width
        } else {
            val maxDelta = (mMaxWidth - mMinWidth) * travel / mWidthRampLength
            mCurrentWidth + (width - mCurrentWidth).coerceIn(-maxDelta, maxDelta)
        }.coerceAtLeast(1f)
        mCurrentWidth = finalWidth
        mInkX.add(x)
        mInkY.add(y)
        mInkWidth.add(finalWidth)
        mLastInkWidth = finalWidth
    }

    private fun renderStroke() {
        val canvas = ensureStampCanvas() ?: return
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        val count = mInkX.size
        if (count == 0) return
        for (i in 0 until count) {
            canvas.drawCircle(mInkX[i], mInkY[i], inkRadius(i), mStampPaint)
        }
        for (i in 0 until count - 1) {
            stampSegment(canvas, i, i + 1)
        }
    }

    private fun stampSegment(canvas: Canvas, from: Int, to: Int) {
        val x0 = mInkX[from]
        val y0 = mInkY[from]
        val r0 = inkRadius(from)
        val x1 = mInkX[to]
        val y1 = mInkY[to]
        val r1 = inkRadius(to)
        val dx = x1 - x0
        val dy = y1 - y0
        val length = sqrt(dx * dx + dy * dy)
        if (length < 0.0001f) return
        val nx = -dy / length
        val ny = dx / length
        mStampPath.reset()
        mStampPath.moveTo(x0 + nx * r0, y0 + ny * r0)
        mStampPath.lineTo(x1 + nx * r1, y1 + ny * r1)
        mStampPath.lineTo(x1 - nx * r1, y1 - ny * r1)
        mStampPath.lineTo(x0 - nx * r0, y0 - ny * r0)
        mStampPath.close()
        canvas.drawPath(mStampPath, mStampPaint)
    }

    private fun inkRadius(index: Int): Float = max(mInkWidth[index] * 0.5f, 0.5f)

    private fun commitStroke() {
        if (mInkX.size == 0) return
        val target = ensureInkCanvas() ?: return
        renderStroke()
        val stamp = mStampBitmap ?: return
        target.drawBitmap(stamp, 0f, 0f, mInkCoverPaint)
        stamp.eraseColor(Color.TRANSPARENT)
    }

    private fun ensureInkCanvas(): Canvas? {
        if (width <= 0 || height <= 0) return null
        val bitmap = mInkBitmap
        if (bitmap == null || bitmap.width != width || bitmap.height != height) {
            val newBitmap = createBitmap(width, height)
            mInkBitmap = newBitmap
            mInkCanvas = Canvas(newBitmap)
        }
        return mInkCanvas
    }

    private fun ensureStampCanvas(): Canvas? {
        if (width <= 0 || height <= 0) return null
        val bitmap = mStampBitmap
        if (bitmap == null || bitmap.width != width || bitmap.height != height) {
            val newBitmap = createBitmap(width, height)
            mStampBitmap = newBitmap
            mStampCanvas = Canvas(newBitmap)
        }
        return mStampCanvas
    }

    private fun endStroke(x: Float, y: Float, time: Long) {
        addPoint(x, y, time)
        recordRecognitionPoint(x, y)
        mSBPoint.add(-1)
        mSBPoint.add(0)
        recognitionData()
        finishStroke()
        mLastUpTime = System.currentTimeMillis()
        mActivePointerId = MotionEvent.INVALID_POINTER_ID
        handler?.removeCallbacks(mAutoClearRunnable)
        handler?.postDelayed(mAutoClearRunnable, mAutoClearDelay)
    }

    private fun finishStroke() {
        if (!mDrawing) return
        mDrawing = false
        flushPoint()
        if (mPointCount >= 2) {
            appendQuad((mPrevPoint.x + mLastPoint.x) * 0.5f, (mPrevPoint.y + mLastPoint.y) * 0.5f, mLastPoint.x, mLastPoint.y, mLastPoint.x, mLastPoint.y, mCurrentWidth)
        }
        taperStrokeEnd()
        commitStroke()
        invalidate()
    }

    private fun taperStrokeEnd() {
        val count = mInkX.size
        if (count < 2) return
        var index = count - 1
        var remain = mTailClampLength
        while (index > 1 && remain > 0f) {
            remain -= distance(mInkX[index - 1], mInkY[index - 1], mInkX[index], mInkY[index])
            index--
        }
        if (index > 1) {
            val refWidth = mInkWidth[index]
            for (i in index until count) {
                if (mInkWidth[i] > refWidth) mInkWidth.set(i, refWidth)
            }
        }
        remain = mEndTaperLength
        index = count - 1
        while (index > 0 && remain > 0f) {
            remain -= distance(mInkX[index - 1], mInkY[index - 1], mInkX[index], mInkY[index])
            val progress = ((mEndTaperLength - remain) / mEndTaperLength).coerceIn(0f, 1f)
            val factor = END_WIDTH_RATIO + (1f - END_WIDTH_RATIO) * progress
            mInkWidth.set(index, max(mInkWidth[index] * factor, 1f))
            index--
        }
    }

    private fun widthFor(velocityDpPerMs: Float): Float {
        val ratio = ((velocityDpPerMs - VELOCITY_SLOW) / (VELOCITY_FAST - VELOCITY_SLOW)).coerceIn(0f, 1f)
        val eased = ratio.toDouble().pow(0.9).toFloat()
        return mMaxWidth - (mMaxWidth - mMinWidth) * eased
    }

    private fun recordRecognitionPoint(x: Float, y: Float) {
        mSBPoint.add(x.toInt().toShort())
        mSBPoint.add(y.toInt().toShort())
    }

    private val mAutoClearRunnable = Runnable { clear() }

    private fun recognitionData() {
        if (mSBPoint.isEmpty()) return
        val service = mService ?: return
        val data = mSBPoint.toMutableList()
        recognitionExecutor.execute {
            HWEngine.recognitionData(data) { item ->
                service.postDelayed({ service.responseHandwritingResultEvent(item) }, 20)
            }
        }
    }

    private fun onKeyPressHandwriting(x: Int, y: Int): SoftKey? {
        return mSoftKeyboard!!.mapToKey(x, y)
    }

    private fun convertDpToPx(dp: Float): Int {
        return (density * dp).roundToInt()
    }

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        return sqrt(dx * dx + dy * dy)
    }

    private class InputPoint {
        var x = 0f
        var y = 0f
        var time = 0L

        fun set(point: InputPoint) {
            x = point.x
            y = point.y
            time = point.time
        }

        fun set(x: Float, y: Float, time: Long) {
            this.x = x
            this.y = y
            this.time = time
        }
    }

    private class FloatList(initialCapacity: Int = 256) {
        private var mData = FloatArray(initialCapacity)
        var size = 0
            private set

        fun add(value: Float) {
            if (size == mData.size) mData = mData.copyOf(mData.size * 2)
            mData[size++] = value
        }

        operator fun get(index: Int): Float = mData[index]

        fun set(index: Int, value: Float) {
            mData[index] = value
        }

        fun clear() {
            size = 0
        }
    }

    companion object {
        private const val MIN_WIDTH_RATIO = 0.32f      // 最快时笔宽约占最大笔宽的比例
        private const val END_WIDTH_RATIO = 0.25f      // 收笔出锋处的笔宽比例
        private const val VELOCITY_FILTER_WEIGHT = 0.4f
        private const val VELOCITY_START_COMPENSATION = 1.35f // 起笔速度偏低的补偿系数
        private const val SMOOTH_WEIGHT = 0.25f        // 三点位置平滑权重
        private const val VELOCITY_SLOW = 0.02f        // dp/ms，慢写
        private const val VELOCITY_FAST = 0.30f        // dp/ms，快写
        private const val RAW_BUFFER_SIZE = 3
        private const val MIN_EMIT_DISTANCE = 0.5f
        private val recognitionExecutor: ExecutorService =
            Executors.newSingleThreadExecutor { runnable ->
                Thread(runnable, "handwriting-recognition").apply { isDaemon = true }
            }
    }
}
