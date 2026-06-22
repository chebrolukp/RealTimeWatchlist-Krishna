package com.doximity.realtimewatchlist_krishna_doximity.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.components.EmptyState
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.components.ErrorBanner
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.Instrument

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SearchContent(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onAdd = viewModel::addToWatchlist,
        modifier = modifier,
    )
}

@Composable
fun SearchContent(
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onAdd: (Instrument) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            label = { Text("Search instruments") },
            placeholder = { Text("AAPL, BTC, EUR/USD…") },
            singleLine = true,
        )

        uiState.errorMessage?.let { message ->
            ErrorBanner(message = message)
        }

        when {
            uiState.isSearching -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.query.isBlank() -> {
                EmptyState(
                    title = "Find an instrument",
                    message = "Search by ticker or company name. Results come from Finnhub.",
                    modifier = Modifier.fillMaxSize(),
                )
            }

            uiState.hasSearched && uiState.results.isEmpty() && uiState.errorMessage == null -> {
                EmptyState(
                    title = "No results",
                    message = "Try another symbol or company name.",
                    modifier = Modifier.fillMaxSize(),
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(uiState.results, key = { it.instrument.symbol }) { result ->
                        SearchResultCard(
                            result = result,
                            onAdd = onAdd,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultCard(
    result: SearchResultUiModel,
    onAdd: (Instrument) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = result.instrument.displaySymbol,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = result.instrument.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = result.instrument.type,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            AddToWatchlistButton(
                isInWatchlist = result.isInWatchlist,
                onClick = { onAdd(result.instrument) },
            )
        }
    }
}

@Composable
private fun AddToWatchlistButton(
    isInWatchlist: Boolean,
    onClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        onClick = onClick,
        enabled = !isInWatchlist,
        modifier = Modifier.size(40.dp),
        shape = RoundedCornerShape(8.dp),
        color = colorScheme.surfaceContainerLow,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (isInWatchlist) Icons.Default.Check else Icons.Default.Add,
                contentDescription = if (isInWatchlist) {
                    "Already in watchlist"
                } else {
                    "Add to watchlist"
                },
                tint = if (isInWatchlist) {
                    colorScheme.onSurfaceVariant
                } else {
                    colorScheme.primary
                },
            )
        }
    }
}
