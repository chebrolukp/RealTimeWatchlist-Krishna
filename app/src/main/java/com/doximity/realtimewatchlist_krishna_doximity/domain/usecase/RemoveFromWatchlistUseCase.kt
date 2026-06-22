package com.doximity.realtimewatchlist_krishna_doximity.domain.usecase

import com.doximity.realtimewatchlist_krishna_doximity.domain.interactor.WatchlistInteractor
import javax.inject.Inject

class RemoveFromWatchlistUseCase @Inject constructor(
    private val watchlistInteractor: WatchlistInteractor,
) {
    operator fun invoke(symbol: String) {
        watchlistInteractor.removeSymbol(symbol)
    }
}
