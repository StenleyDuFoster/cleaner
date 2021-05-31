package com.stenleone.clenner.interfaces

import kotlinx.coroutines.flow.Flow

interface CleaningManager {

    fun getData(): CleanData
    fun clean(): Flow<Int>
    fun cleanSuccess() = Unit

}