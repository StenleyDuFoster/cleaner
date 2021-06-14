package com.stenleone.clenner.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color.RED
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.stenleone.clenner.R
import com.stenleone.clenner.ui.activity.MainActivity
import com.stenleone.clenner.util.extencial.vectorToBitmap
import com.stenleone.clenner.util.notification.NotificationBuilder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class CreateOrUpdateNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "CreateOrUpdateNotificationWorker"

        fun start(context: Context) {

            val workRequest = PeriodicWorkRequestBuilder<CreateOrUpdateNotificationWorker>(15, TimeUnit.MINUTES)
                .setInitialDelay(0, TimeUnit.MINUTES)
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

        var result = Result.success()

        val notifyBuilder = NotificationBuilder(
            context = context,
            "title",
            "sub"
        )

        notifyBuilder.createCleanNotify()
        createDefaultNotify()

        return result
    }

    private fun sendNotification(id: Int) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("NOTIFICATION_ID", id)

        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val bitmap = applicationContext.vectorToBitmap(com.stenleone.clenner.R.drawable.ic_app_logo)
        val titleNotification = "applicationContext.getString(R.string.notification_title)"
        val subtitleNotification = "applicationContext.getString(R.string.notification_subtitle)"
        val pendingIntent = getActivity(applicationContext, 0, intent, 0)
        val notification = NotificationCompat.Builder(applicationContext, "NOTIFICATION_CHANNEL")
            .setLargeIcon(bitmap).setSmallIcon(com.stenleone.clenner.R.drawable.ic_app_logo)
            .setContentTitle(titleNotification).setContentText(subtitleNotification)
            .setDefaults(DEFAULT_ALL).setContentIntent(pendingIntent).setAutoCancel(true)

        notification.priority = PRIORITY_MAX

        if (SDK_INT >= O) {
            notification.setChannelId("NOTIFICATION_CHANNEL")

            val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
                .setContentType(CONTENT_TYPE_SONIFICATION).build()

            val channel =
                NotificationChannel("NOTIFICATION_CHANNEL", "NOTIFICATION_NAME", IMPORTANCE_HIGH)

            channel.enableLights(true)
            channel.lightColor = RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtoneManager, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(id, notification.build())
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

    fun createDefaultBuildNotification(): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, "CleanerId")
            .setContentTitle("title")
            .setContentText("subTitle")
            .setStyle(NotificationCompat.BigTextStyle().bigText("bigText"))
            .setSmallIcon(R.drawable.ic_app_logo)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(500, 500, 500, 500))
            .setAutoCancel(false)
    }

    fun createDefaultNotify() {
        val notifyManager = createNotificationManager()
        val buildNotify = createDefaultBuildNotification()
        notifyManager.notify(NotificationBuilder.DEFAULT_NOTIFY_ID, buildNotify.build())
    }


}