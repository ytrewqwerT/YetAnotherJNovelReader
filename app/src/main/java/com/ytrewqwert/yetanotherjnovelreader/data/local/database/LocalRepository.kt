package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import android.content.Context
import kotlinx.coroutines.flow.Flow

class LocalRepository private constructor(appContext: Context) {
    companion object {
        @Volatile
        private var INSTANCE: LocalRepository? = null
        fun getInstance(context: Context): LocalRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: LocalRepository(context.applicationContext).also { INSTANCE = it }
        }
    }

    private val partDao: PartDao = PartRoomDatabase.getInstance(appContext).partDao()

    suspend fun insertSeries(vararg series: Serie) { partDao.insertSeries(*series) }
    suspend fun insertVolumes(vararg volumes: Volume) { partDao.insertVolumes(*volumes) }
    suspend fun insertParts(vararg parts: Part) { partDao.insertParts(*parts) }
    suspend fun insertProgress(vararg progress: Progress) { partDao.insertProgress(*progress) }
    suspend fun insertFollows(vararg follows: Follow) { partDao.insertFollows(*follows) }

    suspend fun deleteFollows(vararg follows: Follow) { partDao.deleteFollows(*follows) }

    fun getSeries(): Flow<List<Serie>> = partDao.getAllSeries()
    fun getSerieVolumes(serieId: String): Flow<List<Volume>> = partDao.getSerieVolumes(serieId)
    fun getVolumeParts(volumeId: String): Flow<List<PartWithProgress>> = partDao.getVolumeParts(volumeId)
    fun getPartsSince(time: String): Flow<List<PartWithProgress>> = partDao.getPartsSince(time)
    fun getFollows(): Flow<List<Follow>> = partDao.getAllFollows()

    suspend fun getParts(vararg partId: String): List<PartWithProgress> = partDao.getParts(*partId)
}