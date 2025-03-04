package com.yuyan.inputmethod.util

object LX17PinYinUtils {
    private val lx17KeyMap = mapOf(
        'b' to "b",
        'd' to "d",
        'u' to "d",
        'c' to "f",
        'f' to "f",
        'g' to "g",
        'h' to "h",
        'a' to "h",
        'p' to "h",
        'i' to "j",
        'j' to "j",
        'k' to "j",
        'l' to "l",
        'm' to "m",
        's' to "m",
        'n' to "n",
        'r' to "n",
        'q' to "q",
        't' to "t",
        'w' to "w",
        'z' to "w",
        'e' to "w",
        'x' to "x",
        'o' to "x",
        'v' to "x",
        'y' to "y",
    )

    fun pinyin2Lx17Key(pinyin: Char): String = lx17KeyMap[pinyin]?:pinyin.toString()
}