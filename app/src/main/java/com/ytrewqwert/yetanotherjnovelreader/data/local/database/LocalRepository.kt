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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/** Exposes methods for interacting with the local database. */
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
        with(PartRoomDatabase.getInstance(appContext)) {
            serieDao = serieDao()
            volumeDao = volumeDao()
            partDao = partDao()
            followDao = followDao()
            progressDao = progressDao()

        }
    }

    suspend fun updateFollows(vararg follows: Follow) {
        withContext(Dispatchers.IO) { followDao.update(*follows) }
    }

    suspend fun upsertSeries(vararg series: Serie) {
        withContext(Dispatchers.IO) { serieDao.upsert(*series) }
    }
    suspend fun upsertVolumes(vararg volumes: Volume) {
        withContext(Dispatchers.IO) { volumeDao.upsert(*volumes) }
    }
    suspend fun upsertParts(vararg parts: Part) {
        withContext(Dispatchers.IO) { partDao.upsert(*parts) }
    }
    suspend fun upsertProgress(vararg progress: Progress) {
        withContext(Dispatchers.IO) { progressDao.upsert(*progress) }
    }
    suspend fun upsertFollows(vararg follows: Follow) {
        withContext(Dispatchers.IO) { followDao.upsert(*follows) }
    }

    suspend fun deleteFollows(vararg follows: Follow) {
        withContext(Dispatchers.IO) { followDao.delete(*follows) }
    }

    fun getSerie(serieId: String): Flow<SerieFull> = serieDao.getSerie(serieId)
    fun getVolume(volumeId: String): Flow<VolumeFull> = volumeDao.getVolume(volumeId)

    fun getSeries(): Flow<List<SerieFull>> = serieDao.getAllSeries()
    fun getSerieVolumes(serieId: String): Flow<List<VolumeFull>> = volumeDao.getSerieVolumes(serieId)
    fun getVolumeParts(volumeId: String): Flow<List<PartFull>> = partDao.getVolumeParts(volumeId)
    /** Retrieves parts ordered by their release date, newest first. */
    fun getRecentParts(): Flow<List<PartFull>> = partDao.getRecentParts()
    /** Retrieves the next part for the user to read, for each of their followed series. */
    fun getUpNextParts(): Flow<List<PartFull>> = partDao.getUpNextParts()

    suspend fun getVolumes(vararg volumeId: String): List<VolumeFull> = withContext(Dispatchers.IO) {
        volumeDao.getVolumes(*volumeId)
    }
    suspend fun getParts(vararg partId: String): List<PartFull> = withContext(Dispatchers.IO) {
        partDao.getParts(*partId)
    }
    suspend fun getLatestFinishedPart(serieId: String): PartFull? = withContext(Dispatchers.IO) {
        partDao.getLatestFinishedPart(serieId)
    }
    suspend fun getAllFollows(): List<Follow> = withContext(Dispatchers.IO) {
        followDao.getAllFollows()
    }
    suspend fun getProgressPendingUpload(): List<Progress> = withContext(Dispatchers.IO) {
        progressDao.getProgressPendingUpload()
    }
}