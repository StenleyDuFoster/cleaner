package com.stenleone.clenner.managers.config

import android.graphics.Color
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import java.util.concurrent.TimeUnit
import javax.inject.Inject

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

    fun getIntAsync(config: Config, success: (Int) -> Unit, failure: (String) -> Unit) {
        firebaseRemoteConfig.fetchAndActivate()
            .addOnSuccessListener {
                firebaseRemoteConfig.getLong(config.key).toInt().let { intValue -> success(intValue) }
            }
            .addOnFailureListener {
                failure(it.message.toString())
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

    fun getStringAsync(config: Config, success: (String) -> Unit, failure: (String) -> Unit, vararg replace: String) {

        firebaseRemoteConfig.fetchAndActivate()
            .addOnSuccessListener {
                firebaseRemoteConfig.getString(config.key).let { string ->
                    var result = string
                    replace.forEach { result = result.replaceFirst("%s", it) }
                    success(result)
                }
            }
            .addOnFailureListener {
                failure(it.message.toString())
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