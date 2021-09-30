package com.stenleone.clenner.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.stenleone.clenner.util.notification.CleanAlertNotification

class NotificationDismissReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("test", "NotificationDismissReceiver")
        CleanAlertNotification.setLastAlarmNotificationIsClosed(context, true)
    }
}