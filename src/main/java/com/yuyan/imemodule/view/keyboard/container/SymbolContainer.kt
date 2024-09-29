package com.yuyan.imemodule.view.keyboard.container

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.SymbolAdapter
import com.yuyan.imemodule.adapter.SymbolTypeAdapter
import com.yuyan.imemodule.application.LauncherModel
import com.yuyan.imemodule.callback.OnRecyclerItemClickListener
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.manager.SymbolsManager
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
    private var mRVSymbolsView: RecyclerView? = null
    private var mRVSymbolsType: RecyclerView? = null
    @SuppressLint("ClickableViewAccessibility")
    private fun initView(context: Context) {
        val mLLSymbolType = LayoutInflater.from(getContext())
            .inflate(R.layout.sdk_view_symbols_emoji_type, this, false) as LinearLayout
        mRVSymbolsType = mLLSymbolType.findViewById(R.id.rv_symbols_emoji_type)
        val layoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        mRVSymbolsType?.setLayoutManager(layoutManager)
        mRVSymbolsView = RecyclerView(context)
        mLLSymbolType.visibility = VISIBLE
        val ivDelete = mLLSymbolType.findViewById<ImageView>(R.id.iv_symbols_emoji_type_delete)
        val isKeyBorder = ThemeManager.prefs.keyBorder.getValue()
        if (isKeyBorder) {
            val mActiveTheme = activeTheme
            val keyRadius = ThemeManager.prefs.keyRadius.getValue()
            val bg = GradientDrawable()
            bg.setColor(mActiveTheme.keyBackgroundColor)
            bg.shape = GradientDrawable.RECTANGLE
            bg.cornerRadius = keyRadius.toFloat() // 设置圆角半径
            ivDelete.background = bg
        }

        ivDelete.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 播放按键声音和震动
                    DevicesUtils.tryPlayKeyDown(SoftKey(KeyEvent.KEYCODE_DEL))
                    DevicesUtils.tryVibrate(this)
                }
                MotionEvent.ACTION_MOVE -> { }
                MotionEvent.ACTION_UP -> {
                    inputView.responseKeyEvent(SoftKey(KeyEvent.KEYCODE_DEL))
                }
            }
            true
        }
        val layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(ALIGN_PARENT_BOTTOM)
        mLLSymbolType.layoutParams = layoutParams
        this.addView(mLLSymbolType)
        val layoutParams2 = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
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
            inputView.resetToIdleState()
            KeyboardManager.instance.switchKeyboard(mInputModeSwitcher!!.skbLayout)
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

    private var lastPosition = 0 // 记录上次选中的位置，再次点击关闭符号界面

    init {
        initView(context)
    }

    private fun onTypeItemClickOperate(position: Int) {
        if (position < 0) return
        if (lastPosition != position) {
            val symbols = SymbolsManager.instance!!.getmSymbols(position)
            inputView.showSymbols(symbols)
            updateSymbols({ parent: RecyclerView.Adapter<*>?, _: View?, pos: Int -> onItemClickOperate(parent, pos) }, position)
        } else {
            inputView.resetToIdleState()
            KeyboardManager.instance.switchKeyboard(mInputModeSwitcher!!.skbLayout)
        }
    }

    //显示表情和符号
    private fun updateSymbols(listener: OnRecyclerItemClickListener, viewType: Int) {
        lastPosition = viewType
        val faceData = SymbolsManager.instance!!.getmSymbolsData(viewType)
        val mSymbolAdapter = SymbolAdapter(context, faceData, viewType)
        val manager = FlexboxLayoutManager(context)
        manager.justifyContent = JustifyContent.SPACE_AROUND // 设置主轴对齐方式为居左
        mRVSymbolsView!!.setLayoutManager(manager)
        mSymbolAdapter.setOnItemClickLitener(listener)
        mRVSymbolsView!!.setAdapter(mSymbolAdapter)
    }

    /**
     * 切换显示界面
     */
    fun setSymbolsView(showType: Int) {
        lastPosition = showType
        updateSymbols({ parent: RecyclerView.Adapter<*>?, _: View?, position: Int ->
            onItemClickOperate(parent, position)
        }, showType)
        val data = resources.getStringArray(R.array.symbolType)
        val adapter = SymbolTypeAdapter(context, data, showType)
        adapter.setOnItemClickLitener { _: RecyclerView.Adapter<*>?, _: View?, position: Int ->
            // 播放按键声音和震动
            DevicesUtils.tryPlayKeyDown()
            DevicesUtils.tryVibrate(this)
            onTypeItemClickOperate(position)
        }
        mRVSymbolsType!!.setAdapter(adapter)
    }
}
