package com.yuyan.imemodule.database.pamas

import android.content.ContentValues

/**
 * SQL插入参数
 * @version 1.0.0
 */
open class InsertParams(val tableName: String?, val contentValues: ContentValues?) {

    override fun toString(): String {
        return "mTableName : " + tableName +
                "mContentValues : " + contentValues.toString()
    }
}
