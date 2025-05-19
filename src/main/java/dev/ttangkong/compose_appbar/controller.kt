package dev.ttangkong.compose_appbar

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

data class AppBarStateConnection(
    val state: AppBarState,
    val behavior: AppBarBehavior,
)

class SliverController {
    private val connections = arrayListOf<AppBarStateConnection>()

    // Optionally defined according to the given argument in a composition.
    var scrollableState: ScrollableState? = null

    fun attach(state: AppBarState, behavior: AppBarBehavior) {
        connections.add(AppBarStateConnection(state, behavior))
    }

    fun detach(state: AppBarState) {
        connections.removeIf { it.state == state }
    }

    // Scrolls the attached a slivers in this controller.
    // And returns the total consumed by a slivers.
    fun onScroll(available: Float, source: NestedScrollSource): Float {
        val targets = if (available > 0) connections.reversed() else connections
        var consumed = 0f

        for (it in targets) {
            consumed += it.behavior.handleScrollBy(
                appbar = it.state,
                scroll = scrollableState,
                available = available - consumed,
                source = source
            )

            // Ends unnecessary traversal as all the given scroll offset
            // have finally been consumed by a sliver.
            if (consumed == available) break
        }

        return consumed
    }

    companion object {
        @SuppressLint("CompositionLocalNaming")
        val Provider = staticCompositionLocalOf<SliverController> { error("슬라이버 컨트롤러가 하위 요소로 전파되지 못했습니다.") }
    }
}