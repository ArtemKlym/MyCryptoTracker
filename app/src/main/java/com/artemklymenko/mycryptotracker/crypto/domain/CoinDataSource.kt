package com.artemklymenko.mycryptotracker.crypto.domain

import com.artemklymenko.mycryptotracker.core.domain.util.NetworkError
import com.artemklymenko.mycryptotracker.core.domain.util.Result

interface CoinDataSource {
    suspend fun getCoins(): Result<List<Coin>, NetworkError>
}