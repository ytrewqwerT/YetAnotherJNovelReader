package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import android.content.Context
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.follow.Follow
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.follow.FollowDao
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.Part
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartDao
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.progress.Progress
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.progress.ProgressDao
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.Serie
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieDao
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.Volume
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeDao
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

    private val serieDao: SerieDao
    private val volumeDao: VolumeDao
    private val partDao: PartDao
    private val followDao: FollowDao
    private val progressDao: ProgressDao

    init {
        val db = PartRoomDatabase.getInstance(appContext)
        serieDao = db.serieDao()
        volumeDao = db.volumeDao()
        partDao = db.partDao()
        followDao = db.followDao()
        progressDao = db.progressDao()
    }

    suspend fun upsertSeries(vararg series: Serie) { serieDao.upsert(*series) }
    suspend fun upsertVolumes(vararg volumes: Volume) { volumeDao.upsert(*volumes) }
    suspend fun upsertParts(vararg parts: Part) { partDao.upsert(*parts) }
    suspend fun upsertProgress(vararg progress: Progress) { progressDao.upsert(*progress) }
    suspend fun insertFollows(vararg follows: Follow) { followDao.insert(*follows) }

    suspend fun deleteFollows(vararg follows: Follow) { followDao.delete(*follows) }

    fun getSeries(): Flow<List<SerieFull>> = serieDao.getAllSeries()
    fun getSerieVolumes(serieId: String): Flow<List<VolumeFull>> = volumeDao.getSerieVolumes(serieId)
    fun getVolumeParts(volumeId: String): Flow<List<PartFull>> = partDao.getVolumeParts(volumeId)
    fun getPartsSince(time: String): Flow<List<PartFull>> = partDao.getPartsSince(time)

    suspend fun getParts(vararg partId: String): List<PartFull> = partDao.getParts(*partId)
}