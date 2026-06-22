package com.doximity.realtimewatchlist_krishna_doximity.domain.usecase

import com.doximity.realtimewatchlist_krishna_doximity.domain.interactor.WatchlistInteractor
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.WatchlistOverview
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveWatchlistWithPricesUseCase @Inject constructor(
    private val watchlistInteractor: WatchlistInteractor,
) {
    operator fun invoke(): StateFlow<WatchlistOverview> {
        watchlistInteractor.start()
        return watchlistInteractor.overview
    }
}
