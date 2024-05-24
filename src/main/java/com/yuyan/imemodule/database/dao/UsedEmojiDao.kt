package com.yuyan.imemodule.database.dao

import com.yuyan.imemodule.database.helper.UsedEmojiDBHelper
import com.yuyan.imemodule.database.provider.BaseDataProvider

/**
 * 常用表情符号Dao
 * Created by jianghuan
 */
class UsedEmojiDao(dataProvider: BaseDataProvider?) {
    private var mCalllogDatabaseHelper: UsedEmojiDBHelper? = null

    init {
        mCalllogDatabaseHelper = UsedEmojiDBHelper(dataProvider)
    }

    @Synchronized
    fun insertUsedEmoji(character: String?, useTime: Long): Boolean {
        return mCalllogDatabaseHelper!!.insertUsedEmoji(character!!, useTime)
    }

    @get:Synchronized
    val allUsedEmoji: Array<String>
        get() = mCalllogDatabaseHelper!!.allUsedEmoji
}
