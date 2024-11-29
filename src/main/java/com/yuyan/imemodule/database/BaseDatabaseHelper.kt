package com.yuyan.imemodule.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.yuyan.imemodule.database.DatabaseUtils.delete
import com.yuyan.imemodule.database.DatabaseUtils.insert
import com.yuyan.imemodule.database.DatabaseUtils.query
import com.yuyan.imemodule.database.DatabaseUtils.rawQuery
import com.yuyan.imemodule.database.DatabaseUtils.update
import com.yuyan.imemodule.database.pamas.DeletePamas
import com.yuyan.imemodule.database.pamas.InsertParams
import com.yuyan.imemodule.database.pamas.UpdatePamas

/**
 * 基础数据库实现类(所有数据库都需要继承该类)
 *
 * @version 1.0.0
 */
class BaseDatabaseHelper private constructor(context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {
    constructor(context: Context) : this(context, DATABASE_NAME, DB_MIN_VERSION)

    @Throws(DatabaseException::class)
    fun insert(list: List<InsertParams>?, userPhone: String): Boolean {
        return insert(this, list, userPhone)
    }

    @Throws(DatabaseException::class)
    fun delete(list: List<DeletePamas>?, userPhone: String?): Boolean {
        return delete(this, list, userPhone!!)
    }

    /**
     * 更新 update
     */
    @Throws(DatabaseException::class)
    fun update(
        tableName: String,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String?>?
    ): Int {
        return update(
            this, tableName, values, selection,
            selectionArgs
        )
    }

    /**
     * 更新 update
     */
    @Throws(DatabaseException::class)
    fun update(list: List<UpdatePamas>?, userPhone: String?): Boolean {
        return update(this, list, userPhone!!)
    }

    /**
     * 查询 query
     */
    fun query(
        tableName: String,
        projection: Array<String?>?,
        selection: String?,
        selectionArgs: Array<String?>?,
        sortOrder: String?
    ): Cursor? {
        return query(
            tableName, projection, selection, selectionArgs, null,
            null, sortOrder
        )
    }

    /**
     * 查询 query
     */
    fun query(
        tableName: String, projection: Array<String?>?,
        selection: String?, selectionArgs: Array<String?>?, groupBy: String?,
        having: String?, sortOrder: String?
    ): Cursor? {
        return query(
            this, tableName, projection, selection,
            selectionArgs, groupBy, having, sortOrder
        )
    }

    /**
     * 通过SQL查询 rawQuery
     */
    fun rawQuery(sql: String, selectionArgs: Array<String?>?): Cursor? {
        return rawQuery(this, sql, selectionArgs)
    }

    /**
     * 查询 query
     */
    fun query(
        tableName: String,
        projection: Array<String?>?,
        selection: String?,
        selectionArgs: Array<String?>?,
        sortOrder: String?,
        limit: String?
    ): Cursor? {
        return query(tableName, projection, selection, selectionArgs, null, null, sortOrder, limit)
    }

    /**
     * 查询 query
     */
    fun query(
        tableName: String,
        projection: Array<String?>?,
        selection: String?,
        selectionArgs: Array<String?>?,
        groupBy: String?,
        having: String?,
        sortOrder: String?,
        limit: String?
    ): Cursor? {
        return query(
            this,
            tableName,
            projection,
            selection,
            selectionArgs,
            groupBy,
            having,
            sortOrder,
            limit
        )
    }

    override fun onCreate(db: SQLiteDatabase) {}
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion != 1 || newVersion != 2) return
    }

    /**
     * 判断某张表是否存在
     *
     * @param tabName 表名
     * @return
     */
    private fun tabIsExist(db: SQLiteDatabase, tabName: String?): Boolean {
        var result = false
        if (tabName == null) {
            return false
        }
        val cursor: Cursor?
        try {
            val sql =
                "select count(*) as c from sqlite_master where type ='table' and name ='" + tabName.trim { it <= ' ' } + "' "
            cursor = db.rawQuery(sql, null)
            if (cursor.moveToNext()) {
                val count = cursor.getInt(0)
                if (count > 0) {
                    result = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    /**
     * 创建表的api，如果是需要登录才能创建的表，需要同时传递userPhone和loginTableList
     * @param unloginTableList  无需登录就能创建的表
     */
    fun createTables(unloginTableList: List<String?>?) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            if (!unloginTableList.isNullOrEmpty()) {
                for (table in unloginTableList) {
                    db.execSQL(table)
                }
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
        }
    }

    companion object {
        /**
         * 数据库名
         */
        private const val DATABASE_NAME = "sdk_security.db"

        /**
         * 数据库版本号
         */
        private const val DB_MIN_VERSION = 2
    }
}
