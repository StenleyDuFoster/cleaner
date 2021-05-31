package com.stenleone.clenner.managers.clean

import com.stenleone.clenner.interfaces.CleaningManager
import com.stenleone.clenner.managers.preferences.SharedPreferences
import com.stenleone.clenner.model.SystemCleaningStateData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.random.Random

class SystemCleaningManager @Inject constructor(private val pref: SharedPreferences): CleaningManager {

    override fun clean() = flow {
        var progress = 100

        while (progress > 0) {
            delay(Random.nextLong(600, 1000))

            var newValue = Random.nextInt(1, 20)

            if (newValue < progress) {
                progress -= newValue
            } else {
                progress = 0
            }

            emit(100 - progress)
        }
    }

    override fun getData(): SystemCleaningStateData {
        return SystemCleaningStateData().also {
            if (!pref.isSystemClean) {
                it.addData()
            }
        }

    }

    override fun cleanSuccess() {
        pref.isSystemClean = true
    }

}