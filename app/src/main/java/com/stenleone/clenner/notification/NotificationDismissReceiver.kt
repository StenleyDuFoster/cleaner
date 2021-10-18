package com.stenleone.clenner.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationDismissReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("test", "NotificationDismissReceiver")
        CleanAlertNotification.setLastAlarmNotificationIsClosed(context, true)
    }
}