package com.yuyan.imemodule.db.entry

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "side_symbol")
data class SideSymbol(
    @PrimaryKey
    @ColumnInfo(name = "symbolKey")
    val symbolKey: String,
    @ColumnInfo(name = "symbolValue")
    val symbolValue: String,
    @ColumnInfo(name = "type")
    val type: String = "pinyin",
)