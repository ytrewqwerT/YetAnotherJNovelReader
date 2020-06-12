package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import androidx.room.*

/** An abstract [Dao] class defining some basic operations for an [Entity] of type [T]. */
abstract class BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(vararg items: T): LongArray

    @Update
    abstract suspend fun update(vararg items: T)

    @Transaction
    open suspend fun upsert(vararg items: T) {
        // Array<T>.filter() returns a List<T>, and converting List<T> to Array<T> is a pain, so
        // don't bother filtering existing items to apply update() on and instead just apply update
        // on everything (before insert) since updating does nothing if the item doesn't exist
        update(*items)
        insert(*items)
    }

    @Delete
    abstract suspend fun delete(vararg items: T)
}