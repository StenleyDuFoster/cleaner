package com.stenleone.clenner.managers.preferences

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private const val SHARED_PREFERENCES_NAME = "cleaner.main_sPref"

        private const val SENDED_DATA_AFTER_FIRST_OPEN = "sended_data_after_first_open"
        private const val LAST_SYSTEM_CLEAN_DATE = "last_system_clean_date"
        private const val LAST_MEMORY_CLEAN_DATE = "last_memory_clean_date"
        private const val LAST_CPU_CLEAN_DATE = "last_cpu_clean_date"

        const val CLEAN_DELAY_IN_HOUR = 4
    }

    private val sharedPreferences by lazy { context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE) }

    var isSendedDataAfterFirstOpen: Boolean
        get() {
            return sharedPreferences.getBoolean(SENDED_DATA_AFTER_FIRST_OPEN, false)
        }
        set(value) {
            sharedPreferences.edit {
                putBoolean(SENDED_DATA_AFTER_FIRST_OPEN, value)
            }
        }

    var isSystemClean: Boolean
        get() {
            val oldCleanMillis = sharedPreferences.getString(LAST_SYSTEM_CLEAN_DATE, null)
            return getBooleanByOldTime(oldCleanMillis)
        }
        set(value) {
            sharedPreferences.edit {
                putString(LAST_SYSTEM_CLEAN_DATE, System.currentTimeMillis().toString())
            }
        }

    var isMemoryClean: Boolean
        get() {
            val oldCleanMillis = sharedPreferences.getString(LAST_MEMORY_CLEAN_DATE, null)
            return getBooleanByOldTime(oldCleanMillis)
        }
        set(value) {
            sharedPreferences.edit {
                putString(LAST_MEMORY_CLEAN_DATE, System.currentTimeMillis().toString())
            }
        }

    var isCpuClean: Boolean
        get() {
            val oldCleanMillis = sharedPreferences.getString(LAST_CPU_CLEAN_DATE, null)
            return getBooleanByOldTime(oldCleanMillis)
        }
        set(value) {
            sharedPreferences.edit {
                putString(LAST_CPU_CLEAN_DATE, System.currentTimeMillis().toString())
            }
        }

    private fun getBooleanByOldTime(oldTimeMillis: String?): Boolean {
        if (oldTimeMillis == null) {
            return false
        }

        val cleanDate = Calendar.getInstance().also {
            it.time = Date(oldTimeMillis.toLong())
        }

        val cleanDateWithDelay = cleanDate.also {
            it.add(Calendar.HOUR, CLEAN_DELAY_IN_HOUR)
        }

        return cleanDateWithDelay.time.time > System.currentTimeMillis()
    }
}