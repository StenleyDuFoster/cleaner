package com.stenleone.clenner.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import com.stenleone.clenner.R
import com.stenleone.clenner.ui.activity.MainActivity
import kotlin.random.Random

class CleanAlertNotification {
    companion object {
        private const val LAST_ALARM_NOTIFICATION_IS_CLOSED = "LAST_ALARM_NOTIFICATION_IS_CLOSED"
        private const val LAST_ALARM_NOTIFICATION_STYLE = "LAST_ALARM_NOTIFICATION_STYLE"
        private const val NOTIFICATION_STYLE_LAYOUT = "layoutRes"
        private const val NOTIFICATION_STYLE_TEXT = "textRes"
        private const val ALERT_PUSH_CODE = 999

        fun createOnDismissIntent(
            context: Context,
            notificationId: Int
        ): PendingIntent {
            val intent = Intent(context, NotificationDismissReceiver::class.java)
            intent.putExtra("com.stenleone.clenner:notificationId", notificationId)
            return PendingIntent.getBroadcast(
                context.applicationContext,
                notificationId, intent, PendingIntent.FLAG_ONE_SHOT
            )
        }

        fun getLastAlarmNotificationIsClosed(context: Context): Boolean {
            return context.getSharedPreferences(NOTIFY_STORAGE, Context.MODE_PRIVATE).getBoolean(
                LAST_ALARM_NOTIFICATION_IS_CLOSED, true)
        }

        fun getLastAlarmNotificationStyleInt(context: Context, suffix: String): Int {
            return context.getSharedPreferences(NOTIFY_STORAGE, Context.MODE_PRIVATE).getInt(
                LAST_ALARM_NOTIFICATION_STYLE + suffix, 0)
        }

        fun setLastAlarmNotificationIsClosed(context: Context, value: Boolean) {
            context.getSharedPreferences(NOTIFY_STORAGE, Context.MODE_PRIVATE).edit {
                putBoolean(LAST_ALARM_NOTIFICATION_IS_CLOSED, value)
            }
        }

        fun setLastAlarmNotificationStyleInt(context: Context, suffix: String, value: Int) {
            if (suffix.isNotEmpty())
                context.getSharedPreferences(NOTIFY_STORAGE, Context.MODE_PRIVATE).edit {
                    putInt(LAST_ALARM_NOTIFICATION_STYLE + suffix, value)
                }
        }

        fun send(context: Context, resetLastStyle: Boolean) {
            val layoutRes = if (resetLastStyle)
                when(Random.nextInt(3)) {
                    0 -> R.layout.alarm_notification_0
                    1  -> R.layout.alarm_notification_1
                    else -> R.layout.alarm_notification_2
                } else getLastAlarmNotificationStyleInt(context, NOTIFICATION_STYLE_LAYOUT)

            val textRes = if (resetLastStyle)
                when(Random.nextInt(3)) {
                    0 -> R.string.text_alarm_notification_1
                    1  -> R.string.text_alarm_notification_2
                    else -> R.string.text_alarm_notification_3
                } else getLastAlarmNotificationStyleInt(context, NOTIFICATION_STYLE_TEXT)

            if (layoutRes == ResourcesCompat.ID_NULL || textRes == ResourcesCompat.ID_NULL) {
                send(context, true)
                return
            }

            Log.d("test", "NOTIFICATION_STYLE_LAYOUT=$layoutRes")
            Log.d("test", "NOTIFICATION_STYLE_TEXT=$textRes")

            setLastAlarmNotificationStyleInt(context, NOTIFICATION_STYLE_LAYOUT, layoutRes)
            setLastAlarmNotificationStyleInt(context, NOTIFICATION_STYLE_TEXT, textRes)

            Log.d("test", "Send AlertNotification, useLastStyle=$resetLastStyle")

            val notificationLayout = RemoteViews(context.packageName, layoutRes).apply {
                setTextViewText(R.id.textNotify, context.getString(textRes))
            }

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
                .setDeleteIntent(createOnDismissIntent(context, ALERT_PUSH_CODE))
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setFullScreenIntent(context.getFullScreenPendingIntent2(), true)
                .setContentIntent(context.getFullScreenPendingIntent())

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            with(notificationManager) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    buildChannel()
                val notification = builder.build()
                notify(ALERT_PUSH_CODE, notification)
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

        private fun Context.getFullScreenPendingIntent(): PendingIntent {
                val intent = MainActivity.newInstant(this, true)
            return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        private fun Context.getFullScreenPendingIntent2(): PendingIntent {
            val intent = MainActivity.newInstant(this, true)
            return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}