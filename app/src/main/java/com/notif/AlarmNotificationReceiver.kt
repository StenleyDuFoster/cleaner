package com.notif

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmNotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val isPhoneLocked = isPhoneLocked(context)

        if (intent.action == SEND_PUSH_ACTION) {
            if (isPhoneLocked) {
                setIsWaiting(context, true)
                context.startService(Intent(context, CheckStateScreenReceiver::class.java))
                Log.d("test", "AlarmNotificationReceiver: Wait unlock device to send AlertNotification")
            } else {
                CleanAlertNotification.setLastAlarmNotificationIsClosed(context, false)
                CleanAlertNotification.send(context, false)
                AlarmNotificationService.updateSingleAlarm(context)
            }
        }
    }

    private fun setIsWaiting(context: Context, flag: Boolean) {
        val mSettings = context.getSharedPreferences(NOTIFY_STORAGE, Context.MODE_PRIVATE)

        val editor = mSettings.edit()
        editor.putBoolean("isWaiting", flag)
        editor.apply()
    }

    private fun isPhoneLocked(context: Context): Boolean {
        val mSettings = context.getSharedPreferences(NOTIFY_STORAGE, Context.MODE_PRIVATE)

        return mSettings.getBoolean("isLocked", false)
    }
}