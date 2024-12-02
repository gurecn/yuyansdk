package com.yuyan.imemodule.database.provider

import android.content.ContentValues
import android.database.Cursor
import android.text.TextUtils
import com.yuyan.imemodule.database.BaseDatabaseHelper
import com.yuyan.imemodule.database.DatabaseException
import com.yuyan.imemodule.database.pamas.DeletePamas
import com.yuyan.imemodule.database.pamas.InsertParams
import com.yuyan.imemodule.database.pamas.UpdatePamas

/**
 * 数据表
 */
class BaseDataProvider(databaseHelper: BaseDatabaseHelper?) {
    private var mLock: Any = Any()
    private var mDBHelper: BaseDatabaseHelper? = databaseHelper

    fun query(table: String, projection: Array<String?>?, selection: String?, selectionArgs: Array<String?>?, sortOrder: String?): Cursor? {
        synchronized(mLock) {
            return mDBHelper!!.query(table, projection, selection, selectionArgs, sortOrder)
        }
    }

    fun query(table: String, projection: Array<String?>?, selection: String?, selectionArgs: Array<String?>?, sortOrder: String?, limit: String?): Cursor? {
        synchronized(mLock) {
            return mDBHelper!!.query(table, projection, selection, selectionArgs, sortOrder, limit)
        }
    }

    fun delete(list: List<DeletePamas>?): Boolean {
        synchronized(mLock) {
            var isSuccess = false
            try {
                isSuccess = mDBHelper!!.delete(list, "")
            } catch (e: DatabaseException) {
                e.printStackTrace()
            }
            return isSuccess
        }
    }

    fun insert(list: List<InsertParams>?): Boolean {
        synchronized(mLock) {
            var isSuccess = false
            try {
                if (mDBHelper != null) {
                    mDBHelper!!.insert(list, "")
                }
                isSuccess = true
            } catch (e: DatabaseException) {
                e.printStackTrace()
            }
            return isSuccess
        }
    }

    /**
     * 更新
     */
    fun update(tableName: String, values: ContentValues?, selection: String?): Boolean {
        synchronized(mLock) {
            var isSuccess = false
            try {
                val count = mDBHelper!!.update(tableName, values, selection, null)
                if (count > 0) {
                    isSuccess = true
                }
            } catch (e: DatabaseException) {
                e.printStackTrace()
            }
            return isSuccess
        }
    }

    /**
     * 更新
     */
    fun update(
        tableName: String,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String?>?
    ): Boolean {
        synchronized(mLock) {
            var isSuccess = false
            try {
                val count = mDBHelper!!.update(tableName, values, selection, selectionArgs)
                if (count > 0) {
                    isSuccess = true
                }
            } catch (e: DatabaseException) {
                e.printStackTrace()
            }
            return isSuccess
        }
    }

    fun update(list: List<UpdatePamas>?): Boolean {
        synchronized(mLock) {
            var isSuccess = false
            try {
                isSuccess = mDBHelper!!.update(list, "")
            } catch (e: DatabaseException) {
                e.printStackTrace()
            }
            return isSuccess
        }
    }

    fun clearDatabase(tableName: String, where: String?) {
        if (mDBHelper != null) {
            try {
                val db = mDBHelper!!.writableDatabase
                if (TextUtils.isEmpty(where)) {
                    db.execSQL("DELETE  FROM $tableName")
                } else {
                    db.execSQL("DELETE  FROM $tableName WHERE $where;")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 创建无需登录的数据表，有新的表时需添加到list中
     * @param unloginTableList
     */
    fun createTable(unloginTableList: List<String?>?) {
        if (mDBHelper != null) {
            mDBHelper!!.createTables(unloginTableList)
        }
    }
}
