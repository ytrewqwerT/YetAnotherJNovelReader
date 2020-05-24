package com.ytrewqwert.yetanotherjnovelreader.data.local.database.follow

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.BaseDao

@Dao
abstract class FollowDao : BaseDao<Follow>() {
    @Transaction
    @Query("SELECT * FROM Follow")
    abstract suspend fun getAllFollows(): List<Follow>
}