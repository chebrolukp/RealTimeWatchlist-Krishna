package com.doximity.realtimewatchlist_krishna_doximity.data.local

import com.doximity.realtimewatchlist_krishna_doximity.data.local.entity.WatchlistEntity
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.Instrument
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.WatchlistItem

fun WatchlistEntity.toDomain(): WatchlistItem = WatchlistItem(
    symbol = symbol,
    displaySymbol = displaySymbol,
    description = description,
    type = type,
    addedAtEpochMs = addedAtEpochMs,
)

fun Instrument.toEntity(addedAtEpochMs: Long): WatchlistEntity = WatchlistEntity(
    symbol = symbol,
    displaySymbol = displaySymbol,
    description = description,
    type = type,
    addedAtEpochMs = addedAtEpochMs,
)
