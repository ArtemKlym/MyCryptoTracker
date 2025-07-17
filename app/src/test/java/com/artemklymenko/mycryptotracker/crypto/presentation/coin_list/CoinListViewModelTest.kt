package com.artemklymenko.mycryptotracker.crypto.presentation.coin_list

import com.artemklymenko.mycryptotracker.crypto.domain.Coin
import com.artemklymenko.mycryptotracker.crypto.domain.FakeCoinDataSource
import com.artemklymenko.mycryptotracker.crypto.presentation.coin_list.CoinListState
import com.artemklymenko.mycryptotracker.crypto.presentation.coin_list.CoinListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
}