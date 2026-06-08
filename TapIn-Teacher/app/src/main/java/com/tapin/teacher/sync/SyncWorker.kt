package com.tapin.teacher.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.tapin.teacher.data.repository.AttendanceRepository
import com.tapin.teacher.data.repository.Result
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: AttendanceRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "TapInSyncWorker"
        const val KEY_PROGRESS = "sync_progress"
        const val KEY_SYNCED   = "synced_count"

        fun enqueueOneTime(context: Context) {
            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.KEEP, request)
        }

        fun enqueuePeriodic(context: Context) {
            val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "${WORK_NAME}_periodic",
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }
    }

    override suspend fun doWork(): Result {
        setProgress(workDataOf(KEY_PROGRESS to 0))

        return when (val result = repository.syncPendingRecords { pct ->
            setProgressAsync(workDataOf(KEY_PROGRESS to pct))
        }) {
            is com.tapin.teacher.data.repository.Result.Success -> {
                setProgress(workDataOf(KEY_PROGRESS to 100, KEY_SYNCED to result.data))
                Result.success(workDataOf(KEY_SYNCED to result.data))
            }
            is com.tapin.teacher.data.repository.Result.Error -> {
                if (runAttemptCount < 3) Result.retry()
                else Result.failure(workDataOf("error" to result.message))
            }
        }
    }
}
