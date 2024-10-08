package com.yuyan.imemodule.database.dao

import com.yuyan.imemodule.database.helper.ClipBoardDBHelper
import com.yuyan.imemodule.database.provider.BaseDataProvider
import com.yuyan.imemodule.entity.ClipBoardDataBean

/**
 * CilpboardDao
 * @author KongXR
 */
class ClipboardDao(dataProvider: BaseDataProvider?) {
    private val mClipboardDatabaseHelper: ClipBoardDBHelper

    init {
        mClipboardDatabaseHelper = ClipBoardDBHelper(dataProvider!!)
    }

    @Synchronized
    fun insertClopboard(copyCotent: String?): Boolean {
        return mClipboardDatabaseHelper.insertClipboard(copyCotent)
    }


    @Synchronized
    fun deleteClipboard(copyCotentBean: ClipBoardDataBean?): Boolean {
        return if(copyCotentBean != null) {
            mClipboardDatabaseHelper.deleteClipboard(copyCotentBean)
        } else {
            false
        }
    }

    @Synchronized
    fun getAllClipboardContent(): MutableList<ClipBoardDataBean> {
        return mClipboardDatabaseHelper.getAllClipboardContent()
    }

    @Synchronized
    fun clearAllClipBoardContent() {
        mClipboardDatabaseHelper.clearAllClipBoardContent()
    }
}
