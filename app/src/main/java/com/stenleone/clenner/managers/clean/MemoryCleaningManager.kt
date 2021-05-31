package com.stenleone.clenner.managers.clean

import android.app.ActivityManager
import android.content.Context
import com.stenleone.clenner.interfaces.CleaningManager
import com.stenleone.clenner.managers.preferences.SharedPreferences
import com.stenleone.clenner.model.MemoryCleaningStateData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.random.Random


class MemoryCleaningManager @Inject constructor(@ApplicationContext private val context: Context, private val pref: SharedPreferences) : CleaningManager {

    companion object {
        private const val BYTES_PER_MB = 1048576L
    }

    override fun getData(): MemoryCleaningStateData {
        val memoryInfo = ActivityManager.MemoryInfo()
        (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(memoryInfo)
        val consumedMemory: Long = (memoryInfo.totalMem - memoryInfo.availMem) / BYTES_PER_MB

        var usageMemoryInProcent = ((memoryInfo.totalMem - memoryInfo.availMem) * 100) / memoryInfo.totalMem

        if (!pref.isMemoryClean) {
            usageMemoryInProcent += Random.nextInt(5, 15)
        }

        val data = MemoryCleaningStateData(
            memoryInfo.totalMem / BYTES_PER_MB,
            consumedMemory,
            usageMemoryInProcent,
            memoryInfo.availMem
        )

        return data
    }

    override fun clean() = flow {
        val installedApplications = context.packageManager.getInstalledApplications(0)
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val packageName = context.applicationContext.packageName
        for (next in installedApplications) {
            if (next.flags and 1 != 1 && next.packageName != packageName) {
                activityManager.killBackgroundProcesses(next.packageName)
            }
        }

        var progress = 100

        while (progress > 0) {
            var newValue = Random.nextInt(1, 30)

            if (newValue < progress) {
                progress -= newValue
            } else {
                progress = 0
            }

            emit(100 - progress)
            delay(Random.nextLong(600, 1200))
        }
    }

    override fun cleanSuccess() {
        pref.isMemoryClean = true
    }

}