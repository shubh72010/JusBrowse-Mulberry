package com.jusdots.jusbrowse.ui.components


import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.jusdots.jusbrowse.ui.components.JusBrowseIcons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AirlockViewer(
    onDismiss: () -> Unit,
    onDownload: (String) -> Unit,
    initialUrl: String = "",
    initialMimeType: String = "",
    mediaList: List<MediaItem> = emptyList(),
    initialIndex: Int = 0,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onDismiss()
    }
    
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(
        initialPage = if (initialIndex in mediaList.indices) initialIndex else 0,
        pageCount = { mediaList.size.coerceAtLeast(1) }
    )
    val scope = rememberCoroutineScope()
    
    // Immersive container
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (mediaList.isNotEmpty()) {
                androidx.compose.foundation.pager.HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    pageSpacing = 16.dp,
                    key = { page -> if (page < mediaList.size) mediaList[page].url else page }
                ) { page ->
                    val item = mediaList[page]
                    val itemMimeType = when {
                        item.url.contains(".webp") || item.url.contains(".jpg") || item.url.contains(".png") -> "image/"
                        item.url.contains(".mp4") || item.url.contains(".webm") || item.url.contains("vid_") -> "video/"
                        item.url.contains(".mp3") || item.url.contains(".wav") || item.url.contains("aud_") -> "audio/"
                        else -> initialMimeType // Fallback to passed mimetype
                    }
                    
                    val isCurrentPage = pagerState.currentPage == page
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        when {
                            itemMimeType.startsWith("image/") -> ImageAirlock(item.url)
                            itemMimeType.startsWith("video/") -> VideoAirlock(url = item.url, isVisible = isCurrentPage)
                            itemMimeType.startsWith("audio/") -> AudioAirlock(item.url)
                            else -> UnsupportedMedia(itemMimeType)
                        }
                    }
                }
            } else {
                // Fallback for single item (Legacy)
                when {
                    initialMimeType.startsWith("image/") -> ImageAirlock(initialUrl)
                    initialMimeType.startsWith("video/") -> VideoAirlock(initialUrl)
                    initialMimeType.startsWith("audio/") -> AudioAirlock(initialUrl)
                    else -> UnsupportedMedia(initialMimeType)
                }
            }
            
            // Top Bar
            val currentUrl = if (mediaList.isNotEmpty()) mediaList[pagerState.currentPage].url else initialUrl
            AirlockTopBar(
                onDownload = { onDownload(currentUrl) },
                onClose = onDismiss
            )

            // Page Indicator (Bottom)
            if (mediaList.size > 1) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp + 48.dp) // Above controls if any, or just safe area
                        .background(Color.Black, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${pagerState.currentPage + 1} / ${mediaList.size}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                
                // Navigation Buttons (Left/Right)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Button
                    if (pagerState.currentPage > 0) {
                        Surface(
                            onClick = { 
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            shape = CircleShape,
                            color = Color.Black,
                            contentColor = Color.White,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = JusBrowseIcons.ArrowBackIosNew,
                                    contentDescription = "Previous",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }

                    // Right Button
                    if (pagerState.currentPage < mediaList.size - 1) {
                        Surface(
                            onClick = { 
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            shape = CircleShape,
                            color = Color.Black,
                            contentColor = Color.White,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = JusBrowseIcons.ArrowForwardIos,
                                    contentDescription = "Next",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageAirlock(url: String) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        if (scale > 1f) {
            offset += offsetChange
        } else {
            offset = Offset.Zero
        }
    }
    
    // Reset offset when scale returns to 1
    LaunchedEffect(scale) {
        if (scale <= 1f) {
            offset = Offset.Zero
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .transformable(
                state = state,
                lockRotationOnZoomPan = true
            ),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun VideoAirlock(url: String, isVisible: Boolean = true) {
    val context = LocalContext.current
    var showControls by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(false) }
    var duration by remember { mutableLongStateOf(0L) }
    var position by remember { mutableLongStateOf(0L) }
    var isBuffering by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(false) }

    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }

    LaunchedEffect(isVisible, url) {
        if (isVisible) {
            val player = ExoPlayer.Builder(context).build().apply {
                val uri = if (url.startsWith("/")) {
                    Uri.fromFile(java.io.File(url))
                } else {
                    Uri.parse(url)
                }
                setMediaItem(androidx.media3.common.MediaItem.fromUri(uri))
                prepare()
                playWhenReady = true

                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        isBuffering = playbackState == Player.STATE_BUFFERING
                        if (playbackState == Player.STATE_READY) {
                            duration = this@apply.duration
                        }
                    }
                    override fun onIsPlayingChanged(playing: Boolean) {
                        isPlaying = playing
                    }
                })
            }
            exoPlayer = player

            while (isActive) {
                position = player.currentPosition
                delay(500)
            }
        } else {
            exoPlayer?.release()
            exoPlayer = null
        }
    }

    if (!isVisible || exoPlayer == null) return
    val player = exoPlayer!!

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { showControls = !showControls }
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isBuffering) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Custom Video Controls
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
            ) {
                // Center Play/Pause
                IconButton(
                    onClick = { if (isPlaying) player.pause() else player.play() },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isPlaying) JusBrowseIcons.Pause else JusBrowseIcons.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Bottom Controls
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                            )
                        )
                        .navigationBarsPadding()
                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                ) {
                    Slider(
                        value = position.toFloat(),
                        onValueChange = { player.seekTo(it.toLong()) },
                        valueRange = 0f..(if (duration > 0) duration.toFloat() else 1f),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        )
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(position),
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = formatTime(duration),
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    // Mute Button for Video
                    IconButton(
                        onClick = { 
                            isMuted = !isMuted
                            player.volume = if (isMuted) 0f else 1f
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(
                            imageVector = if (isMuted) JusBrowseIcons.VolumeOff else JusBrowseIcons.VolumeUp,
                            contentDescription = "Mute",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioAirlock(url: String) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var duration by remember { mutableLongStateOf(0L) }
    var position by remember { mutableLongStateOf(0L) }
    var isMuted by remember { mutableStateOf(false) }
    
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val uri = if (url.startsWith("/")) {
                Uri.fromFile(java.io.File(url))
            } else {
                Uri.parse(url)
            }
            setMediaItem(androidx.media3.common.MediaItem.fromUri(uri))
            prepare()
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) duration = this@apply.duration
                }
                override fun onIsPlayingChanged(playing: Boolean) { isPlaying = playing }
            })
        }
    }
    
    LaunchedEffect(exoPlayer) {
        while (isActive) {
            position = exoPlayer.currentPosition
            delay(200)
        }
    }
    
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier
                .size(240.dp)
                .clip(RoundedCornerShape(32.dp)),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = JusBrowseIcons.GraphicEq,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(120.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = "Airlock Streaming",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                shadow = Shadow(Color.Black, Offset(2f, 2f), 4f)
            )
        )
        Text(
            text = "Secured Media Feed",
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Slider(
            value = position.toFloat(),
            onValueChange = { exoPlayer.seekTo(it.toLong()) },
            valueRange = 0f..(if (duration > 0) duration.toFloat() else 1f),
            modifier = Modifier.fillMaxWidth(0.9f)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(position), color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            Text(formatTime(duration), color = Color.Gray, style = MaterialTheme.typography.labelSmall)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            IconButton(onClick = { exoPlayer.seekTo(position - 10000) }) {
                Icon(JusBrowseIcons.Replay10, null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            
            IconButton(
                onClick = { if (isPlaying) exoPlayer.pause() else exoPlayer.play() },
                modifier = Modifier
                    .size(84.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(
                    imageVector = if (isPlaying) JusBrowseIcons.Pause else JusBrowseIcons.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            IconButton(onClick = { exoPlayer.seekTo(position + 10000) }) {
                Icon(JusBrowseIcons.FastForward, null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        IconButton(
            onClick = { 
                isMuted = !isMuted
                exoPlayer.volume = if (isMuted) 0f else 1f
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = if (isMuted) JusBrowseIcons.VolumeOff else JusBrowseIcons.VolumeUp,
                contentDescription = "Mute",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun UnsupportedMedia(mimeType: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(JusBrowseIcons.Photo, null, tint = Color.Gray, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Preview unavailable", color = Color.White)
            Text(mimeType, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun AirlockTopBar(
    onDownload: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                )
            )
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(JusBrowseIcons.Close, "Close", tint = Color.White)
        }
        
        IconButton(
            onClick = onDownload,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), CircleShape)
        ) {
            Icon(JusBrowseIcons.Download, "Download", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

private fun formatTime(millis: Long): String {
    val seconds = (millis / 1000).toInt()
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(minutes, secs)
}
