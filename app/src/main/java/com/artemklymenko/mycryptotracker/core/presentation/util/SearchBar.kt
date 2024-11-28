package com.artemklymenko.mycryptotracker.core.presentation.util

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.artemklymenko.mycryptotracker.R
import com.artemklymenko.mycryptotracker.crypto.presentation.coin_list.CoinListAction
import com.artemklymenko.mycryptotracker.crypto.presentation.coin_list.CoinListState
import com.artemklymenko.mycryptotracker.crypto.presentation.coin_list.components.CoinListItem
import com.artemklymenko.mycryptotracker.ui.theme.MyCryptoTrackerTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarView(
    state: CoinListState,
    onAction: (CoinListAction) -> Unit
) {
    var search by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    BackHandler(enabled = isActive) {
        isActive = false
        focusManager.clearFocus()
    }
    SearchBar(
        query = search,
        onQueryChange = {
            search = it
            onAction(CoinListAction.OnSearchChange(search))
        },
        onSearch = {
            onAction(CoinListAction.OnSearchChange(search))
        },
        active = isActive,
        onActiveChange = {
            isActive = it
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            if (search.isNotEmpty()) {
                IconButton(onClick = {
                    search = ""
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.clear),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.filteredCoins) { coinUi ->
                CoinListItem(
                    coinUi = coinUi,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onAction(CoinListAction.OnCoinClick(coinUi))
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun TopBarPreview() {
    MyCryptoTrackerTheme {
        SearchBarView(state = CoinListState()) {}
    }
}