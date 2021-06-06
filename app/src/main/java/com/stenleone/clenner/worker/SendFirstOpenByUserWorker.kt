package com.stenleone.clenner.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.stenleone.clenner.managers.config.Config
import com.stenleone.clenner.managers.config.ConfigService
import com.stenleone.clenner.managers.preferences.SharedPreferences
import com.stenleone.clenner.network.ApiService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SendFirstOpenByUserWorker @AssistedInject constructor(
    private val prefs: SharedPreferences,
    private val config: ConfigService,
    private val apiService: ApiService,
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

        return if (apiService.postUserAppOpen(config.getString(Config.POST_USER_DATA_URL)).isSuccessful) {
            prefs.isSendedDataAfterFirstOpen = true
            Result.success()
        } else {
            Result.failure()
        }

    }

}