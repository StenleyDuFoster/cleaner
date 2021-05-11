package com.stenleone.clenner.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.stenleone.clenner.util.notification.NotificationBuilder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
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
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.stenleone.clenner.R
import com.stenleone.clenner.ui.activity.MainActivity
import com.stenleone.clenner.util.extencial.vectorToBitmap

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

        return result
    }


}