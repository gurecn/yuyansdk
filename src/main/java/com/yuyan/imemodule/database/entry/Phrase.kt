package com.yuyan.imemodule.database.entry

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phrase")
data class Phrase(
    @PrimaryKey
    @ColumnInfo(name = "content")
    var content: String,
    @ColumnInfo(name = "isKeep")
    var isKeep: Int = 0,
    @ColumnInfo(name = "t9")
    var t9: String,
    @ColumnInfo(name = "qwerty")
    var qwerty: String,
    @ColumnInfo(name = "lx17")
    var lx17: String,
    @ColumnInfo(name = "time")
    val time: Long = System.currentTimeMillis(),
)