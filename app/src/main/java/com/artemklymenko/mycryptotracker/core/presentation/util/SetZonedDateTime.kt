package com.artemklymenko.mycryptotracker.core.presentation.util

import java.time.ZonedDateTime

fun setStartZonedDateTime(interval: String): ZonedDateTime {
    return when {
        interval.contains("m1") -> ZonedDateTime.now().minusMinutes(20)
        interval.contains("m5") -> ZonedDateTime.now().minusMinutes(80)
        interval.contains("m30") -> ZonedDateTime.now().minusHours(10)
        interval.contains("h1") -> ZonedDateTime.now().minusHours(18)
        interval.contains("h2") -> ZonedDateTime.now().minusHours(32)
        interval.contains("h6") -> ZonedDateTime.now().minusDays(6)
        interval.contains("d1") -> ZonedDateTime.now().minusWeeks(2)
        else -> ZonedDateTime.now().minusDays(5)
    }
}

