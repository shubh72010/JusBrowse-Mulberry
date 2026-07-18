package com.jusdots.jusbrowse.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.jusdots.jusbrowse.ui.components.JusBrowseIcons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DragDropOverlay(
    isDragging: Boolean,
    isHovering: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isDragging,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(bottomStart = 100.dp))
                .background(
                    if (isHovering) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                )
                .border(
                    width = 2.dp,
                    color = if (isHovering) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = RoundedCornerShape(bottomStart = 100.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Content offset to the top-right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 24.dp, end = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.layout.Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = JusBrowseIcons.Download,
                        contentDescription = "Drop to Download",
                        modifier = Modifier.size(32.dp),
                        tint = if (isHovering) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Download",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isHovering) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
