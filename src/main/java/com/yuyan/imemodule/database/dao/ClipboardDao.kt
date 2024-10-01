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
    fun editOrInsertClopboard(copyCotentBean: ClipBoardDataBean?, isKeepCick: Boolean): Boolean {
        return mClipboardDatabaseHelper.editOrInsertClopboard(copyCotentBean!!, isKeepCick)
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
    fun getAllClipboardContent(timestamp: String?): MutableList<ClipBoardDataBean> {
        return mClipboardDatabaseHelper.getAllClipboardContent(timestamp)
    }

    @Synchronized
    fun getAllClipboardContents(timestamp: String?): List<String> {
        return mClipboardDatabaseHelper.getAllClipboardContents(timestamp)
    }

    @Synchronized
    fun getLastClipboardContent():ClipBoardDataBean? {
        return mClipboardDatabaseHelper.getLastClipboardContent();
    }
    @Synchronized
    fun clearAllClipBoardContent() {
        mClipboardDatabaseHelper.clearAllClipBoardContent()
    }
}
