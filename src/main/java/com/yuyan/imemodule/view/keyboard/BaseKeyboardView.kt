package com.yuyan.imemodule.view.keyboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityManager
import com.yuyan.imemodule.data.theme.ThemeManager.prefs
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.entity.keyboard.SoftKeyboard
import com.yuyan.imemodule.utils.DevicesUtils.tryPlayKeyDown
import com.yuyan.imemodule.utils.DevicesUtils.tryVibrate
import com.yuyan.imemodule.view.popup.KeyDef
import com.yuyan.imemodule.view.popup.PopupAction
import com.yuyan.imemodule.view.popup.PopupAction.ChangeFocusAction
import com.yuyan.imemodule.view.popup.PopupAction.DismissAction
import com.yuyan.imemodule.view.popup.PopupAction.PreviewAction
import com.yuyan.imemodule.view.popup.PopupAction.ShowKeyboardAction
import com.yuyan.imemodule.view.popup.PopupAction.TriggerAction
import com.yuyan.imemodule.view.popup.PopupComponent
import com.yuyan.imemodule.view.popup.PopupComponent.Companion.get
import kotlin.math.abs

/**
 * 键盘根布局
 *
 * 由于之前键盘体验问题，当前基于Android内置键盘[android.inputmethodservice.KeyboardView]进行调整开发。
 */
open class BaseKeyboardView(mContext: Context?) : View(mContext) {
    private val popupComponent: PopupComponent = get()
    private var swipeEnabled: Boolean
    protected var mSoftKeyboard: SoftKeyboard? = null
    /** The accessibility manager for accessibility support  */
    private val mAccessibilityManager: AccessibilityManager
    private var mDownTime: Long = 0
    private var mLastMoveTime: Long = 0
    private var mLastKey: SoftKey? = null
    private var mLastCodeX = 0
    private var mLastCodeY = 0
    private var mCurrentKeyPressed: SoftKey? = null // 按下的按键，用于界面更新
    var mCurrentKey: SoftKey? = null
    private var mLastKeyTime: Long = 0
    private var mCurrentKeyTime: Long = 0
    private var mGestureDetector: GestureDetector? = null
    private var mRepeatKeyIndex: SoftKey? = null
    protected var mInvalidatedKey: SoftKey? = null
    private var mAbortKey = false
    private var mLongPressKey = false
    private var mSwipeMoveKey = false
    private val mSwipeTracker = SwipeTracker()
    private var mOldPointerX = 0f
    private var mOldPointerY = 0f
    private var mLastSentIndex: SoftKey? = null
    private var mLastTapTime: Long = 0
    private var mHandler: Handler? = null
    /** Whether the keyboard bitmap needs to be redrawn before it's blitted.  */
    protected var mDrawPending = false
    /** The dirty region in the keyboard bitmap  */
    protected var mDirtyRect = Rect()
    /** The keyboard bitmap for faster updates  */
    protected var mBuffer: Bitmap? = null
    /** Notes if the keyboard just changed, so that we could possibly reallocate the mBuffer.  */
    protected var mKeyboardChanged = false
    /** The canvas for the above mutable keyboard bitmap  */
    protected var mCanvas: Canvas? = null
    protected var mClipRegion = Rect(0, 0, 0, 0)
    private var mOldPointerCount = 1

    //输入法服务
    protected var mService: InputView? = null
    fun setResponseKeyEvent(service: InputView?) {
        mService = service
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initGestureDetector()
        if (mHandler == null) {
            mHandler = object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        MSG_REPEAT -> {
                            if (repeatKey()) {
                                val repeat = Message.obtain(this, MSG_REPEAT)
                                sendMessageDelayed(repeat, REPEAT_INTERVAL.toLong())
                            }
                        }

                        MSG_LONGPRESS -> openPopupIfRequired()
                    }
                }
            }
        }
    }

    private fun initGestureDetector() {
        if (mGestureDetector == null) {
            mGestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    if(!mLongPressKey && mCurrentKey?.keyCode == KeyEvent.KEYCODE_SPACE) {
                        if(!dispatchGestureEvent(distanceX.toInt(), distanceY.toInt())){
                            onPopupAction(ChangeFocusAction(0, e2.x - e1!!.x, e2.y - e1.y))
                        }
                    } else {
                        onPopupAction(ChangeFocusAction(0, e2.x - e1!!.x, e2.y - e1.y))
                    }
                    return true
                }
            })
            mGestureDetector!!.setIsLongpressEnabled(false)
        }
    }

    private fun detectAndSendKey(key: SoftKey?, eventTime: Long) {
        if (mLongPressKey || key == null) return
        mService?.responseKeyEvent(key)
        mLastSentIndex = key
        mLastTapTime = eventTime
    }

    fun invalidateAllKeys() {
        mDirtyRect.union(0, 0, width, height)
        mDrawPending = true
        invalidate()
    }

    fun invalidateKey(key: SoftKey?) {
        mInvalidatedKey = key
        if (key == null) return
        mDirtyRect.union(key.mLeft, key.mTop, key.mRight, key.mBottom)
        onBufferDraw()
        invalidate(key.mLeft, key.mTop, key.mRight, key.mBottom)
    }

    open fun onBufferDraw() {}
    private fun openPopupIfRequired() {
        showPreview(null)
        if (mCurrentKey != null && !TextUtils.isEmpty(mCurrentKey!!.getkeyLabel())) {
            val bounds = Rect(mCurrentKey!!.mLeft, mCurrentKey!!.mTop, mCurrentKey!!.mRight, mCurrentKey!!.mBottom)
            onPopupAction(ShowKeyboardAction(0, KeyDef.Popup.Key(mCurrentKey!!.getkeyLabel()), mService, bounds))
            mLongPressKey = true
        }
    }

    override fun onHoverEvent(event: MotionEvent): Boolean {
        if (mAccessibilityManager.isTouchExplorationEnabled && event.pointerCount == 1) {
            val action = event.action
            when (action) {
                MotionEvent.ACTION_HOVER_ENTER -> {
                    event.action = MotionEvent.ACTION_DOWN
                }

                MotionEvent.ACTION_HOVER_MOVE -> {
                    event.action = MotionEvent.ACTION_MOVE
                }

                MotionEvent.ACTION_HOVER_EXIT -> {
                    event.action = MotionEvent.ACTION_UP
                }
            }
            return onTouchEvent(event)
        }
        return true
    }

    init {
        mAccessibilityManager =
            mContext?.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        swipeEnabled = prefs.spaceSwipeMoveCursor.getValue()
    }

    override fun onTouchEvent(me: MotionEvent): Boolean {
        val pointerCount = me.pointerCount
        val action = me.action
        var result: Boolean
        val now = me.eventTime
        if (pointerCount != mOldPointerCount) {
            if (pointerCount == 1) {
                val down = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, me.x, me.y, me.metaState)
                result = onModifiedTouchEvent(down)
                down.recycle()
                if (action == MotionEvent.ACTION_UP) {
                    result = onModifiedTouchEvent(me)
                }
            } else {
                val up = MotionEvent.obtain(
                    now, now, MotionEvent.ACTION_UP,
                    mOldPointerX, mOldPointerY, me.metaState
                )
                result = onModifiedTouchEvent(up)
                up.recycle()
            }
        } else {
            if (pointerCount == 1) {
                result = onModifiedTouchEvent(me)
                mOldPointerX = me.x
                mOldPointerY = me.y
            } else {
                result = true
            }
        }
        mOldPointerCount = pointerCount
        return result
    }

    private fun onModifiedTouchEvent(me: MotionEvent): Boolean {
        val touchX = me.x.toInt()
        val touchY = me.y.toInt()
        val action = me.action
        val eventTime = me.eventTime
        val keyIndex = getKeyIndices(touchX, touchY)
        if (action == MotionEvent.ACTION_DOWN) mSwipeTracker.clear()
        mSwipeTracker.addMovement(me)
        if (mAbortKey && action != MotionEvent.ACTION_DOWN) {
            return true
        }
        if (mGestureDetector!!.onTouchEvent(me)) {
            mHandler!!.removeMessages(MSG_REPEAT)
            mHandler!!.removeMessages(MSG_LONGPRESS)
            return true
        }
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mAbortKey = false
                mSwipeMoveKey = false
                mLongPressKey = false
                mLastCodeX = touchX
                mLastCodeY = touchY
                mLastKeyTime = 0
                mCurrentKeyTime = 0
                mLastKey = null
                mCurrentKey = keyIndex
                mDownTime = me.eventTime
                mLastMoveTime = mDownTime
                checkMultiTap(eventTime, keyIndex)
                // 播放按键声音和震动
                tryPlayKeyDown(mCurrentKey)
                tryVibrate(this)
                if(keyIndex != null)onPress(keyIndex)
                if (mCurrentKey != null && mCurrentKey!!.repeatable()) {
                    mRepeatKeyIndex = mCurrentKey
                    val msg = mHandler!!.obtainMessage(MSG_REPEAT)
                    mHandler!!.sendMessageDelayed(msg, REPEAT_START_DELAY.toLong())
                    repeatKey()
                    if (mAbortKey) {
                        mRepeatKeyIndex = null
                        return true
                    }
                }
                if (mCurrentKey != null) {
                    val msg = mHandler!!.obtainMessage(MSG_LONGPRESS, me)
                    mHandler!!.sendMessageDelayed(msg, prefs.longPressTimeout.getValue().toLong())
                }
                showPreview(mCurrentKey)
            }

            MotionEvent.ACTION_UP -> {
                removeMessages()
                if (keyIndex === mCurrentKey) {
                    mCurrentKeyTime += eventTime - mLastMoveTime
                } else {
                    resetMultiTap()
                    mLastKey = mCurrentKey
                    mLastKeyTime = mCurrentKeyTime + eventTime - mLastMoveTime
                    mCurrentKey = keyIndex
                    mCurrentKeyTime = 0
                }
                if (mCurrentKeyTime < mLastKeyTime && mCurrentKeyTime < DEBOUNCE_TIME && mLastKey != null) {
                    mCurrentKey = mLastKey
                }
                if (mRepeatKeyIndex == null && !mAbortKey && !mSwipeMoveKey) {
                    detectAndSendKey(mCurrentKey, eventTime)
                }
                mRepeatKeyIndex = null
                showPreview(null)
            }

            MotionEvent.ACTION_CANCEL -> {
                removeMessages()
                mAbortKey = true
                showPreview(null)
            }
        }
        return true
    }

    private fun dispatchGestureEvent(countX: Int, countY: Int) : Boolean {
        var result = false
        if (swipeEnabled) {
            val absCountX = abs(countX.toDouble()).toInt()
            val absCountY = abs(countY.toDouble()).toInt()
            if (absCountX > 1 || absCountY > 1) {
                val key = SoftKey()
                if (absCountY > absCountX) {
                    key.keyCode =
                        if (countY > 1) KeyEvent.KEYCODE_DPAD_UP else KeyEvent.KEYCODE_DPAD_DOWN
                } else {
                    key.keyCode =
                        if (countX > 1) KeyEvent.KEYCODE_DPAD_LEFT else KeyEvent.KEYCODE_DPAD_RIGHT
                }
                mSwipeMoveKey = true
                mService!!.responseKeyEvent(key)
                result = true
            }
        }
        return result
    }

    private fun repeatKey(): Boolean {
        detectAndSendKey(mCurrentKey, mLastTapTime)
        return true
    }

    private fun removeMessages() {
        if (mHandler != null) {
            mHandler!!.removeMessages(MSG_REPEAT)
            mHandler!!.removeMessages(MSG_LONGPRESS)
            mHandler!!.removeMessages(MSG_SHOW_PREVIEW)
        }
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        closing()
    }

    private fun resetMultiTap() {
        mLastSentIndex = null
        mLastTapTime = -1
    }

    private fun checkMultiTap(eventTime: Long, key: SoftKey?) {
        if (key == null) return
        if (eventTime > mLastTapTime + MULTITAP_INTERVAL || key !== mLastSentIndex) {
            resetMultiTap()
        }
    }

    private class SwipeTracker {
        val mPastX = FloatArray(NUM_PAST)
        val mPastY = FloatArray(NUM_PAST)
        val mPastTime = LongArray(NUM_PAST)
        fun clear() {
            mPastTime[0] = 0
        }

        fun addMovement(ev: MotionEvent) {
            val time = ev.eventTime
            val N = ev.historySize
            for (i in 0 until N) {
                addPoint(
                    ev.getHistoricalX(i), ev.getHistoricalY(i),
                    ev.getHistoricalEventTime(i)
                )
            }
            addPoint(ev.x, ev.y, time)
        }

        private fun addPoint(x: Float, y: Float, time: Long) {
            var drop = -1
            var i: Int
            val pastTime = mPastTime
            i = 0
            while (i < NUM_PAST) {
                if (pastTime[i] == 0L) {
                    break
                } else if (pastTime[i] < time - LONGEST_PAST_TIME) {
                    drop = i
                }
                i++
            }
            if (i == NUM_PAST && drop < 0) {
                drop = 0
            }
            if (drop == i) drop--
            val pastX = mPastX
            val pastY = mPastY
            if (drop >= 0) {
                val start = drop + 1
                val count = NUM_PAST - drop - 1
                System.arraycopy(pastX, start, pastX, 0, count)
                System.arraycopy(pastY, start, pastY, 0, count)
                System.arraycopy(pastTime, start, pastTime, 0, count)
                i -= drop + 1
            }
            pastX[i] = x
            pastY[i] = y
            pastTime[i] = time
            i++
            if (i < NUM_PAST) {
                pastTime[i] = 0
            }
        }

        companion object {
            const val NUM_PAST = 4
            const val LONGEST_PAST_TIME = 200
        }
    }

    private fun showPreview(key: SoftKey?) {
        if (mCurrentKeyPressed === key) {
            val triggerAction = TriggerAction(0, mService)
            onPopupAction(triggerAction)
            onPopupAction(DismissAction(0))
            mLongPressKey = false
            return
        }
        val oldKeyPressed = mCurrentKeyPressed
        if (oldKeyPressed != null) {
            oldKeyPressed.onReleased()
            if(mService == null) return
            if (mService!!.mInputModeSwitcher.isEnglish && (mService!!.mDecInfo.composingStrForDisplay.isBlank() ||  mService!!.mDecInfo.composingStrForDisplay.length == 1)) {
                invalidateAllKeys()
            } else {
                invalidateKey(oldKeyPressed)
            }
        }
        if (key != null) {
            key.onPressed()
            invalidateKey(key)
            showBalloonText(key)
        } else {
            onPopupAction(DismissAction(0))
        }
        mCurrentKeyPressed = key
    }

    private fun onPopupAction(action: PopupAction) {
        popupComponent.listener.onPopupAction(action)
    }

    open fun closing() {
        removeMessages()
    }

    private fun showBalloonText(key: SoftKey) {
        val keyboardBalloonShow = prefs.keyboardBalloonShow.getValue()
        if (keyboardBalloonShow && !TextUtils.isEmpty(key.getkeyLabel())) {
            val bounds = Rect(key.mLeft, key.mTop, key.mRight, key.mBottom)
            onPopupAction(PreviewAction(0, key.getkeyLabel(), bounds))
        }
    }

    open fun getKeyIndices(x: Int, y: Int): SoftKey? {
        return null
    }

    open fun setSoftKeyboard(softSkb: SoftKeyboard) {
        mSoftKeyboard = softSkb
    }

    fun getSoftKeyboard(): SoftKeyboard {
        return mSoftKeyboard!!
    }

    open fun onPress(key: SoftKey) {}

    companion object {
        private const val MSG_SHOW_PREVIEW = 1
        private const val MSG_REPEAT = 3
        private const val MSG_LONGPRESS = 4
        private const val DEBOUNCE_TIME = 70
        private const val REPEAT_INTERVAL = 50 // ~20 keys per second
        private const val REPEAT_START_DELAY = 400
        private const val MULTITAP_INTERVAL = 800 // milliseconds
    }
}
