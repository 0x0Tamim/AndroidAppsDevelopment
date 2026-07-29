package com.echojournal.app.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.Constraints
import com.echojournal.app.data.AppDatabase
import com.echojournal.app.repository.EntryRepository
import java.util.concurrent.TimeUnit

class DriveSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (!DriveSyncManager.isSignedIn(applicationContext)) return Result.success()
        val repository = EntryRepository(AppDatabase.getInstance(applicationContext).entryDao())
        val ok = DriveSyncManager.syncNow(applicationContext, repository)
        return if (ok) Result.success() else Result.retry()
    }

    companion object {
        private const val WORK_NAME = "echo_journal_drive_sync"

        fun schedulePeriodic(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = PeriodicWorkRequestBuilder<DriveSyncWorker>(30, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request
            )
        }

        fun syncOnceNow(context: Context) {
            val request = androidx.work.OneTimeWorkRequestBuilder<DriveSyncWorker>()
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
