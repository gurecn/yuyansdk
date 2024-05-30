package com.yuyan.imemodule.manager

import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.database.BaseDatabaseHelper
import com.yuyan.imemodule.database.provider.BaseDataProvider

/**
 * 本地存储相关类的管理
 * @author KongXR
 */
class LocalRepository private constructor() {
    var dataProvider: BaseDataProvider? = null //无需登录的数据库provider类
        private set

    private fun init() {
        val mContext = ImeSdkApplication.context
        val baseDatabaseHelper = BaseDatabaseHelper(mContext)
        dataProvider = BaseDataProvider(baseDatabaseHelper)
    }

    companion object {
        private var sInstance: LocalRepository? = null
        @JvmStatic
        val instance: LocalRepository
            /**
             * 获取实例<br></br>
             */
            get() {
                if (sInstance == null) {
                    sInstance = LocalRepository()
                    sInstance!!.init()
                }
                return sInstance!!
            }
    }
}
