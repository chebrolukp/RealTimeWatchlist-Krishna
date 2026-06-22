package com.doximity.realtimewatchlist_krishna_doximity.data.repository

import com.doximity.realtimewatchlist_krishna_doximity.domain.model.Instrument
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.WatchlistItem
import com.doximity.realtimewatchlist_krishna_doximity.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryWatchlistRepository @Inject constructor() : WatchlistRepository {

    private val items = MutableStateFlow<List<WatchlistItem>>(emptyList())

    override fun observeWatchlist(): Flow<List<WatchlistItem>> = items.asStateFlow()

    override suspend fun addInstrument(instrument: Instrument) {
        items.update { current ->
            if (current.any { it.symbol == instrument.symbol }) {
                current
            } else {
                current + instrument.toWatchlistItem()
            }
        }
    }

    override suspend fun removeInstrument(symbol: String) {
        items.update { current -> current.filterNot { it.symbol == symbol } }
    }

    override suspend fun isInWatchlist(symbol: String): Boolean =
        items.value.any { it.symbol == symbol }

    private fun Instrument.toWatchlistItem(): WatchlistItem = WatchlistItem(
        symbol = symbol,
        displaySymbol = displaySymbol,
        description = description,
        type = type,
        addedAtEpochMs = System.currentTimeMillis(),
    )
}
