package com.doximity.realtimewatchlist_krishna_doximity.core.ui.util

import android.content.Context
import com.doximity.realtimewatchlist_krishna_doximity.R
import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.ConnectionState
import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.PriceStatus

fun connectionStateLabel(context: Context, state: ConnectionState): String = when (state) {
    ConnectionState.Disconnected -> context.getString(R.string.connection_disconnected)
    ConnectionState.Connecting -> context.getString(R.string.connection_connecting)
    ConnectionState.Connected -> context.getString(R.string.connection_live)
    ConnectionState.Reconnecting -> context.getString(R.string.connection_reconnecting)
}

fun priceStatusLabel(context: Context, status: PriceStatus): String = when (status) {
    PriceStatus.Live -> context.getString(R.string.price_status_live)
    PriceStatus.Stale -> context.getString(R.string.price_status_stale)
    PriceStatus.Unavailable -> context.getString(R.string.price_status_unavailable)
}

fun formatWatchlistEntryContentDescription(
    context: Context,
    displaySymbol: String,
    description: String,
    price: Double?,
    change: Double?,
    percentChange: Double?,
    status: PriceStatus,
    connectionState: ConnectionState,
): String {
    val statusText = buildStatusTextForAccessibility(context, status, connectionState)
    val priceText = price?.let(::formatPrice) ?: context.getString(R.string.a11y_price_unavailable)
    val changeText = if (change != null && percentChange != null) {
        "${formatChange(change)}, ${formatPercentChange(percentChange)}"
    } else {
        context.getString(R.string.a11y_change_unavailable)
    }
    return context.getString(
        R.string.a11y_watchlist_entry,
        displaySymbol,
        description,
        priceText,
        changeText,
        statusText,
    )
}

fun buildWatchlistStatusText(
    context: Context,
    status: PriceStatus,
    connectionState: ConnectionState,
): String = when {
    connectionState == ConnectionState.Reconnecting ->
        context.getString(R.string.reconnecting_with_status, priceStatusLabel(context, status))
    status == PriceStatus.Stale -> context.getString(R.string.price_may_be_stale)
    else -> priceStatusLabel(context, status)
}

private fun buildStatusTextForAccessibility(
    context: Context,
    status: PriceStatus,
    connectionState: ConnectionState,
): String = when {
    connectionState == ConnectionState.Reconnecting ->
        context.getString(
            R.string.a11y_stream_reconnecting,
            priceStatusLabel(context, status),
        )
    status == PriceStatus.Stale -> context.getString(R.string.a11y_price_may_be_stale)
    else -> context.getString(R.string.a11y_price_status, priceStatusLabel(context, status))
}
