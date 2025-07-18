package com.artemklymenko.mycryptotracker.crypto.presentation.coin_list

import com.artemklymenko.mycryptotracker.crypto.domain.Coin
import com.artemklymenko.mycryptotracker.crypto.domain.CoinPrice
import com.artemklymenko.mycryptotracker.crypto.domain.FakeCoinDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.ZonedDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class CoinListViewModelTest {

    private lateinit var fakeCoinDataSource: FakeCoinDataSource
    private lateinit var viewModel: CoinListViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeCoinDataSource = FakeCoinDataSource().apply {
            addCoins(
                Coin("btc", 1, "Bitcoin", "BTC", 1000000000.0, 50000.0, 1.5),
                Coin("eth", 2, "Ethereum", "ETH", 500000000.0, 3000.0, -0.5)
            )
        }
        viewModel = CoinListViewModel(fakeCoinDataSource)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCoins should update state with coins`() = testScope.runTest {
        val collectedStates = mutableListOf<CoinListState>()

        val job = launch {
            viewModel.state.take(2).collect {
                collectedStates.add(it)
            }
        }

        advanceUntilIdle()

        job.cancel()

        val finalState = collectedStates.last()
        assertEquals(2, finalState.coins.size)
        assertEquals("Bitcoin", finalState.coins[0].name)
    }

    @Test
    fun `setInterval loads history for selected coin`() = testScope.runTest {
        val coinId = "btc"
        val now = ZonedDateTime.now()

        val history = listOf(
            CoinPrice(dateTime = now.minusMinutes(3), priceUsd = 47000.0),
            CoinPrice(dateTime = now.minusMinutes(2), priceUsd = 48000.0),
            CoinPrice(dateTime = now.minusMinutes(1), priceUsd = 49000.0),
        )

        fakeCoinDataSource.setCoinHistory(coinId, history)

        viewModel.state
            .filter { it.coins.isNotEmpty() }
            .first()

        val coinUi = viewModel.state.value.coins.first { it.id == coinId }

        viewModel.onAction(CoinListAction.OnCoinClick(coinUi))
        advanceUntilIdle()

        viewModel.onAction(CoinListAction.OnIntervalChange("m5"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("m1", state.selectedInterval)
        assertEquals(3, state.selectedCoin?.coinPriceHistory?.size)
    }
}