package com.doximity.realtimewatchlist_krishna_doximity.core.ui.util

import com.doximity.realtimewatchlist_krishna_doximity.R
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.model.UiText
import com.doximity.realtimewatchlist_krishna_doximity.data.remote.FinnhubApiException
import com.doximity.realtimewatchlist_krishna_doximity.data.remote.FinnhubForbiddenException
import com.doximity.realtimewatchlist_krishna_doximity.data.remote.FinnhubRateLimitException
import com.doximity.realtimewatchlist_krishna_doximity.data.remote.FinnhubUnauthorizedException

fun Throwable.toUiText(): UiText = when (this) {
    is FinnhubUnauthorizedException -> UiText.Resource(R.string.error_finnhub_unauthorized)
    is FinnhubForbiddenException -> UiText.Resource(R.string.error_finnhub_forbidden)
    is FinnhubRateLimitException -> UiText.Resource(R.string.error_finnhub_rate_limit)
    is FinnhubApiException -> when {
        isEmptyBody -> UiText.Resource(R.string.error_finnhub_empty_response)
        httpCode != null -> UiText.Resource(R.string.error_finnhub_request_failed, listOf(httpCode))
        else -> UiText.Resource(R.string.error_generic)
    }
    else -> message?.takeIf { it.isNotBlank() }?.let(UiText::Dynamic)
        ?: UiText.Resource(R.string.error_generic)
}
