package com.yuyan.imemodule.application

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.data.theme.ThemeManager.onSystemDarkModeChange
import com.yuyan.imemodule.data.theme.ThemeManager.prefs
import com.yuyan.imemodule.manager.SymbolsManager.Companion.initInstance
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.service.ClipBoardService
import com.yuyan.imemodule.ui.utils.isDarkMode
import com.yuyan.imemodule.utils.AssetUtils.copyFileOrDir
import com.yuyan.imemodule.utils.thread.ThreadPoolUtils

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
        LauncherModel.initSingleton(context) //初始化启动相关设置
    }

    /**
     * 可以在子线程初始化的操作
     */
    private fun onInitDataChildThread() {
        ThreadPoolUtils.executeSingleton {
            imeInit()
            initInstance(context) //初始化符合和表情管理类
            //初始化键盘主题
            val isFollowSystemDayNight = prefs.followSystemDayNightTheme.getValue()
            if (isFollowSystemDayNight) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            if(AppPrefs.getInstance().clipboard.clipboardListening.getValue()) {
                val intent = Intent(context, ClipBoardService::class.java)
                startService(intent)
            }

        }
    }

    private fun imeInit() {
        // 复制词库文件
        val dataDictVersion = AppPrefs.getInstance().internal.dataDictVersion.getValue()
        if (dataDictVersion < CustomConstant.CURRENT_RIME_DICT_DATA_VERSIOM) {
            //rime词库
            copyFileOrDir(context, "rime", "", CustomConstant.RIME_DICT_PATH, true)
            //手写词典,暂时与rime词库同时判断
            copyFileOrDir(context, "hdw", "", CustomConstant.HDW_DICT_PATH, false)
            AppPrefs.getInstance().internal.dataDictVersion.setValue(CustomConstant.CURRENT_RIME_DICT_DATA_VERSIOM)
        }
        val config = BundledEmojiCompatConfig(context)
        config.setReplaceAll(true)
        EmojiCompat.init(config)
    }

    companion object {
        private var mInstance: Application? = null
        @JvmStatic
        val context: Context
            /**
             * 获取ApplicationContext
             * @return ApplicationContext
             */
            get() = mInstance!!.applicationContext
    }
}
