package com.yuyan.imemodule.database.helper

import android.content.ContentValues
import android.database.Cursor
import com.yuyan.imemodule.database.pamas.InsertParams
import com.yuyan.imemodule.database.pamas.UpdatePamas
import com.yuyan.imemodule.database.provider.BaseDataProvider
import com.yuyan.imemodule.database.table.ClipboardTable
import com.yuyan.imemodule.entity.ClipBoardDataBean
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.utils.TimeUtils
import com.yuyan.imemodule.utils.TimeUtils.getCurrentTimeInString

/**
 * 剪贴板dbhelper
 */
class ClipBoardDBHelper(private val mHelper: BaseDataProvider) {
    /**
     * 插入一个复制文本内容
     */
    fun insertClipboard(copyContent: String?): Boolean {
        val result: Boolean
        val contentId = checkExist(copyContent)
        if (contentId.isBlank()) {
            deleteOverageItems()
            val contentValues = ContentValues()
            contentValues.put(ClipboardTable.COPY_CONTENT, copyContent)
            val list = ArrayList<InsertParams>()
            val insert = InsertParams(ClipboardTable.TABLE_NAME, contentValues)
            list.add(insert)
            result = mHelper.insert(list)
        } else {
            val list = ArrayList<UpdatePamas>()
            val values = ContentValues()
            values.put(ClipboardTable.CONTENT_ID, contentId)
            values.put(ClipboardTable.COPY_TIME, getCurrentTimeInString(TimeUtils.DEFAULT_DATE_FORMATTER))
            val updatePamas = UpdatePamas(ClipboardTable.TABLE_NAME, values, ClipboardTable.CONTENT_ID + " = ? ", arrayOf(contentId))
            list.add(updatePamas)
            result = mHelper.update(list)
        }
        return result
    }

    /**
     * 更新置顶模式
     */
    fun updateClipboard(copyCotentBean: ClipBoardDataBean): Boolean {
        val result: Boolean
        val contentId = checkExist(copyCotentBean.copyContent)
        if (contentId.isBlank()) {
            deleteOverageItems()
            val contentValues = ContentValues()
            contentValues.put(ClipboardTable.COPY_CONTENT, copyCotentBean.copyContent)
            contentValues.put(ClipboardTable.IS_KEEP, if(copyCotentBean.isKeep) 1 else 0)
            val list = ArrayList<InsertParams>()
            val insert = InsertParams(ClipboardTable.TABLE_NAME, contentValues)
            list.add(insert)
            result = mHelper.insert(list)
        } else {
            val list = ArrayList<UpdatePamas>()
            val values = ContentValues()
            values.put(ClipboardTable.CONTENT_ID, contentId)
            values.put(ClipboardTable.IS_KEEP, if(copyCotentBean.isKeep) 1 else 0)
            val updatePamas = UpdatePamas(ClipboardTable.TABLE_NAME, values, ClipboardTable.CONTENT_ID + " = ? ", arrayOf(contentId))
            list.add(updatePamas)
            result = mHelper.update(list)
        }
        return result
    }

    fun deleteClipboard(copyCotentBean: ClipBoardDataBean): Boolean {
        val contentId = checkExist(copyCotentBean.copyContent)
        if (contentId.isNotBlank()) {
            val where = ClipboardTable.CONTENT_ID + " = " + copyCotentBean.copyContentId
            mHelper.clearDatabase(ClipboardTable.TABLE_NAME, where)
            return true
        }
        return false
    }

    /**
     * 检查是否存在该内容
     *
     * @param copyContent 查询内容
     * @return 时间
     */
    private fun checkExist(copyContent: String?): String {
        val cursor = mHelper.query(ClipboardTable.TABLE_NAME, null, ClipboardTable.COPY_CONTENT + " = ?", arrayOf(copyContent), null)
        if (null != cursor) {
            try {
                if (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(ClipboardTable.CONTENT_ID)
                    return cursor.getString(index)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor.close()
            }
        }
        return ""
    }

    /**
     * 清空剪贴板内容
     */
    fun clearAllClipBoardContent() {
        mHelper.clearDatabase(ClipboardTable.TABLE_NAME, null)
    }

    /**
     * 查询所有剪贴板内容
     */
    fun getAllClipboardContent(): MutableList<ClipBoardDataBean> {
        val copyContents = mutableListOf<ClipBoardDataBean>()
        val orderBy = ClipboardTable.IS_KEEP + " DESC, "+ ClipboardTable.COPY_TIME + " DESC"
        val cursor: Cursor? = mHelper.query(ClipboardTable.TABLE_NAME, null, null, null, orderBy)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                var index = cursor.getColumnIndex(ClipboardTable.CONTENT_ID)
                val contentId = cursor.getString(index)
                index = cursor.getColumnIndex(ClipboardTable.COPY_CONTENT)
                val copyContent = cursor.getString(index)
                index = cursor.getColumnIndex(ClipboardTable.IS_KEEP)
                val isKeep = cursor.getInt(index)
                val clipBoardDataBean = ClipBoardDataBean(contentId, copyContent, isKeep == 1)
                copyContents.add(clipBoardDataBean)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return copyContents
    }

    private fun deleteOverageItems() {
        val clipboardHistoryLimit = AppPrefs.getInstance().clipboard.clipboardHistoryLimit.getValue()
        val commonWhere = ClipboardTable.CONTENT_ID + " not in (select " + ClipboardTable.CONTENT_ID + " from " + ClipboardTable.TABLE_NAME + " order by " + ClipboardTable.IS_KEEP + " desc, " + ClipboardTable.COPY_TIME + " desc limit " + clipboardHistoryLimit +")"
        mHelper.clearDatabase(ClipboardTable.TABLE_NAME, commonWhere)
    }
}
