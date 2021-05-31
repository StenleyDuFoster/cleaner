package com.stenleone.clenner.model

import com.stenleone.clenner.interfaces.CleanData
import kotlin.random.Random

data class SystemCleaningStateData(
    var systemGarbageMb: Int = 0,
    var temporaryFilesMb: Int = 0,
    var residualFilesMb: Int = 0,
    var cacheMemoryMb: Int = 0
): CleanData {

    fun cleanData() {
        systemGarbageMb = 0
        temporaryFilesMb = 0
        residualFilesMb = 0
        cacheMemoryMb = 0
    }

    fun addData() {
        systemGarbageMb = Random.nextInt(20) + 5
        temporaryFilesMb = Random.nextInt(15) + 10
        residualFilesMb = Random.nextInt(30) + 15
        cacheMemoryMb = Random.nextInt(25) + 10
    }

    fun getTotalMb(): Int {
        return systemGarbageMb + temporaryFilesMb + residualFilesMb + cacheMemoryMb
    }

}