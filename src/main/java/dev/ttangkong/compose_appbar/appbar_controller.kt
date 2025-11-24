package dev.ttangkong.compose_appbar

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

@Composable
fun rememberAppBarController(): AppBarController {
    return remember { AppBarController() }
}

data class AppBarStateConnection(
    val state: AppBarState,
    val behavior: AppBarBehavior,
)

class AppBarController {
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

    suspend fun onScrollEnd() {
        connections.forEach { it.behavior.handleScrollEnd(it.state, scrollableState) }
    }

    companion object {
        @SuppressLint("CompositionLocalNaming")
        val Provider = staticCompositionLocalOf<AppBarController> { error("앱바 컨트롤러가 하위 요소로 전파되지 못했습니다.") }
    }
}