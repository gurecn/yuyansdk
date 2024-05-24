package com.yuyan.imemodule.database.helper

import android.content.ContentValues
import com.yuyan.imemodule.database.pamas.InsertParams
import com.yuyan.imemodule.database.pamas.UpdatePamas
import com.yuyan.imemodule.database.provider.BaseDataProvider
import com.yuyan.imemodule.database.table.UsedEmoticonsTable

/**
 * 常用颜文字实际操作类
 * Created by Shark on 2017/12/27.
 */
class UsedEmoticonsHelper(dataProvider: BaseDataProvider?) {
    private var mHelper: BaseDataProvider? = null

    init {
        mHelper = dataProvider
    }

    /**
     * 插入一个符号
     */
    fun insertUsedEmoticon(emoji: String, latestTime: Long): Boolean {
        var result = false
        val isExist = checkExist(emoji)
        if (!isExist) {
            val contentValues = ContentValues()
            contentValues.put(UsedEmoticonsTable.LATEST_TIME, latestTime)
            contentValues.put(UsedEmoticonsTable.CHARACTER, emoji)
            val list = ArrayList<InsertParams>()
            val insert = InsertParams(UsedEmoticonsTable.TABLE_NAME, contentValues)
            list.add(insert)
            if (!list.isEmpty()) {
                result = mHelper!!.insert(list)
            }
        } else {
            val list = ArrayList<UpdatePamas>()
            val values = ContentValues()
            values.put(UsedEmoticonsTable.LATEST_TIME, latestTime)
            val updatePamas = UpdatePamas(
                UsedEmoticonsTable.TABLE_NAME,
                values,
                UsedEmoticonsTable.CHARACTER + " = ?",
                arrayOf(emoji)
            )
            list.add(updatePamas)
            if (!list.isEmpty()) {
                result = mHelper!!.update(list)
            }
        }
        return result
    }

    /**
     * 检查是否存在该内容
     */
    private fun checkExist(emoji: String): Boolean {
        val cursor = mHelper!!.query(
            UsedEmoticonsTable.TABLE_NAME,
            null,
            UsedEmoticonsTable.CHARACTER + " = ?",
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

    val allUsedEmoticons: Array<String>
        /**
         * 查询符号
         */
        get() {
            val emojis: MutableList<String> = mutableListOf()
            val orderBy = UsedEmoticonsTable.LATEST_TIME + " DESC"
            val columns = arrayOf<String?>(UsedEmoticonsTable.CHARACTER)
            val cursor = mHelper!!.query(UsedEmoticonsTable.TABLE_NAME, columns, null, null, orderBy)
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val index = cursor.getColumnIndex(UsedEmoticonsTable.CHARACTER)
                    if(index >= 0){
                        emojis.add(cursor.getString(index))
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }
            return emojis.toTypedArray()
        }
}
