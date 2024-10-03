package com.yuyan.imemodule.manager

import android.annotation.SuppressLint
import com.yuyan.imemodule.application.LauncherModel

/**
 * 标点符号管理类和表情
 */
class SymbolsManager private constructor() {
    private val mSymbolsEmoji : HashMap<Int, Array<String>> = HashMap()

    fun getmSymbolsData(): HashMap<Int, Array<String>> {
        return mSymbolsEmoji
    }

    fun getmSymbols(): Array<String> {
        val symbols = LauncherModel.instance.usedCharacterDao!!.allUsedCharacter
        return if(symbols.isEmpty())mSymbolsEmoji[0]!! else symbols
    }

    init {
        mSymbolsEmoji[0] = CHINESE_DATA
        mSymbolsEmoji[1] = ENGLISH_DATA
        mSymbolsEmoji[2] = SHUXUE
        mSymbolsEmoji[3] = TESHU
    }

    companion object {
        fun initInstance() {
            instance = SymbolsManager()
        }

        @SuppressLint("StaticFieldLeak")
        var instance: SymbolsManager? = null
            private set
        private val CHINESE_DATA: Array<String> = arrayOf(
            "，", "。", "？", "！", "、", "：", "；", "……", "“", "”", "‘",
            "’", "（", "）", "~", "—", "·", "#", "@", "|", "《", "》", "〔",
            "〕", "｛", "｝", "［", "］", "『", "』", "【", "】", "「", "」")
        val ENGLISH_DATA: Array<String> = arrayOf(
            ",", ".", "'", "?", "!", "@", ":", ";", "…", "~", "-", "*", "/", "\\",
            "_", "#", "\"", "|", "`", "$", "￥", "^", "&", "￡", "(", ")", "[",
            "]", "{", "}", "<", ">")
        val SHUXUE: Array<String> = arrayOf(
             "×",  "÷",  "+",  "-",  "%",  "/",  "=",  "≈",  "＜",  "＞",  "≤",
            "≥",  "±",  "≡",  "≠",  "∑",  "∏",  "∪",  "∩",  "∈",  "⊙",  "⌒",
            "⊥",  "∥",  "∠",  "∽",  "≌",  "≮",  "≯",  "∧",  "∨",  "√",  "∫",
            "∮",  "∝",  "∞",  "⊙",  "∏",  "º",  "¹",  "²",  "³",  "⁴",  "ⁿ",
            "₁",  "₂",  "₃",  "₄",  "·",  "½",  "⅓",  "⅔",  "¼",  "¾",  "⅛",
            "⅜",  "⅝",  "⅞",  "∴",  "∵",  "∷",  "α",  "β",  "γ",  "δ",  "ε",  "ζ",
            "η",  "θ",  "ι",  "κ",  "λ",  "μ",  "ν",  "ξ",  "ο",  "π",  "ρ",  "σ",
            "τ",  "υ",  "φ",  "χ",  "ψ",  "ω",  "％",  "‰",  "℅",  "°",  "℃",  "℉",
            "㎎",  "㎏",  "㎜",  "㎝",  "㎞",  "㎡",  "㎥",  "㏄",  "㏎",  "m",  "o",  "l",
            "㏕", "㏒",  "㏑")
        val TESHU: Array<String> = arrayOf(
             "Ⅰ",  "Ⅱ",  "Ⅲ",  "Ⅳ",  "Ⅴ",  "Ⅵ",  "Ⅶ",  "Ⅷ",  "Ⅸ",  "Ⅹ",
             "①",  "②",  "③",  "④",  "⑤",  "⑥",  "⑦",  "⑧",  "⑨",  "⑩",
             "○",  "●",  "◇",  "◆",  "□",  "■",  "△",  "▲",  "▽",  "▼",  "▷",  "▶",
             "◁",  "◀",  "☆",  "★",  "♠",  "♡",  "♥",  "♦",  "♧",  "♣",  "☼",  "☀",
             "☺",  "☻",  "◘",  "◙",  "☏",  "☎",  "☜",  "☞",  "◐",  "◑",  "☽",  "☾",  "♀",
             "♂",  "☑",  "√",  "×",  "✔",  "✘",  "㏂",  "㏘",  "✎",  "✐",  "▁",  "▂",  "▃",
            "▄",  "▅",  "▆",  "▇",  "█",  "⊙",  "◎",  "۞",  "✄",  "➴",  "➵",  "卍",  "卐",
             "✈",  "✁",  "♝",  "♞",  "†",  "‡",  "¬",  "￢",  "✌",  "❂",  "❦",  "❧",  "✲",  "❈",
             "❉",  "*",  "✪",  "⊕",  "Θ",  "⊿",  "▫",  "◈",  "▣",  "❤",  "✙",  "۩",  "✖",  "✚",
             "♩",  "♪",  "♫",  "♬",  "¶",  "♭",  "♯",  "∮",  "‖",  "§",  "Ψ",  "▪",  "•",  "‥",
             "…",  "❀",  "๑",  "㊤",  "㊥",  "㊦",  "㊧",  "㊨",  "㊣",  "㈜",  "№",  "㏇",  "㈱",  "㉿",
             "®",  "℗",  "©",  "™",  "℡",  "✍",  "曱",  "甴",  "囍",  "▧",  "▤",  "▨",  "▥",  "▩",
             "▦",  "▓",  "∷",  "▒",  "░",  "╱",  "╲",  "▁",  "▔",  "▏",  "▕",  "↖",  "↙",  "↗",  "↘",
            "↑",  "↓",  "←",  "→",  "↔",  "↕",  "◤",  "◣",  "◥",  "◢",  "ⓐ",  "ⓑ",  "ⓒ",  "ⓓ",
             "ⓔ",  "ⓕ",  "ⓖ",  "ⓗ",  "ⓘ",  "ⓙ",  "ⓚ",  "ⓛ",  "ⓜ",  "ⓝ",  "ⓞ",  "ⓟ",  "ⓠ",  "ⓡ",
             "ⓢ",  "ⓣ",  "ⓤ",  "ⓥ",  "ⓦ",  "ⓧ",  "ⓨ",  "ⓩ", )

    }
}
