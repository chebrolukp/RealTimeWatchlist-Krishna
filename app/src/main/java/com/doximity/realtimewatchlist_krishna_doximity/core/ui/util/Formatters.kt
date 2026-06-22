package com.doximity.realtimewatchlist_krishna_doximity.core.ui.util

import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.ConnectionState
import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.PriceStatus
import java.text.NumberFormat
import java.util.Locale

private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
private val percentFormat = NumberFormat.getNumberInstance(Locale.US).apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
}

fun formatPrice(price: Double): String = when {
    price >= 1 -> currencyFormat.format(price)
    else -> String.format(Locale.US, "%.4f", price)
}

fun formatChange(change: Double): String {
    val prefix = if (change >= 0) "+" else ""
    return prefix + currencyFormat.format(change)
}

fun formatPercentChange(percent: Double): String {
    val prefix = if (percent >= 0) "+" else ""
    return prefix + percentFormat.format(percent) + "%"
}

fun connectionStateLabel(state: ConnectionState): String = when (state) {
    ConnectionState.Disconnected -> "Disconnected"
    ConnectionState.Connecting -> "Connecting…"
    ConnectionState.Connected -> "Live"
    ConnectionState.Reconnecting -> "Reconnecting…"
}

fun priceStatusLabel(status: PriceStatus): String = when (status) {
    PriceStatus.Live -> "Live"
    PriceStatus.Stale -> "Stale"
    PriceStatus.Unavailable -> "Unavailable"
}
