package com.doximity.realtimewatchlist_krishna_doximity.domain.model

import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.ConnectionState
import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.PriceStatus

data class WatchlistOverview(
    val entries: List<WatchlistEntry>,
    val connectionState: ConnectionState,
    val error: Throwable? = null,
)

data class WatchlistEntry(
    val item: WatchlistItem,
    val price: Double?,
    val change: Double?,
    val percentChange: Double?,
    val status: PriceStatus,
    val lastUpdatedMs: Long?,
)
