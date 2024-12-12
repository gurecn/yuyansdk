package com.yuyan.imemodule.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.yuyan.imemodule.database.BaseDao
import com.yuyan.imemodule.database.entry.UsedSymbol

@Dao
interface UsedSymbolDao : BaseDao<UsedSymbol> {
    @Query("select * from usedSymbol where type = 'symbol' ORDER BY time DESC")
    fun getAllUsedSymbol(): List<UsedSymbol>

    @Query("select * from usedSymbol where type = 'emoji' ORDER BY time DESC")
    fun getAllSymbolEmoji(): List<UsedSymbol>
}
