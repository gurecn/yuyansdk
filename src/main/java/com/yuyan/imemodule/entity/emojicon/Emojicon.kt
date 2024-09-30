package com.yuyan.imemodule.entity.emojicon

/**
 * Emoji表情对应表实体类
 * @author Hieu Rocker (rockerhieu@gmail.com)
 */
class Emojicon {
    var value = 0.toChar()
        private set
    var emoji: String = ""
        private set

    override fun hashCode(): Int {
        return emoji.hashCode()
    }

    companion object {
        fun fromCodePoint(codePoint: Int): Emojicon {
            val emoji = Emojicon()
            emoji.emoji = newString(codePoint)
            return emoji
        }

        fun fromChar(ch: Char): Emojicon {
            val emoji = Emojicon()
            emoji.emoji = ch.toString()
            return emoji
        }

        fun fromChars(chars: String): Emojicon {
            val emoji = Emojicon()
            emoji.emoji = chars
            return emoji
        }

        fun newString(codePoint: Int): String {
            return if (Character.charCount(codePoint) == 1) {
                codePoint.toString()
            } else {
                String(Character.toChars(codePoint))
            }
        }
    }
}
