package com.ytrewqwert.yetanotherjnovelreader.data.local.database.progress

import androidx.room.Dao
import androidx.room.Query
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.BaseDao

@Dao
abstract class ProgressDao : BaseDao<Progress>() {
    @Query("SELECT * FROM Progress WHERE pendingUpload = 1")
    abstract suspend fun getProgressPendingUpload(): List<Progress>
}