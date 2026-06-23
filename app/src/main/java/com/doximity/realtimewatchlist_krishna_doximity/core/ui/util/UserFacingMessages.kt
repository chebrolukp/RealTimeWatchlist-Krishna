package com.doximity.realtimewatchlist_krishna_doximity.core.ui.util

import android.content.Context
import com.doximity.realtimewatchlist_krishna_doximity.R
import com.doximity.realtimewatchlist_krishna_doximity.data.remote.FinnhubApiException
import com.doximity.realtimewatchlist_krishna_doximity.data.remote.FinnhubForbiddenException
import com.doximity.realtimewatchlist_krishna_doximity.data.remote.FinnhubRateLimitException
import com.doximity.realtimewatchlist_krishna_doximity.data.remote.FinnhubUnauthorizedException

fun Throwable.toUserMessage(context: Context): String = when (this) {
    is FinnhubUnauthorizedException -> context.getString(R.string.error_finnhub_unauthorized)
    is FinnhubForbiddenException -> context.getString(R.string.error_finnhub_forbidden)
    is FinnhubRateLimitException -> context.getString(R.string.error_finnhub_rate_limit)
    is FinnhubApiException -> when {
        isEmptyBody -> context.getString(R.string.error_finnhub_empty_response)
        httpCode != null -> context.getString(R.string.error_finnhub_request_failed, httpCode)
        else -> context.getString(R.string.error_generic)
    }
    else -> message?.takeIf { it.isNotBlank() } ?: context.getString(R.string.error_generic)
}
