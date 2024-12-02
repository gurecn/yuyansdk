package com.yuyan.imemodule.application

import android.annotation.SuppressLint
import android.content.Context
import androidx.preference.PreferenceManager
import com.yuyan.imemodule.data.flower.FlowerTypefaceMode
import com.yuyan.imemodule.data.theme.ThemeManager.init
import com.yuyan.imemodule.database.dao.ClipboardDao
import com.yuyan.imemodule.database.dao.UsedCharacterDao
import com.yuyan.imemodule.database.dao.UsedEmojiDao
import com.yuyan.imemodule.database.table.ClipboardTable
import com.yuyan.imemodule.database.table.UsedCharacterTable
import com.yuyan.imemodule.database.table.UsedEmojiTable
import com.yuyan.imemodule.manager.LocalRepository
import com.yuyan.imemodule.prefs.AppPrefs.Companion.init

/**
 * 应用启动做一些启动相关的初始化操作
 */
class LauncherModel private constructor() {
    /**
     * 获取常用字符
     */
    var usedCharacterDao: UsedCharacterDao? = null
        private set

    /**
     * 获取常用表情
     */
    var usedEmojiDao: UsedEmojiDao? = null
        private set

    /**
     * 获取粘贴板
     */
    var mClipboardDao: ClipboardDao? = null

    // 花漾字状态
    var flowerTypeface = FlowerTypefaceMode.Disabled

    private fun initData(context: Context) {
        val tables = listOf(UsedCharacterTable.CREATE_TABLE, UsedEmojiTable.CREATE_TABLE, ClipboardTable.CREATE_TABLE)
        val mDataProvider = LocalRepository.instance.dataProvider
        mDataProvider.createTable(tables)
        usedCharacterDao = UsedCharacterDao(mDataProvider)
        usedEmojiDao = UsedEmojiDao(mDataProvider)
        mClipboardDao = ClipboardDao(mDataProvider)
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        init(preferences)
        init(context.resources.configuration)
    }

    companion object {
        /**
         * 获取实例<br></br>
         */
        @SuppressLint("StaticFieldLeak")
        private var mInstance: LauncherModel? = null

        @JvmStatic
        val instance: LauncherModel
            get() {
                return mInstance!!
            }

        /**
         * 初始化单例,在程序启动时调用<br></br>
         */
        @JvmStatic
        fun initSingleton(context: Context) {
            if (mInstance == null) {
                mInstance = LauncherModel()
                mInstance!!.initData(context)
            }
        }
    }
}
