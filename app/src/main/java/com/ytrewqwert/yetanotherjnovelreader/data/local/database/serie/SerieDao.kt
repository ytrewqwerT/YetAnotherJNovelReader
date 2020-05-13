package com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SerieDao : BaseDao<Serie>() {
    @Transaction
    @Query("SELECT * FROM Serie")
    abstract fun getAllSeries(): Flow<List<SerieFull>>
}