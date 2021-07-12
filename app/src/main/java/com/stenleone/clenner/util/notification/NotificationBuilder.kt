package com.stenleone.clenner.util.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.stenleone.clenner.BuildConfig
import com.stenleone.clenner.R
import com.stenleone.clenner.ui.activity.MainActivity

class NotificationBuilder(
    private val context: Context,
    private var title: String = "",
    private var subTitle: String = "",
    private var bigText: String = "",
    private val channelId: String = "CleanerId",
    private val channelName: String = "CleanerChannel"
) {

    companion object {
        const val DEFAULT_NOTIFY_ID = 105
        const val LAYOUT_NOTIFY_ID = 106
        const val PUSH_REQUEST_CODE = 3409
    }

    private val resultIntent = Intent(context, MainActivity::class.java)

    init {
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    fun createNotificationManager(): NotificationManager {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
        return notificationManager
    }

    fun createDefaultBuildNotification(): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(subTitle)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setSmallIcon(R.drawable.ic_app_logo)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    PUSH_REQUEST_CODE,
                    resultIntent,
                    PendingIntent.FLAG_ONE_SHOT
                )
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(500, 500, 500, 500))
            .setAutoCancel(false)
    }

    fun createDefaultLayoutBuildNotification(): NotificationCompat.Builder {
        val notificationLayout = RemoteViews(BuildConfig.APPLICATION_ID, R.layout.notification_lay)
        notificationLayout.setTextViewText(R.id.title, title)
        notificationLayout.setTextViewText(R.id.subTitle, subTitle)

        return NotificationCompat.Builder(context, channelId)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setSmallIcon(R.drawable.ic_app_logo)
            .setCustomContentView(notificationLayout)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    PUSH_REQUEST_CODE,
                    resultIntent,
                    PendingIntent.FLAG_ONE_SHOT
                )
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(500, 500, 500, 500))
            .setAutoCancel(false)
    }

    fun createLayoutBuildNotification(): NotificationCompat.Builder {

        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_lay)
//        notificationLayout.setImageViewResource(R.id.notification_image, R.drawable.ic_app_logo)
//        notificationLayout.setTextViewText(R.id.notification_title, "1")
//        notificationLayout.setTextViewText(R.id.notification_text, "12313")
        notificationLayout.setInt(R.id.notification_root, "setBackgroundColor", ContextCompat.getColor(context, R.color.black))
//        notificationLayout.setOnClickPendingIntent(R.id.rootView, PendingIntent.getActivity(
//            context,
//            PUSH_REQUEST_CODE,
//            resultIntent,
//            PendingIntent.FLAG_ONE_SHOT
//        ))

        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_app_logo)
            .setContent(notificationLayout)
            .setAutoCancel(false)
            .setWhen(0)
            .setSound(null)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) NotificationManager.IMPORTANCE_HIGH else Notification.PRIORITY_MAX)
//            .setContentIntent(PendingIntent.getActivity(context, notification.id, notification.intent(context), PendingIntent.FLAG_ONE_SHOT))
    }

    fun createDefaultNotify() {
        val notifyManager = createNotificationManager()
        val buildNotify = createDefaultBuildNotification()
        notifyManager.notify(DEFAULT_NOTIFY_ID, buildNotify.build())
    }

    fun createDefaultLayoutNotify() {
        val notifyManager = createNotificationManager()
        val buildNotify = createDefaultLayoutBuildNotification()
        notifyManager.notify(DEFAULT_NOTIFY_ID, buildNotify.build())
    }

    fun createCleanNotify() {
        val notifyManager = createNotificationManager()
        val buildNotify = createLayoutBuildNotification()
        notifyManager.notify(LAYOUT_NOTIFY_ID, buildNotify.build())
    }

    fun createNotify(notifyId: Int, notifyManager: NotificationManager, buildNotify: NotificationCompat.Builder) {
        notifyManager.notify(notifyId, buildNotify.build())
    }
}