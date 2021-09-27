package com.stenleone.clenner.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.stenleone.clenner.service.AlarmNotificationService
import com.stenleone.clenner.util.notification.CleanAlertNotification
import java.util.*

class CheckStateScreenReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val isWaiting = getIsWaiting(context)

        Log.d("test", "CheckStateScreenReceiver: " + intent.action.toString())
        if(intent.action == Intent.ACTION_USER_PRESENT) {
            if (isWaiting) {
                Log.d("test", "CheckStateScreenReceiver: Send AlertNotification after waiting")
                setIsWaiting(context, false)
                CleanAlertNotification.setLastAlarmNotificationIsClosed(context, false)
                CleanAlertNotification.send(context, true)
                AlarmNotificationService.updateSingleAlarm(context)
            } else {
                isPhoneLocked(context, false)
                if (!CleanAlertNotification.getLastAlarmNotificationIsClosed(context)) {
                    Log.d("test", "CheckStateScreenReceiver: Refresh notification")
                    CleanAlertNotification.setLastAlarmNotificationIsClosed(context, false)
                    CleanAlertNotification.send(context, false)
                }
            }
//            if (!getIsFirstOpen(context)){
//                setAlarm(context)
//            }
        } else if(intent.action == Intent.ACTION_SCREEN_OFF) {
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
        calendar.set(Calendar.HOUR_OF_DAY, h)
        calendar.set(Calendar.MINUTE, m + 1)
//        calendar.set(Calendar.MINUTE, m)
//        calendar.set(Calendar.HOUR_OF_DAY, h + 2)
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
}