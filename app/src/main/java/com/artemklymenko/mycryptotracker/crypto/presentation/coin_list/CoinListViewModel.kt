package com.artemklymenko.mycryptotracker.crypto.presentation.coin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artemklymenko.mycryptotracker.core.domain.util.onError
import com.artemklymenko.mycryptotracker.core.domain.util.onSuccess
import com.artemklymenko.mycryptotracker.crypto.domain.CoinDataSource
import com.artemklymenko.mycryptotracker.crypto.presentation.coin_detail.DataPoint
import com.artemklymenko.mycryptotracker.crypto.presentation.models.CoinUi
import com.artemklymenko.mycryptotracker.crypto.presentation.models.toCoinUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CoinListViewModel(
    private val coinDataSource: CoinDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(CoinListState())
    val state = _state
        .onStart { loadCoins() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            CoinListState()
        )

    private val _events = Channel<CoinListEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: CoinListAction) {
        when (action) {
            is CoinListAction.OnCoinClick -> {
                selectCoin(action.coinUi)
            }
            is CoinListAction.OnIntervalChange -> {
                setInterval(action.interval)
            }
            is CoinListAction.OnSearchChange -> {
                filteredCoins(action.input)
            }
        }
    }

    private fun setInterval(interval: String) {
        _state.update { it.copy(selectedInterval = interval) }

        val selectedCoin = _state.value.selectedCoin ?: return

        viewModelScope.launch {
            coinDataSource
                .getCoinHistory(
                    coinId = selectedCoin.id,
                    interval = interval,
                    start = ZonedDateTime.now().minusDays(5),
                    end = ZonedDateTime.now()
                )
                .onSuccess { history ->
                    val dataPoints = history
                        .sortedBy { it.dateTime }
                        .map {
                            DataPoint(
                                x = it.dateTime.hour.toFloat(),
                                y = it.priceUsd.toFloat(),
                                xLabel = DateTimeFormatter
                                    .ofPattern("ha\nM/d")
                                    .format(it.dateTime)
                            )
                        }

                    _state.update {
                        it.copy(
                            selectedCoin = it.selectedCoin?.copy(
                                coinPriceHistory = dataPoints
                            )
                        )
                    }
                }
                .onError { error ->
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }

    private fun selectCoin(coinUi: CoinUi) {
        val interval = _state.value.selectedInterval
        _state.update { it.copy(selectedCoin = coinUi) }

        viewModelScope.launch {
            coinDataSource
                .getCoinHistory(
                    coinId = coinUi.id,
                    interval = interval,
                    start = ZonedDateTime.now().minusDays(5),
                    end = ZonedDateTime.now()
                )
                .onSuccess { history ->
                    val dataPoints = history
                        .sortedBy { it.dateTime }
                        .map {
                            DataPoint(
                                x = it.dateTime.hour.toFloat(),
                                y = it.priceUsd.toFloat(),
                                xLabel = DateTimeFormatter
                                    .ofPattern("ha\nM/d")
                                    .format(it.dateTime)
                            )
                        }

                    _state.update {
                        it.copy(
                            selectedCoin = it.selectedCoin?.copy(
                                coinPriceHistory = dataPoints
                            )
                        )
                    }
                }
                .onError { error ->
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }

    private fun filteredCoins(input: String) {
        val query = input.trim().lowercase()
        val filteredList = _state.value.coins.filter { coin ->
            coin.name.lowercase().contains(query) || coin.symbol.lowercase().contains(query)
        }
        _state.update {
            it.copy(filteredCoins = filteredList)
        }
    }

    private fun loadCoins() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                ) }

            coinDataSource
                .getCoins()
                .onSuccess { coins ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            coins = coins.map { it.toCoinUi() },
                            filteredCoins = coins.map { it.toCoinUi() }
                        )
                    }
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }
}