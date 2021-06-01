package com.stenleone.clenner.managers.clean

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import com.stenleone.clenner.managers.preferences.SharedPreferences
import com.stenleone.clenner.model.AppData
import com.stenleone.clenner.util.constans.AppConstant.BYTES_PER_MB
import com.stenleone.clenner.util.extencial.roundOffDecimal
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject
import kotlin.random.Random

class CpuCleaningManager @Inject constructor(@ApplicationContext private val context: Context, private val pref: SharedPreferences) {

    private val listApp = arrayListOf<AppData>()

    fun clean() = flow {

        killBGPocesses()

        var progress = 100

        while (progress > 0) {
            delay(Random.nextLong(600, 1000))

            var newValue = Random.nextInt(1, 20)

            if (newValue < progress) {
                progress -= newValue
            } else {
                progress = 0
            }

            emit(100 - progress)
        }
    }

    private fun killBGPocesses() {
        val installedApplications = context.packageManager.getInstalledApplications(0)
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val packageName = context.applicationContext.packageName
        for (next in installedApplications) {
            if (next.flags and 1 != 1 && next.packageName != packageName) {
                activityManager.killBackgroundProcesses(next.packageName)
            }
        }
    }

    fun getAppList(): java.util.ArrayList<AppData> {
        val packageName = context.applicationContext.packageName
        val packageManager = context.packageManager
        val installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val listApp = ArrayList<AppData>()
        if (installedApplications != null) {
            for (next in installedApplications) {
                if (next.packageName != packageName && next.flags and 1 != 1) {
                    listApp.add(
                        AppData(
                            next.loadIcon(packageManager),
                            next.loadLabel(packageManager).toString(),
                            (File(next.publicSourceDir).length().toFloat() / BYTES_PER_MB.toFloat()).roundOffDecimal()
                        )
                    )
                    if (listApp.size >= 20) {
                        break
                    }
                }
            }
        }
        this.listApp.addAll(listApp)
        return listApp
    }

    fun getTemperature(): Float {
        var temperature = Random.nextDouble(28.0, 34.0)

        if (!pref.isCpuClean) {
            temperature += Random.nextDouble(1.0, 15.0)
        }

        if (temperature >= 45) {
            temperature = 45.0
        }

        return temperature.toFloat().roundOffDecimal()
    }

    fun cleanSuccess() {
        pref.isCpuClean = true
    }
}