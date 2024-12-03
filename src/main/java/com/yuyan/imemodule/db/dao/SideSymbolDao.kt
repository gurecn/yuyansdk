package com.yuyan.imemodule.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.yuyan.imemodule.db.BaseDao
import com.yuyan.imemodule.db.entry.SideSymbol

@Dao
interface SideSymbolDao : BaseDao<SideSymbol> {
    @Query("select * from side_symbol where symbolKey = :key AND type = :type")
    fun getByKey(key: String, type: String = "pinyin"): SideSymbol?

    @Query("select * from side_symbol where type = 'number'")
    fun getAllSideSymbolNumber(): List<SideSymbol>

    @Query("select * from side_symbol where type = 'pinyin'")
    fun getAllSideSymbolPinyin(): List<SideSymbol>

    @Query("delete from side_symbol where symbolKey = :key AND type = :type")
    fun deleteByKey(key: String, type: String = "pinyin")

    @Query("delete from side_symbol where type = :type")
    fun deleteAll(type: String = "pinyin")

    @Query("update side_symbol set symbolValue =:value where symbolKey =:key AND type = :type")
    fun updateSymbol(key: String, value: String, type: String = "pinyin")
}
