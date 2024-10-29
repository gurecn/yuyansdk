package com.yuyan.imemodule.view.keyboard.container

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.SymbolPagerAdapter
import com.yuyan.imemodule.application.LauncherModel
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.data.emojicon.EmojiconData
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.view.keyboard.InputView
import com.yuyan.imemodule.view.keyboard.KeyboardManager


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
    private var mShowType = 0
    private lateinit var mVPSymbolsView: ViewPager2
    private lateinit var tabLayout: TabLayout

    init {
        initView(context)
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun initView(context: Context) {
        val pressKeyBackground = GradientDrawable()
        if (ThemeManager.prefs.keyBorder.getValue()) {
            val mActiveTheme = activeTheme
            val keyRadius = ThemeManager.prefs.keyRadius.getValue()
            pressKeyBackground.setColor(mActiveTheme.genericActiveBackgroundColor)
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
        this.addView(mVPSymbolsView, layoutParams2)
    }

    private fun onItemClickOperate(value: String) {
        val result = value.replace("[ \\r]".toRegex(), "")
        if (mShowType < CustomConstant.EMOJI_TYPR_FACE_DATA) {  // 非表情键盘
            LauncherModel.instance.usedCharacterDao!!.insertUsedCharacter(result, System.currentTimeMillis())
            if(!AppPrefs.getInstance().internal.keyboardLockSymbol.getValue()) {
                inputView.resetToIdleState()
                KeyboardManager.instance.switchKeyboard(mInputModeSwitcher!!.skbLayout)
            }
        } else {  //表情、颜文字
            LauncherModel.instance.usedEmojiDao!!.insertUsedEmoji(result, System.currentTimeMillis())
        }
        val softKey = SoftKey(result)
        DevicesUtils.tryPlayKeyDown(softKey)
        DevicesUtils.tryVibrate(this)
        inputView.responseKeyEvent(softKey)
    }

    /**
     * 切换显示界面
     */
    fun setSymbolsView(showType: Int) {
        mShowType = showType
        var pos = 0
        val mSymbolsEmoji = when (mShowType) {
            5 -> EmojiconData.emoticonData
            4 -> EmojiconData.emojiconData
            else -> {
                pos = showType
                EmojiconData.symbolData
            }
        }
        mVPSymbolsView.adapter = SymbolPagerAdapter(context, mSymbolsEmoji, mShowType){ symbol, _ ->
            onItemClickOperate(symbol)
        }
        val data = mSymbolsEmoji.keys.toList()
        TabLayoutMediator(tabLayout, mVPSymbolsView) { tab, position ->
            tab.icon = ContextCompat.getDrawable(context,data[position].icon)
            tab.view.background = null
        }.attach()
        mVPSymbolsView.currentItem = pos
    }

    fun getMenuMode(): Int {
        return mShowType
    }
}
