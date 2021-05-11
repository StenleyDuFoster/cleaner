package com.stenleone.clenner.managers.permission

import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts

class PermissionManager(private val activity: AppCompatActivity) {

    private val requestPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()
        ) {

        }

    fun getAllPermissions() {
//        requestPermissionLauncher.launch()
    }

    fun destroy() {
        requestPermissionLauncher.unregister()
    }

}