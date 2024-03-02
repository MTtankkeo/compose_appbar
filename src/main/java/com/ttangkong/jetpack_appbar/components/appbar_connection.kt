package com.ttangkong.jetpack_appbar.components

import com.ttangkong.jetpack_appbar.SliverController
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll

@Composable
fun AppBarConnection(
    scrollableState: ScrollableState? = null,
    appBars: @Composable ColumnScope.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    val controller = remember { SliverController() }
    val sliverColumn: @Composable (@Composable ColumnScope.() -> Unit) -> Unit = { children ->
        Column(
            modifier = Modifier.scrollable(
                orientation = Orientation.Vertical,
                state = rememberScrollableState {
                    controller.onScroll(delta = it, NestedScrollSource.Drag)
                }
            )
        ) {
            children()
        }
    }

    // A scrollable state may need to be referenced for handle the nested scroll of a sliver.
    controller.scrollableState = scrollableState

    CompositionLocalProvider(SliverController.Provider provides controller) {
        Column(
            modifier = Modifier.nestedScroll(remember { object : NestedScrollConnection {
                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    // If when not overflowed, not handle nested scroll for a sliver.
                    if (available.y != 0f) {
                        return Offset(
                            x = 0f,
                            y = controller.onScroll(available.y, source)
                        ) // the total consumed by a sliver.
                    }

                    return Offset.Zero
                }

                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    return Offset(
                        x = 0f,
                        y = controller.onScroll(available.y, source),
                    ) // the total consumed by a sliver.
                }
            } })
        ) {
            sliverColumn(appBars)
            content()
        }
    }
}