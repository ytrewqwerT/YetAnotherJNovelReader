package com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
abstract class VolumeDao : BaseDao<Volume>() {
    @Transaction
    @Query("SELECT * FROM Volume WHERE serieId = :serieId")
    abstract fun getSerieVolumes(serieId: String): Flow<List<VolumeFull>>
}