package com.yuyan.imemodule.keyboard

import android.annotation.SuppressLint
import android.content.Context
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
import android.view.ViewConfiguration
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.entity.keyboard.SoftKeyboard
import com.yuyan.imemodule.manager.InputModeSwitcher
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.KeyboardSymbolSlideUpMod
import com.yuyan.imemodule.prefs.behavior.PopupMenuMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.view.popup.PopupComponent
import com.yuyan.imemodule.view.popup.PopupComponent.Companion.get
import java.util.LinkedList
import java.util.Queue
import kotlin.math.abs

/**
 * 键盘根布局
 *
 * 由于之前键盘体验问题，当前基于Android内置键盘[android.inputmethodservice.KeyboardView]进行调整开发。
 */
open class BaseKeyboardView(mContext: Context?) : View(mContext) {
    private val popupComponent: PopupComponent = get()
    protected var mSoftKeyboard: SoftKeyboard? = null
    private var mCurrentKey: SoftKey? = null
    private var mGestureDetector: GestureDetector? = null
    protected var mLongPressKey = false
    private var mAbortKey = false
    private var mHandler: Handler? = null
    protected var mDrawPending = false
    protected var mService: InputView? = null
    fun setResponseKeyEvent(service: InputView) {
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
                                sendMessageDelayed(repeat, nextRepeatInterval())
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
                override fun onScroll(downEvent: MotionEvent?, currentEvent: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                    if (currentEvent.pointerCount <= 1) updateGestureOffset(currentEvent.x, currentEvent.y)
                    if(mLongPressKey && mCurrentKey?.getkeyLabel()?.isNotBlank() == true){
                        popupComponent.changeFocus(currentEvent.x - downEvent!!.x, currentEvent.y - downEvent.y)
                    } else {
                        dispatchGestureEvent(downEvent, currentEvent)
                    }
                    return true
                }
            })
            mGestureDetector!!.setIsLongpressEnabled(false)
        }
    }

    fun invalidateKey() {
        mDrawPending = true
        invalidate()
    }

    open fun onBufferDraw() {}
    private fun openPopupIfRequired() {
        if(mCurrentKey != null) {
            val softKey = mCurrentKey!!
            val keyboardSymbol = ThemeManager.prefs.keyboardSymbol.getValue()
            if (softKey.getkeyLabel().isNotBlank() && softKey.code != InputModeSwitcher.USER_KEYCODE_COMMA_EMOJI ) {
                val keyLabel = if (InputModeSwitcher.isLower) softKey.keyLabel.lowercase() else softKey.keyLabel
                val designPreset = setOf("，", "。", ",", ".")
                val smallLabel = if(designPreset.any { it == keyLabel } || !keyboardSymbol) "" else softKey.getmKeyLabelSmall()
                val bounds = Rect(softKey.mLeft, softKey.mTop, softKey.mRight, softKey.mBottom)
                popupComponent.showKeyboard(keyLabel, smallLabel, bounds)
                mLongPressKey = true
            } else if (softKey.code == InputModeSwitcher.USER_KEYCODE_LANG ||
                softKey.code == InputModeSwitcher.USER_KEYCODE_COMMA_EMOJI ||
                    softKey.code == KeyEvent.KEYCODE_SHIFT_LEFT ||
                softKey.code == InputModeSwitcher.USER_KEYCODE_CURSOR_DIRECTION ||
                softKey.code == KeyEvent.KEYCODE_DEL || softKey.code == KeyEvent.KEYCODE_ENTER){
                val bounds = Rect(softKey.mLeft, softKey.mTop, softKey.mRight, softKey.mBottom)
                popupComponent.showKeyboardMenu(softKey, bounds, totalDistanceY)
                mLongPressKey = true
            } else {
                mLongPressKey = true
                mAbortKey = true
                dismissPreview()
            }
        }
    }

    private var motionEventQueue: Queue<MotionEvent> = LinkedList()

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(me: MotionEvent): Boolean {
        var result = false
        if (mGestureDetector!!.onTouchEvent(me)) {
            return true
        }
        when (val action = me.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val actionIndex = me.actionIndex
                val x = me.getX(actionIndex)
                val y = me.getY(actionIndex)
                val now = me.eventTime
                val down = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, x, y, me.metaState)
                motionEventQueue.offer(down)
                result = onModifiedTouchEvent(me)
                val keyIndex = getKeyIndices(x.toInt(), y.toInt())
                if(keyIndex != null) {
                    DevicesUtils.tryPlayKeyDown(keyIndex.code)
                    DevicesUtils.tryVibrate(this)
                }
                showPreview(keyIndex)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                val now = me.eventTime
                val act = if(action == MotionEvent.ACTION_CANCEL)MotionEvent.ACTION_CANCEL else MotionEvent.ACTION_UP
                while (!motionEventQueue.isEmpty()) {
                    val first = motionEventQueue.poll()
                    if(first!= null) {
                        result = onModifiedTouchEvent(MotionEvent.obtain(now, now, act, first.x, first.y, me.metaState))
                    }
                }
                dismissPreview(commit = action != MotionEvent.ACTION_CANCEL)
            }
            else -> {
                result = onModifiedTouchEvent(me)
            }
        }
        return result
    }

    private fun onModifiedTouchEvent(me: MotionEvent): Boolean {
        when (me.action) {
            MotionEvent.ACTION_DOWN -> {
                mCurrentKey = getKeyIndices(me.x.toInt(), me.y.toInt())
                mAbortKey = false
                mLongPressKey = false
                downX = me.x
                downY = me.y
                lastStepX = me.x
                resetGestureState()
                if(mCurrentKey != null){
                    if (mCurrentKey!!.repeatable()) {
                        val msg = mHandler!!.obtainMessage(MSG_REPEAT)
                        mHandler!!.sendMessageDelayed(msg, REPEAT_START_DELAY)
                    }
                    val msg = mHandler!!.obtainMessage(MSG_LONGPRESS)
                    mHandler!!.sendMessageDelayed(msg, AppPrefs.getInstance().keyboardSetting.longPressTimeout.getValue().toLong())
                }
            }
            MotionEvent.ACTION_UP -> {
                mCurrentKey?.onReleased()
                mCurrentKey = getKeyIndices(me.x.toInt(), me.y.toInt())
                removeMessages()
                if (!mAbortKey && !mLongPressKey && mCurrentKey != null) {
                    mService?.responseKeyEvent(mCurrentKey!!)
                }
                resetGestureState()
            }
            MotionEvent.ACTION_CANCEL -> {
                removeMessages()
                resetGestureState()
            }
        }
        return true
    }

    private var downX: Float = 0f
    private var downY: Float = 0f
    private var totalDistanceX: Float = 0f
    private var totalDistanceY: Float = 0f
    private var lastStepX: Float = 0f
    private var repeatDirection = RepeatDirection.None
    private var fastDeleteLatched = false
    private var fastDeleteSwipeAccum = 0f
    private var lastFastDeleteDx = 0f
    private val touchSlop: Float by lazy { ViewConfiguration.get(context).scaledTouchSlop.toFloat() }
    // 左滑超过该距离进入加速删除
    private val fastDeleteThreshold: Float
        get() = FAST_DELETE_DP * resources.displayMetrics.density
    // 加速后向右滑回该距离退出加速
    private val fastDeleteExitBand: Float
        get() = FAST_DELETE_EXIT_DP * resources.displayMetrics.density
    // 还原需要更长的确认距离
    private val revertConfirmThreshold: Float
        get() = touchSlop * 2f

    private enum class RepeatDirection { None, Delete, DeleteFast, Revert, CursorLeft, CursorRight, CursorUp, CursorDown }

    private fun resetGestureState() {
        totalDistanceX = 0f
        totalDistanceY = 0f
        fastDeleteLatched = false
        fastDeleteSwipeAccum = 0f
        lastFastDeleteDx = 0f
        repeatDirection = RepeatDirection.None
    }

    private fun updateGestureOffset(x: Float, y: Float) {
        totalDistanceX = downX - x
        totalDistanceY = downY - y
        updateFastDeleteLatch()
        repeatDirection = calcRepeatDirection()
    }

    /**
     * 加速删除的锁存/退出判定：
     * - 向左滑动且超过进入阈值 → 锁存加速，之后即使手指停住也保持加速；
     * - 向右滑回一段时间（[fastDeleteExitBand]）→ 退出加速，恢复普通删除。
     */
    private fun updateFastDeleteLatch() {
        if (mCurrentKey?.code != KeyEvent.KEYCODE_DEL) return
        val dx = totalDistanceX
        val deltaX = dx - lastFastDeleteDx // 正值=继续向左，负值=向右
        lastFastDeleteDx = dx
        if (!fastDeleteLatched) {
            if (deltaX > 0 && dx >= fastDeleteThreshold) fastDeleteLatched = true
            return
        }
        if (deltaX >= 0) {
            fastDeleteSwipeAccum = 0f // 继续左滑或静止，重置右滑累计
        } else {
            fastDeleteSwipeAccum -= deltaX
            if (fastDeleteSwipeAccum >= fastDeleteExitBand) {
                fastDeleteLatched = false
                fastDeleteSwipeAccum = 0f
            }
        }
    }

    /**
     * 重复删除/移动光标的方向只由累计位移决定，避免逐帧增量抖动导致反复横跳
     *
     * 删除键：加速删除由锁存标记决定（见 [updateFastDeleteLatch]），下滑还原，其余一律继续删除；
     * 默认及微小抖动继续删除，保证“滑出按键范围”也不会中断删除
     */
    private fun calcRepeatDirection(): RepeatDirection {
        val softKey = mCurrentKey ?: return RepeatDirection.None
        val dx = totalDistanceX
        val dy = totalDistanceY
        return when (softKey.code) {
            KeyEvent.KEYCODE_DEL -> {
                when {
                    dy <= -revertConfirmThreshold && abs(dy) > abs(dx) -> RepeatDirection.Revert
                    else -> if (fastDeleteLatched) RepeatDirection.DeleteFast else RepeatDirection.Delete
                }
            }
            InputModeSwitcher.USER_KEYCODE_CURSOR_DIRECTION -> if (abs(dx) >= abs(dy)) {
                if (dx > 0) RepeatDirection.CursorLeft else RepeatDirection.CursorRight
            } else {
                if (dy < 0) RepeatDirection.CursorDown else RepeatDirection.CursorUp
            }

            else -> RepeatDirection.None
        }
    }

    /**
     * 重复触发的间隔，左滑加速时缩短
     */
    private fun nextRepeatInterval(): Long = when (repeatDirection) {
        RepeatDirection.DeleteFast -> FAST_REPEAT_INTERVAL
        else -> REPEAT_INTERVAL
    }

    // 处理手势滑动
    private fun dispatchGestureEvent(downEvent: MotionEvent?, currentEvent: MotionEvent) : Boolean {
        var result = false
        if(currentEvent.pointerCount > 1) return false    // 避免多指触控导致上屏
        val currentX = currentEvent.x
        val absDistanceX = abs(totalDistanceX)
        val absDistanceY = abs(totalDistanceY)
        val isVertical = absDistanceX * 1.5 < absDistanceY  //横向、竖向累计位移接近时，优先触发左右滑动
        val keyLableSmall = mCurrentKey?.getmKeyLabelSmall()
        val symbolSlideUp = EnvironmentSingleton.instance.heightForCandidatesArea / when(ThemeManager.prefs.symbolSlideUpMod.getValue()){
            KeyboardSymbolSlideUpMod.SHORT -> 3;KeyboardSymbolSlideUpMod.MEDIUM -> 2;else -> 1
        }
        val spaceSwipeMoveCursorSpeed = AppPrefs.getInstance().keyboardSetting.spaceSwipeMoveCursorSpeed.getValue()
        if (!isVertical) {  // 左右滑动
            val isSwipeKey = mCurrentKey?.code == KeyEvent.KEYCODE_SPACE || mCurrentKey?.code == KeyEvent.KEYCODE_0
            if (isSwipeKey && AppPrefs.getInstance().keyboardSetting.spaceSwipeMoveCursor.getValue()) {  // 左右滑动移光标
                val step = currentX - lastStepX
                if (abs(step) >= spaceSwipeMoveCursorSpeed) {
                    val stepSign = if (step < 0) -1 else 1
                    lastStepX = currentX - stepSign * spaceSwipeMoveCursorSpeed
                    removeMessages()
                    mAbortKey = true
                    mService?.responseKeyEvent(SoftKey(code = if (stepSign < 0) KeyEvent.KEYCODE_DPAD_LEFT else KeyEvent.KEYCODE_DPAD_RIGHT))
                    result = true
                }
            }
        } else if(keyLableSmall?.isNotBlank() == true){
            if (totalDistanceY > 0 && absDistanceY > symbolSlideUp && ThemeManager.prefs.keyboardSymbol.getValue()){   // 向上滑动
                mLongPressKey = true
                removeMessages()
                mService?.responseLongKeyEvent(Pair(PopupMenuMode.Text, keyLableSmall))
                result = true
            }
        } else {  // 菜单
            if (isVertical && totalDistanceY > symbolSlideUp) {   // 向上滑动
                mLongPressKey = true
            } else if (downEvent != null) {
                popupComponent.changeFocus(currentEvent.x - downEvent.x, currentEvent.y - downEvent.y)
            }
            // 只有上滑为主（或已选中）时才参与菜单确认
            if (isVertical || popupComponent.isTriggerSelected()) {
                popupComponent.onGestureEvent(totalDistanceY)
            }
        }
        return result
    }

    private fun repeatKey(): Boolean {
        val softKey = mCurrentKey ?: return true
        if (!softKey.repeatable()) return true
        if (softKey.code == KeyEvent.KEYCODE_DEL && mLongPressKey) {
            // 上滑已选中“清空”时暂停删除，避免边删边等清空；回落取消选中后自动恢复
            if (popupComponent.isTriggerSelected()) return true
            when (repeatDirection) {
                RepeatDirection.Revert -> mService?.responseLongKeyEvent(Pair(PopupMenuMode.Revertl, ""))
                else -> mService?.responseKeyEvent(SoftKey(KeyEvent.KEYCODE_DEL))
            }
        } else if (softKey.code == InputModeSwitcher.USER_KEYCODE_CURSOR_DIRECTION) {
            mService?.responseKeyEvent(
                SoftKey(
                    when (repeatDirection) {
                        RepeatDirection.CursorLeft -> KeyEvent.KEYCODE_DPAD_LEFT
                        RepeatDirection.CursorUp -> KeyEvent.KEYCODE_DPAD_UP
                        RepeatDirection.CursorDown -> KeyEvent.KEYCODE_DPAD_DOWN
                        else -> KeyEvent.KEYCODE_DPAD_RIGHT
                    }
                )
            )
        } else {
            mService?.responseKeyEvent(softKey)
        }
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

    /**
     * 显示短按气泡
     */
    private fun showPreview(key: SoftKey?) {
        mCurrentKey?.onReleased()
        if (key != null) {
            key.onPressed()
            showBalloonText(key)
        } else {
            popupComponent.dismissPopup()
        }
        invalidateKey()
    }

    /**
     * 隐藏短按气泡
     */
    private fun dismissPreview(commit: Boolean = true) {
        if (mLongPressKey) {
            // ACTION_CANCEL 时放弃当前选择，不上屏
            if (commit) mService?.responseLongKeyEvent(popupComponent.triggerFocused())
            mLongPressKey = false
        }
        if (mCurrentKey != null) {
            mCurrentKey!!.onReleased()
            if(mService == null) return
            invalidateKey()
        }
        popupComponent.dismissPopup()
        resetGestureState()
    }

    open fun closing() {
        removeMessages()
    }

    private fun showBalloonText(key: SoftKey) {
        val keyboardBalloonShow = AppPrefs.getInstance().keyboardSetting.keyboardBalloonShow.getValue()
        if (keyboardBalloonShow && !TextUtils.isEmpty(key.getkeyLabel())) {
            val bounds = Rect(key.mLeft, key.mTop, key.mRight, key.mBottom)
            popupComponent.showPopup(key.getkeyLabel(), bounds)
        }
    }

    fun getKeyIndices(x: Int, y: Int): SoftKey? {
        return mSoftKeyboard?.mapToKey(x, y)
    }

    open fun setSoftKeyboard(softSkb: SoftKeyboard) {
        mSoftKeyboard = softSkb
    }

    fun getSoftKeyboard(): SoftKeyboard {
        return mSoftKeyboard!!
    }

    companion object {
        private const val MSG_SHOW_PREVIEW = 1
        private const val MSG_REPEAT = 3
        private const val MSG_LONGPRESS = 4
        private const val REPEAT_INTERVAL = 50L // ~20 keys per second
        private const val FAST_REPEAT_INTERVAL = 25L // 左滑加速删除
        private const val REPEAT_START_DELAY = 400L
        private const val FAST_DELETE_DP = 20f
        private const val FAST_DELETE_EXIT_DP = 16f
    }
}
