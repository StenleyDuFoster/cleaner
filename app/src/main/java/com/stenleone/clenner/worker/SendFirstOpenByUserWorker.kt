package com.stenleone.clenner.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.stenleone.clenner.managers.preferences.SharedPreferences
import com.stenleone.clenner.network.ApiService
import com.stenleone.clenner.util.notification.NotificationBuilder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SendFirstOpenByUserWorker @AssistedInject constructor(
//    private val apiService: ApiService,
    private val prefs: SharedPreferences,
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "SendFirstOpenByUserWorker"

        fun start(context: Context) {
            val constraints: Constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequest.Builder(SendFirstOpenByUserWorker::class.java)
                .setConstraints(constraints)
                .addTag(TAG)

            WorkManager
                .getInstance(context)
                .enqueue(workRequest.build())
        }
    }

    override suspend fun doWork(): Result {

        var result = Result.success()

        return result
    }

}