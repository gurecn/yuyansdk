package com.yuyan.imemodule.database.helper

import android.content.ContentValues
import android.database.Cursor
import android.text.TextUtils
import com.yuyan.imemodule.database.pamas.InsertParams
import com.yuyan.imemodule.database.pamas.UpdatePamas
import com.yuyan.imemodule.database.provider.BaseDataProvider
import com.yuyan.imemodule.database.table.ClipboardTable
import com.yuyan.imemodule.entity.ClipBoardDataBean
import com.yuyan.imemodule.utils.TimeUtils
import com.yuyan.imemodule.utils.TimeUtils.getCurrentTimeInString
import com.yuyan.imemodule.utils.TimeUtils.getTime
import java.util.Calendar
import java.util.Date

/**
 * 剪贴板dbhelper
 *
 * @author KongXR
 */
class ClipBoardDBHelper(private val mHelper: BaseDataProvider) {
    /**
     * 插入一个复制文本内容
     */
    fun insertClipboard(copyContent: String?): Boolean {
        var result = false
        val times = checkExist(copyContent)
        if (times <= 0) {
            deleteOverageItems()
            val contentValues = ContentValues()
            contentValues.put(ClipboardTable.COPY_CONTENT, copyContent)
            val list = ArrayList<InsertParams>()
            val insert = InsertParams(ClipboardTable.TABLE_NAME, contentValues)
            list.add(insert)
            if (!list.isEmpty()) {
                result = mHelper.insert(list)
            }
        }
        return result
    }

    /**
     * 编辑或插入剪贴板某条目
     */
    fun editOrInsertClopboard(copyContentBean: ClipBoardDataBean, isKeepClick: Boolean): Boolean {
        var result = false
        if (TextUtils.isEmpty(copyContentBean.copyContentId)) {
            deleteOverageItems()
            val contentValues = ContentValues()
            contentValues.put(ClipboardTable.COPY_CONTENT, copyContentBean.copyContent)
            contentValues.put(ClipboardTable.COPY_EXTENDS, copyContentBean.copyExtends)
            contentValues.put(ClipboardTable.IS_KEEP, copyContentBean.isKeep)
            val list = ArrayList<InsertParams>()
            val insert = InsertParams(ClipboardTable.TABLE_NAME, contentValues)
            list.add(insert)
            if (!list.isEmpty()) {
                result = mHelper.insert(list)
            }
        } else {
            val list = ArrayList<UpdatePamas>()
            val values = ContentValues()
            values.put(ClipboardTable.COPY_CONTENT, copyContentBean.copyContent)
            if (isKeepClick) { //如果是点击收藏就更新时间，如果是编辑就不修改时间
                values.put(
                    ClipboardTable.COPY_TIME,
                    getCurrentTimeInString(TimeUtils.DEFAULT_DATE_FORMATTER)
                )
            }
            values.put(ClipboardTable.COPY_EXTENDS, copyContentBean.copyExtends)
            values.put(ClipboardTable.IS_KEEP, copyContentBean.isKeep)
            val updatePamas = UpdatePamas(
                ClipboardTable.TABLE_NAME, values,
                ClipboardTable.CONTENT_ID + " = ? ", arrayOf<String?>(copyContentBean.copyContentId)
            )
            list.add(updatePamas)
            if (!list.isEmpty()) {
                result = mHelper.update(list)
            }
        }
        return result
    }

    fun deleteClipboard(copyCotentBean: ClipBoardDataBean): Boolean {
        val time = checkExist(copyCotentBean.copyContent)
        if (time > 0) {
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
    private fun checkExist(copyContent: String?): Int {
        val cursor = mHelper.query(
            ClipboardTable.TABLE_NAME, null,
            ClipboardTable.COPY_CONTENT + " = ?", arrayOf(copyContent), null
        )
        if (null != cursor) {
            try {
                if (cursor.moveToNext()) {
                    return cursor.getInt(cursor.getColumnIndex(ClipboardTable.COPY_TIME))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor.close()
            }
        }
        return 0
    }

    /**
     * 清空剪贴板内容
     */
    fun clearAllClipBoardContent() {
        mHelper.clearDatabase(ClipboardTable.TABLE_NAME, null)
    }

    /**
     * 查询特定数量剪贴板内容
     */
    fun getRecentClipboardContent(maxNumber: Int): List<String> {
        val copyContents = ArrayList<String>()
        val orderBy = ClipboardTable.COPY_TIME + " DESC"
        val cursor =
            mHelper.query(ClipboardTable.TABLE_NAME, null, null, null, orderBy, "0,$maxNumber")
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val copyContent =
                    cursor.getString(cursor.getColumnIndex(ClipboardTable.COPY_CONTENT))
                //				String copyTime = cursor.getString(cursor.getColumnIndex(ClipboardTable.COPY_TIME));
                copyContents.add(copyContent)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return copyContents
    }

    /**
     * 查询所有剪贴板内容
     *
     * @param minTimes 某日期以后的 为空则查全部
     */
    fun getAllClipboardContent(minTimes: String?): List<ClipBoardDataBean> {
        deleteOverTimeItems()
        val copyContents = ArrayList<ClipBoardDataBean>()
        val orderBy = ClipboardTable.COPY_TIME + " DESC"
        val cursor: Cursor?
        cursor = if (!TextUtils.isEmpty(minTimes)) {
            mHelper.query(
                ClipboardTable.TABLE_NAME,
                null,
                ClipboardTable.COPY_TIME + " >= ?",
                arrayOf(minTimes),
                orderBy
            )
        } else {
            mHelper.query(ClipboardTable.TABLE_NAME, null, null, null, orderBy)
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val contentId = cursor.getString(cursor.getColumnIndex(ClipboardTable.CONTENT_ID))
                val copyContent =
                    cursor.getString(cursor.getColumnIndex(ClipboardTable.COPY_CONTENT))
                val copyTime = cursor.getString(cursor.getColumnIndex(ClipboardTable.COPY_TIME))
                val isKeep = cursor.getInt(cursor.getColumnIndex(ClipboardTable.IS_KEEP))
                val copyExtends =
                    cursor.getString(cursor.getColumnIndex(ClipboardTable.COPY_EXTENDS))
                val clipBoardDataBean =
                    ClipBoardDataBean(contentId, copyContent, isKeep == 1, copyExtends)
                copyContents.add(clipBoardDataBean)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return copyContents
    }

    /**
     * 获取所有剪切内容
     * @param minTimes
     * @return
     */
    fun getAllClipboardContents(minTimes: String?): List<String> {
        deleteOverTimeItems()
        val copyContents = ArrayList<String>()
        val orderBy = ClipboardTable.COPY_TIME + " DESC"
        val cursor: Cursor?
        cursor = if (!TextUtils.isEmpty(minTimes)) {
            mHelper.query(
                ClipboardTable.TABLE_NAME,
                null,
                ClipboardTable.COPY_TIME + " >= ?",
                arrayOf(minTimes),
                orderBy
            )
        } else {
            mHelper.query(ClipboardTable.TABLE_NAME, null, null, null, orderBy)
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val copyContent =
                    cursor.getString(cursor.getColumnIndex(ClipboardTable.COPY_CONTENT))
                copyContents.add(copyContent)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return copyContents
    }

    val lastClipboardContent: ClipBoardDataBean?
        /**
         * 查询最后刚复制的一条剪贴板内容
         */
        get() {
            var clipBoardDataBean: ClipBoardDataBean? = null
            val orderBy = ClipboardTable.COPY_TIME + " DESC"
            val cursor: Cursor?
            cursor = mHelper.query(ClipboardTable.TABLE_NAME, null, null, null, orderBy, "0,1")
            if (cursor != null && cursor.moveToFirst()) {
                val contentId = cursor.getString(cursor.getColumnIndex(ClipboardTable.CONTENT_ID))
                val copyContent =
                    cursor.getString(cursor.getColumnIndex(ClipboardTable.COPY_CONTENT))
                //			String copyTime = cursor.getString(cursor.getColumnIndex(ClipboardTable.COPY_TIME));
                val isKeep = cursor.getInt(cursor.getColumnIndex(ClipboardTable.IS_KEEP))
                val copyExtends =
                    cursor.getString(cursor.getColumnIndex(ClipboardTable.COPY_EXTENDS))
                clipBoardDataBean =
                    ClipBoardDataBean(contentId, copyContent, isKeep == 1, copyExtends)
                cursor.close()
            }
            return clipBoardDataBean
        }

    //	/**
    //	 * 测试
    //	 */
    //	public ClipBoardDataBean getTestContent() {
    //		ClipBoardDataBean clipBoardDataBean = null;
    //		String orderBy = ClipboardTable.COPY_TIME + " DESC";
    //		Cursor cursor;
    //		cursor = mHelper.query(ClipboardTable.TABLE_NAME, null, null, null, orderBy, "0,1");
    //		if (cursor != null && cursor.moveToFirst()) {
    //			String contentId = cursor.getString(cursor.getColumnIndex(ClipboardTable.CONTENT_ID));
    //			String copyContent = cursor.getString(cursor.getColumnIndex(ClipboardTable.COPY_CONTENT));
    ////			String copyTime = cursor.getString(cursor.getColumnIndex(ClipboardTable.COPY_TIME));
    //			int isKeep = cursor.getInt(cursor.getColumnIndex(ClipboardTable.IS_KEEP));
    //			String copyExtends = cursor.getString(cursor.getColumnIndex(ClipboardTable.COPY_EXTENDS));
    //			String copyTest = cursor.getString(cursor.getColumnIndex(ClipboardTable.COPY_TEST));
    //			LogUtil.d(TAG, "---getTestContent==" + copyTest);
    //			clipBoardDataBean = new ClipBoardDataBean(contentId, copyContent, isKeep == 1, copyExtends);
    //			cursor.close();
    //		}
    //		return clipBoardDataBean;
    //	}
    private fun deleteOverageItems() {
        val commonWhere =
            ClipboardTable.CONTENT_ID + " not in (select " + ClipboardTable.CONTENT_ID + " from " + ClipboardTable.TABLE_NAME + " order by " + ClipboardTable.COPY_TIME + " desc limit 99)"
        mHelper.clearDatabase(ClipboardTable.TABLE_NAME, commonWhere)
    }

    private fun deleteOverTimeItems() {
        val ca = Calendar.getInstance()
        ca.setTime(Date())
        ca.add(Calendar.DATE, -1)
        val lastDay = ca.time
        val timeoutTime = getTime(lastDay.time, TimeUtils.DEFAULT_DATE_FORMATTER)
        val s =
            ClipboardTable.COPY_TIME + " < '" + timeoutTime + "' and " + ClipboardTable.IS_KEEP + " == 0"
        mHelper.clearDatabase(ClipboardTable.TABLE_NAME, s)
    }
}
