package com.io.notification

import android.content.Context
import androidx.core.content.edit

private const val SETTING_STORAGE_NAME = "stenleone.clenner"
private const val IS_FIRST_TIME = "IS_FIRST_TIME"

private fun pref(context: Context) = context.getSharedPreferences(SETTING_STORAGE_NAME, Context.MODE_PRIVATE)

private fun editString(context: Context, key: String, value: String?) = pref(context).edit {
    putString(key, value)
}

private fun getString(context: Context, key: String, default : String) = pref(context).getString(key, default) ?: default

private fun editBoolean(context: Context, key: String, value: Boolean) = pref(context).edit {
    putBoolean(key, value)
}

private fun getBoolean(context: Context, key: String, default : Boolean) = pref(context).getBoolean(key, default)

fun setIsSetAlarm(context : Context) = editBoolean(context, IS_FIRST_TIME, true)

fun isSetAlarm(context: Context) = getBoolean(context, IS_FIRST_TIME, false)