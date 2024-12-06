package com.yuyan.imemodule.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.yuyan.imemodule.db.BaseDao
import com.yuyan.imemodule.db.entry.Clipboard

@Dao
interface ClipboardDao : BaseDao<Clipboard> {

    @Query("select * from clipboard ORDER BY isKeep DESC, time DESC")
    fun getAll(): List<Clipboard>

    @Query("delete from clipboard where content = :content")
    fun deleteByContent(content: String)

    @Query("delete from clipboard")
    fun deleteAll()
}
