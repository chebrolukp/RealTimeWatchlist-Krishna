package com.doximity.realtimewatchlist_krishna_doximity.ui.navigation

import androidx.annotation.StringRes
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.doximity.realtimewatchlist_krishna_doximity.R

sealed class AppDestination(val route: String, @StringRes val labelRes: Int) {
    data object Watchlist : AppDestination("watchlist", R.string.nav_watchlist)
    data object Search : AppDestination("search", R.string.nav_search)
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
