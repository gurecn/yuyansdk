package com.yuyan.imemodule.database.entry

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usedSymbol")
data class UsedSymbol(
    @PrimaryKey
    @ColumnInfo(name = "symbol")
    var symbol: String,
    @ColumnInfo(name = "type")
    val type: String = "symbol",  // symbol、emoji
    @ColumnInfo(name = "time")
    val time: Long = System.currentTimeMillis(),
)