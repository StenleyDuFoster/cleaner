package com.stenleone.clenner.managers.clean

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.stenleone.clenner.util.enum.BatteryClean
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.random.Random


class BatteryCleaningManager @Inject constructor(@ApplicationContext private val context: Context) {

    fun clean(cleanType: BatteryClean) = flow {

        var maxStepProgress: Int

        when (cleanType) {
            BatteryClean.Normal -> {
                maxStepProgress = 50
            }
            BatteryClean.High -> {
                maxStepProgress = 40
            }
            BatteryClean.Maximum -> {
                maxStepProgress = 30
            }
        }

        var progress = 100
        var isBatteryOptimizy = false

        while (progress > 0) {
            var newValue = Random.nextInt(1, maxStepProgress)

            if (newValue < progress) {
                progress -= newValue
            } else {
                progress = 0
            }
            if (progress <= 50 && !isBatteryOptimizy) {
                when (cleanType) {
                    BatteryClean.Normal -> {
                        setNormalMode()
                    }
                    BatteryClean.High -> {
                        setHighMode()
                    }
                    BatteryClean.Maximum -> {
                        setMaximumMode()
                    }
                }
                isBatteryOptimizy = true
            }

            emit(100 - progress)
            delay(Random.nextLong(600, 1200))
        }
    }

    private fun setNormalMode() {
        Settings.System.putInt(context.contentResolver, "accelerometer_rotation", 1)
        Settings.System.putInt(context.contentResolver, "screen_brightness", 255)
        ContentResolver.setMasterSyncAutomatically(true)
    }

    private fun setHighMode() {
        turnOffBluetooth()
        Settings.System.putInt(context.contentResolver, "accelerometer_rotation", 0)
        Settings.System.putInt(context.contentResolver, "screen_brightness", 50)
        ContentResolver.setMasterSyncAutomatically(false)
    }

    private fun setMaximumMode() {
        turnOffWiFi()
        turnOffBluetooth()
        Settings.System.putInt(context.contentResolver, "accelerometer_rotation", 0)
        Settings.System.putInt(context.contentResolver, "screen_brightness", 20)
        ContentResolver.setMasterSyncAutomatically(false)
    }

    private fun turnOffBluetooth() {
        val defaultAdapter = BluetoothAdapter.getDefaultAdapter()
        if (defaultAdapter != null && defaultAdapter.isEnabled) {
            defaultAdapter.disable()
        }
    }

    private fun manualOffWifi() {
        if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE)) {
            val panelIntent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
            panelIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.applicationContext.startActivity(panelIntent)
        }
        Toast.makeText(context, "Turn off Wi-Fi", Toast.LENGTH_SHORT).show()
    }

    private fun turnOffWiFi() {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiManager.isWifiEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                manualOffWifi()
            } else {
                if (!wifiManager.setWifiEnabled(true)) {
                    manualOffWifi()
                }
            }
        }
    }

}