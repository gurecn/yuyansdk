package com.yuyan.imemodule.keyboard

import android.view.KeyEvent
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.behavior.SkbStyleMode

object KeyboardData {
    val layoutQwertyCn: Map<SkbStyleMode, ArrayList<Array<Int>>> = linkedMapOf(
        SkbStyleMode.Google to arrayListOf(
            arrayOf(45, 51, 33, 46, 48, 53, 49, 37, 43, 44),
            arrayOf(29, 47, 32, 34, 35, 36, 38, 39, 40),
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_SHIFT_1, 54, 52, 31, 50, 30, 42, 41, KeyEvent.KEYCODE_DEL)),
        SkbStyleMode.Samsung to arrayListOf(
            arrayOf(45, 51, 33, 46, 48, 53, 49, 37, 43, 44),
            arrayOf(29, 47, 32, 34, 35, 36, 38, 39, 40),
            arrayOf(75, 54, 52, 31, 50, 30, 42, 41, KeyEvent.KEYCODE_DEL)),
        SkbStyleMode.Yuyan to arrayListOf(
            arrayOf(45, 51, 33, 46, 48, 53, 49, 37, 43, 44),
            arrayOf(29, 47, 32, 34, 35, 36, 38, 39, 40),
            arrayOf(75, 54, 52, 31, 50, 30, 42, 41, KeyEvent.KEYCODE_DEL)),
    )

    val layoutT9Cn: Map<SkbStyleMode, ArrayList<Array<Int>>> = linkedMapOf(
        SkbStyleMode.Google to  arrayListOf(
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12, KeyEvent.KEYCODE_APOSTROPHE, KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_D, KeyEvent.KEYCODE_DEL),
            arrayOf(KeyEvent.KEYCODE_G, KeyEvent.KEYCODE_J, KeyEvent.KEYCODE_M, InputModeSwitcherManager.USER_DEF_KEYCODE_CURSOR_DIRECTION_9),
            arrayOf(KeyEvent.KEYCODE_P, KeyEvent.KEYCODE_T, KeyEvent.KEYCODE_W, InputModeSwitcherManager.USER_DEF_KEYCODE_SHIFT_1)),
        SkbStyleMode.Samsung to arrayListOf(
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12, KeyEvent.KEYCODE_APOSTROPHE, KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_D, KeyEvent.KEYCODE_DEL),
            arrayOf(KeyEvent.KEYCODE_G, KeyEvent.KEYCODE_J, KeyEvent.KEYCODE_M, KeyEvent.KEYCODE_CLEAR),
            arrayOf(KeyEvent.KEYCODE_P, KeyEvent.KEYCODE_T, KeyEvent.KEYCODE_W, KeyEvent.KEYCODE_0)),
        SkbStyleMode.Yuyan to arrayListOf(
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12, KeyEvent.KEYCODE_APOSTROPHE, KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_D, KeyEvent.KEYCODE_DEL),
            arrayOf(KeyEvent.KEYCODE_G, KeyEvent.KEYCODE_J, KeyEvent.KEYCODE_M, KeyEvent.KEYCODE_CLEAR),
            arrayOf(KeyEvent.KEYCODE_P, KeyEvent.KEYCODE_T, KeyEvent.KEYCODE_W, KeyEvent.KEYCODE_AT)),
    )

    val layoutHandwritingCn: Map<SkbStyleMode, ArrayList<Array<Int>>> = linkedMapOf(
        SkbStyleMode.Google to arrayListOf(
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12),
            arrayOf(KeyEvent.KEYCODE_DEL)),
        SkbStyleMode.Samsung to arrayListOf(
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12),
            arrayOf(KeyEvent.KEYCODE_DEL)),
        SkbStyleMode.Yuyan to arrayListOf(
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12),
            arrayOf(KeyEvent.KEYCODE_DEL)),
    )

    val layoutStrokeCn: Map<SkbStyleMode, ArrayList<Array<Int>>> = linkedMapOf(
        SkbStyleMode.Google to arrayListOf(
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12, KeyEvent.KEYCODE_H, KeyEvent.KEYCODE_S, KeyEvent.KEYCODE_P, KeyEvent.KEYCODE_DEL),
            arrayOf(KeyEvent.KEYCODE_N, KeyEvent.KEYCODE_Z, KeyEvent.KEYCODE_STAR, KeyEvent.KEYCODE_CLEAR),
            arrayOf(KeyEvent.KEYCODE_MINUS, KeyEvent.KEYCODE_APOSTROPHE, KeyEvent.KEYCODE_EQUALS, KeyEvent.KEYCODE_AT)),
        SkbStyleMode.Samsung to arrayListOf(
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12, KeyEvent.KEYCODE_H, KeyEvent.KEYCODE_S, KeyEvent.KEYCODE_P, KeyEvent.KEYCODE_DEL),
            arrayOf(KeyEvent.KEYCODE_N, KeyEvent.KEYCODE_Z, KeyEvent.KEYCODE_STAR, KeyEvent.KEYCODE_CLEAR),
            arrayOf(KeyEvent.KEYCODE_MINUS, KeyEvent.KEYCODE_APOSTROPHE, KeyEvent.KEYCODE_EQUALS, KeyEvent.KEYCODE_AT)),
        SkbStyleMode.Yuyan to arrayListOf(
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12, KeyEvent.KEYCODE_H, KeyEvent.KEYCODE_S, KeyEvent.KEYCODE_P, KeyEvent.KEYCODE_DEL),
            arrayOf(KeyEvent.KEYCODE_N, KeyEvent.KEYCODE_Z, KeyEvent.KEYCODE_STAR, KeyEvent.KEYCODE_CLEAR),
            arrayOf(KeyEvent.KEYCODE_MINUS, KeyEvent.KEYCODE_APOSTROPHE, KeyEvent.KEYCODE_EQUALS, KeyEvent.KEYCODE_AT)),
    )

    val layoutLX17Cn: Map<SkbStyleMode, ArrayList<Array<Int>>> = linkedMapOf(
        SkbStyleMode.Google to arrayListOf(
            arrayOf(36, 47, 54, 30, 52, 41),
            arrayOf(40, 32, 53, 51, 38, 42),
            arrayOf(31, 45, 35, 34, 48, 67)),
        SkbStyleMode.Samsung to arrayListOf(
            arrayOf(36, 47, 54, 30, 52, 41),
            arrayOf(40, 32, 53, 51, 38, 42),
            arrayOf(31, 45, 35, 34, 48, 67)),
        SkbStyleMode.Yuyan to arrayListOf(
            arrayOf(36, 47, 54, 30, 52, 41),
            arrayOf(40, 32, 53, 51, 38, 42),
            arrayOf(31, 45, 35, 34, 48, 67)),
    )

    val layoutLX17CnWithLeftPrefix: Map<SkbStyleMode, ArrayList<Array<Int>>> = linkedMapOf(
        SkbStyleMode.Google to arrayListOf(
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12, 36, 47, 54, 30, 52, 41),
            arrayOf(40, 32, 53, 51, 38, 42),
            arrayOf(31, 45, 35, 34, 48, 67)),
        SkbStyleMode.Samsung to arrayListOf(
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12, 36, 47, 54, 30, 52, 41),
            arrayOf(40, 32, 53, 51, 38, 42),
            arrayOf(31, 45, 35, 34, 48, 67)),
        SkbStyleMode.Yuyan to arrayListOf(
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12, 36, 47, 54, 30, 52, 41),
            arrayOf(40, 32, 53, 51, 38, 42),
            arrayOf(31, 45, 35, 34, 48, 67)),
    )

    val layoutQwertyEn: Map<SkbStyleMode, ArrayList<Array<Int>>> = linkedMapOf(
        SkbStyleMode.Google to arrayListOf(
            arrayOf(45, 51, 33, 46, 48, 53, 49, 37, 43, 44),
            arrayOf(29, 47, 32, 34, 35, 36, 38, 39, 40),
            arrayOf(54, 52, 31, 50, 30, 42, 41, KeyEvent.KEYCODE_DEL)),
        SkbStyleMode.Samsung to arrayListOf(
            arrayOf(45, 51, 33, 46, 48, 53, 49, 37, 43, 44),
            arrayOf(29, 47, 32, 34, 35, 36, 38, 39, 40),
            arrayOf(54, 52, 31, 50, 30, 42, 41, KeyEvent.KEYCODE_DEL)),
        SkbStyleMode.Yuyan to arrayListOf(
            arrayOf(45, 51, 33, 46, 48, 53, 49, 37, 43, 44),
            arrayOf(29, 47, 32, 34, 35, 36, 38, 39, 40),
            arrayOf(54, 52, 31, 50, 30, 42, 41, KeyEvent.KEYCODE_DEL)),
    )

    val layoutT9Number: Map<SkbStyleMode, ArrayList<Array<Int>>> = linkedMapOf(
        SkbStyleMode.Google to arrayListOf(
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12, 8, 9, 10, KeyEvent.KEYCODE_DEL),
            arrayOf(11, 12, 13, InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14),
            arrayOf(14, 15, 16, InputModeSwitcherManager.USER_DEF_KEYCODE_SHIFT_1)),
        SkbStyleMode.Samsung to arrayListOf(
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12, 8, 9, 10, KeyEvent.KEYCODE_DEL),
            arrayOf(11, 12, 13, InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14),
            arrayOf(14, 15, 16, KeyEvent.KEYCODE_AT)),
        SkbStyleMode.Yuyan to arrayListOf(
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_SYMBOL_12, 8, 9, 10, KeyEvent.KEYCODE_DEL),
            arrayOf(11, 12, 13, InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14),
            arrayOf(14, 15, 16, KeyEvent.KEYCODE_AT)),
    )

    val layoutTextEdit: Map<SkbStyleMode, ArrayList<Array<Int>>> = linkedMapOf(
        SkbStyleMode.Google to arrayListOf(
            arrayOf(KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_RIGHT, InputModeSwitcherManager.USER_DEF_KEYCODE_SELECT_ALL),
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_SELECT_MODE, InputModeSwitcherManager.USER_DEF_KEYCODE_COPY),
            arrayOf(KeyEvent.KEYCODE_DPAD_DOWN, InputModeSwitcherManager.USER_DEF_KEYCODE_PASTE),
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_MOVE_START, InputModeSwitcherManager.USER_DEF_KEYCODE_MOVE_END, KeyEvent.KEYCODE_DEL)),
        SkbStyleMode.Samsung to arrayListOf(
            arrayOf(KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_RIGHT, InputModeSwitcherManager.USER_DEF_KEYCODE_SELECT_ALL),
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_SELECT_MODE, InputModeSwitcherManager.USER_DEF_KEYCODE_COPY),
            arrayOf(KeyEvent.KEYCODE_DPAD_DOWN, InputModeSwitcherManager.USER_DEF_KEYCODE_PASTE),
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_MOVE_START, InputModeSwitcherManager.USER_DEF_KEYCODE_MOVE_END, KeyEvent.KEYCODE_DEL)),
        SkbStyleMode.Yuyan to arrayListOf(
            arrayOf(KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_RIGHT, InputModeSwitcherManager.USER_DEF_KEYCODE_SELECT_ALL),
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_SELECT_MODE, InputModeSwitcherManager.USER_DEF_KEYCODE_COPY),
            arrayOf(KeyEvent.KEYCODE_DPAD_DOWN, InputModeSwitcherManager.USER_DEF_KEYCODE_PASTE),
            arrayOf(InputModeSwitcherManager.USER_DEF_KEYCODE_MOVE_START, InputModeSwitcherManager.USER_DEF_KEYCODE_MOVE_END, KeyEvent.KEYCODE_DEL)),
    )
}