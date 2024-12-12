package com.yuyan.imemodule.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.yuyan.imemodule.database.BaseDao
import com.yuyan.imemodule.database.entry.Clipboard

@Dao
interface ClipboardDao : BaseDao<Clipboard> {

    @Query("select * from clipboard ORDER BY isKeep DESC, time DESC")
    fun getAll(): List<Clipboard>

    @Query("delete from clipboard where content = :content")
    fun deleteByContent(content: String)

    @Query("delete from clipboard")
    fun deleteAll()
}
