package com.ytrewqwert.yetanotherjnovelreader.data.local.database.progress

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ytrewqwert.yetanotherjnovelreader.data.remote.model.ProgressRaw

/**
 * Identifies how much progress the user has made through the part with ID [partId].
 *
 * @property[progress] How far the user has progressed through this part, between 0 and 1.
 * @property[pendingUpload] Indicates whether the progress needs to be saved to the remote server.
 */
@Entity
data class Progress(
    @PrimaryKey val partId: String,
    val progress: Double,
    val pendingUpload: Boolean
) {
    companion object {
        /** Converts the given [progressRaw] into a [Progress]. */
        fun fromProgressRaw(progressRaw: ProgressRaw) = Progress(
            partId = progressRaw.partId,
            progress = progressRaw.completion.toDouble(),
            pendingUpload = false
        )
    }
}