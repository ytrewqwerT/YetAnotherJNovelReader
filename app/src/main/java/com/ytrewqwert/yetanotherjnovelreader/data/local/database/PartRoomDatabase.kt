package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Serie::class, Volume::class, Part::class, Progress::class, Follow::class],
    version = 2,
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
            ).addMigrations(MIGRATION_1_2).build().also { INSTANCE = it }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE 'Follow' ('serieId' TEXT NOT NULL, PRIMARY KEY('serieId'))"
                )
            }
        }
    }
}