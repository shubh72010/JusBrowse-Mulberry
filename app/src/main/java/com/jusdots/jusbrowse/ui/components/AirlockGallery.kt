package com.jusdots.jusbrowse.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import com.jusdots.jusbrowse.ui.components.JusBrowseIcons
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AirlockGallery(
    mediaData: MediaData,
    onMediaClick: (url: String, mimeType: String, list: List<MediaItem>, index: Int) -> Unit,
    onClose: () -> Unit,
    isVaulting: Boolean = false,
    vaultProgress: Float = 0f,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        "Images" to mediaData.images.size,
        "Videos" to mediaData.videos.size,
        "Audio" to mediaData.audio.size
    )
    
    val galleryPagerState = androidx.compose.foundation.pager.rememberPagerState(
        pageCount = { tabs.size }
    )
    val coroutineScope = rememberCoroutineScope()
    
    // Sync tab selection with pager
    LaunchedEffect(galleryPagerState.currentPage) {
        // Just let it sync via state
    }
    BackHandler {
        onClose()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Airlock Gallery",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
            )
            
            IconButton(
                onClick = onClose,
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    CircleShape
                ).border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            ) {
                Icon(
                    imageVector = JusBrowseIcons.Close,
                    contentDescription = "Close Gallery",
                    tint = Color.White
                )
            }
        }

        // Vaulting Progress
        androidx.compose.animation.AnimatedVisibility(visible = isVaulting) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.5.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Vaulting & Isolating Media...",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearWavyProgressIndicator(
                    progress = { vaultProgress },
                    modifier = Modifier.fillMaxWidth().height(10.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.White.copy(alpha = 0.1f),
                    stroke = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                    trackStroke = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
            }
        }
        
        // Empty state
        if (mediaData.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "⛱️",
                        fontSize = 80.sp
                    )
                    Text(
                        text = "The airlock is empty",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
            return
        }
        
        // Tab row
        ScrollableTabRow(
            selectedTabIndex = galleryPagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 16.dp,
            divider = {},
            indicator = { tabPositions ->
                if (galleryPagerState.currentPage < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[galleryPagerState.currentPage]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) {
            tabs.forEachIndexed { index, pair ->
                Tab(
                    selected = galleryPagerState.currentPage == index,
                    onClick = { 
                        coroutineScope.launch {
                            galleryPagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = pair.first,
                                style = if (galleryPagerState.currentPage == index) 
                                    MaterialTheme.typography.titleSmall 
                                else 
                                    MaterialTheme.typography.bodyMedium
                            )
                            if (pair.second > 0) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 6.dp)
                                        .background(
                                            if (galleryPagerState.currentPage == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            CircleShape
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = pair.second.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (galleryPagerState.currentPage == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
        
        // Content with HorizontalPager for swiping between tabs
        androidx.compose.foundation.pager.HorizontalPager(
            state = galleryPagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = true
        ) { targetTab ->
            when (targetTab) {
                0 -> ImageGrid(
                    images = mediaData.images,
                    onImageClick = { index -> 
                        onMediaClick(mediaData.images[index].url, "image/*", mediaData.images, index) 
                    }
                )
                1 -> VideoList(
                    videos = mediaData.videos,
                    onVideoClick = { index -> 
                        onMediaClick(mediaData.videos[index].url, "video/*", mediaData.videos, index) 
                    }
                )
                2 -> AudioList(
                    audio = mediaData.audio,
                    onAudioClick = { index -> 
                        onMediaClick(mediaData.audio[index].url, "audio/*", mediaData.audio, index) 
                    }
                )
            }
        }
    }
}

@Composable
private fun ImageGrid(
    images: List<MediaItem>,
    onImageClick: (Int) -> Unit
) {
    if (images.isEmpty()) {
        EmptyMediaState("The gallery is empty")
        return
    }
    
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        items(images.size) { index ->
            val item = images[index]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { onImageClick(index) },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.url)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(if (item.url.length % 2 == 0) 0.8f else 1.2f),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun VideoList(
    videos: List<MediaItem>,
    onVideoClick: (Int) -> Unit
) {
    if (videos.isEmpty()) {
        EmptyMediaState("No videos in the airlock")
        return
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(videos.size) { index ->
            val item = videos[index]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVideoClick(index) },
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = JusBrowseIcons.SlowMotionVideo,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = item.title.ifEmpty { "Video Content" },
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = item.metadata.ifEmpty { "Media discovered by JusBrowse" },
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    Icon(
                        imageVector = JusBrowseIcons.PlayCircle,
                        contentDescription = "Play",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AudioList(
    audio: List<MediaItem>,
    onAudioClick: (Int) -> Unit
) {
    if (audio.isEmpty()) {
        EmptyMediaState("Silence in the airlock")
        return
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(audio.size) { index ->
            val item = audio[index]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAudioClick(index) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, 
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = JusBrowseIcons.Audiotrack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(8.dp)
                    )
                    
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = item.title.ifEmpty { "Audio Stream" },
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (item.metadata.isNotEmpty()) {
                            Text(
                                text = item.metadata,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyMediaState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

