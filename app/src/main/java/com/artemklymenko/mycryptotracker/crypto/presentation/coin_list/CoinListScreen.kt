package com.artemklymenko.mycryptotracker.crypto.presentation.coin_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.artemklymenko.mycryptotracker.core.presentation.util.SearchBarView
import com.artemklymenko.mycryptotracker.crypto.presentation.coin_list.components.CoinListItem
import com.artemklymenko.mycryptotracker.crypto.presentation.coin_list.components.previewCoin
import com.artemklymenko.mycryptotracker.ui.theme.MyCryptoTrackerTheme

@Composable
fun CoinListScreen(
    state: CoinListState,
    onAction: (CoinListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            topBar = {
                SearchBarView(
                    state = state,
                    onAction = onAction
                )
            },
            modifier = modifier
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.coins) { coinUi ->
                    CoinListItem(
                        coinUi = coinUi,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("coinItem_${coinUi.id}"),
                        onClick = {
                            onAction(CoinListAction.OnCoinClick(coinUi))
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CoinListPreview() {
    MyCryptoTrackerTheme {
        CoinListScreen(
            state = CoinListState(
                coins = (1..20).map {
                    previewCoin.copy(id = it.toString())
                }
            ),
            modifier = Modifier.background(
                MaterialTheme.colorScheme.background
            ),
            onAction = {}
        )
    }
}