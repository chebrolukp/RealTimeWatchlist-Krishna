package com.doximity.realtimewatchlist_krishna_doximity.data.remote.websocket

import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.ConnectionState
import com.doximity.realtimewatchlist_krishna_doximity.data.remote.dto.WebSocketMessageDto
import com.doximity.realtimewatchlist_krishna_doximity.data.remote.dto.WebSocketSubscriptionDto
import com.doximity.realtimewatchlist_krishna_doximity.data.remote.dto.toDomain
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.PriceUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class FinnhubWebSocketClient @Inject constructor(
    @Named("finnhubWebSocket") private val okHttpClient: OkHttpClient,
    private val json: Json,
    @Named("finnhubApiKey") private val apiKey: String,
    @Named("applicationScope") private val applicationScope: CoroutineScope,
) {
    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _priceUpdates = MutableSharedFlow<PriceUpdate>(extraBufferCapacity = 128)
    val priceUpdates: SharedFlow<PriceUpdate> = _priceUpdates.asSharedFlow()

    private var webSocket: WebSocket? = null
    private val subscribedSymbols = linkedSetOf<String>()
    private var reconnectAttempt = 0
    private var shouldStayConnected = false
    private var isConnecting = false
    private var reconnectJob: Job? = null

    fun ensureConnected() {
        if (apiKey.isBlank()) return
        shouldStayConnected = true
        if (webSocket == null && !isConnecting) {
            connect()
        }
    }

    fun stop() {
        shouldStayConnected = false
        reconnectAttempt = 0
        reconnectJob?.cancel()
        reconnectJob = null
        isConnecting = false
        webSocket?.close(NORMAL_CLOSE_CODE, "Client stopped")
        webSocket = null
        _connectionState.value = ConnectionState.Disconnected
    }

    fun updateSubscriptions(symbols: Set<String>) {
        if (apiKey.isBlank()) return

        val toUnsubscribe = subscribedSymbols - symbols
        val toSubscribe = symbols - subscribedSymbols

        toUnsubscribe.forEach { symbol ->
            sendSubscription(type = "unsubscribe", symbol = symbol)
            subscribedSymbols.remove(symbol)
        }
        toSubscribe.forEach { symbol ->
            sendSubscription(type = "subscribe", symbol = symbol)
            subscribedSymbols.add(symbol)
        }

        if (symbols.isNotEmpty()) {
            ensureConnected()
        } else {
            stop()
        }
    }

    private fun connect() {
        if (apiKey.isBlank() || webSocket != null || isConnecting) return

        reconnectJob?.cancel()
        reconnectJob = null
        isConnecting = true
        _connectionState.value = if (reconnectAttempt == 0) {
            ConnectionState.Connecting
        } else {
            ConnectionState.Reconnecting
        }

        val request = Request.Builder()
            .url("wss://ws.finnhub.io?token=$apiKey")
            .build()

        webSocket = okHttpClient.newWebSocket(
            request,
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    isConnecting = false
                    reconnectAttempt = 0
                    _connectionState.value = ConnectionState.Connected
                    subscribedSymbols.forEach { symbol ->
                        sendSubscription(type = "subscribe", symbol = symbol)
                    }
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    parseMessage(text)?.forEach { update ->
                        _priceUpdates.tryEmit(update)
                    }
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    webSocket.close(code, reason)
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    handleDisconnect(rateLimited = false)
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    val rateLimited = response?.code == 429
                    handleDisconnect(rateLimited = rateLimited)
                }
            },
        )
    }

    private fun handleDisconnect(rateLimited: Boolean) {
        if (webSocket != null) {
            webSocket = null
        }
        isConnecting = false

        if (!shouldStayConnected) {
            reconnectJob?.cancel()
            reconnectJob = null
            _connectionState.value = ConnectionState.Disconnected
            return
        }

        if (reconnectJob?.isActive == true) return

        _connectionState.value = ConnectionState.Reconnecting
        val baseDelay = if (rateLimited) RATE_LIMIT_RECONNECT_DELAY_MS else BASE_RECONNECT_DELAY_MS
        val delayMs = minOf(
            MAX_RECONNECT_DELAY_MS,
            baseDelay * (1 shl reconnectAttempt.coerceAtMost(5)),
        )
        reconnectAttempt++

        reconnectJob = applicationScope.launch {
            delay(delayMs)
            if (shouldStayConnected && webSocket == null && !isConnecting) {
                connect()
            }
        }
    }

    private fun sendSubscription(type: String, symbol: String) {
        val payload = json.encodeToString(
            WebSocketSubscriptionDto.serializer(),
            WebSocketSubscriptionDto(type = type, symbol = symbol),
        )
        webSocket?.send(payload)
    }

    private fun parseMessage(text: String): List<PriceUpdate>? {
        val message = runCatching {
            json.decodeFromString(WebSocketMessageDto.serializer(), text)
        }.getOrNull() ?: return null

        if (message.type != "trade") return null
        return message.data.orEmpty().map { it.toDomain() }
    }

    private companion object {
        const val NORMAL_CLOSE_CODE = 1000
        const val BASE_RECONNECT_DELAY_MS = 2_000L
        const val RATE_LIMIT_RECONNECT_DELAY_MS = 30_000L
        const val MAX_RECONNECT_DELAY_MS = 60_000L
    }
}
