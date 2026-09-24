package com.samuel.miformacionctma.sync

import android.content.Context
import android.util.Log
import androidx.work.*
import com.samuel.miformacionctma.data.local.AppDatabase

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.getDatabase(applicationContext)
            val syncManager = SyncManager(applicationContext, db)
            syncManager.syncAll()
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error en SyncWorker: ${e.message}", e)
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "OneTimeSyncWorker"

        fun triggerOneTimeSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                syncRequest
            )
        }
    }
}
