package com.yuyan.imemodule.view.keyboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.VectorDrawable
import android.text.TextUtils
import android.view.KeyEvent
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.data.theme.ThemeManager.prefs
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.entity.keyboard.SoftKeyToggle
import com.yuyan.imemodule.entity.keyboard.SoftKeyboard
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import kotlin.math.max
import kotlin.math.min

/**
 * 软件盘视图
 *
 * @version 1.0
 * @author: Gaolei  gurecn@gmail.com
 * @date: 2017/12/12
 */
open class TextKeyboard(context: Context?) : BaseKeyboardView(context){
    /**
     * 正常按键的文本大小
     */
    private var mNormalKeyTextSize = 0

    /**
     * 正常按键的文本大小(小值)
     */
    private var mNormalKeyTextSizeSmall = 0
    //绘制按键的画笔
    private val mPaint: Paint = Paint()
    private val mFmi: FontMetricsInt
    private var isKeyBorder = false // 启用按键边框
    protected lateinit var mActiveTheme: Theme
    private var keyRadius = 0

    /**
     * 构造方法
     */
    init {
        mPaint.isAntiAlias = true
        mFmi = mPaint.fontMetricsInt
    }

    override fun getKeyIndices(x: Int, y: Int): SoftKey? {
        return mSoftKeyboard?.mapToKey(x, y)
    }

    /**
     * 设置键盘实体
     *
     * @param softSkb 键盘
     */
    override fun setSoftKeyboard(softSkb: SoftKeyboard) {
        super.setSoftKeyboard(softSkb)
        isKeyBorder = prefs.keyBorder.getValue()
        keyRadius = prefs.keyRadius.getValue()
        mActiveTheme = activeTheme
        mPaint.color = mActiveTheme.keyTextColor
        // Hint to reallocate the buffer if the size changed
        mKeyboardChanged = true
        invalidateView()
    }

    /**
     * 刷新按键状态
     */
    fun updateStates(switcherManager: InputModeSwitcherManager) {
        if (switcherManager.isEnglish) {
            var softKey = mSoftKeyboard?.getKeyByCode(KeyEvent.KEYCODE_ENTER) as SoftKeyToggle??: return
            softKey.enableToggleState(switcherManager.mToggleStates.mStateEnter)
            softKey = mSoftKeyboard?.getKeyByCode(InputModeSwitcherManager.USERDEF_KEYCODE_SHIFT_1) as SoftKeyToggle??: return
            softKey.enableToggleState(switcherManager.mToggleStates.charCase)
            invalidateView()
        } else {
            val softKey = mSoftKeyboard?.getKeyByCode(KeyEvent.KEYCODE_ENTER) as SoftKeyToggle??: return
            if (softKey.enableToggleState(switcherManager.mToggleStates.mStateEnter)) {
                invalidateKey(softKey)
            }
        }
    }

    /**
     * 重置主题
     */
    open fun setTheme(theme: Theme) {
        isKeyBorder = prefs.keyBorder.getValue()
        keyRadius = prefs.keyRadius.getValue()
        mActiveTheme = theme
        mPaint.color = mActiveTheme.keyTextColor
        invalidateView()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var measuredWidth = 0
        var measuredHeight = 0
        if (null != mSoftKeyboard) {
            measuredWidth = mSoftKeyboard!!.skbCoreWidth
            measuredHeight = mSoftKeyboard!!.skbCoreHeight
            measuredWidth += paddingLeft + paddingRight
            measuredHeight += paddingTop + paddingBottom
        }
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    private fun invalidateView() {
        requestLayout()
        invalidateAllKeys()
    }

    public override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (mSoftKeyboard != null) {
            mSoftKeyboard!!.setSkbCoreSize(w, h)
        }
        // Release the buffer, if any and it will be reallocated on the next draw
        mBuffer = null
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mDrawPending || mBuffer == null || mKeyboardChanged) {
            onBufferDraw()
        }
        canvas.drawBitmap(mBuffer!!, 0f, 0f, null)
    }

    override fun onBufferDraw() {
        if (mBuffer == null || mKeyboardChanged) {
            if (mBuffer == null || mBuffer!!.width != width || mBuffer!!.height != height) {
                // Make sure our bitmap is at least 1x1
                val width = max(1.0, width.toDouble()).toInt()
                val height = max(1.0, height.toDouble()).toInt()
                mBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                mCanvas = Canvas(mBuffer!!)
            }
            invalidateAllKeys()
            mKeyboardChanged = false
        }
        if (mSoftKeyboard == null) return
        mCanvas!!.save()
        val canvas = mCanvas
        canvas?.clipRect(mDirtyRect)
        val clipRegion = mClipRegion
        val invalidKey = mInvalidatedKey
        var drawSingleKey = false
        if (invalidKey != null && canvas!!.getClipBounds(clipRegion)) {
            // Is clipRegion completely contained within the invalidated key?
            if (invalidKey.mLeft <= clipRegion.left && invalidKey.mTop <= clipRegion.top && invalidKey.mRight >= clipRegion.right && invalidKey.mBottom >= clipRegion.bottom) {
                drawSingleKey = true
            }
        }
        canvas?.drawColor(0x00000000, PorterDuff.Mode.CLEAR)
        val env = instance
        mNormalKeyTextSize = env.keyTextSize
        mNormalKeyTextSizeSmall = env.keyTextSmallSize
        val keyXMargin = mSoftKeyboard!!.keyXMargin
        val keyYMargin = mSoftKeyboard!!.keyYMargin
        for (softKeys in mSoftKeyboard!!.row) {
            for (softKey in softKeys) {
                if (drawSingleKey && invalidKey !== softKey) continue
                canvas?.let { drawSoftKey(it, softKey, keyXMargin, keyYMargin) }
            }
        }
        mInvalidatedKey = null
        mCanvas!!.restore()
        mDrawPending = false
        mDirtyRect.setEmpty()
    }

    /**
     * 在画布上画一个按键
     *
     * @param canvas     画布
     * @param softKey    需绘制的按键
     * @param keyXMargin 按键左右边间距
     * @param keyYMargin 按键上下边间距
     */
    private fun drawSoftKey(canvas: Canvas, softKey: SoftKey, keyXMargin: Int, keyYMargin: Int) {
        val bg = GradientDrawable()
        var textColor = mActiveTheme.keyTextColor
        if (softKey.pressed) {
            bg.setColor(mActiveTheme.keyPressHighlightColor)
            bg.shape = GradientDrawable.RECTANGLE
            bg.cornerRadius = keyRadius.toFloat() // 设置圆角半径
            bg.setBounds(
                softKey.mLeft + keyXMargin,
                softKey.mTop + keyYMargin,
                softKey.mRight - keyXMargin,
                softKey.mBottom - keyYMargin
            )
            bg.draw(canvas)
            textColor = mActiveTheme.popupTextColor
        } else if (isKeyBorder) {
            val background = when (softKey.keyCode) {
                KeyEvent.KEYCODE_ENTER -> {
                    mActiveTheme.accentKeyBackgroundColor
                }
                KeyEvent.KEYCODE_SPACE -> {
                    mActiveTheme.spaceBarColor
                }
                else -> {
                    mActiveTheme.keyBackgroundColor
                }
            }
            bg.setColor(background)
            bg.shape = GradientDrawable.RECTANGLE
            bg.cornerRadius = keyRadius.toFloat() // 设置圆角半径
            bg.setBounds(
                softKey.mLeft + keyXMargin,
                softKey.mTop + keyYMargin,
                softKey.mRight - keyXMargin,
                softKey.mBottom - keyYMargin
            )
            bg.draw(canvas)
        } else {
           if(softKey.keyCode == KeyEvent.KEYCODE_ENTER) {
               bg.setColor(mActiveTheme.accentKeyBackgroundColor)
               bg.shape = GradientDrawable.OVAL
               val bgWidth = softKey.width() - 2 * keyXMargin
               val bgHeight = softKey.height() - 2 * keyYMargin
               val radius = min(bgWidth, bgHeight)*2/3
               val keyMarginX = (bgWidth - radius)/2
               val keyMarginY = (bgHeight - radius)/2
                bg.setBounds(
                    softKey.mLeft + keyMarginX,
                    softKey.mTop + keyMarginY,
                    softKey.mRight - keyMarginX,
                    softKey.mBottom - keyMarginY
                )
                bg.draw(canvas)
            }

        }
        val keyLabel = if(mService != null && mService!!.mInputModeSwitcher.isEnglish) {
            if (mService!!.mInputModeSwitcher.isEnglishLower || (mService!!.mInputModeSwitcher.isEnglishUpperCase && mService!!.mDecInfo.composingStrForDisplay.isNotEmpty())) {
                softKey.keyLabel?.lowercase()
            } else {
                softKey.keyLabel
            }
        } else {
            softKey.keyLabel
        }
        val keyLabelSmall = softKey.getmKeyLabelSmall()
        val keyMnemonic = softKey.keyMnemonic
        val keyIcon = softKey.keyIcon
        val keyboardSymbol = prefs.keyboardSymbol.getValue()
        val keyboardMnemonic = prefs.keyboardMnemonic.getValue()
        val weightHeigth = softKey.height() / 4f
        if (keyboardSymbol && !TextUtils.isEmpty(keyLabelSmall)) {
            mPaint.color = textColor
            mPaint.textSize = mNormalKeyTextSizeSmall.toFloat()
            val x = softKey.mLeft + (softKey.width() - mPaint.measureText(keyLabelSmall)) / 2.0f
            val y = softKey.mTop + weightHeigth
            canvas.drawText(keyLabelSmall, x, y, mPaint)
        }
        if (null != keyIcon) {
            var  intrinsicWidth = keyIcon.intrinsicWidth
            var  intrinsicHeight = keyIcon.intrinsicHeight
            while(softKey.width() < intrinsicWidth || softKey.height() < intrinsicHeight){
                intrinsicWidth /= 2
                intrinsicHeight /= 2
            }
            val marginLeft = (softKey.width() - intrinsicWidth) / 2
            val marginRight = softKey.width() - intrinsicWidth - marginLeft
            val marginTop = (softKey.height() - intrinsicHeight) / 2
            val marginBottom = softKey.height() - intrinsicHeight - marginTop
            (keyIcon as? VectorDrawable)?.setTint(mActiveTheme.keyTextColor)
            keyIcon.setBounds(softKey.mLeft + marginLeft, softKey.mTop + marginTop, softKey.mRight - marginRight, softKey.mBottom - marginBottom)
            keyIcon.draw(canvas)
        } else if (!TextUtils.isEmpty(keyLabel)) {
            //Label位于中间
            mPaint.color = textColor
            mPaint.textSize = mNormalKeyTextSize.toFloat()
            val x = softKey.mLeft + (softKey.width() - mPaint.measureText(keyLabel)) / 2.0f
            val fontHeight = mFmi.bottom - mFmi.top
            val y = (softKey.mTop + softKey.mBottom) / 2.0f + fontHeight
            canvas.drawText(keyLabel!!, x, y, mPaint)
        }
        if (keyboardMnemonic && !TextUtils.isEmpty(keyMnemonic)) {
            //助记符位于中下方
            mPaint.color = mActiveTheme.popupTextColor
            mPaint.textSize = mNormalKeyTextSizeSmall.toFloat()
            val x = softKey.mLeft + (softKey.width() - mPaint.measureText(keyMnemonic)) / 2.0f
            val y = softKey.mTop + weightHeigth * 3 + weightHeigth / 2.0f
            canvas.drawText(keyMnemonic!!, x, y, mPaint)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        closing()
    }

    override fun closing() {
        super.closing()
        mBuffer = null
        mCanvas = null
    }
}
