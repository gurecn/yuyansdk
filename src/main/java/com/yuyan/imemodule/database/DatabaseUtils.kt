package com.yuyan.imemodule.database

import android.content.ContentValues
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.yuyan.imemodule.database.pamas.DeletePamas
import com.yuyan.imemodule.database.pamas.InsertParams
import com.yuyan.imemodule.database.pamas.UpdatePamas

/**
 * 数据库工具类(封装共有方法及操作)
 * @version 1.0.0
 */
object DatabaseUtils {
    @Throws(DatabaseException::class)
    @JvmStatic
    fun insert(
        sqLiteOpenHelper: SQLiteOpenHelper,
        list: List<InsertParams>?,
        userPhone: String
    ): Boolean {
        var isSucces = false
        if (!list.isNullOrEmpty()) {
            var db: SQLiteDatabase? = null
            try {
                db = sqLiteOpenHelper.writableDatabase
                db.beginTransaction()
                for (instertParam in list) {
                    db.insert(instertParam.tableName + userPhone, null, instertParam.contentValues)
                }
                db.setTransactionSuccessful()
                isSucces = true
            } catch (e: Exception) {
                throw DatabaseException(e)
            } finally {
                if (null != db) {
                    try {
                        db.endTransaction()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        return isSucces
    }

    @Throws(DatabaseException::class)
    fun insert(
        sqLiteOpenHelper: SQLiteOpenHelper,
        tableName: String?,
        initialValues: ContentValues?
    ): Long {
        val rowId: Long = try {
            val db = sqLiteOpenHelper.writableDatabase
            db.insert(tableName, null, initialValues)
        } catch (e: Exception) {
            throw DatabaseException(e)
        }
        return rowId
    }

    /**
     * 批量删除
     */
    @Throws(DatabaseException::class)
    @JvmStatic
    fun delete(
        sqLiteOpenHelper: SQLiteOpenHelper,
        list: List<DeletePamas>?,
        userPhone: String
    ): Boolean {
        var isSucces = false
        if (!list.isNullOrEmpty()) {
            var db: SQLiteDatabase? = null
            try {
                db = sqLiteOpenHelper.writableDatabase
                db.beginTransaction()
                for (deletePamas in list) {
                    db.delete(
                        deletePamas.tableName + userPhone,
                        deletePamas.selection,
                        deletePamas.whereArgs
                    )
                }
                db.setTransactionSuccessful()
                isSucces = true
            } catch (e: Exception) {
                throw DatabaseException(e)
            } finally {
                if (null != db) {
                    try {
                        db.endTransaction()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        return isSucces
    }

    @Throws(DatabaseException::class)
    fun delete(
        sqLiteOpenHelper: SQLiteOpenHelper,
        tableName: String?,
        selection: String?,
        selectionArgs: Array<String?>?
    ): Int {
        val count: Int = try {
            val db = sqLiteOpenHelper.writableDatabase
            db.delete(tableName, selection, selectionArgs)
        } catch (e: Exception) {
            throw DatabaseException(e)
        }
        return count
    }

    @JvmStatic
	@Throws(DatabaseException::class)
    fun update(
        sqLiteOpenHelper: SQLiteOpenHelper,
        tableName: String?,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String?>?
    ): Int {
        val count: Int = try {
            val db = sqLiteOpenHelper.writableDatabase
            db.update(tableName, values, selection, selectionArgs)
        } catch (e: Exception) {
            throw DatabaseException(e)
        }
        return count
    }

    @Throws(DatabaseException::class)
    @JvmStatic
    fun update(
        sqLiteOpenHelper: SQLiteOpenHelper,
        list: List<UpdatePamas>?,
        userPhone: String
    ): Boolean {
        var isSucces = false
        if (!list.isNullOrEmpty()) {
            var db: SQLiteDatabase? = null
            try {
                db = sqLiteOpenHelper.writableDatabase
                db.beginTransaction()
                for (updatePamas in list) {
                    db.update(
                        updatePamas.tableName + userPhone,
                        updatePamas.contentValues,
                        updatePamas.selection,
                        updatePamas.whereArgs
                    )
                }
                db.setTransactionSuccessful()
                isSucces = true
            } catch (e: Exception) {
                throw DatabaseException(e)
            } finally {
                if (null != db) {
                    try {
                        db.endTransaction()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        return isSucces
    }

    @JvmStatic
	fun rawQuery(
        sqLiteOpenHelper: SQLiteOpenHelper,
        sql: String?,
        selectionArgs: Array<String?>?
    ): Cursor? {
        var result: Cursor? = null
        try {
            val db = sqLiteOpenHelper.readableDatabase
            result = db.rawQuery(sql, selectionArgs)
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        return result
    }

    /**
     * 用于单表查询
     */
	@JvmStatic
	fun query(
        sqLiteOpenHelper: SQLiteOpenHelper,
        tableName: String?,
        projection: Array<String?>?,
        selection: String?,
        selectionArgs: Array<String?>?,
        groupBy: String?,
        having: String?,
        sortOrder: String?
    ): Cursor? {
        var result: Cursor? = null
        try {
            val db = sqLiteOpenHelper.readableDatabase
            result = db.query(
                tableName,
                projection,
                selection,
                selectionArgs,
                groupBy,
                having,
                sortOrder
            )
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        return result
    }

    /**
     * 用于单表查询
     */
	@JvmStatic
	fun query(
        sqLiteOpenHelper: SQLiteOpenHelper,
        tableName: String?,
        projection: Array<String?>?,
        selection: String?,
        selectionArgs: Array<String?>?,
        groupBy: String?,
        having: String?,
        sortOrder: String?,
        limit: String?
    ): Cursor? {
        var result: Cursor? = null
        try {
            val db = sqLiteOpenHelper.readableDatabase
            result = db.query(
                tableName,
                projection,
                selection,
                selectionArgs,
                groupBy,
                having,
                sortOrder,
                limit
            )
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        return result
    }
}
