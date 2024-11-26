package com.artemklymenko.mycryptotracker.core.presentation.util

fun getUserFriendlyIntervals(): Map<String, String> {
    return mapOf(
        "m15" to "15 min",
        "m30" to "30 min",
        "h1" to "1 hr",
        "h2" to "2 hr",
        "h6" to "6 hr",
        "h12" to "12 hr",
        "d1" to "1 day"
    )
}