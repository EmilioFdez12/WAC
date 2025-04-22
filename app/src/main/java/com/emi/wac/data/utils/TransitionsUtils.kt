package com.emi.wac.data.utils

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

/**
 * Util for navigation transitions
 */
object TransitionsUtils {

    private const val ANIMATION_DURATION = 300

    fun enterTransition(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
        return scope.slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(ANIMATION_DURATION)
        )
    }

    fun exitTransition(scope: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
        return scope.slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(ANIMATION_DURATION)
        )
    }

    fun popEnterTransition(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
        return scope.slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(ANIMATION_DURATION)
        )
    }

    fun popExitTransition(scope: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
        return scope.slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(ANIMATION_DURATION)
        )
    }
}