package com.yuyan.imemodule.manager

import android.graphics.drawable.Drawable
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.ImeSdkApplication
import java.util.Objects

/**
 * 键盘图标资源
 * Soft keyboard template used by soft keyboards to share common resources. In
 * this way, memory cost is reduced.
 */
class KeyIconManager {
    /**
     * Default key icon list. It is only for keys which do not have popup icons.
     */
    private val mKeyIconRecords = HashMap<Int, Drawable?>()

    init {
        mKeyIconRecords[Objects.hash(InputModeSwitcherManager.USERDEF_KEYCODE_SHIFT_1, 0)] =
            ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.shift_off_icon)
        mKeyIconRecords[Objects.hash(InputModeSwitcherManager.USERDEF_KEYCODE_SHIFT_1, 1)] =
            ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.shift_on_icon)
        mKeyIconRecords[Objects.hash(InputModeSwitcherManager.USERDEF_KEYCODE_SHIFT_1, 2)] =
            ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.shift_lock_icon)
        mKeyIconRecords[Objects.hash(InputModeSwitcherManager.USERDEF_KEYCODE_LANG_2, 0)] =
            ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_skb_key_input_mode_cn_icon)
        mKeyIconRecords[Objects.hash(InputModeSwitcherManager.USERDEF_KEYCODE_LANG_2, 1)] =
            ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_skb_key_input_mode_en_icon)
        mKeyIconRecords[Objects.hash(InputModeSwitcherManager.USERDEF_KEYCODE_EMOJI_6, 0)] =
            ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_vector_menu_skb_emoji)
        mKeyIconRecords[Objects.hash(KeyEvent.KEYCODE_SPACE, 0)] =
            ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_skb_key_space_icon)
        mKeyIconRecords[Objects.hash(KeyEvent.KEYCODE_ENTER, 0)] =
            ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_skb_key_enter_icon)
        mKeyIconRecords[Objects.hash(KeyEvent.KEYCODE_DEL, 0)] =
            ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_skb_key_delete_icon)
        mKeyIconRecords[Objects.hash(InputModeSwitcherManager.USERDEF_KEYCODE_CURSOR_10, 0)] =
            ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_skb_key_cursor_icon)
        mKeyIconRecords[Objects.hash(InputModeSwitcherManager.USERDEF_KEYCODE_LOCK_SYMBOL_11, 0)] =
            ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_vector_menu_skb_symbol)
    }

    fun getDefaultKeyIcon(keyCode: Int, statue: Int): Drawable? {
        return mKeyIconRecords[Objects.hash(keyCode, statue)]
    }

    companion object {
        private var mInstance: KeyIconManager? = null
        @JvmStatic
		val instance: KeyIconManager
            get() {
                if (mInstance == null) {
                    mInstance = KeyIconManager()
                }
                return mInstance!!
            }
    }
}
