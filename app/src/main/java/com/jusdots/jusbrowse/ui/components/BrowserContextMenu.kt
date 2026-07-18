package com.jusdots.jusbrowse.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import com.jusdots.jusbrowse.ui.components.JusBrowseIcons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

data class ContextMenuData(
    val url: String = "",
    val title: String = "", // Used for link text or alt text
    val type: ContextMenuType = ContextMenuType.LINK,
    val extra: String? = null // For image URL
)

enum class ContextMenuType {
    LINK,
    IMAGE,
    IMAGE_LINK, // Image that is also a link
    UNKNOWN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserContextMenu(
    data: ContextMenuData,
    onDismissRequest: () -> Unit,
    onOpenInNewTab: (String) -> Unit,
    onOpenInBackgroundTab: (String) -> Unit,
    onOpenInIncognito: (String) -> Unit,
    onCopyLink: (String) -> Unit,
    onShareLink: (String) -> Unit,
    onDownloadImage: (String) -> Unit,
    onShareImage: (String) -> Unit,
    onPinToDesktop: (String, String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Determine icon based on type
                val headerIcon = when (data.type) {
                    ContextMenuType.IMAGE -> JusBrowseIcons.ImageOutlined
                    ContextMenuType.IMAGE_LINK -> JusBrowseIcons.ImageOutlined
                    else -> JusBrowseIcons.LinkOutlined
                }
                
                Icon(
                    imageVector = headerIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = if (data.type == ContextMenuType.IMAGE) "Image Options" else data.url,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (data.title.isNotEmpty()) {
                        Text(
                            text = data.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Actions List
            
            // LINK ACTIONS
            val targetUrl = data.url
            if (data.type == ContextMenuType.LINK || data.type == ContextMenuType.IMAGE_LINK) {
                ContextMenuItem(
                    text = "Open in new tab",
                    icon = JusBrowseIcons.AddBox,
                    onClick = { onOpenInNewTab(targetUrl) }
                )
                ContextMenuItem(
                    text = "Open in background tab",
                    icon = JusBrowseIcons.TabUnselected,
                    onClick = { onOpenInBackgroundTab(targetUrl) }
                )
                ContextMenuItem(
                    text = "Open in incognito tab",
                    icon = JusBrowseIcons.VisibilityOff, // Incognito icon metaphor
                    onClick = { onOpenInIncognito(targetUrl) }
                )
                ContextMenuItem(
                    text = "Copy link address",
                    icon = JusBrowseIcons.ContentCopy,
                    onClick = { onCopyLink(targetUrl) }
                )
                ContextMenuItem(
                    text = "Share link",
                    icon = JusBrowseIcons.Share,
                    onClick = { onShareLink(targetUrl) }
                )
                ContextMenuItem(
                    text = "Pin to Desktop",
                    icon = JusBrowseIcons.PushPin,
                    onClick = { onPinToDesktop(data.title.ifEmpty { "Shortcut" }, targetUrl) }
                )
            }
            
            // IMAGE ACTIONS
            val imageUrl = data.extra
            if ((data.type == ContextMenuType.IMAGE || data.type == ContextMenuType.IMAGE_LINK) && imageUrl != null) {
                if (data.type == ContextMenuType.IMAGE_LINK) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
                
                ContextMenuItem(
                    text = "Open image in new tab",
                    icon = JusBrowseIcons.ImageOutlined,
                    onClick = { onOpenInNewTab(imageUrl) }
                )
                ContextMenuItem(
                    text = "Download image",
                    icon = JusBrowseIcons.Download,
                    onClick = { onDownloadImage(imageUrl) }
                )
                ContextMenuItem(
                    text = "Share image", // Usually usually sharing the URL of the image
                    icon = JusBrowseIcons.Share,
                    onClick = { onShareImage(imageUrl) }
                )
            }
            
            // Assistant (Placeholder)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            ContextMenuItem(
                text = "Ask JusBrowse Assistant...",
                icon = JusBrowseIcons.AutoAwesome,
                textColor = MaterialTheme.colorScheme.primary,
                iconColor = MaterialTheme.colorScheme.primary,
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
private fun ContextMenuItem(
    text: String,
    icon: ImageVector,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    iconColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // Text left, Icon right
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
    }
}
