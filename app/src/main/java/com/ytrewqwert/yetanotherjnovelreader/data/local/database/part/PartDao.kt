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
    @Query("SELECT * FROM Part WHERE EXISTS (" +
            "  SELECT NULL FROM Follow " +
            "  WHERE Part.serieId = Follow.serieId " +
            "  AND Part.seriesPartNum = Follow.nextPartNum" +
            ") " +
            "ORDER BY Part.title ASC")
    abstract fun getUpNextParts(): Flow<List<PartFull>>

    @Query("SELECT * FROM Part WHERE serieId = :serieId AND EXISTS (" +
            "  SELECT NULL FROM Progress " +
            "  WHERE Part.id = Progress.partId " +
            "  AND Progress.progress = 1.0" +
            ") ORDER BY seriesPartNum DESC")
    abstract suspend fun getLatestFinishedPart(serieId: String): PartFull?
}