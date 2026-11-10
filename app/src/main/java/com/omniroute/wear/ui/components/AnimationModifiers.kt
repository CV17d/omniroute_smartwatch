package com.omniroute.wear.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Modificador personalizado para dar feedback táctil animado ("Sentirse vivo").
 * Al presionarse, reduce la escala al 95% (scaleDown = 0.95f) usando animateFloatAsState
 * y vuelve con física de resorte al soltarse.
 */
fun Modifier.bounceClick(
    interactionSource: MutableInteractionSource,
    scaleDown: Float = 0.95f
): Modifier = composed {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "BounceClickScale"
    )
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
