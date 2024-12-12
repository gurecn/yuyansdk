package com.yuyan.imemodule.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.database.dao.ClipboardDao
import com.yuyan.imemodule.database.dao.PhraseDao
import com.yuyan.imemodule.database.dao.SideSymbolDao
import com.yuyan.imemodule.database.dao.UsedSymbolDao
import com.yuyan.imemodule.database.entry.Clipboard
import com.yuyan.imemodule.database.entry.Phrase
import com.yuyan.imemodule.database.entry.SideSymbol
import com.yuyan.imemodule.database.entry.UsedSymbol
import com.yuyan.imemodule.utils.thread.ThreadPoolUtils

//@Database(entities = [SideSymbol::class, Clipboard::class, UsedSymbol::class], version = 1, exportSchema = false)
@Database(entities = [SideSymbol::class, Clipboard::class, UsedSymbol::class, Phrase::class], version = 2, exportSchema = false)
abstract class DataBaseKT : RoomDatabase() {
    abstract fun sideSymbolDao(): SideSymbolDao
    abstract fun clipboardDao(): ClipboardDao
    abstract fun usedSymbolDao(): UsedSymbolDao
    abstract fun phraseDao(): PhraseDao
    companion object {

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS phrase (content TEXT PRIMARY KEY NOT NULL, isKeep INTEGER NOT NULL, t9 TEXT NOT NULL, qwerty TEXT NOT NULL, lx17 TEXT NOT NULL, time INTEGER NOT NULL)")
            }
        }

        val instance = Room.databaseBuilder(ImeSdkApplication.context, DataBaseKT::class.java, "ime_db")
            .allowMainThreadQueries()
            .addMigrations(MIGRATION_1_2)
            .addCallback(object :Callback(){
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    ThreadPoolUtils.executeSingleton {
                        initDb()
                    }
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    ThreadPoolUtils.executeSingleton {
                        initPhrasesDb()
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

        private fun initPhrasesDb() {  //初始化常用语数据数据
            if(instance.phraseDao().getAll().isNotEmpty())return
            val phrases = listOf(
                Phrase(content = "我的电话是__，常联系。", t9 = "9334", qwerty = "wddh", lx17 = ""),
                Phrase(content = "抱歉，我现在不方便接电话，稍后联系。", t9 = "2799", qwerty = "bqwx", lx17 = ""),
                Phrase(content = "我正在开会，有急事请发短信。", t9 = "9995", qwerty = "wzzk", lx17 = ""),
                Phrase(content = "我很快就到，请稍微等一会儿。", t9 = "9455", qwerty = "whkj", lx17 = ""),
                Phrase(content = "麻烦放驿站，谢谢。", t9 = "6339", qwerty = "mffy", lx17 = ""),
            )
            instance.phraseDao().insertAll(phrases)
        }
    }
}
