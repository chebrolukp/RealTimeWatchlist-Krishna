package com.doximity.realtimewatchlist_krishna_doximity.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.Instrument
import com.doximity.realtimewatchlist_krishna_doximity.domain.usecase.SearchInstrumentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SEARCH_DEBOUNCE_MS = 300L

data class SearchResultUiModel(
    val instrument: Instrument,
    val isInWatchlist: Boolean,
)

data class SearchUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val results: List<SearchResultUiModel> = emptyList(),
    val errorMessage: String? = null,
    val hasSearched: Boolean = false,
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchInstrumentsUseCase: SearchInstrumentsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

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
            delay(SEARCH_DEBOUNCE_MS)
            _uiState.update { it.copy(isSearching = true, hasSearched = true) }
            val result = searchInstrumentsUseCase(query.trim())
            result.fold(
                onSuccess = { instruments ->
                    val enriched = instruments.map { instrument ->
                        SearchResultUiModel(
                            instrument = instrument,
                            isInWatchlist = false, // Set to false for now as per requirement
                        )
                    }
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            results = enriched,
                            errorMessage = null,
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            results = emptyList(),
                            errorMessage = error.message ?: "Search failed",
                        )
                    }
                },
            )
        }
    }

    fun addToWatchlist(instrument: Instrument) {
        // Do nothing for now as per requirement
    }
}
