package com.doximity.realtimewatchlist_krishna_doximity.core.ui.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {
    data class Dynamic(val value: String) : UiText

    data class Resource(
        @StringRes val id: Int,
        val args: List<Any> = emptyList(),
    ) : UiText
}

@Composable
fun UiText.asString(): String = when (this) {
    is UiText.Dynamic -> value
    is UiText.Resource -> when (args.size) {
        0 -> stringResource(id)
        1 -> stringResource(id, args[0])
        2 -> stringResource(id, args[0], args[1])
        else -> stringResource(id, *args.toTypedArray())
    }
}
