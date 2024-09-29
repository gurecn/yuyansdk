
package com.yuyan.imemodule.view.popup

import com.yuyan.imemodule.entity.emojicon.PeopleEmoji

val PopupPreset: Map<String, Array<String>> = hashMapOf(

    //
    // Numbers
    //
    "0" to arrayOf("⁰","∅", "ⁿ",  "°"),
    "1" to arrayOf("¹", "½", "⅓", "¼", "⅕", "⅙", "⅐", "⅛", "⅑", "⅒"),
    "2" to arrayOf("²", "⅔", "⅖"),
    "3" to arrayOf("³", "¾", "⅗", "⅜"),
    "4" to arrayOf("⁴", "⅘"),
    "5" to arrayOf("⁵", "⅚", "⅝"),
    "6" to arrayOf("⁶"),
    "7" to arrayOf("⁷", "⅞"),
    "8" to arrayOf("⁸"),
    "9" to arrayOf("⁹"),

    //
    // Abc
    //
    "q" to arrayOf("Q", "q"),
    "w" to arrayOf("W", "w"),
    "e" to arrayOf("E", "e"),
    "r" to arrayOf("R", "r"),
    "t" to arrayOf("T", "t"),
    "y" to arrayOf("Y", "y"),
    "u" to arrayOf("U", "u"),
    "i" to arrayOf("I", "i"),
    "o" to arrayOf("O", "o"),
    "p" to arrayOf("P", "p"),
    "a" to arrayOf("A", "a"),
    "s" to arrayOf("S", "s"),
    "d" to arrayOf("D", "d"),
    "f" to arrayOf("F", "f"),
    "g" to arrayOf("G", "g"),
    "h" to arrayOf("H", "h"),
    "j" to arrayOf("J", "j"),
    "k" to arrayOf("K", "k"),
    "l" to arrayOf("L", "l"),
    "z" to arrayOf("Z", "z"),
    "x" to arrayOf("X", "x"),
    "c" to arrayOf("C", "c"),
    "v" to arrayOf("V", "v"),
    "b" to arrayOf("B", "b"),
    "n" to arrayOf("N", "n"),
    "m" to arrayOf("M", "m"),

    "Q" to arrayOf("q", "Q"),
    "W" to arrayOf("w", "W"),
    "E" to arrayOf("e", "E"),
    "R" to arrayOf("r", "R"),
    "T" to arrayOf("t", "T"),
    "Y" to arrayOf("y", "Y"),
    "U" to arrayOf("u", "U"),
    "I" to arrayOf("i", "I"),
    "O" to arrayOf("o", "O"),
    "P" to arrayOf("p", "P"),
    "A" to arrayOf("a", "A"),
    "S" to arrayOf("s", "S"),
    "D" to arrayOf("d", "D"),
    "F" to arrayOf("f", "F"),
    "G" to arrayOf("g", "G"),
    "H" to arrayOf("h", "H"),
    "J" to arrayOf("j", "J"),
    "K" to arrayOf("k", "K"),
    "L" to arrayOf("l", "L"),
    "Z" to arrayOf("z", "Z"),
    "X" to arrayOf("x", "X"),
    "C" to arrayOf("c", "C"),
    "V" to arrayOf("v", "V"),
    "B" to arrayOf("b", "B"),
    "N" to arrayOf("n", "N"),
    "M" to arrayOf("m", "M"),

    //
    // T9
    //
    "分词" to arrayOf("'", "‘","’"),
    "ABC" to arrayOf("A", "B", "C", "a", "b", "c"),
    "DEF" to arrayOf("D", "E", "F", "d", "e", "f"),
    "GHI" to arrayOf("G", "H", "I", "g", "h", "i"),
    "JKL" to arrayOf("J", "K", "L", "j", "k", "l"),
    "MNO" to arrayOf("M", "N", "O", "m", "n", "o"),
    "PQRS" to arrayOf("P", "Q", "R", "S", "p", "q", "r", "s"),
    "TUV" to arrayOf("T", "U", "V", "t", "u", "v"),
    "WXYZ" to arrayOf("W", "X", "Y", "Z", "w", "x", "y", "z"),
    "空格" to arrayOf("0"),

    //
    // LX17
    //
    "HP" to arrayOf("H", "P", "a", "ia", "ua"),
    "Sh" to arrayOf("Sh", "en", "in"),
    "Zh" to arrayOf("Zh", "ang", "iao"),
    "oXv" to arrayOf("O", "X", "V", "uai", "uan"),
    "MS" to arrayOf("M", "S", "ie", "uo"),
    "WZ" to arrayOf("W", "Z", "e"),
    "JK" to arrayOf("K", "K", "i"),
    "NR" to arrayOf("N", "R", "an"),
    "Ch" to arrayOf("Ch", "iang", "ui"),
    "Q~" to arrayOf("Q", "~", "ian", "uang"),
    "FC" to arrayOf("F", "C", "iu", "ou"),

    //
    // Other
    //
    "," to arrayOf(",", ".", "?", "!", ":", ";", "(", ")"),
    "." to arrayOf(",", ".", "?", "!", ":", ";", "(", ")"),
    "，" to arrayOf("，", "。", "？", "！", "：", "；", "（", "）"),
    "。" to arrayOf("，", "。", "？", "！", "：", "；", "@", "（", "）"),
    "@" to arrayOf(",", "，", ".", "。", "?", "？", "!", "！", ":", "：", ";", "；"),
    "符号" to arrayOf(",", "，", ".", "。", "?", "？", "!", "！", ":", "：", ";", "；"),
    "表情" to PeopleEmoji.DATA.copyOfRange(0, 10),
    "123" to arrayOf( "0", "1", "2", "3", "4","5", "6", "7", "8", "9"),
)
val PopupSmallPreset: Map<String, Array<String>> = hashMapOf(
    //
    // 半角
    //
    "[" to arrayOf("[", "【", "〔"),
    "]" to arrayOf("]", "】", "〕"),
    "<" to arrayOf("<", "《", "〈", "«"),
    ">" to arrayOf(">", "》", "〉", "»"),
    "(" to arrayOf("(", "（"),
    ")" to arrayOf(")", "）"),
    "{" to arrayOf("{", "『", "〖"),
    "}" to arrayOf("}", "』", "〗"),
    "|" to arrayOf("|", "｜", "¦"),
    "-" to arrayOf("-", "——"),
    "､" to arrayOf("､", "、"),
    "/" to arrayOf("/"),
    "\\" to arrayOf("\\"),
    ":" to arrayOf(":", "："),
    ";" to arrayOf(";", "；"),
    "_" to arrayOf("_", "——"),
    "#" to arrayOf("#"),
    "~" to arrayOf("~", "～"),
    "'" to arrayOf("'", "‘"),
    "\"" to arrayOf("\"", "“", "”"),
    "@" to arrayOf("@"),
    "," to arrayOf(",", "，"),
    "." to arrayOf(".", "。", "·", "•"),
    "&" to arrayOf("&", "$", "¥"),
    "?" to arrayOf("?", "？"),
    "!" to arrayOf("!", "！"),
    "…" to arrayOf("…"),
    //
    // 全角
    //
    "【" to arrayOf("【", "〔", "["),
    "】" to arrayOf("】", "〕", "]"),
    "《" to arrayOf("《", "〈", "<", "«"),
    "》" to arrayOf("》", "〉", ">", "»"),
    "（" to arrayOf("（", "("),
    "）" to arrayOf("）", ")"),
    "『" to arrayOf("『", "〖", "{"),
    "』" to arrayOf("』", "〗", "}"),
    "|" to arrayOf("|", "｜", "¦"),
    "-" to arrayOf("-", "——"),
    "、" to arrayOf("、", "､"),
    "：" to arrayOf("：", ":"),
    "；" to arrayOf("；", ";"),
    "_" to arrayOf("_", "——"),
    "#" to arrayOf("#"),
    "～" to arrayOf("～", "~"),
    "‘" to arrayOf("‘",  "’", "'"),
    "“" to arrayOf("“", "”", "\""),
    "”" to arrayOf("”", "“", "\""),
    "，" to arrayOf("，", ","),
    "。" to arrayOf("。", ".", "·", "•"),
    "&" to arrayOf("&", "$", "¥"),
    "？" to arrayOf("？", "?"),
    "！" to arrayOf( "！", "!"),
    "……" to arrayOf("……"),

    //
    // Numbers
    //
    "0" to arrayOf("0"),
    "1" to arrayOf("1"),
    "2" to arrayOf("2"),
    "3" to arrayOf("3"),
    "4" to arrayOf("4"),
    "5" to arrayOf("5"),
    "6" to arrayOf("6"),
    "7" to arrayOf("7"),
    "8" to arrayOf("8"),
    "9" to arrayOf("9"),
)