package com.yuyan.imemodule.keyboard

import android.graphics.drawable.Drawable
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import java.util.Objects

val keyIconRecords: Map<Int, Drawable?> = mapOf(
    Objects.hash(InputModeSwitcherManager.USER_DEF_KEYCODE_SHIFT_1, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.shift_off_0_icon),
    Objects.hash(InputModeSwitcherManager.USER_DEF_KEYCODE_SHIFT_1, 1) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.shift_on_1_icon),
    Objects.hash(InputModeSwitcherManager.USER_DEF_KEYCODE_SHIFT_1, 2) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.shift_lock_2_icon),
    Objects.hash(InputModeSwitcherManager.USER_DEF_KEYCODE_SHIFT_1, 3) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.shift_off_3_icon),
    Objects.hash(InputModeSwitcherManager.USER_DEF_KEYCODE_SHIFT_1, 4) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.shift_on_4_icon),
    Objects.hash(InputModeSwitcherManager.USER_DEF_KEYCODE_SHIFT_1, 5) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.shift_lock_5_icon),
    Objects.hash(InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_skb_key_input_mode_cn_icon),
    Objects.hash(InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2, 1) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_skb_key_input_mode_en_icon),
    Objects.hash(InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.ic_menu_emoji),
    Objects.hash(KeyEvent.KEYCODE_SPACE, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_skb_key_space_icon),
    Objects.hash(KeyEvent.KEYCODE_ENTER, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_skb_key_enter_icon),
    Objects.hash(KeyEvent.KEYCODE_DEL, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.sdk_skb_key_delete_icon),
    Objects.hash(InputModeSwitcherManager.USER_DEF_KEYCODE_TEXTEDIT_7, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.ic_menu_cursor_icon),
    Objects.hash(InputModeSwitcherManager.USER_DEF_KEYCODE_MOVE_LEFT, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.skb_key_cursor_left_icon),
    Objects.hash(InputModeSwitcherManager.USER_DEF_KEYCODE_MOVE_RIGHT, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.skb_key_cursor_right_icon),
    Objects.hash(InputModeSwitcherManager.USER_DEF_KEYCODE_MOVE_UP, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.skb_key_cursor_up_icon),
    Objects.hash(InputModeSwitcherManager.USER_DEF_KEYCODE_MOVE_DOWN, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.skb_key_cursor_down_icon),
    Objects.hash(InputModeSwitcherManager.USER_DEF_KEYCODE_MOVE_START, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.skb_key_cursor_start_icon),
    Objects.hash(InputModeSwitcherManager.USER_DEF_KEYCODE_MOVE_END, 0) to ContextCompat.getDrawable(ImeSdkApplication.context, R.drawable.skb_key_cursor_end_icon),
)