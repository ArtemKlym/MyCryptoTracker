package com.artemklymenko.mycryptotracker.crypto.domain

import com.artemklymenko.mycryptotracker.core.domain.util.NetworkError
import com.artemklymenko.mycryptotracker.core.domain.util.Result
import java.time.ZonedDateTime

class FakeCoinDataSourceAndroid: CoinDataSource {

    private val coins = mutableListOf<Coin>()
    private val coinHistoryMap = mutableMapOf<String, List<CoinPrice>>()
    private var shouldReturnNetworkError = false

    fun addCoins(vararg newCoins: Coin) {
        coins.addAll(newCoins)
    }

    override suspend fun getCoins(): Result<List<Coin>, NetworkError> {
        return if(shouldReturnNetworkError) {
            Result.Error(NetworkError.UNKNOWN)
        } else {
            Result.Success(coins.toList())
        }
    }

    override suspend fun getCoinHistory(
        coinId: String,
        interval: String,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): Result<List<CoinPrice>, NetworkError> {
        return if(shouldReturnNetworkError) {
            Result.Error(NetworkError.UNKNOWN)
        } else {
            val history = coinHistoryMap[coinId]?.filter {
                it.dateTime.isAfter(start) && it.dateTime.isBefore(end)
            }.orEmpty()
            Result.Success(history)
        }
    }
}