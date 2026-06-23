package com.doximity.realtimewatchlist_krishna_doximity.domain.usecase

import com.doximity.realtimewatchlist_krishna_doximity.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveWatchlistSymbolsUseCase @Inject constructor(
    private val watchlistRepository: WatchlistRepository,
) {
    operator fun invoke(): Flow<Set<String>> =
        watchlistRepository.observeWatchlist()
            .map { items -> items.map { it.symbol }.toSet() }
            .distinctUntilChanged()
}
