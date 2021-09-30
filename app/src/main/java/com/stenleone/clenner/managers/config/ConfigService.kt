package com.stenleone.clenner.managers.config

import android.graphics.Color
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ConfigService @Inject constructor() {

    private val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance().apply {
        val config = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(TimeUnit.HOURS.toSeconds(1))
            .build()
        setConfigSettingsAsync(config)
        setDefaultsAsync(Config.values().associate { it.key to it.defaultValue })
        fetchAndActivate()
    }

    private val firebaseConfig
        get() = firebaseRemoteConfig.apply {
            fetchAndActivate()
        }

    fun getInt(config: Config) = firebaseConfig.getLong(config.key).toInt()

    suspend fun getIntAsync(config: Config): Int {
        return suspendCoroutine { continuation ->
            firebaseRemoteConfig.fetchAndActivate()
                .addOnSuccessListener {
                    try {
                        firebaseRemoteConfig.getLong(config.key).toInt().let { intValue -> continuation.resume(intValue) }
                    } catch (e: Exception) {
                        continuation.resume(10)
                    }
                }
                .addOnFailureListener {
                    continuation.resume(getInt(config))
                }
        }
    }


    fun getLong(config: Config) = firebaseConfig.getLong(config.key)

    fun getString(config: Config, vararg replace: String): String = firebaseConfig.getString(config.key).let { string ->
        var result = string
        replace.forEach { result = result.replaceFirst("%s", it) }
        return result
    }

    fun <T> getDataObject(config: Config, obj: Class<T>): T? {
        return try {
            Gson().fromJson(firebaseConfig.getString(config.key), obj)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getStringAsync(config: Config, vararg replace: String): String {
        return suspendCoroutine { continuation ->
            firebaseRemoteConfig.fetchAndActivate()
                .addOnSuccessListener {
                    firebaseRemoteConfig.getString(config.key).let { string ->
                        var result = string
                        replace.forEach { result = result.replaceFirst("%s", it) }
                        continuation.resume(result)
                    }
                }
                .addOnFailureListener {
                    continuation.resume(getString(config))
                }
        }

    }

    fun getBoolean(config: Config) = firebaseConfig.getBoolean(config.key)

    fun getColorString(config: Config): String {
        val string = getString(config)
        return try {
            Color.parseColor(string)
            string
        } catch (e: IllegalArgumentException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e("CaughtError", "Error", e)
            config.defaultValue as String
        }
    }
}