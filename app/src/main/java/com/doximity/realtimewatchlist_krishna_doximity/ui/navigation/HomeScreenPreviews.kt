package com.doximity.realtimewatchlist_krishna_doximity.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.doximity.realtimewatchlist_krishna_doximity.ui.preview.PreviewSampleData
import com.doximity.realtimewatchlist_krishna_doximity.ui.preview.ScreenPreview
import com.doximity.realtimewatchlist_krishna_doximity.ui.search.SearchContent
import com.doximity.realtimewatchlist_krishna_doximity.ui.theme.RealtimeWatchListKrishnaDoximityTheme
import com.doximity.realtimewatchlist_krishna_doximity.ui.watchlist.WatchlistContent

@ScreenPreview
@Composable
private fun HomeScreenWatchlistTabPreview() {
    RealtimeWatchListKrishnaDoximityTheme {
        HomeScreenPreviewShell(
            selectedRoute = AppDestination.Watchlist.route,
        ) { padding ->
            WatchlistContent(
                uiState = PreviewSampleData.watchlistWithEntries,
                onRemove = {},
                onRefresh = {},
                modifier = padding,
            )
        }
    }
}

@ScreenPreview
@Composable
private fun HomeScreenSearchTabPreview() {
    RealtimeWatchListKrishnaDoximityTheme {
        HomeScreenPreviewShell(
            selectedRoute = AppDestination.Search.route,
        ) { padding ->
            SearchContent(
                uiState = PreviewSampleData.searchResults,
                onQueryChange = {},
                onAdd = {},
                modifier = padding,
            )
        }
    }
}

@Composable
private fun HomeScreenPreviewShell(
    selectedRoute: String,
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        bottomBar = {
            HomeBottomBar(
                selectedRoute = selectedRoute,
                onDestinationSelected = {},
            )
        },
    ) { innerPadding ->
        content(Modifier.padding(innerPadding))
    }
}
