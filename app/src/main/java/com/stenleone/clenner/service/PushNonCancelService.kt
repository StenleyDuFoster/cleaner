package com.stenleone.clenner.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.stenleone.clenner.BuildConfig
import com.stenleone.clenner.R
import com.stenleone.clenner.ui.activity.MainActivity
import com.stenleone.clenner.util.notification.NotificationBuilder

class PushNonCancelService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        val notify = createDefaultLayoutBuildNotification().build()
        startForeground(1, notify)
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    fun createNotificationManager(): NotificationManager {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

    fun createDefaultLayoutBuildNotification(): NotificationCompat.Builder {
        val notificationLayout = RemoteViews(BuildConfig.APPLICATION_ID, R.layout.main_notification_lay)

        return NotificationCompat.Builder(applicationContext, "CleanerId")
            .setSmallIcon(R.drawable.ic_app_logo)
            .setCustomContentView(notificationLayout)
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    NotificationBuilder.PUSH_REQUEST_CODE,
                    Intent(applicationContext, MainActivity::class.java),
                    PendingIntent.FLAG_ONE_SHOT
                )
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(false)
    }
}