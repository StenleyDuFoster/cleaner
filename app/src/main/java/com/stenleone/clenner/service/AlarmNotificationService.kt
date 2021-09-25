package com.stenleone.clenner.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.stenleone.clenner.R
import com.stenleone.clenner.receiver.AlarmNotificationReceiver
import com.stenleone.clenner.receiver.CheckStateScreenReceiver
import com.stenleone.clenner.ui.activity.MainActivity
import kotlin.random.Random


class AlarmNotificationService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(AlarmNotificationReceiver(), IntentFilter("PUSH"))
        registerReceiver(CheckStateScreenReceiver(), IntentFilter().apply {
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_SCREEN_OFF)
        })
        start(this)
    }

    fun start(context: Context) {

        val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)

        val builder = NotificationCompat.Builder(context, "FullScreenIntent")
            .setSmallIcon(R.drawable.ic_app_logo)
            .setContentTitle(context.getString(R.string.app_name))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setContentText("Service is running")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setWhen(0)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        with(notificationManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                buildChannel()
            val notification = builder.build()
            startForeground(1, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun NotificationManager.buildChannel() {
        val name = "FullScreenIntentChannel"
        val descriptionText = "This is used to demonstrate the Full Screen Intent"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("FullScreenIntent", name, importance).apply {
            description = descriptionText
        }

        createNotificationChannel(channel)
    }


}