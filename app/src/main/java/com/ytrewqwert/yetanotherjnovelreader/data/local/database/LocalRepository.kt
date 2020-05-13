package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import android.content.Context
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.follow.Follow
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.Part
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.progress.Progress
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.Serie
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.Volume
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull
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

    suspend fun upsertSeries(vararg series: Serie) { partDao.upsertSeries(*series) }
    suspend fun upsertVolumes(vararg volumes: Volume) { partDao.upsertVolumes(*volumes) }
    suspend fun upsertParts(vararg parts: Part) { partDao.upsertParts(*parts) }
    suspend fun upsertProgress(vararg progress: Progress) { partDao.upsertProgress(*progress) }
    suspend fun insertFollows(vararg follows: Follow) { partDao.insertFollows(*follows) }

    suspend fun deleteFollows(vararg follows: Follow) { partDao.deleteFollows(*follows) }

    fun getSeries(): Flow<List<SerieFull>> = partDao.getAllSeries()
    fun getSerieVolumes(serieId: String): Flow<List<VolumeFull>> = partDao.getSerieVolumes(serieId)
    fun getVolumeParts(volumeId: String): Flow<List<PartFull>> = partDao.getVolumeParts(volumeId)
    fun getPartsSince(time: String): Flow<List<PartFull>> = partDao.getPartsSince(time)

    suspend fun getParts(vararg partId: String): List<PartFull> = partDao.getParts(*partId)
}