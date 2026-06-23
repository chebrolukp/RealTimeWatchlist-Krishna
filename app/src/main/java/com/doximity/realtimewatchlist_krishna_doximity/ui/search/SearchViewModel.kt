package com.doximity.realtimewatchlist_krishna_doximity.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.model.UiText
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.util.toUiText
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.Instrument
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.SearchResult
import com.doximity.realtimewatchlist_krishna_doximity.domain.usecase.AddToWatchlistUseCase
import com.doximity.realtimewatchlist_krishna_doximity.domain.usecase.ObserveWatchlistSymbolsUseCase
import com.doximity.realtimewatchlist_krishna_doximity.domain.usecase.SearchInstrumentsWithWatchlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

private const val SEARCH_DEBOUNCE_MS = 300L

data class SearchUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val results: List<SearchResult> = emptyList(),
    val errorMessage: UiText? = null,
    val hasSearched: Boolean = false,
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchInstrumentsWithWatchlistUseCase: SearchInstrumentsWithWatchlistUseCase,
    private val observeWatchlistSymbolsUseCase: ObserveWatchlistSymbolsUseCase,
    private val addToWatchlistUseCase: AddToWatchlistUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var watchlistSymbols = emptySet<String>()

    init {
        viewModelScope.launch {
            observeWatchlistSymbolsUseCase().collect { symbols ->
                watchlistSymbols = symbols
                _uiState.update { state ->
                    state.copy(
                        results = state.results.map { result ->
                            result.copy(isInWatchlist = symbols.contains(result.instrument.symbol))
                        },
                    )
                }
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query, errorMessage = null) }
        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.update {
                it.copy(
                    isSearching = false,
                    results = emptyList(),
                    hasSearched = false,
                )
            }
            return
        }

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS.milliseconds)
            _uiState.update { it.copy(isSearching = true, hasSearched = true) }
            val result = searchInstrumentsWithWatchlistUseCase(query.trim(), watchlistSymbols)
            result.fold(
                onSuccess = { results ->
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            results = results,
                            errorMessage = null,
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            results = emptyList(),
                            errorMessage = error.toUiText(),
                        )
                    }
                },
            )
        }
    }

    fun addToWatchlist(instrument: Instrument) {
        viewModelScope.launch {
            addToWatchlistUseCase(instrument)
        }
    }
}
