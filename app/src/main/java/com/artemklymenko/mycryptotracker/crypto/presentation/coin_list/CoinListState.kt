package com.artemklymenko.mycryptotracker.crypto.presentation.coin_list

import androidx.compose.runtime.Immutable
import com.artemklymenko.mycryptotracker.crypto.presentation.models.CoinUi

@Immutable
data class CoinListState(
    val isLoading: Boolean = false,
    val coins: List<CoinUi> = emptyList(),
    val selectedInterval: String = "m15",
    val selectedCoin: CoinUi? = null
)
