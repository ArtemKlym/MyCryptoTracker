package com.artemklymenko.mycryptotracker.crypto.data.mappers

import com.artemklymenko.mycryptotracker.crypto.data.networking.dto.CoinDto
import com.artemklymenko.mycryptotracker.crypto.domain.Coin

fun CoinDto.toCoin(): Coin {
    return Coin(
        id = id,
        rank = rank,
        name = name,
        symbol = symbol,
        marketCapUsd = marketCapUsd,
        priceUsd = priceUsd,
        changePercent24Hr = changePercent24Hr
    )
}