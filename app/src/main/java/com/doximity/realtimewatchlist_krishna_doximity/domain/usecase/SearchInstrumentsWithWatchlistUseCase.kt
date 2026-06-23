package com.doximity.realtimewatchlist_krishna_doximity.domain.usecase

import com.doximity.realtimewatchlist_krishna_doximity.domain.model.SearchResult
import javax.inject.Inject

class SearchInstrumentsWithWatchlistUseCase @Inject constructor(
    private val searchInstrumentsUseCase: SearchInstrumentsUseCase,
) {
    suspend operator fun invoke(
        query: String,
        watchlistSymbols: Set<String>,
    ): Result<List<SearchResult>> =
        searchInstrumentsUseCase(query).map { instruments ->
            instruments.map { instrument ->
                SearchResult(
                    instrument = instrument,
                    isInWatchlist = watchlistSymbols.contains(instrument.symbol),
                )
            }
        }
}
