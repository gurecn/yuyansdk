package com.yuyan.imemodule.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.db.dao.SideSymbolDao
import com.yuyan.imemodule.db.entry.SideSymbol
import com.yuyan.imemodule.utils.thread.ThreadPoolUtils

@Database(entities = [SideSymbol::class], version = 1, exportSchema = false)
abstract class DataBaseKT : RoomDatabase() {
    abstract fun sideSymbolDao(): SideSymbolDao
    companion object {
        val instance = Room.databaseBuilder(ImeSdkApplication.context, DataBaseKT::class.java, "ime_db")
            .allowMainThreadQueries()
            .addCallback(object :Callback(){
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    ThreadPoolUtils.executeSingleton {
                        initDb()
                    }
                }
            })
            .build()
        private fun initDb() {  //初始化数据库数据
            val symbolPinyin = listOf("，", "。", "？", "！", "……", "：", "；", ".").map {  symbolKey->
                SideSymbol(symbolKey, symbolKey)
            }
            instance.sideSymbolDao().insertAll(symbolPinyin)
            val symbolNumber = listOf("%", "/", "-", "+", "*", "#", "@").map {  symbolKey->
                SideSymbol(symbolKey, symbolKey, "number")
            }
            instance.sideSymbolDao().insertAll(symbolNumber)
        }
    }
}
