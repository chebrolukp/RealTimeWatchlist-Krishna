@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.doximity.realtimewatchlist_krishna_doximity.ui.search

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.doximity.realtimewatchlist_krishna_doximity.MainDispatcherRule
import com.doximity.realtimewatchlist_krishna_doximity.core.domain.model.ConnectionState
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.Instrument
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.PriceUpdate
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.Quote
import com.doximity.realtimewatchlist_krishna_doximity.domain.model.WatchlistItem
import com.doximity.realtimewatchlist_krishna_doximity.domain.repository.MarketDataRepository
import com.doximity.realtimewatchlist_krishna_doximity.domain.repository.WatchlistRepository
import com.doximity.realtimewatchlist_krishna_doximity.domain.usecase.AddToWatchlistUseCase
import com.doximity.realtimewatchlist_krishna_doximity.domain.usecase.IsInWatchlistUseCase
import com.doximity.realtimewatchlist_krishna_doximity.domain.usecase.SearchInstrumentsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun successfulSearch_populatesResults() = runTest {
        val repository = FakeMarketDataRepositoryForTest(
            searchResult = Result.success(
                listOf(
                    Instrument("AAPL", "AAPL", "Apple Inc.", "Common Stock"),
                ),
            ),
        )
        val watchlistRepository = FakeWatchlistRepositoryForTest()
        val viewModel = SearchViewModel(
            context,
            SearchInstrumentsUseCase(repository),
            AddToWatchlistUseCase(watchlistRepository),
            IsInWatchlistUseCase(watchlistRepository),
        )

        viewModel.onQueryChange("AAPL")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertEquals(1, state.results.size)
        assertEquals("AAPL", state.results.first().instrument.symbol)
    }

    @Test
    fun failedSearch_setsErrorMessage() = runTest {
        val repository = FakeMarketDataRepositoryForTest(
            searchResult = Result.failure(IllegalStateException("Rate limited")),
        )
        val watchlistRepository = FakeWatchlistRepositoryForTest()
        val viewModel = SearchViewModel(
            context,
            SearchInstrumentsUseCase(repository),
            AddToWatchlistUseCase(watchlistRepository),
            IsInWatchlistUseCase(watchlistRepository),
        )

        viewModel.onQueryChange("AAPL")
        advanceUntilIdle()

        assertEquals("Rate limited", viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.results.isEmpty())
    }

    @Test
    fun addToWatchlist_marksResultAsAdded() = runTest {
        val instrument = Instrument("AAPL", "AAPL", "Apple Inc.", "Common Stock")
        val repository = FakeMarketDataRepositoryForTest(
            searchResult = Result.success(listOf(instrument)),
        )
        val watchlistRepository = FakeWatchlistRepositoryForTest()
        val viewModel = SearchViewModel(
            context,
            SearchInstrumentsUseCase(repository),
            AddToWatchlistUseCase(watchlistRepository),
            IsInWatchlistUseCase(watchlistRepository),
        )

        viewModel.onQueryChange("AAPL")
        advanceUntilIdle()
        viewModel.addToWatchlist(instrument)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.results.first().isInWatchlist)
        assertTrue(watchlistRepository.items.contains("AAPL"))
    }

    private class FakeMarketDataRepositoryForTest(
        private val searchResult: Result<List<Instrument>>,
    ) : MarketDataRepository {
        override val connectionState: StateFlow<ConnectionState> =
            MutableStateFlow(ConnectionState.Disconnected)
        override val priceUpdates: Flow<PriceUpdate> = emptyFlow()

        override suspend fun searchInstruments(query: String): Result<List<Instrument>> = searchResult

        override suspend fun getQuote(symbol: String): Result<Quote> =
            Result.success(
                Quote(
                    currentPrice = 100.0,
                    change = 1.0,
                    percentChange = 1.0,
                    previousClose = 99.0,
                    timestampSeconds = 1L,
                ),
            )

        override fun updateLiveSubscriptions(symbols: Set<String>) = Unit
    }

    private class FakeWatchlistRepositoryForTest : WatchlistRepository {
        val items = mutableSetOf<String>()

        override fun observeWatchlist(): Flow<List<WatchlistItem>> = emptyFlow()

        override suspend fun addInstrument(instrument: Instrument) {
            items += instrument.symbol
        }

        override suspend fun removeInstrument(symbol: String) {
            items -= symbol
        }

        override suspend fun isInWatchlist(symbol: String): Boolean = items.contains(symbol)
    }
}
