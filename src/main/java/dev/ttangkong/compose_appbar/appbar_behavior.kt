package dev.ttangkong.compose_appbar

import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

/**
 * Signature for the interface that defines how an appbar reacts to scrolling.
 * It allows customizing appbar handling, alignment, and end-of-scroll behavior.
 */
interface AppBarBehavior {
    /** Handles the appbar offset change when a scroll occurs. */
    fun handleScrollBy(
        appbar: AppBarState,
        scroll: ScrollableState?,
        available: Float,
        source: NestedScrollSource
    ): Float

    /** Handles logic when scrolling ends. */
    suspend fun handleScrollEnd(
        appbar: AppBarState,
        scroll: ScrollableState?
    ) = Unit
}

/**
 * Implementation of [AppBarBehavior] that disables any scrolling effect on the appbar,
 * keeping it fixed regardless of user scroll input.
 */
open class AbsoluteSliverBehavior : AppBarBehavior {
    override fun handleScrollBy(
        appbar: AppBarState,
        scroll: ScrollableState?,
        available: Float,
        source: NestedScrollSource
    ): Float = 0f
}

/**
 * Implementation of [AppBarBehavior] that provides standard scrolling and alignment logic.
 * It allows the app bar to float even when the scroll is not at the top,
 * supports restricting automatic collapse to user drag interactions,
 * and automatically animates the appbar to fully expanded or collapsed
 * positions after a scroll ends using configurable duration and easing.
 */
open class MaterialAppBarBehavior(
    open val floating: Boolean = true,
    open val dragOnlyExpanding: Boolean = false,
    open val alignment: Boolean = true,
    open val alignmentDuration: Int = 500,
    open val alignmentEasing: Easing = Ease,
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
                    && source != NestedScrollSource.UserInput
                    && appbar.shrinkedPercent() == 1f) {
                    return 0f
                }
            }
        }

        return appbar.setOffset(appbar.offset - available)
    }

    override suspend fun handleScrollEnd(
        appbar: AppBarState,
        scroll: ScrollableState?
    ) {
        // If alignment behavior is disabled, do nothing.
        if (!alignment) return

        // If fully expanded or fully collapsed, no alignment needed.
        val expandedPercent = appbar.expandedPercent()
        if (expandedPercent == 0f || expandedPercent == 1f) return

        // Animate the app bar to the target offset using the configured duration and easing.
        val targetOffset = if (expandedPercent > 0.5) 0f else appbar.extent.toFloat()
        appbar.animateTo(targetOffset, alignmentDuration, alignmentEasing)
    }
}
