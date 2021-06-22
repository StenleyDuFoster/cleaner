package com.stenleone.clenner.ui.activity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ActivityCloserPush : AppCompatActivity() {

    companion object {
        const val NOTIFICATION_ID = "NOTIFICATION_ID"

        fun getDismissIntent(notificationId: Int, context: Context?): PendingIntent {
            val intent = Intent(context, ActivityCloserPush::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(NOTIFICATION_ID, notificationId)
            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(intent.getIntExtra(NOTIFICATION_ID, -1))
        finish()
    }
}