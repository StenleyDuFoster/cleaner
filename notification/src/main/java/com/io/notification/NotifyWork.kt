package com.io.notification

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
import android.graphics.PixelFormat
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.*
import androidx.core.content.ContextCompat
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotifyWork(var context: Context, params: WorkerParameters) : Worker(context, params) {

    override  fun doWork(): Result {

        val id = inputData.getLong(NOTIFICATION_ID, 0).toInt()
//        sendNotification(id, context)
        return success()
    }

    private fun sendNotification(id: Int, context: Context) {
        var windowManager = context.getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager

        val valetModeWindow =
            View.inflate(context, R.layout.layout_test, null) as ViewGroup
        val LAYOUT_FLAG: Int
        LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.TOP
        params.x = 0
        params.y = 0
        windowManager.addView(valetModeWindow, params)

        // till here

//        val intent = Intent(
//            applicationContext,
//            Class.forName("com.stenleone.clenner.ui.activity.MainActivity")
//        )
//        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
//        intent.putExtra(NOTIFICATION_ID, id)
//
//        val notificationManager =
//            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        val notificationLayoutExpanded =
//            RemoteViews(applicationContext.packageName, R.layout.layout_notification)
//
//        val bitmap = applicationContext.vectorToBitmap(R.drawable.ic_launcher_foreground)
//        val pendingIntent = getActivity(applicationContext, 0, intent, 0)
//
//        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
//            .setLargeIcon(bitmap)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setAutoCancel(true)
//            .setPriority(PRIORITY_MAX)
//            .setCustomContentView(notificationLayoutExpanded)
//            .setOngoing(true)
//            .setCategory(CATEGORY_ALARM)
//            .setColor(ContextCompat.getColor(applicationContext, R.color.black))
//            .setFullScreenIntent(pendingIntent, true)
//            .setContentIntent(pendingIntent)
//        notification.priority = PRIORITY_MAX
//
//        if (SDK_INT >= O) {
//            notification.setChannelId(NOTIFICATION_CHANNEL)
//
//            val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)
//            val audioAttributes = AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
//                .setContentType(CONTENT_TYPE_SONIFICATION).build()
//
//            val channel =
//                NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_NAME, IMPORTANCE_HIGH)
//
//            channel.enableLights(true)
//            channel.lightColor = RED
//            channel.enableVibration(true)
//            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
//            channel.setSound(ringtoneManager, audioAttributes)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        notificationManager.notify(id, notification.build())
    }

    companion object {
        const val NOTIFICATION_ID = "appName_notification_id"
        const val NOTIFICATION_NAME = "appName"
        const val NOTIFICATION_CHANNEL = "appName_channel_02"
        const val NOTIFICATION_WORK = "appName_notification_work"
        const val NOTIFICATION_INT_ID = 45
    }
}