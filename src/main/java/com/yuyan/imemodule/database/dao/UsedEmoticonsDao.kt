package com.yuyan.imemodule.database.dao

import com.yuyan.imemodule.database.helper.UsedEmoticonsHelper
import com.yuyan.imemodule.database.provider.BaseDataProvider

/**
 * 常用颜文字Dao
 * Created by jianghuan
 */
class UsedEmoticonsDao(dataProvider: BaseDataProvider?) {
    private var mCalllogDatabaseHelper: UsedEmoticonsHelper? = null

    init {
        mCalllogDatabaseHelper = UsedEmoticonsHelper(dataProvider)
    }

    @Synchronized
    fun insertUsedEmoticons(character: String?, useTime: Long): Boolean {
        return mCalllogDatabaseHelper!!.insertUsedEmoticon(character!!, useTime)
    }

    @get:Synchronized
    val allUsedEmoticons: Array<String>
        get() = mCalllogDatabaseHelper!!.allUsedEmoticons
}
