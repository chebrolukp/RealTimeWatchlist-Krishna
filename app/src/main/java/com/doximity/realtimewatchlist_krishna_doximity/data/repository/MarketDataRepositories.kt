package com.doximity.realtimewatchlist_krishna_doximity.data.repository

import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.ConnectionState
import com.doximity.realtimewatchlist_krishna_doximity.data.remote.FinnhubApi
import com.doximity.realtimewatchlist_krishna_doximity.data.remote.dto.toDomain
import com.doximity.realtimewatchlist_krishna_doximity.data.remote.requireFinnhubBody
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.Instrument
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.PriceUpdate
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.Quote
import com.doximity.realtimewatchlist_krishna_doximity.domain.repository.MarketDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinnhubMarketDataRepository @Inject constructor(
    private val finnhubApi: FinnhubApi,
) : MarketDataRepository {

    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    override val priceUpdates: Flow<PriceUpdate> = emptyFlow()

    override suspend fun searchInstruments(query: String): Result<List<Instrument>> =
        runCatching {
            if (query.isBlank()) return@runCatching emptyList()
            finnhubApi.searchSymbols(query).requireFinnhubBody().result.map { it.toDomain() }
        }

    override suspend fun getQuote(symbol: String): Result<Quote> =
        runCatching { finnhubApi.getQuote(symbol).requireFinnhubBody().toDomain() }

    override fun updateLiveSubscriptions(symbols: Set<String>) {
        // Not implemented for now
    }
}
