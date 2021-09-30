package com.io.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.NotificationManager.IMPORTANCE_MIN
import android.app.PendingIntent.getActivity
import android.content.BroadcastReceiver
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
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.io.notification.NotifyWork.Companion.NOTIFICATION_CHANNEL
import com.io.notification.NotifyWork.Companion.NOTIFICATION_ID
import com.io.notification.NotifyWork.Companion.NOTIFICATION_INT_ID
import com.io.notification.NotifyWork.Companion.NOTIFICATION_NAME
import kotlinx.android.synthetic.main.layout_test.view.*
import android.media.RingtoneManager
import android.net.Uri
import java.lang.Exception
import android.os.PowerManager
import android.content.Context.POWER_SERVICE
import android.util.Log
import androidx.core.app.NotificationCompat.*

class AlertReceiver : BroadcastReceiver() {

    @SuppressLint("InvalidWakeLockTag")
    override fun onReceive(context: Context?, intent: Intent?) {

        val powerManager = context?.getSystemService(POWER_SERVICE) as PowerManager

        if (!powerManager.isInteractive) { // if screen is not already on, turn it on (get wake_lock for 10 seconds)
            val wl: PowerManager.WakeLock = powerManager.newWakeLock(
                PowerManager.FULL_WAKE_LOCK
                        or PowerManager.ACQUIRE_CAUSES_WAKEUP
                        or PowerManager.ON_AFTER_RELEASE,
                "MH24_SCREENLOCK"
            )
            wl.acquire(10000)
            val wl_cpu: PowerManager.WakeLock =
                powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MH24_SCREENLOCK")
            wl_cpu.acquire(10000)
        }

        try {
            val notification: Uri = getDefaultUri(TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(context, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var windowManager =
            context?.getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager

        val valetModeWindow = View.inflate(context, R.layout.layout_test, null) as ViewGroup
        val LAYOUT_FLAG: Int

        LAYOUT_FLAG = if (SDK_INT >= O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_DIM_BEHIND
                    or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_FULLSCREEN
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.TOP
        params.dimAmount = 0.5f //black hint (not working without FLAG_DIM_BEHIND)
        params.x = 0
        params.y = 0

        // shows the windowManager on phone display
        windowManager.addView(valetModeWindow, params)

        val intent = Intent(
            context,
            Class.forName("com.stenleone.clenner.ui.activity.MainActivity")
        )

        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(NOTIFICATION_ID, NOTIFICATION_ID)

        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationLayoutExpanded =
            RemoteViews(context.packageName, R.layout.layout_notification)

        val bitmap = context.vectorToBitmap(R.drawable.ic_launcher_foreground)
        val pendingIntent = getActivity(context, 0, intent, 0)

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .setLargeIcon(bitmap)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setPriority(PRIORITY_HIGH)
            .setCustomContentView(notificationLayoutExpanded)
            .setOngoing(true)
            .setVisibility(VISIBILITY_PUBLIC)
            .setColor(ContextCompat.getColor(context, R.color.black))
            .setContentIntent(pendingIntent)

        if (SDK_INT >= O) {
            notification.setChannelId(NOTIFICATION_CHANNEL)

            val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
                .setContentType(CONTENT_TYPE_SONIFICATION).build()

            val channel =
                NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_NAME, IMPORTANCE_HIGH)

            channel.enableLights(true)
            channel.lightColor = RED
            channel.lockscreenVisibility = VISIBILITY_PUBLIC
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtoneManager, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_INT_ID, notification.build())

        // removes WindowManager
        valetModeWindow.iv_cancel.setOnClickListener {
            windowManager.removeView(valetModeWindow)
            notificationManager.cancel(NOTIFICATION_INT_ID)
        }

        // starts Cleaner
        valetModeWindow.tv_control.setOnClickListener {
            val intent = Intent(
                context,
                Class.forName("com.stenleone.clenner.ui.activity.MainActivity")
            )
            intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(NOTIFICATION_ID, NOTIFICATION_ID)
            startActivity(context, intent, null)

            // removes WindowManager
            windowManager.removeView(valetModeWindow)

            // removes notification from status bar
            notificationManager.cancel(NOTIFICATION_INT_ID)
        }
    }
}