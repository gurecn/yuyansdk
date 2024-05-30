package com.yuyan.imemodule.manager

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.yuyan.imemodule.application.LauncherModel
import com.yuyan.imemodule.entity.emojicon.PeopleEmoji
import com.yuyan.imemodule.utils.StringUtils.convertListToString
import java.io.IOException
import java.util.Collections

/**
 * 标点符号管理类和表情
 */
class SymbolsManager private constructor(private val mContext: Context) {
    private val mSymbolsEmoji : HashMap<Int, Array<String>> = HashMap()

    private fun convertValues(list: List<String>): Array<String> {
        return convertListToString(list)
    }

    private fun splitIni(rawlist: List<String>): MutableList<String> {
        val values: MutableList<String> = ArrayList()
        for (value in rawlist) {
            val tmpStrings =
                value.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            Collections.addAll(values, *tmpStrings)
        }
        return values
    }

    @Throws(IOException::class)
    private fun getValuesFromFile(path: String): List<String> {
        val inputStream = mContext.assets.open(path)
        var values: MutableList<String> = ArrayList()
        val count = inputStream.available()
        val b = ByteArray(count)
        while (inputStream.read(b) != -1) {
            values.add(String(b))
        }
        values = splitIni(values)
        inputStream.close()
        return values
    }

    fun getmSymbolsData(position: Int): Array<String> {
        return mSymbolsEmoji[position]!!
    }

    fun getmSymbols(position: Int): Array<String> {
        var data: Array<String>
        if (position == 5) { //常用颜文字
            data = LauncherModel.instance?.usedEmoticonsDao!!.allUsedEmoticons
            if (data.isEmpty()) {
                data = getmSymbolsData(5)
            }
        } else if (position == 4) {  //常用表情
            data = LauncherModel.instance?.usedEmojiDao!!.allUsedEmoji
            if (data.isEmpty()) {
                data = getmSymbolsData(4)
            }
        } else {  //常用符号
            data = LauncherModel.instance?.usedCharacterDao!!.allUsedCharacter
            if (data.isEmpty()) {
                data = getmSymbolsData(0)
            }
        }
        return data
    }

    init {
        try {
            mSymbolsEmoji[0] = CHINESE_DATA
            mSymbolsEmoji[1] = ENGLISH_DATA
            mSymbolsEmoji[2] = convertValues(getValuesFromFile("symbols/shu_xue.ini"))
            mSymbolsEmoji[3] = convertValues(getValuesFromFile("symbols/te_shu.ini"))
            mSymbolsEmoji[4] = PeopleEmoji.DATA
            mSymbolsEmoji[5] = convertValues(getValuesFromFile("symbols/smile.ini"))
        } catch (e: IOException) {
            Log.d("WIVE", "lightViewAnimate$e")
        }
    }

    companion object {
        fun initInstance(context: Context) {
            instance = SymbolsManager(context)
        }

        @SuppressLint("StaticFieldLeak")
        var instance: SymbolsManager? = null
            private set
        private val CHINESE_DATA: Array<String> = arrayOf(
            "，",
            "。",
            "？",
            "！",
            "、",
            "：",
            "；",
            "…",
            "“",
            "”",
            "‘",
            "’",
            "（",
            "）",
            "~",
            "—",
            "·",
            "#",
            "@",
            "|",
            "《",
            "》",
            "〔",
            "〕",
            "｛",
            "｝",
            "［",
            "］",
            "『",
            "』",
            "【",
            "】",
            "「",
            "」"
        )
        val ENGLISH_DATA: Array<String> = arrayOf(
            ",",
            ".",
            "'",
            "?",
            "!",
            "~",
            "@",
            ":",
            ";",
            "-",
            "*",
            "/",
            "\\",
            "_",
            "#",
            "\"",
            "|",
            "`",
            "$",
            "￥",
            "^",
            "&",
            "￡",
            "(",
            ")",
            "[",
            "]",
            "{",
            "}",
            "<",
            ">"
        )
    }
}
