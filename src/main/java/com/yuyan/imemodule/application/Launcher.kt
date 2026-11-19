package com.yuyan.imemodule.application

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.yuyan.imemodule.handwriting.HandwritingRecognizer
import com.yuyan.imemodule.data.emojicon.YuyanEmojiCompat
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.data.theme.ThemeManager.prefs
import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.service.ClipboardHelper
import com.yuyan.imemodule.service.InputDispatcher
import com.yuyan.imemodule.utils.AssetUtils.copyFileOrDir
import com.yuyan.imemodule.utils.thread.ThreadPoolUtils
import com.yuyan.inputmethod.PhraseStore

class Launcher {
    lateinit var context: Context
        private set

    fun initData(context: Context) {
        this.context = context
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
            // 复制词库文件
            val dataDictVersion = AppPrefs.getInstance().internal.dataDictVersion.getValue()
            if (dataDictVersion < CustomConstant.CURRENT_RIME_DICT_DATA_VERSIOM) {
                //rime词库
                copyFileOrDir(context, "rime", "", CustomConstant.RIME_DICT_PATH, true)
                AppPrefs.getInstance().internal.dataDictVersion.setValue(CustomConstant.CURRENT_RIME_DICT_DATA_VERSIOM)
            }
            val hwModelVersion = AppPrefs.getInstance().internal.hwModelVersion.getValue()
            if (hwModelVersion < CustomConstant.CURRENT_HANDWRITING_MODEL_VERSION) {
                if (HandwritingRecognizer.prepareModel(context)) {
                    AppPrefs.getInstance().internal.hwModelVersion.setValue(CustomConstant.CURRENT_HANDWRITING_MODEL_VERSION)
                }
            }
            InputDispatcher.resetIme()  // 解决词库复制慢，导致先调用初始化问题
            migratePhrasesToEngine()
            YuyanEmojiCompat.init(context)
            //初始化键盘主题
            val isFollowSystemDayNight = prefs.followSystemDayNightTheme.getValue()
            if (isFollowSystemDayNight) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    /**
     * 把旧版本保存在 Room 里的常用语迁移到引擎的用户目录，只执行一次。
     * 引擎未就绪时（列表为空）保留标记，下次启动重试。
     */
    private fun migratePhrasesToEngine() {
        val prefs = context.getSharedPreferences("yuyan_phrase_migration", Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_PHRASE_MIGRATED, false)) return
        val existing = PhraseStore.list().mapTo(mutableSetOf()) { it.content }
        if (existing.isEmpty()) return
        DataBaseKT.instance.phraseDao().getAll()
            .filterNot { it.content in existing }
            .forEach { phrase ->
                PhraseStore.add(listOf(phrase.qwerty), phrase.content, phrase.isKeep == 1)
            }
        prefs.edit().putBoolean(KEY_PHRASE_MIGRATED, true).apply()
    }

    companion object {
        private const val KEY_PHRASE_MIGRATED = "phrases_migrated"

        @SuppressLint("StaticFieldLeak")
        val instance = Launcher()
    }
}
