package com.yuyan.imemodule.data.emojicon

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.view.inputmethod.EditorInfo
import androidx.emoji2.text.DefaultEmojiCompatConfig
import androidx.emoji2.text.EmojiCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object YuyanEmojiCompat {
    var mEditorInfo:EditorInfo? = null
    private var metadataVersion: Int = 0
    private var replaceAll: Boolean = false
    var isWeChatInput: Boolean = false
    var isQQChatInput: Boolean = false
    private lateinit var systemFontPaint: Paint
    private lateinit var instanceNoReplace: InstanceHandler
    private lateinit var instanceReplaceAll: InstanceHandler

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /**
     * Initialize this helper and its EmojiCompat instances with given [context]. Immediately begins loading the emoji
     * metadata in a background thread. After this method has been called, it is safe to call [getAsFlow].
     */
    fun init(context: Context) {
        systemFontPaint = Paint().apply {
            typeface = Typeface.DEFAULT
        }
        instanceNoReplace = InstanceHandler(context, replaceAll = false)
        instanceReplaceAll = InstanceHandler(context, replaceAll = true)

        scope.launch {
            instanceNoReplace.load()
        }
        scope.launch {
            instanceReplaceAll.load()
        }
    }

    fun setEditorInfo(editorInfo: EditorInfo?) {
        this.mEditorInfo = editorInfo
        metadataVersion = editorInfo?.extras?.getInt(EmojiCompat.EDITOR_INFO_METAVERSION_KEY, 0) ?: 0
        replaceAll = editorInfo?.extras?.getBoolean(EmojiCompat.EDITOR_INFO_REPLACE_ALL_KEY, false) ?: false
        isWeChatInput = isWechatInput(editorInfo)
        isQQChatInput = isQQChatInput(editorInfo)

    }

    private fun isWechatInput(editorInfo: EditorInfo?): Boolean {
        if(editorInfo == null || editorInfo.packageName != "com.tencent.mm") return  false
        return editorInfo.extras?.getBoolean("IS_CHAT_EDITOR") == true
    }

    private fun isQQChatInput(editorInfo: EditorInfo?): Boolean {
        if(editorInfo == null || editorInfo.packageName != "com.tencent.mobileqq") return  false
        val bundle = editorInfo.extras ?: return false
        return bundle.getInt("SOGOU_EXPRESSION_WEBP") == 1 || bundle.getInt("SOGOU_EXPRESSION") == 1 || bundle.getInt("SUPPORT_SOGOU_EXPRESSION") == 1
    }

    fun getEmojiMatch(emojiCompat:EmojiCompat?, emoji:String):Boolean {
        return emojiCompat?.getEmojiMatch(emoji, metadataVersion) == EmojiCompat.EMOJI_SUPPORTED || systemFontPaint.hasGlyph(emoji)
    }

    /**
     * Gets the current EmojiCompat instance based on [replaceAll] and sets it as the default instance if
     * [setAsDefaultInstance] is true. Calling this method before [init] will cause an exception to be thrown.
     *
     * @return A state flow providing the latest EmojiCompat instance for given args. The flow may provide null if
     *  EmojiCompat is still loading or if it has failed.
     */
    @SuppressLint("RestrictedApi")
    fun getAsFlow(setAsDefaultInstance: Boolean = true): StateFlow<EmojiCompat?> {
        val instanceFlow = if (replaceAll) {
            instanceReplaceAll.publishedInstanceFlow
        } else {
            instanceNoReplace.publishedInstanceFlow
        }
        val instance = instanceFlow.value
        if (setAsDefaultInstance && instance != null) {
            // This API is not really supposed to be used by third-party apps, but it is really handy and does
            // exactly what we need, so we suppress the restriction here
            EmojiCompat.reset(instance)
        }
        return instanceFlow
    }

    private class InstanceHandler(context: Context, replaceAll: Boolean = false) {
        private val initCallback: EmojiCompat.InitCallback = object : EmojiCompat.InitCallback() {
            override fun onInitialized() {
                super.onInitialized()
                publishedInstanceFlow.value = instance
            }
        }

        private val config: EmojiCompat.Config? = DefaultEmojiCompatConfig.create(context)?.apply {
            setReplaceAll(replaceAll)
            setMetadataLoadStrategy(EmojiCompat.LOAD_STRATEGY_MANUAL)
            registerInitCallback(initCallback)
        }

        // Despite its name, `EmojiCompat.reset()` actually creates a new instance, exactly what we need
        private val instance: EmojiCompat? = if (config != null) EmojiCompat.reset(config) else null
        val publishedInstanceFlow = MutableStateFlow<EmojiCompat?>(null)

        /**
         * Manually loads the EmojiCompat instance. Call this method on a background thread to avoid blocking main.
         *
         * @see EmojiCompat.load
         */
        fun load() {
            instance?.load()
        }
    }
}
