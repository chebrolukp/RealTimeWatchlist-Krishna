package com.doximity.realtimewatchlist_krishna_doximity.domain.usecase

import com.doximity.realtimewatchlist_krishna_doximity.domain.interactor.WatchlistInteractor
import javax.inject.Inject

class RefreshWatchlistUseCase @Inject constructor(
    private val watchlistInteractor: WatchlistInteractor,
) {
    suspend operator fun invoke() {
        watchlistInteractor.refresh()
    }
}
