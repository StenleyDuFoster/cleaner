package com.stenleone.clenner.model

import com.stenleone.clenner.interfaces.CleanData

data class MemoryCleaningStateData(
    val totalMemory: Long,
    val consumedMemory: Long,
    val usageProcent: Long,
    val aviableMemory: Long
): CleanData {

}