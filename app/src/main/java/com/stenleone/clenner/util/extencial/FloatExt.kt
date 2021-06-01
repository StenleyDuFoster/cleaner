package com.stenleone.clenner.util.extencial

import java.math.RoundingMode

fun Float.roundOffDecimal(decimalScale: Int = 1): Float {
    return this.toBigDecimal().setScale(decimalScale, RoundingMode.UP).toFloat()
}