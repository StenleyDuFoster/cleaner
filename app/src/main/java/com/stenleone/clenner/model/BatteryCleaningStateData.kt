package com.stenleone.clenner.model

import com.stenleone.clenner.interfaces.CleanData

data class BatteryCleaningStateData(
    val totalMemory: Long,
    val consumedMemory: Long,
    val usageProcent: Long,
    val aviableMemory: Long
): CleanData {

}