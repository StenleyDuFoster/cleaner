package com.stenleone.clenner.managers.clean

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SystemCleaningManager @Inject constructor(@ApplicationContext private val context: Context) {

    fun searchCache(cacheCallBack: (cache: Int) -> Unit) {

        var cache = 0

        context.packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES).forEach {
            cache += (context.createPackageContext(it.packageName, Context.CONTEXT_IGNORE_SECURITY).
        }

        cacheCallBack(cache)
    }

    fun deleteCache() {
        context.packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES).forEach {
            context.createPackageContext(it.packageName, Context.CONTEXT_IGNORE_SECURITY).cacheDir.delete()
        }
    }

}