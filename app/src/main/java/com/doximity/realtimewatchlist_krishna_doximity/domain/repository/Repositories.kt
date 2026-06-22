package com.doximity.realtimewatchlist_krishna_doximity.domain.repository

import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.ConnectionState
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.Instrument
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.PriceUpdate
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.Quote
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.WatchlistItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MarketDataRepository {
    val connectionState: StateFlow<ConnectionState>
    val priceUpdates: Flow<PriceUpdate>

    suspend fun searchInstruments(query: String): Result<List<Instrument>>
    suspend fun getQuote(symbol: String): Result<Quote>
    fun updateLiveSubscriptions(symbols: Set<String>)
}

interface WatchlistRepository {
    fun observeWatchlist(): Flow<List<WatchlistItem>>
    suspend fun addInstrument(instrument: Instrument)
    suspend fun removeInstrument(symbol: String)
    suspend fun isInWatchlist(symbol: String): Boolean
}
