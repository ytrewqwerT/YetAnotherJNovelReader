package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import androidx.room.*
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.follow.Follow
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.Part
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.progress.Progress
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.Serie
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.Volume
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull
import kotlinx.coroutines.flow.Flow

@Dao
interface PartDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSeries(vararg series: Serie): LongArray
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVolumes(vararg volumes: Volume): LongArray
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertParts(vararg parts: Part): LongArray
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProgress(vararg progress: Progress): LongArray
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFollows(vararg follows: Follow): LongArray

    @Update
    suspend fun updateSeries(vararg series: Serie)
    @Update
    suspend fun updateVolumes(vararg volumes: Volume)
    @Update
    suspend fun updateParts(vararg parts: Part)
    @Update
    suspend fun updateProgress(vararg progress: Progress)

    @Transaction
    suspend fun upsertSeries(vararg series: Serie) {
        val inserted = insertSeries(*series)
        val toUpdate = series.filterIndexed { i, _ -> inserted[i] == -1L }
        if (inserted.isNotEmpty()) updateSeries(*toUpdate.toTypedArray())
    }
    @Transaction
    suspend fun upsertVolumes(vararg volumes: Volume) {
        val inserted = insertVolumes(*volumes)
        val toUpdate = volumes.filterIndexed { i, _ -> inserted[i] == -1L }
        if (inserted.isNotEmpty()) updateVolumes(*toUpdate.toTypedArray())
    }
    @Transaction
    suspend fun upsertParts(vararg parts: Part) {
        val inserted = insertParts(*parts)
        val toUpdate = parts.filterIndexed { i, _ -> inserted[i] == -1L }
        if (inserted.isNotEmpty()) updateParts(*toUpdate.toTypedArray())
    }
    @Transaction
    suspend fun upsertProgress(vararg progress: Progress) {
        val inserted = insertProgress(*progress)
        val toUpdate = progress.filterIndexed { i, _ -> inserted[i] == -1L }
        if (inserted.isNotEmpty()) updateProgress(*toUpdate.toTypedArray())
    }

    @Delete
    suspend fun deleteFollows(vararg follows: Follow)

    @Transaction @Query("SELECT * FROM Serie")
    fun getAllSeries(): Flow<List<SerieFull>>
    @Transaction @Query("SELECT * FROM Volume WHERE serieId = :serieId")
    fun getSerieVolumes(serieId: String): Flow<List<VolumeFull>>
    @Transaction @Query("SELECT * FROM Part WHERE volumeId = :volumeId ORDER BY seriesPartNum ASC")
    fun getVolumeParts(volumeId: String): Flow<List<PartFull>>
    @Transaction @Query("SELECT * FROM Part WHERE launchDate >= :time ORDER BY launchDate DESC")
    fun getPartsSince(time: String): Flow<List<PartFull>>

    @Transaction @Query("SELECT * FROM Part WHERE id = :partId")
    suspend fun getParts(vararg partId: String): List<PartFull>
}