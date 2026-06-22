package com.doximity.realtimewatchlist_krishna_doximity.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.doximity.realtimewatchlist_krishna_doximity.ui.search.SearchScreen
import com.doximity.realtimewatchlist_krishna_doximity.ui.search.SearchViewModel
import com.doximity.realtimewatchlist_krishna_doximity.ui.watchlist.WatchlistScreen
import com.doximity.realtimewatchlist_krishna_doximity.ui.watchlist.WatchlistViewModel

sealed class AppDestination(val route: String, val label: String) {
    data object Watchlist : AppDestination("watchlist", "Watchlist")
    data object Search : AppDestination("search", "Search")
}

@Composable
fun HomeScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            HomeBottomBar(
                selectedRoute = currentDestination?.route,
                onDestinationSelected = { destination ->
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Watchlist.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(AppDestination.Watchlist.route) {
                val viewModel: WatchlistViewModel = hiltViewModel()
                WatchlistScreen(viewModel = viewModel)
            }
            composable(AppDestination.Search.route) {
                val viewModel: SearchViewModel = hiltViewModel()
                SearchScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
internal fun HomeBottomBar(
    selectedRoute: String?,
    onDestinationSelected: (AppDestination) -> Unit,
) {
    val destinations = listOf(AppDestination.Watchlist, AppDestination.Search)
    NavigationBar {
        destinations.forEach { destination ->
            NavigationBarItem(
                selected = selectedRoute == destination.route,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    when (destination) {
                        AppDestination.Watchlist -> Icon(Icons.Default.List, contentDescription = null)
                        AppDestination.Search -> Icon(Icons.Default.Search, contentDescription = null)
                    }
                },
                label = { Text(destination.label) },
            )
        }
    }
}
