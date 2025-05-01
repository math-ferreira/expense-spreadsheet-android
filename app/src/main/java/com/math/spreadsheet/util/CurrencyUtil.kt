package com.math.spreadsheet.util

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.roundTo2DecimalPlaces(): Double {
    return BigDecimal(this).setScale(2, RoundingMode.HALF_UP).toDouble()
}
