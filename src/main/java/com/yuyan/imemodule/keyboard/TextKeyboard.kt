package com.yuyan.imemodule.keyboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.KeyEvent
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.data.theme.ThemeManager.prefs
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.entity.keyboard.SoftKeyToggle
import com.yuyan.imemodule.entity.keyboard.SoftKeyboard
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.service.DecodingInfo
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import kotlin.math.max
import kotlin.math.min
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withSave
import com.yuyan.imemodule.entity.keyboard.KeyType
import com.yuyan.imemodule.prefs.behavior.SkbStyleMode

/**
 * 软件盘视图
 */
open class TextKeyboard(context: Context?) : BaseKeyboardView(context){
    private var mKeyboardChanged = false
    private var mBuffer: Bitmap? = null
    private var mCanvas: Canvas? = null
    private var mNormalKeyTextSize = 0   //正常按键的文本大小
    private var mNormalKeyTextSizeSmall = 0  //正常按键的文本大小(小值)
    private val mPaint: Paint = Paint()   //绘制按键的画笔
    private val mFmi: FontMetricsInt
    private var isKeyBorder = false // 启用按键边框
    protected lateinit var mActiveTheme: Theme
    private var keyRadius = 0
    private var keyboardFontBold = false
    private var keyboardSymbol = false
    private var keyboardMnemonic = false
    protected var mDirtyRect = Rect()
    private var skbStyleMode: SkbStyleMode = prefs.skbStyleMode.getValue()

    /**
     * 构造方法
     */
    init {
        mPaint.isAntiAlias = true
        mFmi = mPaint.fontMetricsInt
        keyboardFontBold = prefs.keyboardFontBold.getValue()
        keyboardSymbol = prefs.keyboardSymbol.getValue()
        keyboardMnemonic = AppPrefs.getInstance().keyboardSetting.keyboardMnemonic.getValue()
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
        mKeyboardChanged = true
        invalidateView()
    }

    /**
     * 刷新按键状态
     */
    fun updateStates() {
        if (InputModeSwitcherManager.isEnglish) {
            var softKey = mSoftKeyboard?.getKeyByCode(KeyEvent.KEYCODE_ENTER) as SoftKeyToggle??: return
            softKey.enableToggleState( if(mService!!.isAddPhrases)4 else InputModeSwitcherManager.mToggleStates.mStateEnter)
            softKey = mSoftKeyboard?.getKeyByCode(InputModeSwitcherManager.USER_DEF_KEYCODE_SHIFT_1) as SoftKeyToggle??: return
            var stateId = InputModeSwitcherManager.mToggleStates.charCase
            val isEnglishCell = AppPrefs.getInstance().input.abcSearchEnglishCell.getValue()
            if(isEnglishCell&& stateId in 0..2) stateId += 3
            else if(!isEnglishCell && stateId in 3..5)stateId -= 3
            softKey.enableToggleState(stateId)
            invalidateView()
        } else {
            val softKey = mSoftKeyboard?.getKeyByCode(KeyEvent.KEYCODE_ENTER) as SoftKeyToggle??: return
            if (softKey.enableToggleState(if(mService!!.isAddPhrases)4 else InputModeSwitcherManager.mToggleStates.mStateEnter)) {
                invalidateKey()
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
            measuredWidth = instance.skbWidth +  paddingLeft + paddingRight
            measuredHeight = instance.skbHeight + paddingTop + paddingBottom
        }
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    private fun invalidateView() {
        requestLayout()
        invalidateKey()
    }

    public override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBuffer = null
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mDirtyRect.union(0, 0, width, height)
        if (mDrawPending || mBuffer == null || mKeyboardChanged) {
            onBufferDraw()
        }
        canvas.drawBitmap(mBuffer!!, 0f, 0f, null)
    }

    override fun onBufferDraw() {
        if (mBuffer == null || mKeyboardChanged) {
            if (mBuffer == null || mBuffer!!.width != width || mBuffer!!.height != height) {
                val width = max(1.0, width.toDouble()).toInt()
                val height = max(1.0, height.toDouble()).toInt()
                mBuffer = createBitmap(width, height)
                mCanvas = Canvas(mBuffer!!)
            }
            invalidateKey()
            mKeyboardChanged = false
        }
        if (mSoftKeyboard == null) return
        mCanvas!!.withSave {
            val canvas = mCanvas
            canvas?.clipRect(mDirtyRect)
            canvas?.drawColor(0x00000000, PorterDuff.Mode.CLEAR)
            val env = instance
            mNormalKeyTextSize = env.keyTextSize
            mNormalKeyTextSizeSmall = env.keyTextSmallSize
            val keyXMargin = mSoftKeyboard!!.keyXMargin
            val keyYMargin = if(skbStyleMode == SkbStyleMode.Google && InputModeSwitcherManager.isQwert) mSoftKeyboard!!.keyYMargin * 1.5
                else mSoftKeyboard!!.keyYMargin
            for (softKeys in mSoftKeyboard!!.mKeyRows) {
                for (softKey in softKeys) {
                    canvas?.let { drawSoftKey(it, softKey, keyXMargin, keyYMargin.toInt()) }
                }
            }
            mCanvas!!
        }
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
        bg.shape = GradientDrawable.RECTANGLE
        bg.cornerRadius = keyRadius.toFloat() // 设置圆角半径
        bg.setBounds(softKey.mLeft + keyXMargin, softKey.mTop + keyYMargin, softKey.mRight - keyXMargin, softKey.mBottom - keyYMargin)
        if (softKey.pressed || (mService?.hasSelection == true && softKey.code == InputModeSwitcherManager.USER_DEF_KEYCODE_SELECT_MODE)) {
            bg.setColor(mActiveTheme.keyPressHighlightColor)
            bg.draw(canvas)
        } else if (isKeyBorder) {
            val background = when (softKey.code) {
                KeyEvent.KEYCODE_ENTER -> mActiveTheme.accentKeyBackgroundColor
                KeyEvent.KEYCODE_SPACE-> mActiveTheme.functionKeyBackgroundColor
                else  -> mActiveTheme.keyBackgroundColor
            }
            bg.setColor(background)
            bg.draw(canvas)
        } else if(softKey.code == KeyEvent.KEYCODE_ENTER) {
               bg.setColor(mActiveTheme.accentKeyBackgroundColor)
               bg.shape = GradientDrawable.OVAL
               val bgWidth = softKey.width() -  keyXMargin
               val bgHeight = softKey.height() - keyYMargin
               val radius = min(bgWidth, bgHeight)*3/4
               val keyMarginX = (bgWidth - radius)/2
               val keyMarginY = (bgHeight - radius)/2
                bg.setBounds(softKey.mLeft + keyMarginX, softKey.mTop + keyMarginY, softKey.mRight - keyMarginX, softKey.mBottom - keyMarginY)
                bg.draw(canvas)
        }
        val keyLabel = if(mService != null && InputModeSwitcherManager.isEnglish) {
            if (InputModeSwitcherManager.isEnglishLower || (InputModeSwitcherManager.isEnglishUpperCase && !DecodingInfo.isCandidatesListEmpty)) {
                softKey.keyLabel.lowercase()
            } else softKey.keyLabel
        } else softKey.keyLabel
        val keyLabelSmall = softKey.getmKeyLabelSmall()
        val keyMnemonic = softKey.keyMnemonic
        val keyIcon = if(skbStyleMode == SkbStyleMode.Google && softKey.code == KeyEvent.KEYCODE_SPACE) null
            else if(skbStyleMode == SkbStyleMode.Google && softKey.code == InputModeSwitcherManager.USER_DEF_KEYCODE_CURSOR_DIRECTION_9 && !DecodingInfo.isCandidatesListEmpty) null
            else softKey.keyIcon
        val weightHeigth = softKey.height() / 4f
        val textColor = mActiveTheme.keyTextColor
        if (keyboardSymbol && !TextUtils.isEmpty(keyLabelSmall)) {
            mPaint.color = textColor
            mPaint.setTypeface(Typeface.DEFAULT)
            if(skbStyleMode == SkbStyleMode.Samsung)mPaint.alpha = 128
            mPaint.textSize = mNormalKeyTextSizeSmall.toFloat()
            val x = when(prefs.skbStyleMode.getValue()){
                SkbStyleMode.Yuyan -> softKey.mLeft + (softKey.width() - mPaint.measureText(keyLabelSmall)) / 2.0f
                SkbStyleMode.Samsung -> softKey.mRight - mPaint.measureText(keyLabelSmall) - keyXMargin * 2
                SkbStyleMode.Google -> softKey.mRight - mPaint.measureText(keyLabelSmall) - keyXMargin * 2
            }
            val y = softKey.mTop + weightHeigth * 1.1f
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
            keyIcon.setTint(mActiveTheme.keyTextColor)
            keyIcon.setBounds(softKey.mLeft + marginLeft, softKey.mTop + marginTop, softKey.mRight - marginRight, softKey.mBottom - marginBottom)
            keyIcon.draw(canvas)
        } else if (!TextUtils.isEmpty(keyLabel)) { //Label位于中间
            mPaint.color = textColor
            if(keyboardFontBold) mPaint.typeface = Typeface.DEFAULT_BOLD
            mPaint.textSize =  mNormalKeyTextSize.toFloat()
            val x = softKey.mLeft + (softKey.width() - mPaint.measureText(keyLabel)) / 2.0f
            val fontHeight = mFmi.bottom - mFmi.top
            val y = if(keyLabelSmall.isEmpty()) (softKey.mTop + softKey.mBottom) / 2.0f + fontHeight
            else  (softKey.mTop + softKey.mBottom) / 2.0f + fontHeight *1.5f
            canvas.drawText(keyLabel, x, y, mPaint)
        }
        if (keyboardMnemonic && !TextUtils.isEmpty(keyMnemonic)) {  //助记符位于中下方
            mPaint.color = textColor
            mPaint.typeface = Typeface.DEFAULT
            mPaint.textSize = mNormalKeyTextSizeSmall.toFloat() * 0.7f
            val x = softKey.mLeft + (softKey.width() - mPaint.measureText(keyMnemonic)) / 2.0f
            val y = softKey.mTop + weightHeigth * 3 + weightHeigth / 2.0f
            canvas.drawText(keyMnemonic, x, y, mPaint)
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
