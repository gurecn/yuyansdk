package com.yuyan.imemodule.database.helper

import android.content.ContentValues
import com.yuyan.imemodule.database.pamas.InsertParams
import com.yuyan.imemodule.database.pamas.UpdatePamas
import com.yuyan.imemodule.database.provider.BaseDataProvider
import com.yuyan.imemodule.database.table.UsedCharacterTable

/**
 * 常用符号实际操作类
 * Created by Shark on 2017/12/27.
 */
class UsedChararterDBHelper(dataProvider: BaseDataProvider) {
    private var mHelper: BaseDataProvider

    init {
        mHelper = dataProvider
    }

    /**
     * 插入一个符号
     */
    fun insertUsedCharacter(character: String, latestTime: Long): Boolean {
        var result = false
        val isExist = checkExist(character)
        if (!isExist) {
            val contentValues = ContentValues()
            contentValues.put(UsedCharacterTable.LATEST_TIME, latestTime)
            contentValues.put(UsedCharacterTable.CHARACTER, character)
            val list = ArrayList<InsertParams>()
            val insert = InsertParams(UsedCharacterTable.TABLE_NAME, contentValues)
            list.add(insert)
            if (list.isNotEmpty()) {
                result = mHelper.insert(list)
            }
        } else {
            val list = ArrayList<UpdatePamas>()
            val values = ContentValues()
            values.put(UsedCharacterTable.LATEST_TIME, latestTime)
            val updatePamas = UpdatePamas(UsedCharacterTable.TABLE_NAME, values, UsedCharacterTable.CHARACTER + " = ?", arrayOf(character))
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
    private fun checkExist(character: String): Boolean {
        val cursor = mHelper.query(
            UsedCharacterTable.TABLE_NAME,
            null,
            UsedCharacterTable.CHARACTER + " = ?",
            arrayOf(character),
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

    val allUsedCharacter: Array<String>
        /**
         * 查询符号
         */
        get() {
            val emojis: MutableList<String> = mutableListOf()
            val orderBy = UsedCharacterTable.LATEST_TIME + " DESC"
            val columns = arrayOf<String?>(UsedCharacterTable.CHARACTER)
            val cursor =
                mHelper.query(UsedCharacterTable.TABLE_NAME, columns, null, null, orderBy, "0,10")
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val index = cursor.getColumnIndex(UsedCharacterTable.CHARACTER)
                    if(index >= 0){
                        emojis.add(cursor.getString(index))
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }
            return emojis.toTypedArray()
        }
}
