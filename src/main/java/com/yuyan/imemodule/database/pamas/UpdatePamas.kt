package com.yuyan.imemodule.database.pamas

import android.content.ContentValues

/**
 * SQL更新参数
 *
 */
class UpdatePamas @JvmOverloads constructor(
    mTableName: String?,
    mContentValues: ContentValues?,
    val selection: String,
    val whereArgs: Array<String?>? = null
) : InsertParams(
    mTableName!!, mContentValues!!
) {

    override fun toString(): String {
        return super.toString() + " , mSelection : " + selection
    }
}
