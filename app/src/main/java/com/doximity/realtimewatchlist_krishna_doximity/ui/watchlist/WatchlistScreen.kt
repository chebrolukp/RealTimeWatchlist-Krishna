package com.doximity.realtimewatchlist_krishna_doximity.ui.watchlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.ConnectionState
import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.PriceStatus
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.components.ConnectionBanner
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.components.EmptyState
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.components.ErrorBanner
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.util.connectionStateLabel
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.util.formatChange
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.util.formatPercentChange
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.util.formatPrice
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.util.priceStatusLabel

@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WatchlistContent(
        uiState = uiState,
        onRemove = viewModel::removeSymbol,
        modifier = modifier,
    )
}

@Composable
fun WatchlistContent(
    uiState: WatchlistScreenState,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (uiState.entries.isNotEmpty()) {
            ConnectionBanner(label = connectionStateLabel(uiState.connectionState))
        }

        uiState.errorMessage?.let { message ->
            ErrorBanner(message = message)
        }

        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.entries.isEmpty() -> {
                EmptyState(
                    title = "Watchlist is empty",
                    message = "Search for stocks, crypto, or forex and add symbols to track live prices.",
                    modifier = Modifier.fillMaxSize(),
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(uiState.entries, key = { it.item.symbol }) { entry ->
                        WatchlistItemCard(
                            entry = entry,
                            connectionState = uiState.connectionState,
                            onRemove = onRemove,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WatchlistItemCard(
    entry: WatchlistEntryUiModel,
    connectionState: ConnectionState,
    onRemove: (String) -> Unit,
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
                    text = entry.item.displaySymbol,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = entry.item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = buildStatusText(entry.status, connectionState),
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor(entry.status),
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = entry.price?.let(::formatPrice) ?: "—",
                    style = MaterialTheme.typography.titleMedium,
                )
                entry.change?.let { change ->
                    val changeColor = if (change >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                    Text(
                        text = "${formatChange(change)} (${formatPercentChange(entry.percentChange ?: 0.0)})",
                        style = MaterialTheme.typography.bodySmall,
                        color = changeColor,
                    )
                }
            }

            IconButton(onClick = { onRemove(entry.item.symbol) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove ${entry.item.displaySymbol}",
                )
            }
        }
    }
}

private fun buildStatusText(status: PriceStatus, connectionState: ConnectionState): String {
    return when {
        connectionState == ConnectionState.Reconnecting -> "Reconnecting — ${priceStatusLabel(status)}"
        status == PriceStatus.Stale -> "Price may be stale"
        else -> priceStatusLabel(status)
    }
}

@Composable
private fun statusColor(status: PriceStatus): Color = when (status) {
    PriceStatus.Live -> MaterialTheme.colorScheme.primary
    PriceStatus.Stale -> MaterialTheme.colorScheme.tertiary
    PriceStatus.Unavailable -> MaterialTheme.colorScheme.error
}
