package com.ytrewqwert.yetanotherjnovelreader.data.local.database.part

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.BaseDao

import kotlinx.coroutines.flow.Flow

@Dao
abstract class PartDao : BaseDao<Part>() {
    @Transaction
    @Query("SELECT * FROM Part WHERE id = :partId")
    abstract suspend fun getParts(vararg partId: String): List<PartFull>

    @Transaction
    @Query("SELECT * FROM Part ORDER BY launchDate DESC")
    abstract fun getRecentParts(): Flow<List<PartFull>>

    @Transaction
    @Query("SELECT * FROM Part WHERE volumeId = :volumeId ORDER BY seriesPartNum ASC")
    abstract fun getVolumeParts(volumeId: String): Flow<List<PartFull>>

    @Transaction
    @Query("SELECT * FROM Part WHERE EXISTS ( " +
            "  SELECT NULL FROM Follow " +
            "  WHERE Part.serieId = Follow.serieId " +
            "  AND Part.seriesPartNum = Follow.nextPartNum" +
            ") " +
            "ORDER BY Part.seriesPartNum DESC")
    abstract fun getUpNextParts(): Flow<List<PartFull>>
}