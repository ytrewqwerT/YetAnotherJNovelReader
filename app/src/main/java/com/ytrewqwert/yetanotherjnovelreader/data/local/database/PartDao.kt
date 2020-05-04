package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeries(vararg series: Serie)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolumes(vararg volumes: Volume)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParts(vararg parts: Part)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(vararg progress: Progress)

    @Query("SELECT * FROM Serie")
    fun getAllSeries(): Flow<List<Serie>>
    @Query("SELECT * FROM Volume WHERE serieId = :serieId")
    fun getSerieVolumes(serieId: String): Flow<List<Volume>>
    @Transaction
    @Query("SELECT * FROM Part WHERE volumeId = :volumeId")
    fun getVolumeParts(volumeId: String): Flow<List<PartWithProgress>>
    @Transaction
    @Query("SELECT * FROM Part WHERE launchDate >= :time")
    fun getPartsSince(time: String): Flow<List<PartWithProgress>>
}