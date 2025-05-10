package com.emi.wac.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.ui.unit.IntOffset

/**
 * Util for navigation transitions
 */
object TransitionsUtils {

    private const val ANIMATION_DURATION = 500

    fun enterTransition(): EnterTransition {
        return slideIn(
            initialOffset = { fullSize -> IntOffset(fullSize.width, 0) },
            animationSpec = tween(ANIMATION_DURATION)
        )
    }

    fun exitTransition(): ExitTransition {
        return slideOut(
            targetOffset = { fullSize -> IntOffset(-fullSize.width, 0) },
            animationSpec = tween(ANIMATION_DURATION)
        )
    }

    fun popEnterTransition(): EnterTransition {
        return slideIn(
            initialOffset = { fullSize -> IntOffset(-fullSize.width, 0) },
            animationSpec = tween(ANIMATION_DURATION)
        )
    }

    fun popExitTransition(): ExitTransition {
        return slideOut(
            targetOffset = { fullSize -> IntOffset(fullSize.width, 0) },
            animationSpec = tween(ANIMATION_DURATION)
        )
    }
}