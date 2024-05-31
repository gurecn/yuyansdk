package com.yuyan.imemodule.view.keyboard.container

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.KeyEvent
import android.view.LayoutInflater
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
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.data.theme.ThemeManager.prefs
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.manager.SymbolsManager
import com.yuyan.imemodule.utils.DevicesUtils.tryPlayKeyDown
import com.yuyan.imemodule.utils.DevicesUtils.tryVibrate
import com.yuyan.imemodule.view.keyboard.KeyboardManager

class SymbolContainer(context: Context) : BaseContainer(context) {
    private var mRVSymbolsView: RecyclerView? = null
    private var mRVSymbolsType: RecyclerView? = null
    private fun initView(context: Context) {
        val mLLSymbolType = LayoutInflater.from(getContext())
            .inflate(R.layout.sdk_view_symbols_emoji_type, this, false) as LinearLayout
        mRVSymbolsType = mLLSymbolType.findViewById(R.id.rv_symbols_emoji_type)
        val layoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        mRVSymbolsType?.setLayoutManager(layoutManager)
        mRVSymbolsView = RecyclerView(context)
        mLLSymbolType.visibility = VISIBLE
        val ivDelete = mLLSymbolType.findViewById<ImageView>(R.id.iv_symbols_emoji_type_delete)
        val isKeyBorder = prefs.keyBorder.getValue()
        if (isKeyBorder) {
            val mActiveTheme = activeTheme
            val keyRadius = prefs.keyRadius.getValue()
            val bg = GradientDrawable()
            bg.setColor(mActiveTheme.keyBackgroundColor)
            bg.setShape(GradientDrawable.RECTANGLE)
            bg.setCornerRadius(keyRadius.toFloat()) // 设置圆角半径
            ivDelete.background = bg
        }
        ivDelete.setOnClickListener {
            inputView!!.sendKeyEvent(KeyEvent.KEYCODE_DEL)
            val softKey = SoftKey()
            softKey.keyCode = KeyEvent.KEYCODE_DEL
            tryPlayKeyDown(softKey)
            tryVibrate(this)
        }
        val layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(ALIGN_PARENT_BOTTOM)
        mLLSymbolType.setLayoutParams(layoutParams)
        this.addView(mLLSymbolType)
        val layoutParams2 = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
        layoutParams2.addRule(ABOVE, mLLSymbolType.id)
        mRVSymbolsView!!.setLayoutParams(layoutParams2)
        this.addView(mRVSymbolsView)
    }

    private fun onItemClickOperate(parent: RecyclerView.Adapter<*>?, position: Int) {
        val adapter = parent as SymbolAdapter?
        val s = adapter!!.getItem(position)
        tryPlayKeyDown()
        tryVibrate(this)
        val result = s.replace("[ \\r]".toRegex(), "")
        val viewType = adapter.viewType
        if (viewType < CustomConstant.EMOJI_TYPR_FACE_DATA) {  // 非表情键盘
            LauncherModel.instance!!.usedCharacterDao!!.insertUsedCharacter(
                result,
                System.currentTimeMillis()
            )
            inputView!!.resetToIdleState()
            KeyboardManager.instance!!.switchKeyboard(mInputModeSwitcher!!.skbLayout)
        } else if (viewType == CustomConstant.EMOJI_TYPR_FACE_DATA) {  // Emoji表情
            LauncherModel.instance!!.usedEmojiDao!!.insertUsedEmoji(
                result,
                System.currentTimeMillis()
            )
        } else if (viewType == CustomConstant.EMOJI_TYPR_SMILE_TEXT) { // 颜文字
            LauncherModel.instance!!.usedEmoticonsDao!!.insertUsedEmoticons(
                result,
                System.currentTimeMillis()
            )
        }
        val softKey = SoftKey(result)
        inputView!!.responseKeyEvent(softKey)
    }

    var lastPosition = 0 // 记录上次选中的位置，再次点击关闭符号界面

    init {
        initView(context)
    }

    private fun onTypeItemClickOperate(position: Int) {
        if (position < 0) return
        if (lastPosition != position) {
            val symbols = SymbolsManager.instance!!.getmSymbols(position)
            inputView!!.showSymbols(symbols)
            updateSymbols({ parent: RecyclerView.Adapter<*>?, _: View?, position: Int -> onItemClickOperate(parent, position) }, position)
        } else {
            inputView!!.resetToIdleState()
            KeyboardManager.instance!!.switchKeyboard(mInputModeSwitcher!!.skbLayout)
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
        updateSymbols({ parent: RecyclerView.Adapter<*>?, view: View?, position: Int ->
            onItemClickOperate(
                parent,
                position
            )
        }, showType)
        val data = resources.getStringArray(R.array.symbolType)
        val adapter = SymbolTypeAdapter(context, data, showType)
        adapter.setOnItemClickLitener { parent: RecyclerView.Adapter<*>?, view: View?, position: Int ->
            onTypeItemClickOperate(
                position
            )
        }
        mRVSymbolsType!!.setAdapter(adapter)
    }
}
