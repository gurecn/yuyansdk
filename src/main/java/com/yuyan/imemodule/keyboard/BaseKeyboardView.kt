package com.yuyan.imemodule.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.entity.keyboard.SoftKeyboard
import com.yuyan.imemodule.manager.InputModeSwitcher
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.InputFeedbacks
import com.yuyan.imemodule.prefs.behavior.KeyboardSymbolSlideUpMod
import com.yuyan.imemodule.prefs.behavior.PopupMenuMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
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
                if (popupComponent.showKeyboard(keyLabel, smallLabel, bounds)) {
                    gestureMode = GestureMode.PopupSymbol
                }
                mLongPressKey = true
            } else if (softKey.code == InputModeSwitcher.USER_KEYCODE_LANG ||
                softKey.code == InputModeSwitcher.USER_KEYCODE_COMMA_EMOJI ||
                    softKey.code == KeyEvent.KEYCODE_SHIFT_LEFT ||
                softKey.code == InputModeSwitcher.USER_KEYCODE_CURSOR_DIRECTION ||
                softKey.code == KeyEvent.KEYCODE_DEL || softKey.code == KeyEvent.KEYCODE_ENTER){
                val bounds = Rect(softKey.mLeft, softKey.mTop, softKey.mRight, softKey.mBottom)
                popupComponent.showKeyboardMenu(softKey, bounds, totalDistanceY)
                mLongPressKey = true
                gestureMode = GestureMode.PopupMenu
            } else {
                mLongPressKey = true
                mAbortKey = true
                dismissPreview()
                gestureMode = GestureMode.Aborted
            }
        }
    }

    private var motionEventQueue: Queue<MotionEvent> = LinkedList()

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(me: MotionEvent): Boolean {
        var result = false
        when (val action = me.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val actionIndex = me.actionIndex
                val x = me.getX(actionIndex)
                val y = me.getY(actionIndex)
                val now = me.eventTime
                val down = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, x, y, me.metaState)
                motionEventQueue.offer(down)
                result = onModifiedTouchEvent(me)
                val keyIndex = getKeyIndices(x.toInt(), y.toInt())
                if(keyIndex != null) {
                    InputFeedbacks.clickFeedback(this, keyIndex.code)
                }
                showPreview(keyIndex)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                mAbortKey = true
                removeMessages()
                result = true
            }
            MotionEvent.ACTION_MOVE -> {
                handleMove(me)
                result = true
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
                dismissPreview(commit = action == MotionEvent.ACTION_UP)
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
                downKey = mCurrentKey
                if(mCurrentKey != null){
                    gestureConfig = GestureConfig.load()
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
                if (!mAbortKey && !mLongPressKey && gestureMode == GestureMode.None && mCurrentKey != null) {
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
    private var downKey: SoftKey? = null
    private var gestureMode = GestureMode.None
    private var gestureConfig: GestureConfig? = null
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

    /**
     * 手势模式
     */
    private enum class GestureMode {
        None,        // 尚未形成手势，抬手按普通按键上屏
        SwipeCursor, // 空格左右滑动移光标
        PopupSymbol, // 符号弹窗内选格
        PopupMenu,   // 菜单弹窗（清空/还原等）
        SwipeSymbol, // 上滑直接上屏符号
        Aborted      // 本次按下已放弃上屏
    }

    /**
     * 手势相关配置在按下时快照，滑动过程中只读快照，避免逐帧访问 prefs
     */
    private class GestureConfig(val symbolSlideUp: Float, val keyboardSymbolEnabled: Boolean, val spaceCursorEnabled: Boolean, val spaceCursorSpeed: Float) {
        companion object {
            fun load(): GestureConfig {
                val slideUp = EnvironmentSingleton.instance.heightForCandidatesArea /
                    when (ThemeManager.prefs.symbolSlideUpMod.getValue()) {
                        KeyboardSymbolSlideUpMod.SHORT -> 3
                        KeyboardSymbolSlideUpMod.MEDIUM -> 2
                        else -> 1
                    }
                val setting = AppPrefs.getInstance().keyboardSetting
                return GestureConfig(
                    symbolSlideUp = slideUp.toFloat(),
                    keyboardSymbolEnabled = ThemeManager.prefs.keyboardSymbol.getValue(),
                    spaceCursorEnabled = setting.spaceSwipeMoveCursor.getValue(),
                    spaceCursorSpeed = setting.spaceSwipeMoveCursorSpeed.getValue().toFloat()
                )
            }
        }
    }

    private fun resetGestureState() {
        totalDistanceX = 0f
        totalDistanceY = 0f
        fastDeleteLatched = false
        fastDeleteSwipeAccum = 0f
        lastFastDeleteDx = 0f
        repeatDirection = RepeatDirection.None
        gestureMode = GestureMode.None
        downKey = null
        gestureConfig = null
    }

    /**
     * MOVE 唯一入口：先更新位移与重复方向，再按当前手势模式分发
     */
    private fun handleMove(me: MotionEvent) {
        if (me.pointerCount > 1) return // 多指时不做手势，避免第二根手指干扰
        if (downKey == null) return
        totalDistanceX = downX - me.x
        totalDistanceY = downY - me.y
        updateFastDeleteLatch()
        repeatDirection = calcRepeatDirection()
        when (gestureMode) {
            GestureMode.SwipeCursor -> handleSpaceCursor(me.x)
            GestureMode.PopupSymbol -> popupComponent.changeFocus(me.x - downX, me.y - downY)
            GestureMode.PopupMenu -> handleMenuGesture()
            GestureMode.SwipeSymbol, GestureMode.Aborted -> Unit // 已经处理过，等待抬手
            GestureMode.None -> resolveGesture(me)
        }
    }

    /**
     * 尚未形成手势时判定进入哪种模式
     */
    private fun resolveGesture(me: MotionEvent) {
        val key = downKey ?: return
        val config = gestureConfig ?: return
        val dx = totalDistanceX
        val dy = totalDistanceY
        val absDx = abs(dx)
        val absDy = abs(dy)
        // 小于 touchSlop 的位移不算手势，避免轻微抖动打断按键上屏
        if (absDx < touchSlop && absDy < touchSlop) return
        val isVertical = absDx * 1.5 < absDy // 横向、竖向累计位移接近时，优先触发左右滑动
        if (!isVertical) {
            // 左右滑动：空格/0 移光标
            if (config.spaceCursorEnabled && (key.code == KeyEvent.KEYCODE_SPACE || key.code == KeyEvent.KEYCODE_0)) {
                gestureMode = GestureMode.SwipeCursor
                mAbortKey = true
                handleSpaceCursor(me.x)
            }
            return
        }
        if (dy <= 0) return // 下滑：删除还原由 repeatDirection 处理，其它键忽略
        val smallLabel = key.getmKeyLabelSmall()
        if (smallLabel.isNotBlank()) {
            // 有符号预设的键：上滑直接上屏符号
            if (dy > config.symbolSlideUp && config.keyboardSymbolEnabled) {
                gestureMode = GestureMode.SwipeSymbol
                mLongPressKey = true
                mAbortKey = true
                removeMessages()
                mService?.responseLongKeyEvent(Pair(PopupMenuMode.Text, smallLabel))
            }
            return
        }
        // 无符号预设的键交给菜单弹窗做上滑确认
        if (mLongPressKey) handleMenuGesture()
    }

    /**
     * 菜单弹窗的滑动确认：只有上滑为主（或已选中）时才参与，
     * 避免左滑加速时手指难免上抬，导致误选“清空”而暂停重复删除
     */
    private fun handleMenuGesture() {
        val isVertical = abs(totalDistanceX) * 1.5 < abs(totalDistanceY)
        if (isVertical || popupComponent.isTriggerSelected()) {
            popupComponent.onGestureEvent(totalDistanceY)
        }
    }

    /**
     * 空格左右滑动移光标，每走一段距离移一格
     */
    private fun handleSpaceCursor(currentX: Float) {
        val speed = gestureConfig?.spaceCursorSpeed ?: return
        if (speed <= 0f) return
        val step = currentX - lastStepX
        if (abs(step) < speed) return
        val stepSign = if (step < 0) -1 else 1
        lastStepX = currentX - stepSign * speed
        removeMessages()
        mAbortKey = true
        mService?.responseKeyEvent(SoftKey(code = if (stepSign < 0) KeyEvent.KEYCODE_DPAD_LEFT else KeyEvent.KEYCODE_DPAD_RIGHT))
    }

    /**
     * 加速删除的锁存/退出判定：
     * - 向左滑动且超过进入阈值 → 锁存加速，之后即使手指停住也保持加速；
     * - 向右滑回一段时间（[fastDeleteExitBand]）→ 退出加速，恢复普通删除。
     */
    private fun updateFastDeleteLatch() {
        if (downKey?.code != KeyEvent.KEYCODE_DEL) return
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
        val softKey = downKey ?: return RepeatDirection.None
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
     * 重复触发的间隔：左滑加速删除时缩短，长按删除按下滑还原时放慢
     */
    private fun nextRepeatInterval(): Long = when {
        repeatDirection == RepeatDirection.DeleteFast -> FAST_REPEAT_INTERVAL
        repeatDirection == RepeatDirection.Revert && mLongPressKey -> REVERT_REPEAT_INTERVAL
        else -> REPEAT_INTERVAL
    }

    /**
     * 执行一次重复触发。
     */
    private fun repeatKey(): Boolean {
        val service = mService ?: return false
        val softKey = downKey ?: return false
        if (!softKey.repeatable()) return false
        if (softKey.code == KeyEvent.KEYCODE_DEL && mLongPressKey) {
            if (popupComponent.isTriggerSelected()) return true
            if (repeatDirection == RepeatDirection.Revert) {
                service.responseLongKeyEvent(Pair(PopupMenuMode.Revertl, ""))
            } else {
                service.responseKeyEvent(SoftKey(KeyEvent.KEYCODE_DEL))
            }
        } else if (softKey.code == InputModeSwitcher.USER_KEYCODE_CURSOR_DIRECTION) {
            service.responseKeyEvent(
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
            service.responseKeyEvent(softKey)
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
        private const val REVERT_REPEAT_INTERVAL = 150L // 下滑持续还原删除历史
        private const val REPEAT_START_DELAY = 400L
        private const val FAST_DELETE_DP = 20f
        private const val FAST_DELETE_EXIT_DP = 16f
    }
}
