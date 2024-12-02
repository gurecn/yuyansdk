package com.yuyan.imemodule.database.dao

import com.yuyan.imemodule.database.helper.UsedChararterDBHelper
import com.yuyan.imemodule.database.provider.BaseDataProvider

/**
 * 常用符号Dao
 * Created by jianghuan
 */
class UsedCharacterDao(dataProvider: BaseDataProvider) {
    private var mCalllogDatabaseHelper: UsedChararterDBHelper

    init {
        mCalllogDatabaseHelper = UsedChararterDBHelper(dataProvider)
    }

    @Synchronized
    fun insertUsedCharacter(character: String?, useTime: Long): Boolean {
        return mCalllogDatabaseHelper.insertUsedCharacter(character!!, useTime)
    }

    @get:Synchronized
    val allUsedCharacter: Array<String>
        get() = mCalllogDatabaseHelper.allUsedCharacter
}
