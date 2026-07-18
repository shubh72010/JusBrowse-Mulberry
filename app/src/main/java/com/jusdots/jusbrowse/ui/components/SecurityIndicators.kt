package com.jusdots.jusbrowse.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.jusdots.jusbrowse.ui.components.JusBrowseIcons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jusdots.jusbrowse.ui.theme.InsecureRed
import com.jusdots.jusbrowse.ui.theme.InsecureRedContainer
import com.jusdots.jusbrowse.ui.theme.SecureGreen
import com.jusdots.jusbrowse.ui.theme.SecureGreenContainer
import com.jusdots.jusbrowse.ui.theme.WarningAmber

/**
 * Layer 10: UI/UX Security Indicators — redesigned for M3 Expressive Glassmorphism.
 * Security is communicated through calm color palettes and animated transitions,
 * never through alarming text alone.
 */

/**
 * HTTPS lock icon for address bar.
 * Uses AnimatedContent to smoothly morph between security states.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SecurityLockIcon(
    url: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val isSecure   = url.startsWith("https://")
    val isInsecure = url.startsWith("http://") && !url.startsWith("https://")
    val isAboutPage = url.startsWith("about:") || url.isEmpty()

    // Derive a stable key for AnimatedContent to detect state changes
    val stateKey = when {
        isSecure    -> 0
        isInsecure  -> 1
        isAboutPage -> 2
        else        -> 3
    }

    val icon = when {
        isSecure    -> JusBrowseIcons.Lock
        isInsecure  -> JusBrowseIcons.LockOpen
        isAboutPage -> JusBrowseIcons.InfoOutlined
        else        -> JusBrowseIcons.WarningOutlined
    }
    val tint = when {
        isSecure    -> SecureGreen
        isInsecure  -> InsecureRed
        isAboutPage -> MaterialTheme.colorScheme.onSurfaceVariant
        else        -> WarningAmber
    }
    val backdropColor = when {
        isSecure    -> SecureGreenContainer
        isInsecure  -> InsecureRedContainer
        else        -> Color.Transparent
    }
    val contentDescription = when {
        isSecure    -> "Secure connection (HTTPS)"
        isInsecure  -> "Insecure connection (HTTP)"
        isAboutPage -> "Local page"
        else        -> "Unknown security status"
    }

    AnimatedContent(
        targetState = stateKey,
        transitionSpec = {
            (fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
                    scaleIn(initialScale = 0.75f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)))
                .togetherWith(fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
                        scaleOut(targetScale = 0.75f))
        },
        label = "securityState"
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(backdropColor)
                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * JavaScript status indicator — soft badge style.
 */
@Composable
fun JavaScriptIndicator(
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val backgroundColor = if (isEnabled) WarningAmber.copy(alpha = 0.15f)
    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    val borderColor = if (isEnabled) WarningAmber.copy(alpha = 0.4f)
    else Color.Transparent
    val textColor = if (isEnabled) WarningAmber
    else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 5.dp, vertical = 2.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isEnabled) "JS" else "JS✕",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}

/**
 * Private/Incognito mode indicator — SuggestionChip-style pill badge.
 */
@Composable
fun PrivateTabIndicator(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f), CircleShape)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = JusBrowseIcons.VisibilityOff,
            contentDescription = "Private tab",
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = "Private",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

/**
 * Mixed content warning indicator.
 */
@Composable
fun MixedContentWarning(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(WarningAmber.copy(alpha = 0.15f))
            .border(1.dp, WarningAmber.copy(alpha = 0.4f), CircleShape)
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = JusBrowseIcons.Warning,
            contentDescription = "Mixed content warning",
            tint = WarningAmber,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = "Mixed",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = WarningAmber
        )
    }
}

/**
 * Permission chip showing site permissions — expressive icon-badge style.
 */
@Composable
fun PermissionChip(
    permission: String,
    isGranted: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val icon = when (permission.uppercase()) {
        "GEOLOCATION", "LOCATION" -> if (isGranted) JusBrowseIcons.LocationOn else JusBrowseIcons.LocationOffOutlined
        "CAMERA"                  -> if (isGranted) JusBrowseIcons.Videocam else JusBrowseIcons.VideocamOffOutlined
        "MICROPHONE", "MIC"       -> if (isGranted) JusBrowseIcons.Mic else JusBrowseIcons.MicOffOutlined
        "NOTIFICATIONS"           -> if (isGranted) JusBrowseIcons.Notifications else JusBrowseIcons.NotificationsOffOutlined
        else                      -> JusBrowseIcons.SecurityOutlined
    }
    val backgroundColor = if (isGranted) SecureGreenContainer else MaterialTheme.colorScheme.surfaceVariant.copy(0.4f)
    val borderColor     = if (isGranted) SecureGreen.copy(0.4f) else MaterialTheme.colorScheme.outline.copy(0.2f)
    val tintColor       = if (isGranted) SecureGreen else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, borderColor, CircleShape)
            .size(32.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "$permission: ${if (isGranted) "Granted" else "Denied"}",
            tint = tintColor,
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * Download warning dialog — expressive icon treatment with contextual color palettes.
 */
@Composable
fun DownloadWarningDialog(
    fileName: String,
    warningMessage: String,
    isBlocked: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onScanVirusTotal: (() -> Unit)? = null,
    onScanKoodous: (() -> Unit)? = null
) {
    val isSafeDownload = warningMessage.startsWith("Download") && !isBlocked

    val iconVector = if (isBlocked) JusBrowseIcons.Block
    else if (isSafeDownload) JusBrowseIcons.Download
    else JusBrowseIcons.Warning

    val iconTint = when {
        isBlocked       -> InsecureRed
        isSafeDownload  -> MaterialTheme.colorScheme.primary
        else            -> WarningAmber
    }
    val iconBg = when {
        isBlocked       -> InsecureRedContainer
        isSafeDownload  -> MaterialTheme.colorScheme.primaryContainer.copy(0.4f)
        else            -> WarningAmber.copy(0.1f)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(
                shape = CircleShape,
                color = iconBg,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = if (isBlocked) "Download Blocked" else if (isSafeDownload) "Confirm Download" else "Download Warning"
            )
        },
        text = { Text(text = warningMessage) },
        confirmButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (!isBlocked) {
                    Button(onClick = onConfirm) {
                        Text(if (isSafeDownload) "Download" else "Download Anyway")
                    }
                }
                onScanVirusTotal?.let { scan ->
                    TextButton(onClick = scan) { Text("Scan with VirusTotal") }
                }
                onScanKoodous?.let { scan ->
                    TextButton(onClick = scan) { Text("Scan with Koodous") }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBlocked) "OK" else "Cancel")
            }
        },
        shape = MaterialTheme.shapes.large
    )
}

/**
 * Permission request dialog — calm, clear, trust-building layout.
 */
@Composable
fun PermissionRequestDialog(
    origin: String,
    permissions: List<String>,
    onGrant: () -> Unit,
    onDeny: () -> Unit
) {
    val permissionText = when {
        permissions.contains("GEOLOCATION")  -> "access your location"
        permissions.contains("CAMERA") && permissions.contains("MICROPHONE") -> "access your camera and microphone"
        permissions.contains("CAMERA")       -> "access your camera"
        permissions.contains("MICROPHONE")   -> "access your microphone"
        else -> "access ${permissions.joinToString(", ")}"
    }
    val icon = when {
        permissions.contains("GEOLOCATION")  -> JusBrowseIcons.LocationOn
        permissions.contains("CAMERA")       -> JusBrowseIcons.Videocam
        permissions.contains("MICROPHONE")   -> JusBrowseIcons.Mic
        else                                 -> JusBrowseIcons.Security
    }

    AlertDialog(
        onDismissRequest = onDeny,
        icon = {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(0.4f),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(30.dp))
                }
            }
        },
        title = { Text("Permission Request") },
        text = {
            Column {
                Text(
                    text = origin,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "This site wants to $permissionText")
            }
        },
        confirmButton = {
            Button(onClick = onGrant) { Text("Allow") }
        },
        dismissButton = {
            TextButton(onClick = onDeny) { Text("Block") }
        },
        shape = MaterialTheme.shapes.large
    )
}
