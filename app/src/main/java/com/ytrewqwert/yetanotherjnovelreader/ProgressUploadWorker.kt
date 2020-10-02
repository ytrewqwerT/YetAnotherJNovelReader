package com.ytrewqwert.yetanotherjnovelreader

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ytrewqwert.yetanotherjnovelreader.data.Repository

/** Attempts to push all un-uploaded part progress data to the remote server. Retries on failure. */
class ProgressUploadWorker(
    appContext: Context, workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val repository = Repository.getInstance(applicationContext)
        val success = repository.pushProgressPendingUploadToRemote()
        return if (success) Result.success() else Result.retry()
    }
}