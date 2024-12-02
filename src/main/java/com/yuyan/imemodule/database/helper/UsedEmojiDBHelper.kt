package com.yuyan.imemodule.database.helper

import android.content.ContentValues
import com.yuyan.imemodule.database.pamas.InsertParams
import com.yuyan.imemodule.database.pamas.UpdatePamas
import com.yuyan.imemodule.database.provider.BaseDataProvider
import com.yuyan.imemodule.database.table.UsedEmojiTable

/**
 * 常用表情符号实际操作类
 * Created by Shark on 2017/12/27.
 */
class UsedEmojiDBHelper(dataProvider: BaseDataProvider) {
    private var mHelper: BaseDataProvider

    init {
        mHelper = dataProvider
    }

    /**
     * 插入一个符号
     */
    fun insertUsedEmoji(emoji: String, latestTime: Long): Boolean {
        var result = false
        val isExist = checkExist(emoji)
        if (!isExist) {
            val contentValues = ContentValues()
            contentValues.put(UsedEmojiTable.LATEST_TIME, latestTime)
            contentValues.put(UsedEmojiTable.CHARACTER, emoji)
            val list = ArrayList<InsertParams>()
            val insert = InsertParams(UsedEmojiTable.TABLE_NAME, contentValues)
            list.add(insert)
            if (list.isNotEmpty()) {
                result = mHelper.insert(list)
            }
        } else {
            val list = ArrayList<UpdatePamas>()
            val values = ContentValues()
            values.put(UsedEmojiTable.LATEST_TIME, latestTime)
            val updatePamas = UpdatePamas(UsedEmojiTable.TABLE_NAME, values, UsedEmojiTable.CHARACTER + " = ?", arrayOf(emoji))
            list.add(updatePamas)
            if (list.isNotEmpty()) {
                result = mHelper.update(list)
            }
        }
        return result
    }

    /**
     * 检查是否存在该内容
     */
    private fun checkExist(emoji: String): Boolean {
        val cursor = mHelper.query(
            UsedEmojiTable.TABLE_NAME,
            null,
            UsedEmojiTable.CHARACTER + " = ?",
            arrayOf(emoji),
            null
        )
        if (null != cursor) {
            try {
                if (cursor.moveToNext()) {
                    return true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor.close()
            }
        }
        return false
    }

    val allUsedEmoji: List<String>
        /**
         * 查询符号
         */
        get() {
            val emojis: MutableList<String> = mutableListOf()
            val orderBy = UsedEmojiTable.LATEST_TIME + " DESC"
            val columns = arrayOf<String?>(UsedEmojiTable.CHARACTER)
            val cursor = mHelper.query(UsedEmojiTable.TABLE_NAME, columns, null, null, orderBy, "0,100")
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val index = cursor.getColumnIndex(UsedEmojiTable.CHARACTER)
                    if(index >= 0){
                        emojis.add(cursor.getString(index))
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }
            return emojis
        }
}
