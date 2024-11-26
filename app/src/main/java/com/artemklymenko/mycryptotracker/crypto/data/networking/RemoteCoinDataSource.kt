package com.artemklymenko.mycryptotracker.crypto.data.networking

import com.artemklymenko.mycryptotracker.core.data.networking.constructUrl
import com.artemklymenko.mycryptotracker.core.data.networking.safeCall
import com.artemklymenko.mycryptotracker.core.domain.util.NetworkError
import com.artemklymenko.mycryptotracker.core.domain.util.Result
import com.artemklymenko.mycryptotracker.core.domain.util.map
import com.artemklymenko.mycryptotracker.crypto.data.mappers.toCoin
import com.artemklymenko.mycryptotracker.crypto.data.mappers.toCoinPrice
import com.artemklymenko.mycryptotracker.crypto.data.networking.dto.CoinHistoryDto
import com.artemklymenko.mycryptotracker.crypto.data.networking.dto.CoinsResponseDto
import com.artemklymenko.mycryptotracker.crypto.domain.Coin
import com.artemklymenko.mycryptotracker.crypto.domain.CoinDataSource
import com.artemklymenko.mycryptotracker.crypto.domain.CoinPrice
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.time.ZoneId
import java.time.ZonedDateTime

class RemoteCoinDataSource(
    private val httpClient: HttpClient
) : CoinDataSource {
    override suspend fun getCoins(): Result<List<Coin>, NetworkError> {
        return safeCall<CoinsResponseDto> {
            httpClient.get(
                urlString = constructUrl("/assets")
            )
        }.map { response ->
            response.data.map { it.toCoin() }
        }
    }

    override suspend fun getCoinHistory(
        coinId: String,
        interval: String,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): Result<List<CoinPrice>, NetworkError> {
        val startMills = start
            .withZoneSameInstant(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()
        val endMills = end
            .withZoneSameInstant(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()
        return safeCall<CoinHistoryDto> {
            httpClient.get(
                urlString = constructUrl("/assets/$coinId/history")
            ) {
                parameter("interval", interval)
                parameter("start", startMills)
                parameter("end", endMills)
            }
        }.map { response ->
            response.data.map { it.toCoinPrice() }
        }
    }
}