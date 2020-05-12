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
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFollows(vararg follows: Follow)

    @Delete
    suspend fun deleteFollows(vararg follows: Follow)

    @Transaction @Query("SELECT * FROM Serie")
    fun getAllSeries(): Flow<List<SerieFull>>
    @Transaction @Query("SELECT * FROM Volume WHERE serieId = :serieId")
    fun getSerieVolumes(serieId: String): Flow<List<VolumeFull>>
    @Transaction @Query("SELECT * FROM Part WHERE volumeId = :volumeId")
    fun getVolumeParts(volumeId: String): Flow<List<PartFull>>
    @Transaction @Query("SELECT * FROM Part WHERE launchDate >= :time")
    fun getPartsSince(time: String): Flow<List<PartFull>>
    @Query("SELECT * FROM Follow")
    fun getAllFollows(): Flow<List<Follow>>

    @Transaction @Query("SELECT * FROM Part WHERE id = :partId")
    suspend fun getParts(vararg partId: String): List<PartFull>
    @Query("SELECT * FROM Follow WHERE serieId = :serieId")
    suspend fun getFollows(vararg serieId: String): List<Follow>
}