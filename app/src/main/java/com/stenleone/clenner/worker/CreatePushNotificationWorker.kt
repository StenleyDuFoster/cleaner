package com.stenleone.clenner.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.stenleone.clenner.BuildConfig
import com.stenleone.clenner.R
import com.stenleone.clenner.managers.config.Config
import com.stenleone.clenner.managers.config.ConfigService
import com.stenleone.clenner.ui.activity.MainActivity
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

            val workRequest = PeriodicWorkRequestBuilder<CreatePushNotificationWorker>(timeShow.toLong(), TimeUnit.HOURS)
                .addTag(TAG)

            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork(
                    TAG,
                    ExistingPeriodicWorkPolicy.REPLACE,
                    workRequest.build()
                )
        }
    }

    override suspend fun doWork(): Result {

        var title = ""
        var subTitle = ""

        when ((1..3).random()) {
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

//        notifyBuilder.createDefaultLayoutNotify()
        createNotificationManager().notify(NotificationBuilder.DEFAULT_NOTIFY_ID, createDefaultLayoutBuildNotification(title, subTitle).build())

        return Result.success()
    }


    fun createNotificationManager(): NotificationManager {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "CleanerId",
                    "CleanerChannel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
        return notificationManager
    }

    fun createDefaultLayoutBuildNotification(title: String, subTitle: String): NotificationCompat.Builder {
        val notificationLayout = RemoteViews(BuildConfig.APPLICATION_ID, R.layout.notification_lay)
        notificationLayout.setTextViewText(R.id.title, title)
        notificationLayout.setTextViewText(R.id.subTitle, subTitle)

        return NotificationCompat.Builder(context, "CleanerId")
            .setSmallIcon(R.drawable.ic_app_logo)
            .setCustomContentView(notificationLayout)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    NotificationBuilder.PUSH_REQUEST_CODE,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_ONE_SHOT
                )
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(500, 500, 500, 500))
            .setAutoCancel(false)
    }

}