package com.artemklymenko.mycryptotracker.crypto.presentation.coin_list

import com.artemklymenko.mycryptotracker.crypto.presentation.models.CoinUi

sealed interface CoinListAction {
    data class OnCoinClick(val coinUi: CoinUi): CoinListAction
}