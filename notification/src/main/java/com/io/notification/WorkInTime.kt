package com.io.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class WorkInTime {

//     fun runEveryDay(hourOfTheDay: Int, context: Context) {
//        val repeatInterval = 1 // In days
//        val flexTime: Long = calculateFlex(hourOfTheDay, repeatInterval)
//        schedule(repeatInterval, flexTime, context, TimeUnit.DAYS)
//    }
//
//    fun runEveryHour(repeatInterval: Int, flexTime: Long, context: Context){
//        schedule(repeatInterval, flexTime, context, TimeUnit.HOURS)
//    }
//
//    private fun schedule(repeatInterval: Int, flexTime: Long, context: Context, timeUnit: TimeUnit) {
//        val myConstraints: Constraints = Constraints.Builder()
//            .setRequiresBatteryNotLow(false)
//            .build()
//
//        val workRequest = PeriodicWorkRequestBuilder<NotifyWork>(
//            repeatInterval.toLong(), timeUnit,
//            flexTime, TimeUnit.MILLISECONDS
//        )
//            .setConstraints(myConstraints)
//            .build()
//
//        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
//            "YOUR_NICE_WORK_TAG",
//            ExistingPeriodicWorkPolicy.REPLACE,
//            workRequest
//        )
//    }

    fun timeSetSpecific(hour: Int, context: Context) {

        if (!isSetAlarm(context)) {
            setIsSetAlarm(context)

            val alarmManager =
                context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlertReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                intent,
                0,
            )

            val calendar = Calendar.getInstance()

            calendar[Calendar.HOUR_OF_DAY] = hour
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent,
            )
        }
    }

    fun timeSetEveryHours(context: Context) {
        val alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0)

        val calendar = Calendar.getInstance()

        calendar[Calendar.HOUR_OF_DAY]
        calendar[Calendar.MINUTE]
        calendar[Calendar.SECOND]
        calendar[Calendar.MILLISECOND]
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )
    }
}