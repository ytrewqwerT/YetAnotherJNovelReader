package com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SerieDao : BaseDao<Serie>() {
    @Transaction
    @Query("SELECT * FROM Serie ORDER BY created DESC")
    abstract fun getAllSeries(): Flow<List<SerieFull>>

    @Transaction
    @Query("SELECT * FROM Serie WHERE id = :serieId")
    abstract fun getSerie(serieId: String): Flow<SerieFull>
}