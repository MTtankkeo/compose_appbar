package com.devttangkong.compose_appbar

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

interface AppBarBehavior {
    fun handleScrollEnd(sliver: AppBarState) = Unit
    fun handleScrollFling(sliver: AppBarState) = Unit

    fun handleScrollBy(
        appbar: AppBarState,
        scroll: ScrollableState?,
        available: Float,
        source: NestedScrollSource
    ): Float
}

class AbsoluteSliverBehavior : AppBarBehavior {
    override fun handleScrollBy(
        appbar: AppBarState,
        scroll: ScrollableState?,
        available: Float,
        source: NestedScrollSource
    ): Float = 0f
}

open class MaterialAppBarBehavior(
    open val floating: Boolean = true,
    open val dragOnlyExpanding: Boolean = false,
) : AppBarBehavior {
    override fun handleScrollBy(
        appbar: AppBarState,
        scroll: ScrollableState?,
        available: Float,
        source: NestedScrollSource
    ): Float {
        // APPBAR SCROLLING CONSTRAINTS

        if (!floating) {
            assert(scroll != null) {
                "The [isAlwaysScrolling] option is enabled, but proper operation requires a scroll state. " +
                "Therefore, please pass the ScrollableState as an argument to AppBarConnection."
            }

            // for floating
            if (scroll?.canScrollBackward == true) {
                return 0f
            } else {
                // for drag only expanding
                if (dragOnlyExpanding
                    && source != NestedScrollSource.Drag
                    && appbar.shrinkedPercent() == 1f) {
                    return 0f
                }
            }
        }

        return appbar.setOffset(appbar.offset - available)
    }
}