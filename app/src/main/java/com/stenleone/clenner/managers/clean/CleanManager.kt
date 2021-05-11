package com.stenleone.clenner.managers.clean

import androidx.appcompat.app.AppCompatActivity

class CleanManager {

    lateinit var activity: AppCompatActivity

    fun bind(activity: AppCompatActivity) {
        this.activity = activity
    }

    private fun setup() {

    }

}