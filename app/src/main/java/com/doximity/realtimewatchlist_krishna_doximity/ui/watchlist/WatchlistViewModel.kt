package com.doximity.realtimewatchlist_krishna_doximity.ui.watchlist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.ConnectionState
import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.PriceStatus
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.util.toUserMessage
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.WatchlistItem
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.WatchlistOverview
import com.doximity.realtimewatchlist_krishna_doximity.domain.usecase.ObserveWatchlistWithPricesUseCase
import com.doximity.realtimewatchlist_krishna_doximity.domain.usecase.RefreshWatchlistUseCase
import com.doximity.realtimewatchlist_krishna_doximity.domain.usecase.RemoveFromWatchlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    val isRefreshing: Boolean = false,
    val entries: List<WatchlistEntryUiModel> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.Disconnected,
    val errorMessage: String? = null,
)

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    observeWatchlistWithPricesUseCase: ObserveWatchlistWithPricesUseCase,
    private val refreshWatchlistUseCase: RefreshWatchlistUseCase,
    private val removeFromWatchlistUseCase: RemoveFromWatchlistUseCase,
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<WatchlistScreenState> = combine(
        observeWatchlistWithPricesUseCase().map { overview -> overview.toUiState() },
        isRefreshing,
    ) { state, refreshing ->
        state.copy(isRefreshing = refreshing)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WatchlistScreenState(),
    )

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.update { true }
            try {
                refreshWatchlistUseCase()
            } finally {
                isRefreshing.update { false }
            }
        }
    }

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
            errorMessage = error?.toUserMessage(context),
        )
    }
}
