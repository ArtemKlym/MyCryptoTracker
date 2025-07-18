package com.artemklymenko.mycryptotracker.core.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.artemklymenko.mycryptotracker.crypto.domain.Coin
import com.artemklymenko.mycryptotracker.crypto.domain.FakeCoinDataSourceAndroid
import com.artemklymenko.mycryptotracker.crypto.presentation.coin_list.CoinListViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class AdaptiveCoinListDetailPaneTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun clickingOnCoinNavigatesToDetailScreen() = runTest {
        val fakeDataSource = FakeCoinDataSourceAndroid().apply {
            addCoins(
                Coin("btc", 1, "Bitcoin", "BTC", 1000000000.0, 50000.0, 1.5),
                Coin("eth", 2, "Ethereum", "ETH", 500000000.0, 3000.0, -0.5)
            )
        }

        val viewModel = CoinListViewModel(fakeDataSource)

        composeTestRule.setContent {
            AdaptiveCoinListDetailPane(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("coinItem_btc").assertIsDisplayed()

        composeTestRule.onNodeWithTag("coinItem_btc").performClick()

        composeTestRule.onNodeWithTag("coinDetailScreen").assertIsDisplayed()
    }

}