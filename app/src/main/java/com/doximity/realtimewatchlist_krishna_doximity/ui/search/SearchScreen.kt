package com.doximity.realtimewatchlist_krishna_doximity.ui.search

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doximity.realtimewatchlist_krishna_doximity.R
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.adaptive.AdaptiveContentContainer
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.adaptive.adaptiveContentPadding
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.adaptive.adaptiveListColumnCount
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.components.EmptyState
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.components.ErrorBanner
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.components.LoadingIndicator
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.model.asString
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.Instrument
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.SearchResult
import com.doximity.realtimewatchlist_krishna_doximity.ui.theme.CardBackground
import com.doximity.realtimewatchlist_krishna_doximity.ui.theme.ListItemBackground
import com.doximity.realtimewatchlist_krishna_doximity.ui.theme.PageBackground

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
    val contentPadding = adaptiveContentPadding()
    val columnCount = adaptiveListColumnCount()
    val searchHint = stringResource(R.string.search_hint)
    val searchLoadingMessage = stringResource(R.string.search_loading)

    AdaptiveContentContainer(
        modifier = modifier
            .fillMaxSize()
            .background(PageBackground),
        applyHorizontalPadding = false,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
                label = { Text(stringResource(R.string.search_label)) },
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                singleLine = true,
                supportingText = {
                    Text(searchHint)
                },
            )

            uiState.errorMessage?.let { message ->
                ErrorBanner(
                    message = message.asString(),
                    modifier = Modifier.padding(horizontal = contentPadding),
                )
            }

            when {
                uiState.isSearching -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        LoadingIndicator(message = searchLoadingMessage)
                    }
                }

                uiState.query.isBlank() -> {
                    EmptyState(
                        title = stringResource(R.string.search_empty_title),
                        message = searchHint,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                uiState.hasSearched && uiState.results.isEmpty() && uiState.errorMessage == null -> {
                    EmptyState(
                        title = stringResource(R.string.search_no_results_title),
                        message = stringResource(R.string.search_no_results_message),
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                else -> {
                    val listPadding = PaddingValues(
                        horizontal = contentPadding,
                        vertical = contentPadding / 2,
                    )
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
                                items = uiState.results,
                                key = { it.instrument.symbol },
                            ) { result ->
                                SearchResultCard(
                                    result = result,
                                    onAdd = onAdd,
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = listPadding,
                            verticalArrangement = listSpacing,
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
    }
}

@Composable
private fun SearchResultCard(
    result: SearchResult,
    onAdd: (Instrument) -> Unit,
) {
    val instrument = result.instrument
    val inWatchlistSuffix = stringResource(R.string.already_in_watchlist_suffix)
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = false) {
                contentDescription = buildString {
                    append(instrument.displaySymbol)
                    append(", ")
                    append(instrument.description)
                    append(", ")
                    append(instrument.type)
                    if (result.isInWatchlist) {
                        append(". ")
                        append(inWatchlistSuffix)
                    }
                }
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
                .background(ListItemBackground)
                .padding(adaptiveContentPadding()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(adaptiveContentPadding()),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = instrument.displaySymbol,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = instrument.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = instrument.type,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            AddToWatchlistButton(
                displaySymbol = instrument.displaySymbol,
                isInWatchlist = result.isInWatchlist,
                onClick = { onAdd(instrument) },
            )
        }
    }
}

@Composable
private fun AddToWatchlistButton(
    displaySymbol: String,
    isInWatchlist: Boolean,
    onClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val actionDescription = if (isInWatchlist) {
        stringResource(R.string.already_in_watchlist, displaySymbol)
    } else {
        stringResource(R.string.add_to_watchlist, displaySymbol)
    }
    val addedStateLabel = if (isInWatchlist) {
        stringResource(R.string.state_added)
    } else {
        stringResource(R.string.state_not_added)
    }

    Surface(
        onClick = onClick,
        enabled = !isInWatchlist,
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .size(48.dp)
            .semantics {
                contentDescription = actionDescription
                this[SemanticsProperties.StateDescription] = addedStateLabel
            },
        shape = RoundedCornerShape(8.dp),
        color = colorScheme.surfaceContainerLow,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (isInWatchlist) Icons.Default.Check else Icons.Default.Add,
                contentDescription = null,
                tint = colorScheme.primary,
            )
        }
    }
}
