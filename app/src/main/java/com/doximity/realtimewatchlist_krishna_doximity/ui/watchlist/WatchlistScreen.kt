package com.doximity.realtimewatchlist_krishna_doximity.ui.watchlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.ConnectionState
import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.PriceStatus
import com.doximity.realtimewatchlist_krishna_doximity.R
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.adaptive.AdaptiveContentContainer
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.adaptive.adaptiveContentPadding
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.adaptive.adaptiveListColumnCount
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.components.ConnectionBanner
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.components.EmptyState
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.components.ErrorBanner
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.components.LoadingIndicator
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.util.buildWatchlistStatusText
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.util.connectionStateLabel
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.util.formatChange
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.util.formatPercentChange
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.util.formatPrice
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.util.formatWatchlistEntryContentDescription
import com.doximity.realtimewatchlist_krishna_doximity.ui.theme.Error
import com.doximity.realtimewatchlist_krishna_doximity.ui.theme.Tertiary

@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WatchlistContent(
        uiState = uiState,
        onRemove = viewModel::removeSymbol,
        onRefresh = viewModel::refresh,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistContent(
    uiState: WatchlistScreenState,
    onRemove: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentPadding = adaptiveContentPadding()
    val columnCount = adaptiveListColumnCount()
    val context = LocalContext.current
    val watchlistLoadingMessage = stringResource(R.string.watchlist_loading)

    AdaptiveContentContainer(
        modifier = modifier,
        applyHorizontalPadding = false,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (uiState.entries.isNotEmpty()) {
                ConnectionBanner(
                    label = connectionStateLabel(context, uiState.connectionState),
                    modifier = Modifier.padding(horizontal = contentPadding),
                )
            }

            uiState.errorMessage?.let { message ->
                ErrorBanner(
                    message = message,
                    modifier = Modifier.padding(horizontal = contentPadding),
                )
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        LoadingIndicator(message = watchlistLoadingMessage)
                    }
                }

                else -> {
                    val pullToRefreshState = rememberPullToRefreshState()
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = onRefresh,
                        state = pullToRefreshState,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        when {
                            uiState.entries.isEmpty() -> {
                                EmptyState(
                                    title = stringResource(R.string.watchlist_empty_title),
                                    message = stringResource(R.string.watchlist_empty_message),
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }

                            else -> {
                                val listPadding = PaddingValues(contentPadding)
                                val listSpacing = Arrangement.spacedBy(contentPadding / 2)

                                if (columnCount > 1) {
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(columnCount),
                                        modifier = Modifier.fillMaxSize(),
                                        contentPadding = listPadding,
                                        horizontalArrangement = listSpacing,
                                        verticalArrangement = listSpacing,
                                    ) {
                                        items(
                                            items = uiState.entries,
                                            key = { it.item.symbol },
                                        ) { entry ->
                                            WatchlistItemCard(
                                                entry = entry,
                                                connectionState = uiState.connectionState,
                                                onRemove = onRemove,
                                            )
                                        }
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        contentPadding = listPadding,
                                        verticalArrangement = listSpacing,
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
    val context = LocalContext.current
    val statusText = buildWatchlistStatusText(context, entry.status, connectionState)
    val entryDescription = formatWatchlistEntryContentDescription(
        context = context,
        displaySymbol = entry.item.displaySymbol,
        description = entry.item.description,
        price = entry.price,
        change = entry.change,
        percentChange = entry.percentChange,
        status = entry.status,
        connectionState = connectionState,
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = false) {
                contentDescription = entryDescription
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(adaptiveContentPadding()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(adaptiveContentPadding() / 2),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = entry.item.displaySymbol,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = entry.item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor(entry.status),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = entry.price?.let(::formatPrice) ?: "—",
                    style = MaterialTheme.typography.titleMedium,
                )
                entry.change?.let { change ->
                    val changePrefix = stringResource(
                        if (change >= 0) R.string.price_up else R.string.price_down,
                    )
                    val changeColor = if (change >= 0) Tertiary else Error
                    val formattedChange = formatChange(change)
                    val formattedPercent = formatPercentChange(entry.percentChange ?: 0.0)
                    Text(
                        text = "$formattedChange ($formattedPercent)",
                        style = MaterialTheme.typography.bodySmall,
                        color = changeColor,
                        modifier = Modifier.semantics {
                            contentDescription = stringResource(
                                R.string.a11y_price_change,
                                changePrefix,
                                formattedChange,
                                formattedPercent,
                            )
                        },
                    )
                }
            }

            IconButton(
                onClick = { onRemove(entry.item.symbol) },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .semantics {
                        contentDescription = stringResource(
                            R.string.remove_from_watchlist,
                            entry.item.displaySymbol,
                        )
                    },
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun statusColor(status: PriceStatus) = when (status) {
    PriceStatus.Live -> MaterialTheme.colorScheme.primary
    PriceStatus.Stale -> MaterialTheme.colorScheme.tertiary
    PriceStatus.Unavailable -> MaterialTheme.colorScheme.error
}
