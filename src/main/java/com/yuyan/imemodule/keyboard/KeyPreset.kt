package com.yuyan.imemodule.keyboard

import android.view.KeyEvent
import com.yuyan.imemodule.manager.InputModeSwitcherManager

object KeyPreset {
    val qwertyKeyPreset: Map<Int, Array<String>> = hashMapOf(
        45 to arrayOf("Q", "["),
        51 to arrayOf("W", "]"),
        33 to arrayOf("E", "<"),
        46 to arrayOf("R", ">"),
        48 to arrayOf("T", "("),
        53 to arrayOf("Y", ")"),
        49 to arrayOf("U", "{"),
        37 to arrayOf("I", "}"),
        43 to arrayOf("O", "|"),
        44 to arrayOf("P", "-"),
        29 to arrayOf("A", "､"),
        47 to arrayOf("S", "/"),
        32 to arrayOf("D", "\\"),
        34 to arrayOf("F", ";"),
        35 to arrayOf("G", "_"),
        36 to arrayOf("H", "*"),
        38 to arrayOf("J", "~"),
        39 to arrayOf("K", "'"),
        40 to arrayOf("L", "\""),
        74 to arrayOf(";"),
        75 to arrayOf("分词"),
        54 to arrayOf("Z", "@"),
        52 to arrayOf("X", "#"),
        31 to arrayOf("C", ":"),
        50 to arrayOf("V", "&"),
        30 to arrayOf("B", "?"),
        42 to arrayOf("N", "!"),
        41 to arrayOf("M", "…"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13 to arrayOf(",", "."),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14 to arrayOf(".", ","),
        62 to arrayOf("空格"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 to arrayOf("符号"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5 to arrayOf("123"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4 to arrayOf("表情"),
    )

    val qwertyKeyNumberPreset: Map<Int, Array<String>> = hashMapOf(
        45 to arrayOf("Q", "1"),
        51 to arrayOf("W", "2"),
        33 to arrayOf("E", "3"),
        46 to arrayOf("R", "4"),
        48 to arrayOf("T", "5"),
        53 to arrayOf("Y", "6"),
        49 to arrayOf("U", "7"),
        37 to arrayOf("I", "8"),
        43 to arrayOf("O", "9"),
        44 to arrayOf("P", "0"),
        29 to arrayOf("A", "-"),
        47 to arrayOf("S", "/"),
        32 to arrayOf("D", ":"),
        34 to arrayOf("F", ";"),
        35 to arrayOf("G", "("),
        36 to arrayOf("H", ")"),
        38 to arrayOf("J", "~"),
        39 to arrayOf("K", "'"),
        40 to arrayOf("L", "\""),
        74 to arrayOf(";"),
        75 to arrayOf("分词"),
        54 to arrayOf("Z", "@"),
        52 to arrayOf("X", "_"),
        31 to arrayOf("C", "#"),
        50 to arrayOf("V", "&"),
        30 to arrayOf("B", "?"),
        42 to arrayOf("N", "!"),
        41 to arrayOf("M", "…"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13 to arrayOf(",", "."),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14 to arrayOf(".", ","),
        62 to arrayOf("空格"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 to arrayOf("符号"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5 to arrayOf("123"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4 to arrayOf("表情"),
    )

    val qwertyPYKeyPreset: Map<Int, Array<String>> = hashMapOf(
        45 to arrayOf("Q", "【"),
        51 to arrayOf("W", "】"),
        33 to arrayOf("E", "《"),
        46 to arrayOf("R", "》"),
        48 to arrayOf("T", "（"),
        53 to arrayOf("Y", "）"),
        49 to arrayOf("U", "{"),
        37 to arrayOf("I", "}"),
        43 to arrayOf("O", "|"),
        44 to arrayOf("P", "-"),
        29 to arrayOf("A", "、"),
        47 to arrayOf("S", "/"),
        32 to arrayOf("D", "\\"),
        34 to arrayOf("F", "；"),
        35 to arrayOf("G", "_"),
        36 to arrayOf("H", "~"),
        38 to arrayOf("J", "‘"),
        39 to arrayOf("K", "“"),
        40 to arrayOf("L", "”"),
        74 to arrayOf("ing", "", ""),
        75 to arrayOf("分词"),
        54 to arrayOf("Z", "@"),
        52 to arrayOf("X", "#"),
        31 to arrayOf("C", "："),
        50 to arrayOf("V", "&"),
        30 to arrayOf("B", "？"),
        42 to arrayOf("N", "！"),
        41 to arrayOf("M", "……"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13 to arrayOf("，", "。"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14 to arrayOf("。", "，"),
        62 to arrayOf("空格"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 to arrayOf("符号"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5 to arrayOf("123"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4 to arrayOf("表情"),
    )

    val qwertyPYKeyNumberPreset: Map<Int, Array<String>> = hashMapOf(
        45 to arrayOf("Q", "1"),
        51 to arrayOf("W", "2"),
        33 to arrayOf("E", "3"),
        46 to arrayOf("R", "4"),
        48 to arrayOf("T", "5"),
        53 to arrayOf("Y", "6"),
        49 to arrayOf("U", "7"),
        37 to arrayOf("I", "8"),
        43 to arrayOf("O", "9"),
        44 to arrayOf("P", "0"),
        29 to arrayOf("A", "-"),
        47 to arrayOf("S", "/"),
        32 to arrayOf("D", "\\"),
        34 to arrayOf("F", "；"),
        35 to arrayOf("G", "（"),
        36 to arrayOf("H", "）"),
        38 to arrayOf("J", "～"),
        39 to arrayOf("K", "“"),
        40 to arrayOf("L", "”"),
        74 to arrayOf("ing", ":"),
        75 to arrayOf("分词"),
        54 to arrayOf("Z", "@"),
        52 to arrayOf("X", "."),
        31 to arrayOf("C", "#"),
        50 to arrayOf("V", "、"),
        30 to arrayOf("B", "？"),
        42 to arrayOf("N", "！"),
        41 to arrayOf("M", "……"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13 to arrayOf("，", "。"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14 to arrayOf("。", "，"),
        62 to arrayOf("空格"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 to arrayOf("符号"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5 to arrayOf("123"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4 to arrayOf("表情"),
    )

    val lx17PYKeyPreset: Map<Int, Array<String>> = hashMapOf(
        36 to arrayOf( "HP", "-"),
        47 to arrayOf("Sh", "/"),
        54 to arrayOf("Zh", "\\"),
        30 to arrayOf("B", "；"),
        52 to arrayOf("oXv", "（"),
        41 to arrayOf("MS", "）"),
        40 to arrayOf("L", "～"),
        32 to arrayOf("D", "“"),
        53 to arrayOf("Y", "”"),
        51 to arrayOf("WZ", "："),
        38 to arrayOf("JK", "@"),
        42 to arrayOf("NR", "."),
        31 to arrayOf("Ch", "#"),
        45 to arrayOf("Q~", "、"),
        35 to arrayOf("G", "？"),
        34 to arrayOf("FC", "！"),
        48 to arrayOf("T", "……"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13 to arrayOf("，", "。"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14 to arrayOf("。", "，"),
        62 to arrayOf("空格"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 to arrayOf("符号"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5 to arrayOf("123"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4 to arrayOf("表情"),
    )

    val lx17PYKeyNumberPreset: Map<Int, Array<String>> = hashMapOf(
        36 to arrayOf( "HP", "@"),
        47 to arrayOf("Sh", "；"),
        54 to arrayOf("Zh", "1"),
        30 to arrayOf("B", "2"),
        52 to arrayOf("oXv", "3"),
        41 to arrayOf("MS", "？"),
        40 to arrayOf("L", "/"),
        32 to arrayOf("D", "～"),
        53 to arrayOf("Y", "4"),
        51 to arrayOf("WZ", "5"),
        38 to arrayOf("JK", "6"),
        42 to arrayOf("NR", "！"),
        31 to arrayOf("Ch", "……"),
        45 to arrayOf("Q~", "、"),
        35 to arrayOf("G", "7"),
        34 to arrayOf("FC", "8"),
        48 to arrayOf("T", "9"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13 to arrayOf("，", "。"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14 to arrayOf("。", "，"),
        62 to arrayOf("空格", "0"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 to arrayOf("符号"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5 to arrayOf("123"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4 to arrayOf("表情"),
    )

    val t9PYKeyPreset: Map<Int, Array<String>> = hashMapOf(
        7 to arrayOf("0"),  // 数字键盘使用
        9 to arrayOf("ABC", "2"),
        10 to arrayOf("DEF", "3"),
        11 to arrayOf("GHI", "4"),
        12 to arrayOf("JKL", "5"),
        13 to arrayOf("MNO", "6"),
        14 to arrayOf("PQRS", "7"),
        15 to arrayOf("TUV", "8"),
        16 to arrayOf("WXYZ", "9"),
        28 to arrayOf("重输"),
        75 to arrayOf("分词", "1"),
        77 to arrayOf("@"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13 to arrayOf("，", "。"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14 to arrayOf("。", "，"),
        62 to arrayOf("空格", "0"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 to arrayOf("符号"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5 to arrayOf("123"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_RETURN_6 to arrayOf("返回"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_4 to arrayOf("表情"),
    )

    val strokeKeyPreset: Map<Int, Array<String>> = hashMapOf(
        36 to arrayOf("一", "1"),
        47 to arrayOf("丨", "2"),
        44 to arrayOf("丿", "3"),
        42 to arrayOf("丶", "4"),
        54 to arrayOf("⼄", "5"),
        17 to arrayOf("*", "6"),
        69 to arrayOf("-", "7"),
        70 to arrayOf("=", "9"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_STAR_17 to arrayOf("*", "6"),
        28 to arrayOf("重输"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13 to arrayOf("，", "7"),
        75 to arrayOf("@#", "8"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14 to arrayOf("。", "9"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_SYMBOL_3 to arrayOf("符号"),
        InputModeSwitcherManager.USER_DEF_KEYCODE_NUMBER_5 to arrayOf("123"),
        77 to arrayOf("@"),
    )

    val t9NumberKeyPreset: Map<Int, Array<String>> = hashMapOf(
        7 to arrayOf("0"),
        8 to arrayOf("1"),
        9 to arrayOf("2"),
        10 to arrayOf("3"),
        11 to arrayOf("4"),
        12 to arrayOf("5"),
        13 to arrayOf("6"),
        14 to arrayOf("7"),
        15 to arrayOf("8"),
        16 to arrayOf("9"),
        77 to arrayOf("@"),
        0 to arrayOf("."),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_COMMA_13 to arrayOf(","),
        InputModeSwitcherManager.USER_DEF_KEYCODE_LEFT_PERIOD_14 to arrayOf("."),
        62 to arrayOf("空格"),
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