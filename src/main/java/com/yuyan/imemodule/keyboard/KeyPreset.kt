package com.yuyan.imemodule.keyboard

import android.view.KeyEvent
import com.yuyan.imemodule.manager.InputModeSwitcherManager

object KeyPreset {
    val qwertyKeyPreset: Map<Int, Array<String>> = hashMapOf(
        KeyEvent.KEYCODE_Q to arrayOf("Q", "["),
        KeyEvent.KEYCODE_W to arrayOf("W", "]"),
        KeyEvent.KEYCODE_E to arrayOf("E", "<"),
        KeyEvent.KEYCODE_R to arrayOf("R", ">"),
        KeyEvent.KEYCODE_T to arrayOf("T", "("),
        KeyEvent.KEYCODE_Y to arrayOf("Y", ")"),
        KeyEvent.KEYCODE_U to arrayOf("U", "{"),
        KeyEvent.KEYCODE_I to arrayOf("I", "}"),
        KeyEvent.KEYCODE_O to arrayOf("O", "|"),
        KeyEvent.KEYCODE_P to arrayOf("P", "-"),
        KeyEvent.KEYCODE_A to arrayOf("A", "､"),
        KeyEvent.KEYCODE_S to arrayOf("S", "/"),
        KeyEvent.KEYCODE_D to arrayOf("D", "\\"),
        KeyEvent.KEYCODE_F to arrayOf("F", ";"),
        KeyEvent.KEYCODE_G to arrayOf("G", "_"),
        KeyEvent.KEYCODE_H to arrayOf("H", "*"),
        KeyEvent.KEYCODE_J to arrayOf("J", "~"),
        KeyEvent.KEYCODE_K to arrayOf("K", "'"),
        KeyEvent.KEYCODE_L to arrayOf("L", "\""),
        KeyEvent.KEYCODE_SEMICOLON to arrayOf(";"),
        KeyEvent.KEYCODE_APOSTROPHE to arrayOf("分词"),
        KeyEvent.KEYCODE_Z to arrayOf("Z", "@"),
        KeyEvent.KEYCODE_X to arrayOf("X", "#"),
        KeyEvent.KEYCODE_C to arrayOf("C", ":"),
        KeyEvent.KEYCODE_V to arrayOf("V", "&"),
        KeyEvent.KEYCODE_B to arrayOf("B", "?"),
        KeyEvent.KEYCODE_N to arrayOf("N", "!"),
        KeyEvent.KEYCODE_M to arrayOf("M", "…"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_8 to  arrayOf(","),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13 to arrayOf(",", "."),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14 to arrayOf(".", ","),
        KeyEvent.KEYCODE_SPACE to arrayOf("空格"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 to arrayOf("符号"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5 to arrayOf("123"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4 to arrayOf("表情"),
    )

    val qwertyKeyNumberPreset: Map<Int, Array<String>> = hashMapOf(
        KeyEvent.KEYCODE_Q to arrayOf("Q", "1"),
        KeyEvent.KEYCODE_W to arrayOf("W", "2"),
        KeyEvent.KEYCODE_E to arrayOf("E", "3"),
        KeyEvent.KEYCODE_R to arrayOf("R", "4"),
        KeyEvent.KEYCODE_T to arrayOf("T", "5"),
        KeyEvent.KEYCODE_Y to arrayOf("Y", "6"),
        KeyEvent.KEYCODE_U to arrayOf("U", "7"),
        KeyEvent.KEYCODE_I to arrayOf("I", "8"),
        KeyEvent.KEYCODE_O to arrayOf("O", "9"),
        KeyEvent.KEYCODE_P to arrayOf("P", "0"),
        KeyEvent.KEYCODE_A to arrayOf("A", "-"),
        KeyEvent.KEYCODE_S to arrayOf("S", "/"),
        KeyEvent.KEYCODE_D to arrayOf("D", ":"),
        KeyEvent.KEYCODE_F to arrayOf("F", ";"),
        KeyEvent.KEYCODE_G to arrayOf("G", "("),
        KeyEvent.KEYCODE_H to arrayOf("H", ")"),
        KeyEvent.KEYCODE_J to arrayOf("J", "~"),
        KeyEvent.KEYCODE_K to arrayOf("K", "'"),
        KeyEvent.KEYCODE_L to arrayOf("L", "\""),
        KeyEvent.KEYCODE_SEMICOLON to arrayOf(";"),
        KeyEvent.KEYCODE_APOSTROPHE to arrayOf("分词"),
        KeyEvent.KEYCODE_Z to arrayOf("Z", "@"),
        KeyEvent.KEYCODE_X to arrayOf("X", "_"),
        KeyEvent.KEYCODE_C to arrayOf("C", "#"),
        KeyEvent.KEYCODE_V to arrayOf("V", "&"),
        KeyEvent.KEYCODE_B to arrayOf("B", "?"),
        KeyEvent.KEYCODE_N to arrayOf("N", "!"),
        KeyEvent.KEYCODE_M to arrayOf("M", "…"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_8 to  arrayOf(","),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13 to arrayOf(",", "."),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14 to arrayOf(".", ","),
        KeyEvent.KEYCODE_SPACE to arrayOf("空格"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 to arrayOf("符号"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5 to arrayOf("123"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4 to arrayOf("表情"),
    )

    val qwertyPYKeyPreset: Map<Int, Array<String>> = hashMapOf(
        KeyEvent.KEYCODE_Q to arrayOf("Q", "【"),
        KeyEvent.KEYCODE_W to arrayOf("W", "】"),
        KeyEvent.KEYCODE_E to arrayOf("E", "《"),
        KeyEvent.KEYCODE_R to arrayOf("R", "》"),
        KeyEvent.KEYCODE_T to arrayOf("T", "（"),
        KeyEvent.KEYCODE_Y to arrayOf("Y", "）"),
        KeyEvent.KEYCODE_U to arrayOf("U", "{"),
        KeyEvent.KEYCODE_I to arrayOf("I", "}"),
        KeyEvent.KEYCODE_O to arrayOf("O", "|"),
        KeyEvent.KEYCODE_P to arrayOf("P", "-"),
        KeyEvent.KEYCODE_A to arrayOf("A", "、"),
        KeyEvent.KEYCODE_S to arrayOf("S", "/"),
        KeyEvent.KEYCODE_D to arrayOf("D", "\\"),
        KeyEvent.KEYCODE_F to arrayOf("F", "；"),
        KeyEvent.KEYCODE_G to arrayOf("G", "_"),
        KeyEvent.KEYCODE_H to arrayOf("H", "~"),
        KeyEvent.KEYCODE_J to arrayOf("J", "‘"),
        KeyEvent.KEYCODE_K to arrayOf("K", "“"),
        KeyEvent.KEYCODE_L to arrayOf("L", "”"),
        KeyEvent.KEYCODE_SEMICOLON to arrayOf("ing", "", ""),
        KeyEvent.KEYCODE_APOSTROPHE to arrayOf("分词"),
        KeyEvent.KEYCODE_Z to arrayOf("Z", "@"),
        KeyEvent.KEYCODE_X to arrayOf("X", "#"),
        KeyEvent.KEYCODE_C to arrayOf("C", "："),
        KeyEvent.KEYCODE_V to arrayOf("V", "&"),
        KeyEvent.KEYCODE_B to arrayOf("B", "？"),
        KeyEvent.KEYCODE_N to arrayOf("N", "！"),
        KeyEvent.KEYCODE_M to arrayOf("M", "……"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_8 to  arrayOf("，"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13 to arrayOf("，", "。"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14 to arrayOf("。", "，"),
        KeyEvent.KEYCODE_SPACE to arrayOf("空格"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 to arrayOf("符号"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5 to arrayOf("123"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4 to arrayOf("表情"),
    )

    val qwertyPYKeyNumberPreset: Map<Int, Array<String>> = hashMapOf(
        KeyEvent.KEYCODE_Q to arrayOf("Q", "1"),
        KeyEvent.KEYCODE_W to arrayOf("W", "2"),
        KeyEvent.KEYCODE_E to arrayOf("E", "3"),
        KeyEvent.KEYCODE_R to arrayOf("R", "4"),
        KeyEvent.KEYCODE_T to arrayOf("T", "5"),
        KeyEvent.KEYCODE_Y to arrayOf("Y", "6"),
        KeyEvent.KEYCODE_U to arrayOf("U", "7"),
        KeyEvent.KEYCODE_I to arrayOf("I", "8"),
        KeyEvent.KEYCODE_O to arrayOf("O", "9"),
        KeyEvent.KEYCODE_P to arrayOf("P", "0"),
        KeyEvent.KEYCODE_A to arrayOf("A", "-"),
        KeyEvent.KEYCODE_S to arrayOf("S", "/"),
        KeyEvent.KEYCODE_D to arrayOf("D", "\\"),
        KeyEvent.KEYCODE_F to arrayOf("F", "；"),
        KeyEvent.KEYCODE_G to arrayOf("G", "（"),
        KeyEvent.KEYCODE_H to arrayOf("H", "）"),
        KeyEvent.KEYCODE_J to arrayOf("J", "～"),
        KeyEvent.KEYCODE_K to arrayOf("K", "“"),
        KeyEvent.KEYCODE_L to arrayOf("L", "”"),
        KeyEvent.KEYCODE_SEMICOLON to arrayOf("ing", ":"),
        KeyEvent.KEYCODE_APOSTROPHE to arrayOf("分词"),
        KeyEvent.KEYCODE_Z to arrayOf("Z", "@"),
        KeyEvent.KEYCODE_X to arrayOf("X", "."),
        KeyEvent.KEYCODE_C to arrayOf("C", "#"),
        KeyEvent.KEYCODE_V to arrayOf("V", "、"),
        KeyEvent.KEYCODE_B to arrayOf("B", "？"),
        KeyEvent.KEYCODE_N to arrayOf("N", "！"),
        KeyEvent.KEYCODE_M to arrayOf("M", "……"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_8 to  arrayOf("，"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13 to arrayOf("，", "。"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14 to arrayOf("。", "，"),
        KeyEvent.KEYCODE_SPACE to arrayOf("空格"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 to arrayOf("符号"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5 to arrayOf("123"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4 to arrayOf("表情"),
    )

    val lx17PYKeyPreset: Map<Int, Array<String>> = hashMapOf(
        KeyEvent.KEYCODE_H to arrayOf( "HP", "-"),
        KeyEvent.KEYCODE_S to arrayOf("Sh", "/"),
        KeyEvent.KEYCODE_Z to arrayOf("Zh", "\\"),
        KeyEvent.KEYCODE_B to arrayOf("B", "；"),
        KeyEvent.KEYCODE_X to arrayOf("oXv", "（"),
        KeyEvent.KEYCODE_M to arrayOf("MS", "）"),
        KeyEvent.KEYCODE_L to arrayOf("L", "～"),
        KeyEvent.KEYCODE_D to arrayOf("D", "“"),
        KeyEvent.KEYCODE_Y to arrayOf("Y", "”"),
        KeyEvent.KEYCODE_W to arrayOf("WZ", "："),
        KeyEvent.KEYCODE_J to arrayOf("JK", "@"),
        KeyEvent.KEYCODE_N to arrayOf("NR", "."),
        KeyEvent.KEYCODE_C to arrayOf("Ch", "#"),
        KeyEvent.KEYCODE_Q to arrayOf("Q~", "、"),
        KeyEvent.KEYCODE_G to arrayOf("G", "？"),
        KeyEvent.KEYCODE_F to arrayOf("FC", "！"),
        KeyEvent.KEYCODE_T to arrayOf("T", "……"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_8 to  arrayOf("，"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13 to arrayOf("，", "。"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14 to arrayOf("。", "，"),
        KeyEvent.KEYCODE_SPACE to arrayOf("空格"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 to arrayOf("符号"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5 to arrayOf("123"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4 to arrayOf("表情"),
    )

    val lx17PYKeyNumberPreset: Map<Int, Array<String>> = hashMapOf(
        KeyEvent.KEYCODE_H to arrayOf( "HP", "@"),
        KeyEvent.KEYCODE_S to arrayOf("Sh", "；"),
        KeyEvent.KEYCODE_Z to arrayOf("Zh", "1"),
        KeyEvent.KEYCODE_B to arrayOf("B", "2"),
        KeyEvent.KEYCODE_X to arrayOf("oXv", "3"),
        KeyEvent.KEYCODE_M to arrayOf("MS", "？"),
        KeyEvent.KEYCODE_L to arrayOf("L", "/"),
        KeyEvent.KEYCODE_D to arrayOf("D", "～"),
        KeyEvent.KEYCODE_Y to arrayOf("Y", "4"),
        KeyEvent.KEYCODE_W to arrayOf("WZ", "5"),
        KeyEvent.KEYCODE_J to arrayOf("JK", "6"),
        KeyEvent.KEYCODE_N to arrayOf("NR", "！"),
        KeyEvent.KEYCODE_C to arrayOf("Ch", "……"),
        KeyEvent.KEYCODE_Q to arrayOf("Q~", "、"),
        KeyEvent.KEYCODE_G to arrayOf("G", "7"),
        KeyEvent.KEYCODE_F to arrayOf("FC", "8"),
        KeyEvent.KEYCODE_T to arrayOf("T", "9"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_8 to  arrayOf("，"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13 to arrayOf("，", "。"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14 to arrayOf("。", "，"),
        KeyEvent.KEYCODE_SPACE to arrayOf("空格", "0"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 to arrayOf("符号"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5 to arrayOf("123"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4 to arrayOf("表情"),
    )

    val t9PYKeyPreset: Map<Int, Array<String>> = hashMapOf(
        KeyEvent.KEYCODE_0 to arrayOf("0"),  // 数字键盘使用
        KeyEvent.KEYCODE_A to arrayOf("ABC", "2"),
        KeyEvent.KEYCODE_D to arrayOf("DEF", "3"),
        KeyEvent.KEYCODE_G to arrayOf("GHI", "4"),
        KeyEvent.KEYCODE_J to arrayOf("JKL", "5"),
        KeyEvent.KEYCODE_M to arrayOf("MNO", "6"),
        KeyEvent.KEYCODE_P to arrayOf("PQRS", "7"),
        KeyEvent.KEYCODE_T to arrayOf("TUV", "8"),
        KeyEvent.KEYCODE_W to arrayOf("WXYZ", "9"),
        KeyEvent.KEYCODE_CLEAR to arrayOf("重输"),
        KeyEvent.KEYCODE_APOSTROPHE to arrayOf("分词", "1"),
        KeyEvent.KEYCODE_AT to arrayOf("@"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_8 to  arrayOf("，"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13 to arrayOf("，", "。"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14 to arrayOf("。", "，"),
        KeyEvent.KEYCODE_SPACE to arrayOf("空格", "0"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 to arrayOf("符号"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5 to arrayOf("123"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_RETURN_6 to arrayOf("返回"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4 to arrayOf("表情"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_CURSOR_DIRECTION_9 to arrayOf("清除"),
    )

    val strokeKeyPreset: Map<Int, Array<String>> = hashMapOf(
        KeyEvent.KEYCODE_H to arrayOf("一", "1"),
        KeyEvent.KEYCODE_S to arrayOf("丨", "2"),
        KeyEvent.KEYCODE_P to arrayOf("丿", "3"),
        KeyEvent.KEYCODE_N to arrayOf("丶", "4"),
        KeyEvent.KEYCODE_Z to arrayOf("⼄", "5"),
        KeyEvent.KEYCODE_STAR to arrayOf("*", "6"),
        KeyEvent.KEYCODE_MINUS to arrayOf("-", "7"),
        KeyEvent.KEYCODE_EQUALS to arrayOf("=", "9"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_STAR_17 to arrayOf("*", "6"),
        KeyEvent.KEYCODE_CLEAR to arrayOf("重输"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_8 to  arrayOf("，"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13 to arrayOf("，", "7"),
        KeyEvent.KEYCODE_APOSTROPHE to arrayOf("@#", "8"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14 to arrayOf("。", "9"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 to arrayOf("符号"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5 to arrayOf("123"),
        KeyEvent.KEYCODE_AT to arrayOf("@"),
    )

    val t9NumberKeyPreset: Map<Int, Array<String>> = hashMapOf(
        KeyEvent.KEYCODE_0 to arrayOf("0"),
        KeyEvent.KEYCODE_1 to arrayOf("1"),
        KeyEvent.KEYCODE_2 to arrayOf("2"),
        KeyEvent.KEYCODE_3 to arrayOf("3"),
        KeyEvent.KEYCODE_4 to arrayOf("4"),
        KeyEvent.KEYCODE_5 to arrayOf("5"),
        KeyEvent.KEYCODE_6 to arrayOf("6"),
        KeyEvent.KEYCODE_7 to arrayOf("7"),
        KeyEvent.KEYCODE_8 to arrayOf("8"),
        KeyEvent.KEYCODE_9 to arrayOf("9"),
        KeyEvent.KEYCODE_AT  to arrayOf("@"),
        KeyEvent.KEYCODE_PERIOD to arrayOf("."),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_8 to  arrayOf(","),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13 to arrayOf(","),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14 to arrayOf("."),
        KeyEvent.KEYCODE_SPACE to arrayOf("空格"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 to arrayOf("符号"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5 to arrayOf("123"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_RETURN_6 to arrayOf("返回"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4 to arrayOf("表情"),
    )

    val textEditKeyPreset: Map<Int, Array<String>> = hashMapOf(
        KeyEvent.KEYCODE_DPAD_LEFT to arrayOf("左移"),
        KeyEvent.KEYCODE_DPAD_UP to arrayOf("上移"),
        KeyEvent.KEYCODE_DPAD_RIGHT to arrayOf("右移"),
        KeyEvent.KEYCODE_DPAD_DOWN to arrayOf("下移"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_MOVE_START to arrayOf("开始"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_MOVE_END to arrayOf("结束"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SELECT_ALL to arrayOf("全选"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SELECT_MODE to arrayOf("选择"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_COPY to arrayOf("复制"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_PASTE to arrayOf("粘贴"),
    )

    val textEditMenuPreset: Map<Int, Int> = hashMapOf(
        InputModeSwitcherManager.USER_DEF_KEYCODE_SELECT_ALL to android.R.id.selectAll,
        InputModeSwitcherManager.USER_DEF_KEYCODE_SELECT_MODE to android.R.id.edit,
        InputModeSwitcherManager.USER_DEF_KEYCODE_COPY to android.R.id.copy,
        InputModeSwitcherManager.USER_DEF_KEYCODE_CUT to android.R.id.cut,
        InputModeSwitcherManager.USER_DEF_KEYCODE_PASTE to android.R.id.paste,
    )


    fun getKeyPreset(key:String):Map<Int, Array<String>> {
        return if (key == "qwertyKeyNumberPreset") {
            qwertyKeyNumberPreset
        } else if (key == "qwertyPYKeyPreset") {
            qwertyPYKeyPreset
        } else if (key == "qwertyPYKeyNumberPreset") {
            qwertyPYKeyNumberPreset
        } else if (key == "lx17PYKeyPreset") {
            lx17PYKeyPreset
        } else if (key == "lx17PYKeyNumberPreset") {
            lx17PYKeyNumberPreset
        } else if (key == "t9PYKeyPreset") {
            t9PYKeyPreset
        } else if (key == "t9NumberKeyPreset") {
            t9NumberKeyPreset
        } else if (key == "strokeKeyPreset") {
            strokeKeyPreset
        } else if (key == "textEditKeyPreset") {
            textEditKeyPreset
        } else {
            qwertyKeyPreset
        }
    }
}