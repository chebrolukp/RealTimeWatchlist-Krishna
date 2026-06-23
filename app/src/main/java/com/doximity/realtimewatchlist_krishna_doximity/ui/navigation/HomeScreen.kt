package com.doximity.realtimewatchlist_krishna_doximity.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.doximity.realtimewatchlist_krishna_doximity.R
import com.doximity.realtimewatchlist_krishna_doximity.core.ui.adaptive.useNavigationRail
import com.doximity.realtimewatchlist_krishna_doximity.ui.search.SearchScreen
import com.doximity.realtimewatchlist_krishna_doximity.ui.search.SearchViewModel
import com.doximity.realtimewatchlist_krishna_doximity.ui.watchlist.WatchlistScreen
import com.doximity.realtimewatchlist_krishna_doximity.ui.watchlist.WatchlistViewModel

sealed class AppDestination(val route: String, @StringRes val labelRes: Int) {
    data object Watchlist : AppDestination("watchlist", R.string.nav_watchlist)
    data object Search : AppDestination("search", R.string.nav_search)
}

@Composable
fun HomeScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val useRail = useNavigationRail()

    val onDestinationSelected: (AppDestination) -> Unit = { destination ->
        navController.navigate(destination.route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    if (useRail) {
        Row(modifier = Modifier.fillMaxSize()) {
            HomeNavigationRail(
                selectedRoute = currentDestination?.route,
                onDestinationSelected = onDestinationSelected,
            )
            HomeNavHost(
                navController = navController,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
        }
    } else {
        Scaffold(
            bottomBar = {
                HomeBottomBar(
                    selectedRoute = currentDestination?.route,
                    onDestinationSelected = onDestinationSelected,
                )
            },
        ) { innerPadding ->
            HomeNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun HomeNavHost(
    navController: androidx.navigation.NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Watchlist.route,
        modifier = modifier,
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

@Composable
internal fun HomeBottomBar(
    selectedRoute: String?,
    onDestinationSelected: (AppDestination) -> Unit,
) {
    val destinations = listOf(AppDestination.Watchlist, AppDestination.Search)
    val navColors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        unselectedIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
        selectedTextColor = MaterialTheme.colorScheme.primary,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
    )
    NavigationBar {
        destinations.forEach { destination ->
            val selected = selectedRoute == destination.route
            NavigationBarItem(
                selected = selected,
                onClick = { onDestinationSelected(destination) },
                colors = navColors,
                icon = {
                    HomeDestinationIcon(
                        destination = destination,
                        selected = selected,
                    )
                },
                label = { Text(stringResource(destination.labelRes)) },
            )
        }
    }
}

@Composable
internal fun HomeNavigationRail(
    selectedRoute: String?,
    onDestinationSelected: (AppDestination) -> Unit,
) {
    val destinations = listOf(AppDestination.Watchlist, AppDestination.Search)
    val navColors = NavigationRailItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        unselectedIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
        selectedTextColor = MaterialTheme.colorScheme.primary,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
    )
    NavigationRail {
        destinations.forEach { destination ->
            val selected = selectedRoute == destination.route
            NavigationRailItem(
                selected = selected,
                onClick = { onDestinationSelected(destination) },
                colors = navColors,
                icon = {
                    HomeDestinationIcon(
                        destination = destination,
                        selected = selected,
                    )
                },
                label = { Text(stringResource(destination.labelRes)) },
            )
        }
    }
}

@Composable
private fun HomeDestinationIcon(
    destination: AppDestination,
    selected: Boolean,
) {
    val tint = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
    }
    when (destination) {
        AppDestination.Watchlist -> Icon(
            imageVector = Icons.AutoMirrored.Filled.List,
            contentDescription = null,
            tint = tint,
        )
        AppDestination.Search -> Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = tint,
        )
    }
}
