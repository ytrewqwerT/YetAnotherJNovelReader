package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Serie::class, Volume::class, Part::class],
    version = 1,
    exportSchema = true
)
abstract class PartRoomDatabase : RoomDatabase() {
    abstract fun partDao(): PartDao

    companion object {
        @Volatile
        private var INSTANCE: PartRoomDatabase? = null
        fun getInstance(context: Context): PartRoomDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                PartRoomDatabase::class.java,
                "part_database"
            ).build().also { INSTANCE = it }
        }
    }
}