package com.omniroute.wear.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.omniroute.wear.ui.theme.OmniTheme

/**
 * Píldora inferior animada de sincronización para Wear OS.
 * Envuelta en AnimatedVisibility (fadeIn + slideInVertically) y con CircularProgressIndicator de 2.dp.
 */
@Composable
fun SyncStatusPill(
    visible: Boolean,
    isOnline: Boolean,
    pendingSyncCount: Int,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = OmniTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val pillShape = RoundedCornerShape(percent = 50)

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        exit = fadeOut(animationSpec = tween(250)) + slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(250)
        ),
        modifier = modifier
    ) {
        val bgColor = if (!isOnline) {
            if (colors.isDark) Color(0xFF280D0F) else Color(0xFFFFF1F2)
        } else {
            colors.surfaceCard
        }

        val borderColor = if (!isOnline) colors.negativeBorder else colors.borderSubtle
        val contentColor = if (!isOnline) colors.negativeText else colors.textPrimary

        Row(
            modifier = Modifier
                .bounceClick(interactionSource = interactionSource, scaleDown = 0.95f)
                .clip(pillShape)
                .background(bgColor)
                .border(BorderStroke(1.dp, borderColor), pillShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onRetry
                )
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (!isOnline) {
                Icon(
                    imageVector = Icons.Rounded.CloudOff,
                    contentDescription = "Offline",
                    tint = contentColor,
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = if (pendingSyncCount > 0) "Offline ($pendingSyncCount en cola)" else "Sin conexión",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            } else {
                // Loader circular muy pequeño y trazo fino (2.dp) en vez de emoji estático
                CircularProgressIndicator(
                    modifier = Modifier.size(11.dp),
                    strokeWidth = 2.dp,
                    indicatorColor = colors.primaryGreen,
                    trackColor = colors.borderSubtle
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (pendingSyncCount > 0) "Sincronizando $pendingSyncCount..." else "Sincronizando...",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
            }
        }
    }
}
