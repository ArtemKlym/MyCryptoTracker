package com.artemklymenko.mycryptotracker.crypto.data.networking

import com.artemklymenko.mycryptotracker.core.data.networking.constructUrl
import com.artemklymenko.mycryptotracker.core.data.networking.safeCall
import com.artemklymenko.mycryptotracker.core.domain.util.NetworkError
import com.artemklymenko.mycryptotracker.core.domain.util.Result
import com.artemklymenko.mycryptotracker.core.domain.util.map
import com.artemklymenko.mycryptotracker.crypto.data.mappers.toCoin
import com.artemklymenko.mycryptotracker.crypto.data.networking.dto.CoinsResponseDto
import com.artemklymenko.mycryptotracker.crypto.domain.Coin
import com.artemklymenko.mycryptotracker.crypto.domain.CoinDataSource
import io.ktor.client.HttpClient
import io.ktor.client.request.get

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
}