package com.stenleone.clenner.receiver

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.stenleone.clenner.R
import com.stenleone.clenner.ui.activity.MainActivity
import java.util.*
import kotlin.random.Random


class CheckStateScreenReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val isWaiting = getIsWaiting(context)

        if(intent.action == Intent.ACTION_USER_PRESENT) {
            Log.d("test", "present")
            if (isWaiting) {
                setIsWaiting(context, false)
                start(context)
            } else {
                isPhoneLocked(context, false)
            }
            if (!getIsFirstOpen(context)){
                setAlarm(context)
            }
        } else if(intent.action == Intent.ACTION_SCREEN_OFF){
            Log.d("test", "ACTION_SCREEN_OFF")
            isPhoneLocked(context, true)

        }

    }

    private fun setAlarm(context: Context) {
        val notifyIntent = Intent(context, AlarmNotificationReceiver::class.java)
        notifyIntent.action = "PUSH"
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            notifyIntent,
            0
        )
        var alarmManager: AlarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        Log.d("test", "set alarm")
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val h = calendar.get(Calendar.HOUR_OF_DAY)
        val m = calendar.get(Calendar.MINUTE)
        calendar.set(Calendar.HOUR_OF_DAY, h + 2)
        calendar.set(Calendar.MINUTE, m)
        alarmManager.setExact(
            AlarmManager.RTC,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private fun isPhoneLocked(context: Context, flag: Boolean) {
        val mSettings = context.getSharedPreferences("NOTIFY_PRESENT", Context.MODE_PRIVATE)

        val editor = mSettings.edit()
        editor.putBoolean("isLocked", flag)
        editor.apply()
    }
    private fun getIsWaiting(context: Context): Boolean {
        val mSettings = context.getSharedPreferences("NOTIFY_PRESENT", Context.MODE_PRIVATE)
        val ans = mSettings.getBoolean("isWaiting", false)

        return ans
    }
    private fun setIsWaiting(context: Context, flag: Boolean) {
        val mSettings = context.getSharedPreferences("NOTIFY_PRESENT", Context.MODE_PRIVATE)

        val editor = mSettings.edit()
        editor.putBoolean("isWaiting", flag)
        editor.apply()
    }

    private fun getIsFirstOpen(context: Context): Boolean {
        val mSettings = context.getSharedPreferences("NOTIFY_PRESENT", Context.MODE_PRIVATE)
        val ans = mSettings.getBoolean("isFirstOpen", false)

        return ans
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
//        intent.flags =
//            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun Context.getFullScreenPendingIntent2(): PendingIntent {
        //intent to activity from click on notification
        val intent = Intent()
//

        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

}