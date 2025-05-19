package dev.ttangkong.compose_appbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun rememberAppBarState(): AppBarState {
    return rememberSaveable(saver = AppBarState.Saver) { AppBarState(0f) }
}

open class AppBarState(
    initialOffset: Float,
) {
    var offset by mutableFloatStateOf(initialOffset)

    // Defined when layout phase or composition of a target component.
    // And, this value is mostly equal to the height of the component.
    var extent = 0

    fun expandedPercent() = 1 - shrinkedPercent()
    fun shrinkedPercent() = if(extent == 0) offset else offset / extent

    open fun applyBoundaryConditions(value: Float): Float {
        return value.coerceIn(0f, extent.toFloat())
    }

    fun setOffset(newOffset: Float): Float { // phase
        // assert(newOffset >= minExtent) { "Given new offset has overflowed the min-extent." }
        // assert(newOffset <= maxExtent) { "Given new offset has overflowed the max-extent." }

        val oldOffset = offset

        if (offset != newOffset) {
            offset = applyBoundaryConditions(newOffset)
            return oldOffset - offset
        }
        return 0f
    }

    companion object {
        val Saver = Saver<AppBarState, Float>(
            save =    { it.offset },
            restore = { AppBarState(it) }
        )
    }
}