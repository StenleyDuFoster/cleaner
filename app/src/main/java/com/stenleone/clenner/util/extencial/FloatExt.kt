package com.stenleone.clenner.util.extencial

import java.math.RoundingMode

fun Float.roundOffDecimal(): Float {
    return this.toBigDecimal().setScale(1, RoundingMode.UP).toFloat()
}