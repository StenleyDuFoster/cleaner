package com.io.notification

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import androidx.work.PeriodicWorkRequest
import java.util.*
import java.util.concurrent.TimeUnit

fun Context.vectorToBitmap(drawableId: Int): Bitmap? {
    val drawable = getDrawable(drawableId) ?: return null
    val bitmap = createBitmap(
        drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    ) ?: return null
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun calculateFlex(hourOfTheDay: Int, periodInDays: Int): Long {

    val cal1 = Calendar.getInstance()
    cal1[Calendar.HOUR_OF_DAY] = hourOfTheDay
    cal1[Calendar.MINUTE] = 0
    cal1[Calendar.SECOND] = 0

    // Initialize a calendar with now.
    val cal2 = Calendar.getInstance()
    if (cal2.timeInMillis < cal1.timeInMillis) {
        // Add the worker periodicity.
        cal2.timeInMillis = cal2.timeInMillis + TimeUnit.DAYS.toMillis(periodInDays.toLong())
    }
    val delta = cal2.timeInMillis - cal1.timeInMillis
    return if (delta > PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS) delta else PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS
}