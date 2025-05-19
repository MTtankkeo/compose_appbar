package com.devttangkong.compose_appbar.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.devttangkong.compose_appbar.AppBarBehavior
import com.devttangkong.compose_appbar.AppBarState
import com.devttangkong.compose_appbar.MaterialAppBarBehavior
import com.devttangkong.compose_appbar.SliverController
import com.devttangkong.compose_appbar.rememberAppBarState
import kotlin.math.roundToInt

// This alignment constants for only the hide-able appbar.
//
// Used by [AppBar]
enum class AppBarAlignment {
    Scroll,
    Center,
    Absolute,
}

// This appbar is a hide-able appbar that does not change its size directly.
//
// Used by [AppBarConnection].
@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    minExtent: Dp = 0.dp,
    alignment: AppBarAlignment = AppBarAlignment.Scroll,
    behavior: AppBarBehavior = MaterialAppBarBehavior(),
    state: AppBarState = rememberAppBarState(),
    content: @Composable (AppBarState) -> Unit,
) {
    val controller = SliverController.Provider.current

    // When a component is entered the composition, attaches a sliver state form a controller.
    LaunchedEffect(key1 = "Attach state to controller") {
        controller.attach(state, behavior)
    }

    // When a component is leave the composition, detaches a sliver state from a controller.
    DisposableEffect(key1 = "Detach state to controller") {
        onDispose { controller.detach(state) }
    }

    Box(
        modifier = modifier
            .clipToBounds()
            .layout() { measurable, constraints ->
                // The appbar component should not always consider the height of
                // the parent component calculations.
                val placeable = measurable.measure(
                    constraints.copy(maxHeight = Constraints.Infinity)
                )

                // The extent of the appbar is equal to the calculated height - min-extent.
                val extent = placeable.height - minExtent.toPx()
                assert(extent >= 0f) {
                    "The measured height of the appbar is less than the given min-extent."
                }

                state.extent = extent.roundToInt()

                layout(constraints.maxWidth, placeable.height - state.offset.toInt()) {
                    // Layout the appbar components in the suitable position,
                    // depending on the given argument.
                    placeable.placeRelative(
                        x = 0,
                        y = (when (alignment) {
                            AppBarAlignment.Scroll -> -state.offset
                            AppBarAlignment.Center -> -state.offset / 2
                            AppBarAlignment.Absolute -> 0f
                        }).toInt()
                    )
                }
            }
    ) {
        content(state)
    }
}

// Used by [AppBarConnection].
@Composable
fun SizedAppBar(
    modifier: Modifier = Modifier,
    minExtent: Dp = 0.dp,
    maxExtent: Dp,
    behavior: AppBarBehavior = MaterialAppBarBehavior(),
    state: AppBarState = rememberAppBarState(),
    content: @Composable (AppBarState) -> Unit,
) {
    val controller = SliverController.Provider.current

    // When a component is entered the composition, attaches a sliver state form a controller.
    LaunchedEffect(key1 = "Attach state to controller") {
        controller.attach(state, behavior)
    }

    // When a component is leave the composition, detaches a sliver state from a controller.
    DisposableEffect(key1 = "Detach state to controller") {
        onDispose { controller.detach(state) }
    }

    assert(maxExtent > minExtent) {
        "The [minExtent] must be less than the [maxExtent]."
    }
    val minExtentPx = with(LocalDensity.current) { minExtent.toPx() }.toInt()
    val maxExtentPx = with(LocalDensity.current) { maxExtent.toPx() }.toInt()
    state.extent = maxExtentPx - minExtentPx

    Box(
        modifier = modifier
            .clipToBounds()
            .layout() { measurable, constraints ->
                val height = maxExtentPx - state.offset.toInt()

                // The appbar component should not always consider the height of
                // the parent component calculations.
                val placeable = measurable.measure(
                    constraints.copy(maxHeight = height)
                )

                layout(constraints.maxWidth, height) { placeable.placeRelative(x = 0, y = 0) }
            }
    ) {
        content(state)
    }
}