package com.yuyan.imemodule.application

import android.annotation.SuppressLint
import android.content.Context
import androidx.preference.PreferenceManager
import com.yuyan.imemodule.data.theme.ThemeManager.init
import com.yuyan.imemodule.database.dao.ClipboardDao
import com.yuyan.imemodule.database.dao.UsedCharacterDao
import com.yuyan.imemodule.database.dao.UsedEmojiDao
import com.yuyan.imemodule.database.dao.UsedEmoticonsDao
import com.yuyan.imemodule.database.table.ClipboardTable
import com.yuyan.imemodule.database.table.UsedCharacterTable
import com.yuyan.imemodule.database.table.UsedEmojiTable
import com.yuyan.imemodule.database.table.UsedEmoticonsTable
import com.yuyan.imemodule.manager.LocalRepository
import com.yuyan.imemodule.prefs.AppPrefs.Companion.init

/**
 * 应用启动做一些启动相关的初始化操作
 * User:Gaolei  gurecn@gmail.com
 * Date:2016/10/10
 * I'm glad to share my knowledge with you all.
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
     * 获取常用颜文字
     */
    var usedEmoticonsDao: UsedEmoticonsDao? = null
        private set
    var mClipboardDao: ClipboardDao? = null
    private fun initData(context: Context) {
        val unLoginTables = ArrayList<String>()
        unLoginTables.add(UsedCharacterTable.CREATE_TABLE)
        unLoginTables.add(UsedEmojiTable.CREATE_TABLE)
        unLoginTables.add(UsedEmoticonsTable.CREATE_TABLE)
        unLoginTables.add(ClipboardTable.CREATE_TABLE)
        val mDataProvider = LocalRepository.instance.dataProvider
        mDataProvider!!.createTable(unLoginTables)
        usedCharacterDao = UsedCharacterDao(mDataProvider)
        usedEmojiDao = UsedEmojiDao(mDataProvider)
        usedEmoticonsDao = UsedEmoticonsDao(mDataProvider)
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
        val instance: LauncherModel?
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
