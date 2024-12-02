package com.yuyan.imemodule.manager

import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.database.BaseDatabaseHelper
import com.yuyan.imemodule.database.provider.BaseDataProvider

/**
 * 本地存储相关类的管理
 * @author KongXR
 */
class LocalRepository private constructor() {
    lateinit var dataProvider: BaseDataProvider //无需登录的数据库provider类
        private set

    private fun init() {
        dataProvider = BaseDataProvider(BaseDatabaseHelper(ImeSdkApplication.context))
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
