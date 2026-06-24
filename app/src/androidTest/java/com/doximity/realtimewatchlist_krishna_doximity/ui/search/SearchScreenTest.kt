package com.doximity.realtimewatchlist_krishna_doximity.ui.search

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasStateDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.doximity.realtimewatchlist_krishna_doximity.CompactPhoneTestContent
import com.doximity.realtimewatchlist_krishna_doximity.ui.preview.PreviewSampleData
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun searchIdle_showsEmptyState() {
        composeRule.setContent {
            CompactPhoneTestContent {
                SearchContent(
                    uiState = PreviewSampleData.searchIdle,
                    onQueryChange = {},
                    onAdd = {},
                )
            }
        }

        composeRule.onNodeWithText("Find an instrument").assertIsDisplayed()
    }

    @Test
    fun searchResults_displaysInstruments() {
        composeRule.setContent {
            CompactPhoneTestContent {
                SearchContent(
                    uiState = PreviewSampleData.searchResults,
                    onQueryChange = {},
                    onAdd = {},
                )
            }
        }

        composeRule.onNodeWithText("AAPL").assertIsDisplayed()
        composeRule.onNodeWithText("MSFT").assertIsDisplayed()
        composeRule.onNodeWithText("BTC/USDT").assertIsDisplayed()
    }

    @Test
    fun searchResults_addButton_invokesCallback() {
        var addedSymbol: String? = null

        composeRule.setContent {
            CompactPhoneTestContent {
                SearchContent(
                    uiState = PreviewSampleData.searchResults,
                    onQueryChange = {},
                    onAdd = { addedSymbol = it.symbol },
                )
            }
        }

        composeRule.onNodeWithContentDescription("Add MSFT to watchlist")
            .assertIsEnabled()
            .performClick()

        assertEquals("MSFT", addedSymbol)
    }

    @Test
    fun searchResults_addedInstrument_showsAddedState() {
        composeRule.setContent {
            CompactPhoneTestContent {
                SearchContent(
                    uiState = PreviewSampleData.searchResults,
                    onQueryChange = {},
                    onAdd = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription("AAPL already in watchlist")
            .assertIsNotEnabled()
        composeRule.onNode(hasStateDescription("Added")).assertIsDisplayed()
        composeRule.onAllNodes(hasStateDescription("Not added")).assertCountEquals(2)
        composeRule.onNodeWithContentDescription("Add MSFT to watchlist")
            .assertIsEnabled()
    }

    @Test
    fun searchError_showsErrorBanner() {
        composeRule.setContent {
            CompactPhoneTestContent {
                SearchContent(
                    uiState = PreviewSampleData.searchError,
                    onQueryChange = {},
                    onAdd = {},
                )
            }
        }

        composeRule.onNodeWithText("Rate limit exceeded. Try again shortly.")
            .assertIsDisplayed()
    }

    @Test
    fun searchNoResults_showsEmptyResultsMessage() {
        composeRule.setContent {
            CompactPhoneTestContent {
                SearchContent(
                    uiState = PreviewSampleData.searchEmpty,
                    onQueryChange = {},
                    onAdd = {},
                )
            }
        }

        composeRule.onNodeWithText("No results").assertIsDisplayed()
        composeRule.onNodeWithText("Try another symbol or company name.")
            .assertIsDisplayed()
    }

    @Test
    fun searchField_invokesQueryChange() {
        val queryState = mutableStateOf("")

        composeRule.setContent {
            CompactPhoneTestContent {
                SearchContent(
                    uiState = SearchUiState(query = queryState.value),
                    onQueryChange = { queryState.value = it },
                    onAdd = {},
                )
            }
        }

        composeRule.onNode(hasSetTextAction())
            .performClick()
            .performTextInput("AAPL")

        composeRule.runOnIdle {
            assertEquals("AAPL", queryState.value)
        }
    }
}
