package com.yuyan.imemodule.view.keyboard.container

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.SymbolAdapter
import com.yuyan.imemodule.adapter.SymbolTypeAdapter
import com.yuyan.imemodule.application.LauncherModel
import com.yuyan.imemodule.callback.OnRecyclerItemClickListener
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.data.emojicon.EmojiconData
import com.yuyan.imemodule.data.emojicon.YuyanEmojiCompat
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.view.keyboard.InputView
import com.yuyan.imemodule.view.keyboard.KeyboardManager
import splitties.dimensions.dp
import kotlin.math.ceil


/**
 * 符号键盘容器
 * 包含符号界面、符号类型行（居底）。
 * 与输入键哦安不同的是，此处两个界面均使用RecyclerView实现。
 * 其中：
 * 符号界面使用RecyclerView + FlexboxLayoutManager实现Grid布局。
 * 符号类型行使用RecyclerView + LinearLayoutManager实现水平ListView效果。
 */
@SuppressLint("ViewConstructor")
class SymbolContainer(context: Context, inputView: InputView) : BaseContainer(context, inputView) {
    private lateinit var mPaint : Paint // 测量字符串长度
    private var lastPosition = 0 // 记录上次选中的位置，再次点击关闭符号界面
    private var mShowType = 0
    private var mSymbolsEmoji : Map<EmojiconData.Category, List<String>>? = null
    private var mRVSymbolsView: RecyclerView? = null
    private var mRVSymbolsType: RecyclerView? = null
    @SuppressLint("ClickableViewAccessibility")
    private fun initView(context: Context) {
        mPaint = Paint()
        mPaint.textSize = dp(20f)
        val mLLSymbolType = LayoutInflater.from(getContext()).inflate(R.layout.sdk_view_symbols_emoji_type, this, false) as LinearLayout
        mRVSymbolsType = mLLSymbolType.findViewById(R.id.rv_symbols_emoji_type)
        val layoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        mRVSymbolsType?.setLayoutManager(layoutManager)
        mRVSymbolsView = RecyclerView(context)
        mLLSymbolType.visibility = VISIBLE
        val ivReturn = mLLSymbolType.findViewById<ImageView>(R.id.iv_symbols_emoji_type_return)
        ivReturn.drawable.setTint(activeTheme.keyTextColor)
        val ivDelete = mLLSymbolType.findViewById<ImageView>(R.id.iv_symbols_emoji_type_delete)
        ivDelete.drawable.setTint(activeTheme.keyTextColor)
        val isKeyBorder = ThemeManager.prefs.keyBorder.getValue()
        if (isKeyBorder) {
            val keyRadius = ThemeManager.prefs.keyRadius.getValue()
            val bg = GradientDrawable()
            bg.setColor(activeTheme.keyBackgroundColor)
            bg.shape = GradientDrawable.RECTANGLE
            bg.cornerRadius = keyRadius.toFloat() // 设置圆角半径
            ivDelete.background = bg
            ivReturn.background = bg
        }
        ivReturn.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 播放按键声音和震动
                    DevicesUtils.tryPlayKeyDown(SoftKey(KeyEvent.KEYCODE_DEL))
                    DevicesUtils.tryVibrate(this)
                }
                MotionEvent.ACTION_UP -> {
                    inputView.resetToIdleState()
                    KeyboardManager.instance.switchKeyboard(mInputModeSwitcher!!.skbLayout)
                }
            }
            true
        }
        ivDelete.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 播放按键声音和震动
                    DevicesUtils.tryPlayKeyDown(SoftKey(KeyEvent.KEYCODE_DEL))
                    DevicesUtils.tryVibrate(this)
                }
                MotionEvent.ACTION_UP -> {
                    inputView.responseKeyEvent(SoftKey(KeyEvent.KEYCODE_DEL))
                }
            }
            true
        }
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        layoutParams.addRule(ALIGN_PARENT_BOTTOM)
        mLLSymbolType.layoutParams = layoutParams
        this.addView(mLLSymbolType)
        val layoutParams2 = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        layoutParams2.addRule(ABOVE, mLLSymbolType.id)
        mRVSymbolsView!!.layoutParams = layoutParams2
        this.addView(mRVSymbolsView)
    }

    private fun onItemClickOperate(parent: RecyclerView.Adapter<*>?, position: Int) {
        val adapter = parent as SymbolAdapter?
        val s = adapter!!.getItem(position)
        val result = s.replace("[ \\r]".toRegex(), "")
        val viewType = adapter.viewType
        if (viewType < CustomConstant.EMOJI_TYPR_FACE_DATA) {  // 非表情键盘
            LauncherModel.instance.usedCharacterDao!!.insertUsedCharacter(result, System.currentTimeMillis())
            if(!AppPrefs.getInstance().internal.keyboardLockSymbol.getValue()) {
                inputView.resetToIdleState()
                KeyboardManager.instance.switchKeyboard(mInputModeSwitcher!!.skbLayout)
            }
        } else if (viewType == CustomConstant.EMOJI_TYPR_FACE_DATA) {  // Emoji表情
            LauncherModel.instance.usedEmojiDao!!.insertUsedEmoji(result, System.currentTimeMillis())
        } else if (viewType == CustomConstant.EMOJI_TYPR_SMILE_TEXT) { // 颜文字
            LauncherModel.instance.usedEmoticonsDao!!.insertUsedEmoticons(result, System.currentTimeMillis())
        }
        val softKey = SoftKey(result)
        DevicesUtils.tryPlayKeyDown(softKey)
        DevicesUtils.tryVibrate(this)
        inputView.responseKeyEvent(softKey)
    }


    init {
        initView(context)
    }

    private fun onTypeItemClickOperate(position: Int) {
        if (position < 0) return
        if (lastPosition != position) {
            if(mShowType != 4 && mShowType != 5) {
                inputView.showSymbols(LauncherModel.instance.usedCharacterDao!!.allUsedCharacter)
            }
            updateSymbols({ parent: RecyclerView.Adapter<*>?, _: View?, pos: Int -> onItemClickOperate(parent, pos) }, position)
        } else {
            inputView.resetToIdleState()
            KeyboardManager.instance.switchKeyboard(mInputModeSwitcher!!.skbLayout)
        }
    }

    //显示表情和符号
    private fun updateSymbols(listener: OnRecyclerItemClickListener, position: Int) {
        lastPosition = position
        val faceData =  if(mShowType == 4 && position == 0){
            LauncherModel.instance.usedEmojiDao!!.allUsedEmoji
        } else {
            mSymbolsEmoji?.get(mSymbolsEmoji?.keys!!.toList()[position])
        }
        if(faceData == null)return
        val layoutManager = GridLayoutManager(context, 8)
        if(faceData.isNotEmpty()) {
            calculateColumn(faceData)
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(i: Int): Int {
                    return mHashMapSymbols[i] ?: 1
                }
            }
        }
        val mSymbolAdapter = SymbolAdapter(context, faceData, mShowType)
        mRVSymbolsView!!.setLayoutManager(layoutManager)
        mSymbolAdapter.setOnItemClickLitener(listener)
        mRVSymbolsView!!.setAdapter(mSymbolAdapter)
    }

    private val mHashMapSymbols = HashMap<Int, Int>() //候选词索引列数对应表

    /**
     * 计算符号列表实际所占列数
     */
    private fun calculateColumn(data: List<String>) {
        mHashMapSymbols.clear()
        val itemWidth = EnvironmentSingleton.instance.skbWidth/16
        var mCurrentColumn = 0
        for (position in data.indices) {
            val candidate = data[position]
            var count = getSymbolsCount(candidate, itemWidth)
            var nextCount = 0
            if (data.size > position + 1) {
                val nextCandidate = data[position + 1]
                nextCount = getSymbolsCount(nextCandidate, itemWidth)
            }
            if (mCurrentColumn + count + nextCount > 8) {
                count = 8 - mCurrentColumn
                mCurrentColumn = 0
            } else {
                mCurrentColumn = (mCurrentColumn + count) % 8
            }
            mHashMapSymbols[position] = count
        }
    }

    /**
     * 根据词长计算当前候选词需占的列数
     */
    private fun getSymbolsCount(data: String, itemWidth:Int): Int {
        return if (!TextUtils.isEmpty(data)) {
            val bounds = Rect()
            mPaint.getTextBounds(data, 0, data.length, bounds)
            val x = ceil(bounds.width().toFloat().div(itemWidth)).toInt()
            if(x >= 8) 8
            else if(x >= 4) 4
            else if(x > 1) 2
            else  1
        } else 0
    }

    /**
     * 切换显示界面
     */
    fun setSymbolsView(showType: Int) {
        mShowType = showType
        var pos = showType
        mSymbolsEmoji = when (mShowType) {
            4 -> {
                pos = 0
                val emojiCompatInstance = YuyanEmojiCompat.getAsFlow().value
                EmojiconData.emojiconData.mapValues { (category, emojiList) ->
                    if(category.label=="🔥") emojiList
                    else emojiList.filter { emoji ->
                            YuyanEmojiCompat.getEmojiMatch(emojiCompatInstance, emoji)
                    }
                }
            }
            5 -> {
                pos = 0
                EmojiconData.emoticonData
            }
            else -> {
                EmojiconData.symbolData
            }
        }
        updateSymbols({ parent: RecyclerView.Adapter<*>?, _: View?, position: Int -> onItemClickOperate(parent, position) }, pos)
        val data = mSymbolsEmoji?.keys!!.toList()
        val adapter = SymbolTypeAdapter(context, data, lastPosition)
        adapter.setOnItemClickLitener { _: RecyclerView.Adapter<*>?, _: View?, position: Int ->
            // 播放按键声音和震动
            DevicesUtils.tryPlayKeyDown()
            DevicesUtils.tryVibrate(this)
            onTypeItemClickOperate(position)
        }
        mRVSymbolsType!!.setAdapter(adapter)
    }

    fun getMenuMode(): Int {
        return mShowType
    }
}
