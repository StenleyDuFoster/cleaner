package com.notif

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class CheckStateScreenReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val isWaiting = getIsWaiting(context)

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
        } else if(intent.action == Intent.ACTION_SCREEN_OFF) {
            isPhoneLocked(context, true)

        }

    }

    private fun isPhoneLocked(context: Context, flag: Boolean) {
        val mSettings = context.getSharedPreferences(NOTIFY_STORAGE, Context.MODE_PRIVATE)

        val editor = mSettings.edit()
        editor.putBoolean("isLocked", flag)
        editor.apply()
    }
    private fun getIsWaiting(context: Context): Boolean {
        val mSettings = context.getSharedPreferences(NOTIFY_STORAGE, Context.MODE_PRIVATE)
        val ans = mSettings.getBoolean("isWaiting", false)

        return ans
    }
    private fun setIsWaiting(context: Context, flag: Boolean) {
        val mSettings = context.getSharedPreferences(NOTIFY_STORAGE, Context.MODE_PRIVATE)

        val editor = mSettings.edit()
        editor.putBoolean("isWaiting", flag)
        editor.apply()
    }
}