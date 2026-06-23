package com.doximity.realtimewatchlist_krishna_doximity.ui.watchlist

import androidx.compose.runtime.Composable
import com.doximity.realtimewatchlist_krishna_doximity.ui.preview.PreviewSampleData
import com.doximity.realtimewatchlist_krishna_doximity.ui.preview.ScreenPreview
import com.doximity.realtimewatchlist_krishna_doximity.ui.theme.RealtimeWatchListKrishnaDoximityTheme

@ScreenPreview
@Composable
private fun WatchlistScreenEmptyPreview() {
    RealtimeWatchListKrishnaDoximityTheme {
        WatchlistContent(
            uiState = PreviewSampleData.watchlistEmpty,
            onRemove = {},
            onRefresh = {},
        )
    }
}

@ScreenPreview
@Composable
private fun WatchlistScreenLoadingPreview() {
    RealtimeWatchListKrishnaDoximityTheme {
        WatchlistContent(
            uiState = PreviewSampleData.watchlistLoading,
            onRemove = {},
            onRefresh = {},
        )
    }
}

@ScreenPreview
@Composable
private fun WatchlistScreenWithEntriesPreview() {
    RealtimeWatchListKrishnaDoximityTheme {
        WatchlistContent(
            uiState = PreviewSampleData.watchlistWithEntries,
            onRemove = {},
            onRefresh = {},
        )
    }
}

@ScreenPreview
@Composable
private fun WatchlistScreenReconnectingPreview() {
    RealtimeWatchListKrishnaDoximityTheme {
        WatchlistContent(
            uiState = PreviewSampleData.watchlistReconnecting,
            onRemove = {},
            onRefresh = {},
        )
    }
}
