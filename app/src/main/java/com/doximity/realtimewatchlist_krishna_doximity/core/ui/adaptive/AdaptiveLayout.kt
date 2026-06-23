package com.doximity.realtimewatchlist_krishna_doximity.core.ui.adaptive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AppWindowWidthSizeClass {
    Compact,
    Medium,
    Expanded,
}

@Composable
fun rememberAppWindowWidthSizeClass(): AppWindowWidthSizeClass {
    val widthDp = LocalConfiguration.current.screenWidthDp
    return remember(widthDp) {
        when {
            widthDp < 600 -> AppWindowWidthSizeClass.Compact
            widthDp < 840 -> AppWindowWidthSizeClass.Medium
            else -> AppWindowWidthSizeClass.Expanded
        }
    }
}

@Composable
fun adaptiveContentPadding(): Dp = when (rememberAppWindowWidthSizeClass()) {
    AppWindowWidthSizeClass.Compact -> 16.dp
    AppWindowWidthSizeClass.Medium -> 24.dp
    AppWindowWidthSizeClass.Expanded -> 32.dp
}

@Composable
fun adaptiveListColumnCount(): Int = when (rememberAppWindowWidthSizeClass()) {
    AppWindowWidthSizeClass.Expanded -> 2
    else -> 1
}

@Composable
fun adaptiveMaxContentWidth(): Dp? = when (rememberAppWindowWidthSizeClass()) {
    AppWindowWidthSizeClass.Expanded -> 960.dp
    AppWindowWidthSizeClass.Medium -> 720.dp
    AppWindowWidthSizeClass.Compact -> null
}

@Composable
fun useNavigationRail(): Boolean =
    rememberAppWindowWidthSizeClass() != AppWindowWidthSizeClass.Compact

@Composable
fun AdaptiveContentContainer(
    modifier: Modifier = Modifier,
    applyHorizontalPadding: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val horizontalPadding = if (applyHorizontalPadding) adaptiveContentPadding() else 0.dp
    val maxWidth = adaptiveMaxContentWidth()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier = Modifier
                .then(if (maxWidth != null) Modifier.widthIn(max = maxWidth) else Modifier)
                .fillMaxWidth(),
            content = content,
        )
    }
}
