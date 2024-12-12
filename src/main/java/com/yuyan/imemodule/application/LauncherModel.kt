package com.yuyan.imemodule.application

import android.annotation.SuppressLint
import android.content.Context
import androidx.preference.PreferenceManager
import com.yuyan.imemodule.data.flower.FlowerTypefaceMode
import com.yuyan.imemodule.data.theme.ThemeManager.init
import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.prefs.AppPrefs.Companion.init

/**
 * 应用启动做一些启动相关的初始化操作
 */
class LauncherModel private constructor() {

    // 花漾字状态
    var flowerTypeface = FlowerTypefaceMode.Disabled

    private fun initData(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        init(preferences)
        init(context.resources.configuration)
        DataBaseKT.instance.sideSymbolDao().getAllSideSymbolPinyin()  //操作一次查询，提前创建数据库，避免使用时才创建数据库
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
