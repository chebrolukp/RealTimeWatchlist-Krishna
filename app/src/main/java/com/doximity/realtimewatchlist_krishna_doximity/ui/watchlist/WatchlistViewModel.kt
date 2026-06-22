package com.doximity.realtimewatchlist_krishna_doximity.ui.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.ConnectionState
import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.PriceStatus
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.WatchlistItem
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.WatchlistOverview
import com.doximity.realtimewatchlist_krishna_doximity.domain.usecase.ObserveWatchlistWithPricesUseCase
import com.doximity.realtimewatchlist_krishna_doximity.domain.usecase.RemoveFromWatchlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class WatchlistEntryUiModel(
    val item: WatchlistItem,
    val price: Double?,
    val change: Double?,
    val percentChange: Double?,
    val status: PriceStatus,
)

data class WatchlistScreenState(
    val isLoading: Boolean = true,
    val entries: List<WatchlistEntryUiModel> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.Disconnected,
    val errorMessage: String? = null,
)

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    observeWatchlistWithPricesUseCase: ObserveWatchlistWithPricesUseCase,
    private val removeFromWatchlistUseCase: RemoveFromWatchlistUseCase,
) : ViewModel() {

    val uiState: StateFlow<WatchlistScreenState> = observeWatchlistWithPricesUseCase()
        .map { overview -> overview.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WatchlistScreenState(),
        )

    fun removeSymbol(symbol: String) {
        removeFromWatchlistUseCase(symbol)
    }

    private fun WatchlistOverview.toUiState(): WatchlistScreenState {
        return WatchlistScreenState(
            isLoading = false,
            entries = entries.map { entry ->
                WatchlistEntryUiModel(
                    item = entry.item,
                    price = entry.price,
                    change = entry.change,
                    percentChange = entry.percentChange,
                    status = entry.status,
                )
            },
            connectionState = connectionState,
            errorMessage = errorMessage,
        )
    }
}
