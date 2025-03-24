package com.yuyan.imemodule.application

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.yuyan.imemodule.data.emojicon.YuyanEmojiCompat
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.data.theme.ThemeManager.onSystemDarkModeChange
import com.yuyan.imemodule.data.theme.ThemeManager.prefs
import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.service.ClipboardHelper
import com.yuyan.imemodule.utils.isDarkMode
import com.yuyan.imemodule.utils.AssetUtils.copyFileOrDir
import com.yuyan.imemodule.utils.thread.ThreadPoolUtils
import com.yuyan.inputmethod.core.Kernel

open class ImeSdkApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        mInstance = this
        initData()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        onSystemDarkModeChange(newConfig.isDarkMode())
    }

    private fun initData() {
        currentInit()
        onInitDataChildThread()
    }

    private fun currentInit() {
        AppPrefs.init(PreferenceManager.getDefaultSharedPreferences(context))
        ThemeManager.init(context.resources.configuration)
        DataBaseKT.instance.sideSymbolDao().getAllSideSymbolPinyin()  //操作一次查询，提前创建数据库，避免使用时才创建数据库
        ClipboardHelper.init()
    }

    /**
     * 可以在子线程初始化的操作
     */
    private fun onInitDataChildThread() {
        ThreadPoolUtils.executeSingleton {
            imeInit()
            //初始化键盘主题
            val isFollowSystemDayNight = prefs.followSystemDayNightTheme.getValue()
            if (isFollowSystemDayNight) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun imeInit() {
        // 复制词库文件
        val dataDictVersion = AppPrefs.getInstance().internal.dataDictVersion.getValue()
        if (dataDictVersion < CustomConstant.CURRENT_RIME_DICT_DATA_VERSIOM) {
            //rime词库
            copyFileOrDir(context, "rime", "", CustomConstant.RIME_DICT_PATH, true)
            AppPrefs.getInstance().internal.dataDictVersion.setValue(CustomConstant.CURRENT_RIME_DICT_DATA_VERSIOM)
        }
        Kernel.resetIme()  // 解决词库复制慢，导致先调用初始化问题
        YuyanEmojiCompat.init(context)
    }

    companion object {
        private var mInstance: Application? = null
        @JvmStatic
        val context: Context
            get() = mInstance!!.applicationContext
    }
}
