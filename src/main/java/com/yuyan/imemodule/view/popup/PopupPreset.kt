
package com.yuyan.imemodule.view.popup

val PopupPreset: Map<String, Array<String>> = hashMapOf(
    //
    // Latin
    //
    "q" to arrayOf("[", "【", "〔", "Q"),
    "w" to arrayOf("]", "】", "〕", "W"),
    "e" to arrayOf("<", "《", "〈", "«", "E"),
    "r" to arrayOf(">", "》", "〉", "»", "R"),
    "t" to arrayOf("(", "（", "T"),
    "y" to arrayOf(")", "）", "Y",),
    "u" to arrayOf("{", "『", "〖", "U"),
    "i" to arrayOf("}", "』", "〗", "I"),
    "o" to arrayOf("|", "｜", "§", "¦", "O"),
    "p" to arrayOf("-", "_", "——", "～", "P"),
    "a" to arrayOf("、", "A", "ā", "á", "ǎ", "à"),
    "s" to arrayOf("/", "$", "¥", "S", "ß", "ś", "š", "ş"),
    "d" to arrayOf(":", "：", "D", "ð"),
    "f" to arrayOf(";", "；", "F"),
    "g" to arrayOf("_", "——", "～", "G", "ğ"),
    "h" to arrayOf("#", "H"),
    "j" to arrayOf("~", "J"),
    "k" to arrayOf("'", "‘", "K"),
    "l" to arrayOf("\"", "“", "”", "L", "ł"),
    "z" to arrayOf("@", "Z", "ž", "ź", "ż"),
    "x" to arrayOf(",", "，", "X", "×"),
    "c" to arrayOf(".", "。", "·", "•", "C", "ç", "ć", "č"),
    "v" to arrayOf("&", "$", "¥", "V", "ǖ", "ǘ", "ǚ", "ǜ"),
    "b" to arrayOf("?", "？", "B"),
    "n" to arrayOf("!", "！", "|", "¦", "N", "ñ", "ń"),
    "m" to arrayOf("…", "M"),

    //
    // Upper case Latin
    //
    "Q" to arrayOf("[", "【", "〔", "q"),
    "W" to arrayOf("]", "】", "〕", "w"),
    "E" to arrayOf("<", "《", "〈", "«", "e"),
    "R" to arrayOf(">", "》", "〉", "»", "r"),
    "T" to arrayOf("(", "（",  "t"),
    "Y" to arrayOf(")", "）", "y"),
    "U" to arrayOf("{", "『", "〖", "u"),
    "I" to arrayOf("}", "』", "〗",  "i"),
    "O" to arrayOf("|", "｜", "§", "¦", "o"),
    "P" to arrayOf("-", "_", "——", "～", "p"),
    "A" to arrayOf("-", "a", "ā", "á", "ǎ", "à"),
    "S" to arrayOf("/", "s", "ẞ", "Ś", "Š", "Ş"),
    "D" to arrayOf(":", "：", "d", "Ð"),
    "F" to arrayOf(";", "；", "f"),
    "G" to arrayOf("_", "——", "～", "G", "ğ"),
    "H" to arrayOf("#", "H"),
    "J" to arrayOf("~", "j"),
    "K" to arrayOf("'", "‘", "k"),
    "L" to arrayOf("\"", "“", "”", "l", "Ł"),
    "Z" to arrayOf("@", "z", "Ž", "Ź", "Ż"),
    "X" to arrayOf(",", "，", "x"),
    "C" to arrayOf(".", "。", "·", "•", "c", "Ç", "Ć", "Č"),
    "V" to arrayOf("&", "$", "¥", "v", "ǖ", "ǘ", "ǚ", "ǜ"),
    "B" to arrayOf("?",  "？", "b"),
    "N" to arrayOf("!", "！", "|", "¦", "n", "Ñ", "Ń"),
    "M" to arrayOf("…", "m"),
    //
    // Numbers
    //
    "0" to arrayOf("∅", "ⁿ", "⁰", "°"),
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
    // Punctuation
    //
    "." to arrayOf(",", "?", "!", ":", ";", "_", "%", "$", "^", "&"),
    "-" to arrayOf("—", "–", "·"),
    "?" to arrayOf("¿", "‽"),
    "'" to arrayOf("‘", "’", "‚", "›", "‹"),
    "!" to arrayOf("¡"),
    "\"" to arrayOf("“", "”", "„", "»", "«"),
    "/" to arrayOf("÷"),
    "#" to arrayOf("№"),
    "%" to arrayOf("‰", "℅"),
    "^" to arrayOf("↑", "↓", "←", "→"),
    "+" to arrayOf("±"),
    "<" to arrayOf("≤", "«", "‹", "⟨"),
    "=" to arrayOf("∞", "≠", "≈"),
    ">" to arrayOf("≥", "»", "›", "⟩"),
    //
    // Currency
    //
    "$" to arrayOf("¢", "€", "£", "¥", "₹", "₽", "₺", "₩", "₱", "₿"),
    "符" to arrayOf(",", ".", "?", "!", ":", ";", "@", "(", ")"),

)


val PopupChinesePreset: Map<String, Array<String>> = hashMapOf(
    //
    // Latin
    //
    "q" to arrayOf("【", "〔", "[", "Q"),
    "w" to arrayOf("】", "〕", "]", "W"),
    "e" to arrayOf("《", "〈", "<", "«", "E"),
    "r" to arrayOf("》", "〉", ">", "»", "R"),
    "t" to arrayOf("（", "(", "T"),
    "y" to arrayOf("）", ")", "Y",),
    "u" to arrayOf("『", "〖", "{", "U"),
    "i" to arrayOf("』", "〗", "}", "I"),
    "o" to arrayOf("|", "｜", "§", "¦", "O"),
    "p" to arrayOf("-", "_", "——", "～", "P"),
    "a" to arrayOf("、", "A", "ā", "á", "ǎ", "à"),
    "s" to arrayOf("/", "$", "¥", "S", "ß", "ś", "š", "ş"),
    "d" to arrayOf("：", ":", "D", "ð"),
    "f" to arrayOf("；", ";", "F"),
    "g" to arrayOf("_", "——", "～", "G", "ğ"),
    "h" to arrayOf("#", "H"),
    "j" to arrayOf("~", "J"),
    "k" to arrayOf( "‘", "'","K"),
    "l" to arrayOf("“", "”", "\"", "L", "ł"),
    "z" to arrayOf("@", "Z", "ž", "ź", "ż"),
    "x" to arrayOf("，", ",", "X", "×"),
    "c" to arrayOf("。", ".", "·", "•", "C", "ç", "ć", "č"),
    "v" to arrayOf("&", "$", "¥", "V", "ǖ", "ǘ", "ǚ", "ǜ"),
    "b" to arrayOf("？", "?", "B"),
    "n" to arrayOf( "！", "!","|", "¦", "N", "ñ", "ń"),
    "m" to arrayOf("…", "M"),

    //
    // Upper case Latin
    //
    "Q" to arrayOf("【", "〔", "[", "q"),
    "W" to arrayOf("】", "〕", "]", "w"),
    "E" to arrayOf("《", "〈", "<", "«", "e"),
    "R" to arrayOf("》", "〉", ">", "»", "r"),
    "T" to arrayOf("（", "(",  "t"),
    "Y" to arrayOf("）", ")",  "y"),
    "U" to arrayOf("『", "〖", "{", "u"),
    "I" to arrayOf("』", "〗", "}",  "i"),
    "O" to arrayOf("|", "｜", "§", "¦",  "o"),
    "P" to arrayOf("-", "_", "——", "～", "p"),
    "A" to arrayOf("、","a", "ā", "á", "ǎ", "à"),
    "S" to arrayOf("/", "s", "ẞ", "Ś", "Š", "Ş"),
    "D" to arrayOf("：", ":", "d", "Ð"),
    "F" to arrayOf( "；", ";","f"),
    "G" to arrayOf("_", "——", "～", "G", "ğ"),
    "H" to arrayOf("#", "H"),
    "J" to arrayOf("~", "j"),
    "K" to arrayOf("‘", "'", "k"),
    "L" to arrayOf( "“", "”", "\"", "l", "Ł"),
    "Z" to arrayOf("@", "z", "Ž", "Ź", "Ż"),
    "X" to arrayOf("，", ",", "x"),
    "C" to arrayOf( "。", ".","·", "•", "c", "Ç", "Ć", "Č"),
    "V" to arrayOf("&", "$", "¥", "v", "ǖ", "ǘ", "ǚ", "ǜ"),
    "B" to arrayOf("？", "?",  "b"),
    "N" to arrayOf("！", "!", "|", "¦", "n", "Ñ", "Ń"),
    "M" to arrayOf("…", "m"),

    "符" to arrayOf("，", "。", "？", "！", "：", "；", "@", "（", "）"),

    //
    // T9
    //
    "分词" to arrayOf("1", "'", "‘","’"),
    "ABC" to arrayOf("2", "A", "B", "C"),
    "DEF" to arrayOf("3", "D", "E", "F"),
    "GHI" to arrayOf("4", "G", "H", "I"),
    "JKL" to arrayOf("5", "J", "K", "L"),
    "MNO" to arrayOf("6", "M", "N", "O"),
    "PQRS" to arrayOf("7", "P", "Q", "R", "S"),
    "TUV" to arrayOf("8", "T", "U", "V"),
    "WXYZ" to arrayOf("9", "W", "X", "Y", "Z"),
    "空格" to arrayOf("0"),

    //
    // LX17
    //
    "HP" to arrayOf("-", "H", "P", "a", "ia", "ua"),
    "Sh" to arrayOf("/", "Sh", "en", "in"),
    "Zh" to arrayOf(";", "Zh", "ang", "iao"),
    "oXv" to arrayOf("#", "O", "X", "V", "uai", "uan"),
    "MS" to arrayOf("~", "M", "S", "ie", "uo"),
    "WZ" to arrayOf("@", "W", "Z", "e"),
    "JK" to arrayOf("&", "K", "K", "i"),
    "NR" to arrayOf("！", "N", "R", "an"),
    "Ch" to arrayOf("，", "Ch", "iang", "ui"),
    "Q~" to arrayOf("。", "Q", "~", "ian", "uang"),
    "FC" to arrayOf("…", "F", "C", "iu", "ou"),
)