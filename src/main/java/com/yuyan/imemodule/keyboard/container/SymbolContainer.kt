package com.yuyan.imemodule.keyboard.container

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.SymbolPagerAdapter
import com.yuyan.imemodule.data.emojicon.EmojiconData
import com.yuyan.imemodule.data.emojicon.YuyanEmojiCompat
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.database.entry.UsedSymbol
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.prefs.behavior.SymbolMode
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.keyboard.InputView
import com.yuyan.imemodule.keyboard.KeyboardManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import splitties.dimensions.dp
import splitties.views.dsl.constraintlayout.bottomOfParent
import splitties.views.dsl.constraintlayout.lParams
import splitties.views.dsl.core.add
import splitties.views.dsl.core.matchParent
import splitties.views.dsl.core.wrapContent
import kotlin.math.max
import kotlin.random.Random


/**
 * 符号键盘容器
 * 包含符号界面、符号类型行（居底）。
 * 与输入键哦安不同的是，此处两个界面均使用RecyclerView实现。
 * 其中：
 * 符号界面使用RecyclerView + FlexboxLayoutManager实现Grid布局。
 * 符号类型行使用RecyclerView + LinearLayoutManager实现水平ListView效果。
 */
@SuppressLint("ViewConstructor", "ClickableViewAccessibility")
class SymbolContainer(context: Context, inputView: InputView) : BaseContainer(context, inputView) {
    private var mShowType: SymbolMode = SymbolMode.Symbol
    private var mVPSymbolsView: ViewPager2
    private var tabLayout: TabLayout
    private val ivDelete: ImageView
    var isLockSymbol = false
    private var mHandler: Handler? = null

    companion object {
        private const val MSG_REPEAT = 3
        private const val REPEAT_INTERVAL = 50L // ~20 keys per second
        private const val REPEAT_START_DELAY = 400L
    }
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mHandler == null) {
            mHandler = object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    if (isLockSymbol && repeatKey()) {
                        val repeat = Message.obtain(this, MSG_REPEAT)
                        sendMessageDelayed(repeat, REPEAT_INTERVAL)
                    }
                }
            }
        }
    }

    private fun repeatKey(): Boolean {
        inputView.responseKeyEvent(SoftKey(KeyEvent.KEYCODE_DEL))
        return true
    }

    init {
        val pressKeyBackground = GradientDrawable()
        if (ThemeManager.prefs.keyBorder.getValue()) {
            val keyRadius = ThemeManager.prefs.keyRadius.getValue()
            pressKeyBackground.setColor(activeTheme.keyPressHighlightColor)
            pressKeyBackground.setShape(GradientDrawable.RECTANGLE)
            pressKeyBackground.setCornerRadius(keyRadius.toFloat()) // 设置圆角半径
        }
        val mLLSymbolType = LayoutInflater.from(getContext()).inflate(R.layout.sdk_view_symbols_emoji_type, this, false) as LinearLayout
        tabLayout = mLLSymbolType.findViewById<TabLayout?>(R.id.tab_symbols_emoji_type).apply {
            addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.view?.background = pressKeyBackground
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab?.view?.background = null
                }
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
        mVPSymbolsView = ViewPager2(context)
        mLLSymbolType.visibility = VISIBLE
        val ivReturn:ImageView = mLLSymbolType.findViewById(R.id.iv_symbols_emoji_type_return)
        ivReturn.drawable.setTint(activeTheme.keyTextColor)
        ivDelete = mLLSymbolType.findViewById(R.id.iv_symbols_emoji_type_delete)
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
                    DevicesUtils.tryPlayKeyDown(KeyEvent.KEYCODE_DEL)
                    DevicesUtils.tryVibrate(this)
                }
                MotionEvent.ACTION_UP -> {
                    KeyboardManager.instance.switchKeyboard()
                }
            }
            true
        }
        ivDelete.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 播放按键声音和震动
                    DevicesUtils.tryPlayKeyDown(KeyEvent.KEYCODE_DEL)
                    DevicesUtils.tryVibrate(this)
                    if(isLockSymbol) {
                        mHandler?.sendEmptyMessageDelayed(MSG_REPEAT, REPEAT_START_DELAY)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if(!isLockSymbol){
                        isLockSymbol = true
                        ivDelete.setImageResource(R.drawable.sdk_skb_key_delete_icon)
                        ivDelete.drawable.setTint(activeTheme.keyTextColor)
                    } else {
                        repeatKey()
                        mHandler?.removeMessages(MSG_REPEAT)
                    }
                }
            }
            true
        }
        add(mLLSymbolType, lParams(matchParent, wrapContent){
            bottomOfParent(0)
        })
        add(mVPSymbolsView, lParams(matchParent, matchParent){
            bottomToTop = mLLSymbolType.id
        })
    }

    private fun onItemClickOperate(value: String) {
        val result = value.replace("[ \\r]".toRegex(), "")
        val softKey = SoftKey(label = result)
        DevicesUtils.tryPlayKeyDown()
        DevicesUtils.tryVibrate(this)
        if (mShowType == SymbolMode.Symbol) {  // 非表情键盘
            DataBaseKT.instance.usedSymbolDao().insert(UsedSymbol(symbol = result))
            val num = max(DataBaseKT.instance.usedSymbolDao().getCount("symbol") - 50, 0)
            DataBaseKT.instance.usedSymbolDao().deleteOldest("symbol", num)
            if(!isLockSymbol) KeyboardManager.instance.switchKeyboard()
            inputView.responseKeyEvent(softKey)
        } else {  //表情、颜文字
            if(!YuyanEmojiCompat.isWeChatInput || mVPSymbolsView.currentItem != 1 ) {
                DataBaseKT.instance.usedSymbolDao().insert(UsedSymbol(symbol = result, type = "emoji"))
                val num = max(DataBaseKT.instance.usedSymbolDao().getCount("emoji") - 50, 0)
                DataBaseKT.instance.usedSymbolDao().deleteOldest("emoji", num)
                inputView.responseKeyEvent(softKey)
            } else {
                val emojions = EmojiconData.wechatEmojiconData[value]
                if(emojions?.isNotEmpty() == true) {
                    CoroutineScope(Dispatchers.Main).launch {
                        emojions[Random.nextInt(emojions.size)].forEach {
                            inputView.responseKeyEvent(SoftKey(label = it))
                            inputView.performEditorAction(EditorInfo.IME_ACTION_SEND)
                            delay(100)
                        }
                    }
                }
            }
        }
    }

    /**
     * 切换显示界面
     */
    fun setSymbolsView() {
        mShowType = SymbolMode.Symbol
        isLockSymbol = false   // 符号键默认未锁定，表情键盘默认锁定
        ivDelete.setImageResource(R.drawable.icon_symbol_lock)
        ivDelete.drawable.setTint(activeTheme.keyTextColor)
        val mSymbolsEmoji = EmojiconData.symbolData
        mVPSymbolsView.adapter = SymbolPagerAdapter(context, mSymbolsEmoji, mShowType){ symbol, _ ->
            onItemClickOperate(symbol)
        }
        val data = mSymbolsEmoji.keys.toList()
        TabLayoutMediator(tabLayout, mVPSymbolsView) { tab, position ->
            tab.view.background = null
            tab.setCustomView(ImageView(context).apply {
                setImageDrawable(ContextCompat.getDrawable(context,data[position]).apply {
                    this?.setTint(activeTheme.keyTextColor)
                })
            })
            tab.view.setPadding(dp(5))
        }.attach()
        mVPSymbolsView.currentItem = 0
    }

    /**
     * 切换显示界面
     */
    fun setEmojisView(showType: SymbolMode) {
        mShowType = showType
        isLockSymbol = true   // 符号键默认未锁定，表情键盘默认锁定
        ivDelete.setImageResource(R.drawable.sdk_skb_key_delete_icon)
        ivDelete.drawable.setTint(activeTheme.keyTextColor)
        val mSymbolsEmoji = when (mShowType) {
            SymbolMode.Emoticon -> EmojiconData.emoticonData
            else -> {
                if (!YuyanEmojiCompat.isWeChatInput) {
                    val data = LinkedHashMap<Int, List<String>>()
                    data.putAll(EmojiconData.emojiconData)
                    data.remove(R.drawable.icon_emojibar_wechat)
                    data
                } else EmojiconData.emojiconData
            }
        }
        mVPSymbolsView.adapter = SymbolPagerAdapter(context, mSymbolsEmoji, mShowType){ symbol, _ ->
            onItemClickOperate(symbol)
        }
        val data = mSymbolsEmoji.keys.toList()
        TabLayoutMediator(tabLayout, mVPSymbolsView) { tab, position ->
            tab.view.background = null
            tab.setCustomView(ImageView(context).apply {
                setImageDrawable(ContextCompat.getDrawable(context,data[position]).apply {
                    this?.setTint(activeTheme.keyTextColor)
                })
            })
            tab.view.setPadding(dp(5))
        }.attach()
    }

    fun getMenuMode(): SymbolMode {
        return mShowType
    }
}
