package com.doximity.realtimewatchlist_krishna_doximity.domain.interactor

import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.ConnectionState
import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.PriceStatus
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.PriceUpdate
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.Quote
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.WatchlistEntry
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.WatchlistItem
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.WatchlistOverview
import com.doximity.realtimewatchlist_krishna_doximity.domain.repository.MarketDataRepository
import com.doximity.realtimewatchlist_krishna_doximity.domain.repository.WatchlistRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class WatchlistInteractor @Inject constructor(
    private val watchlistRepository: WatchlistRepository,
    private val marketDataRepository: MarketDataRepository,
    @Named("applicationScope") private val applicationScope: CoroutineScope,
) {
    private val quoteSnapshots = MutableStateFlow<Map<String, QuoteSnapshot>>(emptyMap())
    private val livePrices = MutableStateFlow<Map<String, LivePrice>>(emptyMap())
    private var priceUpdatesJob: Job? = null
    private var watchlistJob: Job? = null

    private val _overview = MutableStateFlow(
        WatchlistOverview(emptyList(), ConnectionState.Disconnected),
    )
    val overview: StateFlow<WatchlistOverview> = _overview.asStateFlow()

    fun start() {
        if (watchlistJob != null) return

        watchlistJob = applicationScope.launch {
            combine(
                watchlistRepository.observeWatchlist(),
                quoteSnapshots,
                livePrices,
                marketDataRepository.connectionState,
            ) { items, snapshots, live, connection ->
                buildOverview(items, snapshots, live, connection)
            }.collectLatest { overview ->
                _overview.value = overview
            }
        }

        applicationScope.launch {
            watchlistRepository.observeWatchlist().collectLatest { items ->
                val symbols = items.map { it.symbol }.toSet()
                marketDataRepository.updateLiveSubscriptions(symbols)
                refreshQuotes(items)
                restartPriceUpdates()
            }
        }
    }

    fun removeSymbol(symbol: String) {
        applicationScope.launch {
            watchlistRepository.removeInstrument(symbol)
        }
    }

    private suspend fun refreshQuotes(items: List<WatchlistItem>) {
        if (items.isEmpty()) {
            quoteSnapshots.value = emptyMap()
            livePrices.value = emptyMap()
            return
        }

        val snapshots = items.associate { item ->
            val result = marketDataRepository.getQuote(item.symbol)
            item.symbol to result.fold(
                onSuccess = { quote ->
                    QuoteSnapshot(
                        quote = quote,
                        errorMessage = null,
                    )
                },
                onFailure = { error ->
                    QuoteSnapshot(
                        quote = null,
                        errorMessage = error.message,
                    )
                },
            )
        }
        quoteSnapshots.value = snapshots

        val initialLive = snapshots.mapNotNull { (symbol, snapshot) ->
            snapshot.quote?.takeIf { it.hasPrice }?.let { quote ->
                symbol to LivePrice(
                    price = quote.currentPrice,
                    change = quote.change,
                    percentChange = quote.percentChange,
                    timestampMs = quote.timestampSeconds * 1_000,
                )
            }
        }.toMap()
        if (initialLive.isNotEmpty()) {
            livePrices.update { current -> current + initialLive }
        }
    }

    private fun restartPriceUpdates() {
        priceUpdatesJob?.cancel()
        priceUpdatesJob = applicationScope.launch {
            marketDataRepository.priceUpdates.collect { update ->
                applyLiveUpdate(update)
            }
        }
    }

    private fun applyLiveUpdate(update: PriceUpdate) {
        val snapshot = quoteSnapshots.value[update.symbol]?.quote
        val previousClose = snapshot?.previousClose
        val change = previousClose?.let { update.price - it }
        val percentChange = if (previousClose != null && previousClose != 0.0 && change != null) {
            (change / previousClose) * 100
        } else {
            snapshot?.percentChange
        }

        livePrices.update { current ->
            current + (
                update.symbol to LivePrice(
                    price = update.price,
                    change = change ?: snapshot?.change,
                    percentChange = percentChange ?: snapshot?.percentChange,
                    timestampMs = update.timestampMs,
                )
                )
        }
    }

    private fun buildOverview(
        items: List<WatchlistItem>,
        snapshots: Map<String, QuoteSnapshot>,
        live: Map<String, LivePrice>,
        connection: ConnectionState,
    ): WatchlistOverview {
        val now = System.currentTimeMillis()
        val firstError = snapshots.values.firstOrNull { it.errorMessage != null }?.errorMessage

        val entries = items.map { item ->
            val snapshot = snapshots[item.symbol]
            val livePrice = live[item.symbol]
            val quote = snapshot?.quote

            val price = livePrice?.price ?: quote?.currentPrice
            val change = livePrice?.change ?: quote?.change
            val percentChange = livePrice?.percentChange ?: quote?.percentChange
            val lastUpdatedMs = livePrice?.timestampMs ?: quote?.timestampSeconds?.times(1_000)

            val status = when {
                snapshot?.errorMessage != null && price == null -> PriceStatus.Unavailable
                price == null || price <= 0.0 -> PriceStatus.Unavailable
                livePrice == null && quote?.hasPrice == true -> PriceStatus.Stale
                lastUpdatedMs != null && now - lastUpdatedMs > STALE_THRESHOLD_MS -> PriceStatus.Stale
                connection == ConnectionState.Reconnecting && livePrice == null -> PriceStatus.Stale
                connection == ConnectionState.Reconnecting -> PriceStatus.Live
                else -> PriceStatus.Live
            }

            WatchlistEntry(
                item = item,
                price = price?.takeIf { it > 0.0 },
                change = change,
                percentChange = percentChange,
                status = status,
                lastUpdatedMs = lastUpdatedMs,
            )
        }

        return WatchlistOverview(
            entries = entries,
            connectionState = if (items.isEmpty()) ConnectionState.Disconnected else connection,
            errorMessage = firstError,
        )
    }

    private data class QuoteSnapshot(
        val quote: Quote?,
        val errorMessage: String?,
    )

    private data class LivePrice(
        val price: Double,
        val change: Double?,
        val percentChange: Double?,
        val timestampMs: Long,
    )

    companion object {
        const val STALE_THRESHOLD_MS = 30_000L
    }
}
