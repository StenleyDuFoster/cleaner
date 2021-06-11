package com.stenleone.clenner.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.stenleone.clenner.managers.config.Config
import com.stenleone.clenner.managers.config.ConfigService
import com.stenleone.clenner.util.notification.NotificationBuilder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class CreatePushNotificationWorker @AssistedInject constructor(
    private val configService: ConfigService,
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "CreatePushNotificationWorker"

        fun start(context: Context, timeShow: Int) {

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

        var title = configService.getStringAsync(Config.CLEAN_PUSH_TITLE)
        var subTitle = configService.getStringAsync(Config.CLEAN_PUSH_SUB_TITLE)

        if (title == "def" || subTitle == "def") {
            return Result.failure()
        }

        val notifyBuilder = NotificationBuilder(
            context = context,
            title,
            subTitle
        )

        notifyBuilder.createDefaultNotify()

        return result
    }

}