package com.artemklymenko.mycryptotracker.crypto.presentation.coin_list

import com.artemklymenko.mycryptotracker.core.domain.util.NetworkError

sealed interface CoinListEvent {
    data class Error(val error: NetworkError): CoinListEvent
}