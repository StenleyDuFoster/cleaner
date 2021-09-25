package com.stenleone.clenner.receiver

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.stenleone.clenner.R
import com.stenleone.clenner.ui.activity.MainActivity
import kotlin.random.Random


class AlarmNotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val isPhoneLocked = isPhoneLocked(context)

        if (intent.action == "PUSH") {
            if (isPhoneLocked) {
                setIsWaiting(context, true)
                context.startService(Intent(context, CheckStateScreenReceiver::class.java))
            } else {
                start(context)
            }
        }
    }


    private fun setIsWaiting(context: Context, flag: Boolean) {
        val mSettings = context.getSharedPreferences("NOTIFY_PRESENT", Context.MODE_PRIVATE)

        val editor = mSettings.edit()
        editor.putBoolean("isWaiting", flag)
        editor.apply()
    }

    private fun isPhoneLocked(context: Context): Boolean {
        val mSettings = context.getSharedPreferences("NOTIFY_PRESENT", Context.MODE_PRIVATE)

        return mSettings.getBoolean("isLocked", false)
    }

    fun start(context: Context) {

        val notificationLayout = setNotificationLayout(context)

        val builder = NotificationCompat.Builder(context, "FullScreenIntent")
            .setSmallIcon(R.drawable.ic_app_logo)
            .setContentTitle(context.getString(R.string.app_name))
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContent(notificationLayout)
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)
            .setCustomHeadsUpContentView(notificationLayout)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAllowSystemGeneratedContextualActions(false)
            .setOngoing(true)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(context.getFullScreenPendingIntent2(), true)
            .setContentIntent(context.getFullScreenPendingIntent())

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        with(notificationManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                buildChannel()
            val notification = builder.build()
            notify(999, notification)
        }
    }

    private fun setNotificationLayout(context: Context): RemoteViews {

        val notificationLayout: RemoteViews = when(Random.nextInt(3)){
            0 -> {
                RemoteViews(context.packageName, R.layout.alarm_notification)
            }
            1  -> {
                RemoteViews(context.packageName, R.layout.alarm_notification2)

            }
            else -> {
                RemoteViews(context.packageName, R.layout.alarm_notification3)

            }
        }

        when(Random.nextInt(3)){
            0 -> {
                notificationLayout.setTextViewText(R.id.textNotify, context.getString(R.string.text_alarm_notification_1))
            }
            1  -> {
                notificationLayout.setTextViewText(R.id.textNotify, context.getString(R.string.text_alarm_notification_2))
            }
            2 -> {
                notificationLayout.setTextViewText(R.id.textNotify, context.getString(R.string.text_alarm_notification_3))
            }
        }

        return notificationLayout
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
    private fun Context.getFullScreenPendingIntent(): PendingIntent {
        //intent to activity from click on notification
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun Context.getFullScreenPendingIntent2(): PendingIntent {
        val intent = Intent()
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}