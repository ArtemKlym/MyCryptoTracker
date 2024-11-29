package com.artemklymenko.mycryptotracker.core.presentation.util

import java.time.ZonedDateTime

fun setStartZonedDateTime(interval: String): ZonedDateTime {
    return when {
        interval.contains("m1") -> ZonedDateTime.now().minusMinutes(5)
        interval.contains("m5") -> ZonedDateTime.now().minusMinutes(30)
        interval.contains("m30") -> ZonedDateTime.now().minusHours(6)
        interval.contains("h1") -> ZonedDateTime.now().minusHours(12)
        interval.contains("h2") -> ZonedDateTime.now().minusHours(24)
        interval.contains("h6") -> ZonedDateTime.now().minusDays(3)
        interval.contains("d1") -> ZonedDateTime.now().minusDays(5)
        else -> ZonedDateTime.now().minusDays(5)
    }
}

