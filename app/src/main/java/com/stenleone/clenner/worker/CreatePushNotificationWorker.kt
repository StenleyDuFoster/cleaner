package com.stenleone.clenner.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.stenleone.clenner.managers.preferences.SharedPreferences
import com.stenleone.clenner.network.ApiService
import com.stenleone.clenner.util.notification.NotificationBuilder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class CreatePushNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "CreatePushNotificationWorker"

        fun start(context: Context) {

            val workRequest = PeriodicWorkRequestBuilder<CreatePushNotificationWorker>(6, TimeUnit.HOURS)
                .setInitialDelay(6, TimeUnit.HOURS)
                .addTag(TAG)

            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork(
                    TAG,
                    ExistingPeriodicWorkPolicy.KEEP,
                    workRequest.build()
                )
        }
    }

    override suspend fun doWork(): Result {

        var result = Result.success()

        val notifyBuilder = NotificationBuilder(
            context = context,
            "title",
            "sub"
        )

        notifyBuilder.createDefaultNotify()

        return result
    }

}