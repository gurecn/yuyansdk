package com.yuyan.imemodule.database.entry

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "side_symbol")
data class SideSymbol(
    @PrimaryKey
    @ColumnInfo(name = "symbolKey")
    var symbolKey: String,
    @ColumnInfo(name = "symbolValue")
    var symbolValue: String,
    @ColumnInfo(name = "type")
    val type: String = "pinyin",
)