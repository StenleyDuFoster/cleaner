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
        const val DEF_PUSH_VALUE = "def"

        fun start(context: Context, timeShow: Int) {

            val workRequest = PeriodicWorkRequestBuilder<CreatePushNotificationWorker>(6, TimeUnit.HOURS)
                .setInitialDelay(timeShow.toLong(), TimeUnit.HOURS)
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

        var title = ""
        var subTitle = ""

        when ((0..3).random()) {
            1 -> {
                title = configService.getStringAsync(Config.CLEAN_PUSH_TITLE)
                subTitle = configService.getStringAsync(Config.CLEAN_PUSH_SUB_TITLE)
            }
            2 -> {
                title = configService.getStringAsync(Config.CLEAN_PUSH_TITLE_2)
                subTitle = configService.getStringAsync(Config.CLEAN_PUSH_SUB_TITLE_2)
            }
            3 -> {
                title = configService.getStringAsync(Config.CLEAN_PUSH_TITLE_3)
                subTitle = configService.getStringAsync(Config.CLEAN_PUSH_SUB_TITLE_3)
            }
        }

        if (title == DEF_PUSH_VALUE || subTitle == DEF_PUSH_VALUE) {
            return Result.failure()
        }

        val notifyBuilder = NotificationBuilder(
            context = context,
            title,
            subTitle
        )

        notifyBuilder.createDefaultNotify()

        return Result.success()
    }

}