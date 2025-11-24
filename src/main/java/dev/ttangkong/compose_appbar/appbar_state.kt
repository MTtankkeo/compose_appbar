package dev.ttangkong.compose_appbar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
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

open class AppBarState(initialOffset: Float) {
    var offset by mutableFloatStateOf(initialOffset)

    var offsetAnimatable: Animatable<Float, AnimationVector1D>? = null

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

    suspend fun animateTo(
        targetOffset: Float,
        duration: Int,
        easing: Easing,
    ) {
        offsetAnimatable?.stop()
        offsetAnimatable = Animatable(offset).apply {
            animateTo(
                targetOffset,
                tween(durationMillis = duration, easing = easing),
            ) {
                offset = value
            }

            offsetAnimatable = null
        }
    }

    companion object {
        val Saver = Saver<AppBarState, Float>(
            save =    { it.offset },
            restore = { AppBarState(it) }
        )
    }
}