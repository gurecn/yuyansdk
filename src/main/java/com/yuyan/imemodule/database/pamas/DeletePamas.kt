package com.yuyan.imemodule.database.pamas

import android.content.ContentValues

/**
 * SQL删除参数
 *
 */
class DeletePamas(mTableName: String, mContentValues: ContentValues, val selection: String, val whereArgs: Array<String>) : InsertParams(mTableName, mContentValues) {
    override fun toString(): String {
        return super.toString() + " , mSelection : " + selection
    }
}
