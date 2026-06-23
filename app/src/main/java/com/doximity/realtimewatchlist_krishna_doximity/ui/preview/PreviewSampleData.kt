package com.doximity.realtimewatchlist_krishna_doximity.ui.preview

import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.ConnectionState
import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.PriceStatus
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.model.UiText
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.Instrument
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.SearchResult
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.WatchlistItem
import com.doximity.realtimewatchlist_krishna_doximity.ui.search.SearchUiState
import com.doximity.realtimewatchlist_krishna_doximity.ui.watchlist.WatchlistEntryUiModel
import com.doximity.realtimewatchlist_krishna_doximity.ui.watchlist.WatchlistScreenState

object PreviewSampleData {
    val apple = Instrument("AAPL", "AAPL", "Apple Inc.", "Common Stock")
    val microsoft = Instrument("MSFT", "MSFT", "Microsoft Corp.", "Common Stock")
    val bitcoin = Instrument("BINANCE:BTCUSDT", "BTC/USDT", "Bitcoin / Tether", "Crypto")

    val searchIdle = SearchUiState(
        query = "",
    )

    val searchLoading = SearchUiState(
        query = "AAPL",
        isSearching = true,
        hasSearched = true,
    )

    val searchResults = SearchUiState(
        query = "A",
        results = listOf(
            SearchResult(apple, isInWatchlist = true),
            SearchResult(microsoft, isInWatchlist = false),
            SearchResult(bitcoin, isInWatchlist = false),
        ),
        hasSearched = true,
    )

    val searchEmpty = SearchUiState(
        query = "ZZZZZ",
        results = emptyList(),
        hasSearched = true,
    )

    val searchError = SearchUiState(
        query = "AAPL",
        errorMessage = UiText.Dynamic("Rate limit exceeded. Try again shortly."),
        hasSearched = true,
    )

    val watchlistEmpty = WatchlistScreenState(
        isLoading = false,
        entries = emptyList(),
    )

    val watchlistLoading = WatchlistScreenState(
        isLoading = true,
    )

    val watchlistWithEntries = WatchlistScreenState(
        isLoading = false,
        connectionState = ConnectionState.Connected,
        entries = listOf(
            WatchlistEntryUiModel(
                item = WatchlistItem(
                    symbol = "AAPL",
                    displaySymbol = "AAPL",
                    description = "Apple Inc.",
                    type = "Common Stock",
                    addedAtEpochMs = 0L,
                ),
                price = 190.42,
                change = 2.15,
                percentChange = 1.14,
                status = PriceStatus.Live,
            ),
            WatchlistEntryUiModel(
                item = WatchlistItem(
                    symbol = "MSFT",
                    displaySymbol = "MSFT",
                    description = "Microsoft Corp.",
                    type = "Common Stock",
                    addedAtEpochMs = 0L,
                ),
                price = 420.10,
                change = -1.80,
                percentChange = -0.43,
                status = PriceStatus.Live,
            ),
            WatchlistEntryUiModel(
                item = WatchlistItem(
                    symbol = "BINANCE:BTCUSDT",
                    displaySymbol = "BTC/USDT",
                    description = "Bitcoin / Tether",
                    type = "Crypto",
                    addedAtEpochMs = 0L,
                ),
                price = null,
                change = null,
                percentChange = null,
                status = PriceStatus.Unavailable,
            ),
        ),
    )

    val watchlistReconnecting = watchlistWithEntries.copy(
        connectionState = ConnectionState.Reconnecting,
        entries = watchlistWithEntries.entries.map { entry ->
            if (entry.item.symbol == "AAPL") {
                entry.copy(status = PriceStatus.Stale)
            } else {
                entry
            }
        },
    )
}
