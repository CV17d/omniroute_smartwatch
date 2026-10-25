package com.omniroute.wear.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.omniroute.wear.ui.theme.TextPrimary
import com.omniroute.wear.ui.theme.TextSecondary

/**
 * High-contrast address display tailored for circular Wear OS screens.
 * Text is horizontally constrained to avoid edge clipping along bezel arcs.
 */
@Composable
fun AddressCard(
    address: String,
    recipient: String,
    modifier: Modifier = Modifier,
    isEmphasized: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // Safe circular margin
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = address,
            color = TextPrimary,
            fontSize = if (isEmphasized) 17.sp else 14.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        if (recipient.isNotBlank()) {
            Text(
                text = recipient,
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
