package com.artemklymenko.mycryptotracker.core.presentation.util

fun getUserFriendlyIntervals(): Map<String, String> {
    return mapOf(
        "m1" to "1 min",
        "m5" to "5 min",
        "m30" to "30 min",
        "h1" to "1 hr",
        "h2" to "2 hr",
        "h6" to "6 hr",
        "d1" to "1 day"
    )
}