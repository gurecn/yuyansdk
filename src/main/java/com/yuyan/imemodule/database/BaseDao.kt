package com.yuyan.imemodule.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bean: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(bean: List<T>)

    @Delete
    fun delete(bean:T)

    @Update
    fun update(bean: T)
}
