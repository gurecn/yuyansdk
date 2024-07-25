package com.yuyan.imemodule.view.keyboard

import android.graphics.drawable.Drawable
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import java.util.Objects

val keyIconRecords: Map<Int, Drawable?> = mapOf(
    Objects.hash(InputModeSwitcherManager.USERDEF_KEYCODE_SHIFT_1, 1) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.shift_off_icon),
    Objects.hash(InputModeSwitcherManager.USERDEF_KEYCODE_SHIFT_1, 2) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.shift_on_icon),
    Objects.hash(InputModeSwitcherManager.USERDEF_KEYCODE_SHIFT_1, 3) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.shift_lock_icon),
    Objects.hash(InputModeSwitcherManager.USERDEF_KEYCODE_LANG_2, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_skb_key_input_mode_cn_icon),
    Objects.hash(InputModeSwitcherManager.USERDEF_KEYCODE_LANG_2, 1) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_skb_key_input_mode_en_icon),
    Objects.hash(InputModeSwitcherManager.USERDEF_KEYCODE_EMOJI_6, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_vector_menu_skb_emoji),
    Objects.hash(KeyEvent.KEYCODE_SPACE, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_skb_key_space_icon),
    Objects.hash(KeyEvent.KEYCODE_ENTER, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_skb_key_enter_icon),
    Objects.hash(KeyEvent.KEYCODE_DEL, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_skb_key_delete_icon),
    Objects.hash(InputModeSwitcherManager.USERDEF_KEYCODE_CURSOR_10, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_skb_key_cursor_icon),
    Objects.hash(InputModeSwitcherManager.USERDEF_KEYCODE_LOCK_SYMBOL_11, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_vector_menu_skb_symbol),
)