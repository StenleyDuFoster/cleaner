package com.stenleone.clenner.managers.preferences

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private const val SHARED_PREFERENCES_NAME = "cleaner.main_sPref"

        private const val SENDED_DATA_AFTER_FIRST_OPEN = "sended_data_after_first_open"
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
}