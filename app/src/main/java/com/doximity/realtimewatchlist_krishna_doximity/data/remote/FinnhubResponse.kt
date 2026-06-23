package com.doximity.realtimewatchlist_krishna_doximity.data.remote

import retrofit2.Response

internal fun <T> Response<T>.requireFinnhubBody(): T {
    if (isSuccessful) {
        val body = body()
        if (body != null) return body
        throw FinnhubApiException(isEmptyBody = true)
    }
    throw when (code()) {
        401 -> FinnhubUnauthorizedException()
        403 -> FinnhubForbiddenException()
        429 -> FinnhubRateLimitException()
        else -> FinnhubApiException(httpCode = code())
    }
}

class FinnhubApiException(
    val httpCode: Int? = null,
    val isEmptyBody: Boolean = false,
) : Exception()
