package com.yuyan.imemodule.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.yuyan.imemodule.database.BaseDao
import com.yuyan.imemodule.database.entry.Phrase

@Dao
interface PhraseDao : BaseDao<Phrase> {

    @Query("select * from phrase ORDER BY isKeep DESC, time DESC")
    fun getAll(): List<Phrase>

    @Query("select * from phrase  where qwerty = :index or t9 = :index or lx17 = :index ORDER BY isKeep DESC, time DESC")
    fun query(index: String): List<Phrase>

    @Query("delete from phrase where content = :content")
    fun deleteByContent(content: String)

    @Query("select * from phrase where content = :content")
    fun queryByContent(content: String): Phrase

    @Query("delete from phrase")
    fun deleteAll()
}
