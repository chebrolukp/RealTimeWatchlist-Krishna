package com.doximity.realtimewatchlist_krishna_doximity.domain.usecase

import com.doximity.realtimewatchlist_krishna_doximity.domain.repository.WatchlistRepository
import javax.inject.Inject

class IsInWatchlistUseCase @Inject constructor(
    private val watchlistRepository: WatchlistRepository,
) {
    suspend operator fun invoke(symbol: String): Boolean =
        watchlistRepository.isInWatchlist(symbol)
}
