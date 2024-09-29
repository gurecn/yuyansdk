package com.yuyan.imemodule.view.keyboard

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
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.entity.keyboard.SoftKeyboard
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.AppPrefs
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
    private var mCurrentKeyPressed: SoftKey? = null // 按下的按键，用于界面更新
    private var mCurrentKey: SoftKey? = null
    private var mGestureDetector: GestureDetector? = null
    protected var mInvalidatedKey: SoftKey? = null
    private var mAbortKey = false
    private var mLongPressKey = false
    private var mSwipeMoveKey = false
    private var mHandler: Handler? = null
    /** Whether the keyboard bitmap needs to be redrawn before it's blitted.  */
    protected var mDrawPending = false

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
                override fun onScroll(downEvent: MotionEvent?, currentEvent: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                    if(mLongPressKey){
                        popupComponent.changeFocus(currentEvent.x - downEvent!!.x, currentEvent.y - downEvent.y)
                    } else {
                        dispatchGestureEvent(downEvent, currentEvent, distanceX, distanceY)
                    }
                    return true
                }
            })
            mGestureDetector!!.setIsLongpressEnabled(false)
        }
    }

    private fun detectAndSendKey(key: SoftKey?) {
        if (mLongPressKey || key == null) return
        mService?.responseKeyEvent(key)
    }

    fun invalidateAllKeys() {
        mDrawPending = true
        invalidate()
    }

    fun invalidateKey(key: SoftKey?) {
        mInvalidatedKey = key
        if (key == null) return
        onBufferDraw()
        invalidate()
    }

    open fun onBufferDraw() {}
    private fun openPopupIfRequired() {
        if(mCurrentKey != null) {
            val softKey = mCurrentKey!!
            val keyboardSymbol = ThemeManager.prefs.keyboardSymbol.getValue()
            if (keyboardSymbol && !TextUtils.isEmpty(softKey.getkeyLabel())) {
                val keyLabel = if (mService!!.mInputModeSwitcher.isEnglishLower || (mService!!.mInputModeSwitcher.isEnglishUpperCase && mService!!.mDecInfo.composingStrForDisplay.isNotEmpty()))
                    softKey.keyLabel.lowercase()  else softKey.keyLabel
                val bounds = Rect(softKey.mLeft, softKey.mTop, softKey.mRight, softKey.mBottom)
                popupComponent.showKeyboard(keyLabel, softKey.getmKeyLabelSmall(), bounds)
                mLongPressKey = true
            } else if (softKey.keyCode == InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2 ||
                    softKey.keyCode == InputModeSwitcherManager.USER_DEF_KEYCODE_SHIFT_1 ||
                    softKey.keyCode == KeyEvent.KEYCODE_DEL){
                val bounds = Rect(softKey.mLeft, softKey.mTop, softKey.mRight, softKey.mBottom)
                popupComponent.showKeyboardMenu(softKey, bounds)
                mLongPressKey = true
            } else {
                mLongPressKey = true
                mSwipeMoveKey = true
                dismissPreview()
            }
        }
    }


    private var motionEventQueue: Queue<MotionEvent> = LinkedList()

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(me: MotionEvent): Boolean {
        val result: Boolean
        when (val action = me.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val actionIndex = me.actionIndex
                val x = me.getX(actionIndex)
                val y = me.getY(actionIndex)
                val now = me.eventTime
                val down = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, x, y, me.metaState)
                motionEventQueue.offer(down)
                result = onModifiedTouchEvent(me)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                val first = motionEventQueue.poll()
                if (first != null) {
                    val now = me.eventTime
                    val act = if(action == MotionEvent.ACTION_CANCEL)MotionEvent.ACTION_CANCEL else MotionEvent.ACTION_UP
                    val up = MotionEvent.obtain(now, now, act, first.x, first.y, me.metaState)
                    result = onModifiedTouchEvent(up)
                    up.recycle()
                    first.recycle()
                } else {
                    result = onModifiedTouchEvent(me)
                }
            }
            else -> {
                result = onModifiedTouchEvent(me)
            }
        }
        return result
    }

    private fun onModifiedTouchEvent(me: MotionEvent): Boolean {
        val touchX = me.x.toInt()
        val touchY = me.y.toInt()
        val action = me.action
        val keyIndex = getKeyIndices(touchX, touchY)
        if (mAbortKey && action != MotionEvent.ACTION_DOWN) {
            return true
        }
        if (mGestureDetector!!.onTouchEvent(me)) {
            return true
        }
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mAbortKey = false
                mSwipeMoveKey = false
                mLongPressKey = false
                mCurrentKey = keyIndex
                if(keyIndex != null)onPress(keyIndex)
                if (keyIndex != null && keyIndex.repeatable()) {
                    val msg = mHandler!!.obtainMessage(MSG_REPEAT)
                    mHandler!!.sendMessageDelayed(msg, REPEAT_START_DELAY.toLong())
                    if (mAbortKey) return true
                }
                if (keyIndex != null) {
                    val msg = mHandler!!.obtainMessage(MSG_LONGPRESS, me)
                    mHandler!!.sendMessageDelayed(msg, AppPrefs.getInstance().keyboardSetting.longPressTimeout.getValue().toLong())
                }
                showPreview(keyIndex)
            }

            MotionEvent.ACTION_UP -> {
                removeMessages()
                mCurrentKey = keyIndex
                if (!mAbortKey && !mSwipeMoveKey) {
                    if (!mLongPressKey && keyIndex != null) {
                        mService?.responseKeyEvent(keyIndex)
                    }

                }
                dismissPreview()
            }
            MotionEvent.ACTION_CANCEL -> {
                removeMessages()
                mAbortKey = true
                dismissPreview()
            }
        }
        return true
    }

    private var lastEventX:Float = -1f
    private var lastEventY:Float = -1f
    private var lastEventActionIndex:Int = 0
    // 处理手势滑动
    private fun dispatchGestureEvent(downEvent: MotionEvent?, currentEvent: MotionEvent, distanceX: Float, distanceY: Float) : Boolean {
        var result = false
        val currentX = currentEvent.x
        val currentY = currentEvent.y
        if(currentEvent.pointerCount > 1) return false    // 避免多指触控导致上屏
        if(lastEventX < 0 || lastEventActionIndex != currentEvent.actionIndex) {   // 避免多指触控导致符号上屏
            lastEventX = currentX
            lastEventY = currentY
            lastEventActionIndex = currentEvent.actionIndex
            return false
        }
        val relDiffX = abs(currentX - lastEventX)
        val relDiffY = abs(currentY - lastEventY)
        val isVertical = relDiffX * 1.5 < relDiffY  //横向、竖向滑动距离接近时，优先触发左右滑动
        if (!isVertical && relDiffX > 10)  {  // 左右滑动
            val isSwipeKey = mCurrentKey?.keyCode == KeyEvent.KEYCODE_SPACE || mCurrentKey?.keyCode == KeyEvent.KEYCODE_0
            if (isSwipeKey && AppPrefs.getInstance().keyboardSetting.spaceSwipeMoveCursor.getValue()) {  // 左右滑动
                mHandler!!.removeMessages(MSG_LONGPRESS)
                lastEventX = currentX
                lastEventY = currentY
                mSwipeMoveKey = true
                val key = SoftKey()
                key.keyCode = if (distanceX > 0) KeyEvent.KEYCODE_DPAD_LEFT else KeyEvent.KEYCODE_DPAD_RIGHT
                mService!!.responseKeyEvent(key, false)
                result = true
            }
        } else if (isVertical && relDiffY > 10  && ThemeManager.prefs.keyboardSymbol.getValue()){   // 上下滑动
            lastEventX = currentX
            lastEventY = currentY
            lastEventActionIndex = currentEvent.actionIndex
            mLongPressKey = true
            val keyLableSmall = mCurrentKey?.getmKeyLabelSmall()
            if(keyLableSmall?.isNotBlank() == true) {
                mHandler!!.removeMessages(MSG_LONGPRESS)
                mService?.responseLongKeyEvent(null, mCurrentKey?.getmKeyLabelSmall())
                result = true
            }
        } else {
            popupComponent.changeFocus( currentEvent.x - downEvent!!.x, currentEvent.y - downEvent.y)
        }
        return result
    }

    private fun repeatKey(): Boolean {
        if (mCurrentKey != null) {
            mService?.responseKeyEvent(mCurrentKey!!, false)
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
        if (key != null) {
            key.onPressed()
            invalidateKey(key)
            showBalloonText(key)
            mCurrentKeyPressed = key
        } else {
            popupComponent.dismissPopup()
        }
    }

    /**
     * 隐藏短按气泡
     */
    private fun dismissPreview() {
        if (mLongPressKey) {
            mService?.responseLongKeyEvent(mCurrentKeyPressed, popupComponent.triggerFocused())
            mLongPressKey = false
        }
        if (mCurrentKeyPressed != null) {
            mCurrentKeyPressed!!.onReleased()
            if(mService == null) return
            if (mService!!.mInputModeSwitcher.isEnglish && (mService!!.mDecInfo.composingStrForDisplay.isBlank() ||  mService!!.mDecInfo.composingStrForDisplay.length == 1)) {
                invalidateAllKeys()
            } else {
                invalidateKey(mCurrentKeyPressed)
            }
        }
        popupComponent.dismissPopup()
        mCurrentKeyPressed = null
        lastEventX = -1f
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
        private const val REPEAT_INTERVAL = 50 // ~20 keys per second
        private const val REPEAT_START_DELAY = 400
    }
}
