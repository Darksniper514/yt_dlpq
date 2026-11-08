package com.example.ui.screens

import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.DownloadTask
import com.example.data.model.DownloadType
import com.example.ui.LibraryFilter
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.FpsGreen
import com.example.ui.theme.Mp3Purple
import com.example.ui.theme.QualityGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YouTubeBlue
import com.example.ui.theme.YouTubeRed

@Composable
fun LibraryScreen(
    tasks: List<DownloadTask>,
    currentFilter: LibraryFilter,
    searchQuery: String,
    onFilterChange: (LibraryFilter) -> Unit,
    onSearchChange: (String) -> Unit,
    onPlayClick: (DownloadTask) -> Unit,
    onOpenInGallery: (DownloadTask) -> Unit,
    onShareClick: (DownloadTask) -> Unit,
    onDeleteClick: (String) -> Unit,
    onClearCompletedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        // Search & Filter header
        Column(
            modifier = Modifier.padding(top = 10.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search downloaded videos & MP3s…", color = TextSecondary, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear",
                                tint = TextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YouTubeRed,
                    unfocusedBorderColor = DarkOutline,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("library_search_field")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LibraryFilter.values().forEach { filter ->
                        val isSelected = currentFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { onFilterChange(filter) },
                            label = {
                                Text(
                                    text = filter.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = YouTubeRed,
                                selectedLabelColor = Color.White,
                                containerColor = DarkSurface,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = DarkOutline,
                                selectedBorderColor = YouTubeRed
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("filter_${filter.name.lowercase()}")
                        )
                    }
                }

                if (tasks.isNotEmpty()) {
                    TextButton(
                        onClick = onClearCompletedClick,
                        colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("Clear All", fontSize = 12.sp)
                    }
                }
            }
        }

        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.FolderOpen,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No matching downloads" else "Your Library is Empty",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = if (searchQuery.isNotEmpty()) "Try a different search query." else "Downloaded videos are stored directly in DCIM/Camera and appear in Google Photos and Gallery.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    LibraryMediaItemCard(
                        task = task,
                        onPlay = { onPlayClick(task) },
                        onOpenInGallery = { onOpenInGallery(task) },
                        onShare = { onShareClick(task) },
                        onDelete = { onDeleteClick(task.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun LibraryMediaItemCard(
    task: DownloadTask,
    onPlay: () -> Unit,
    onOpenInGallery: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onPlay)
            .testTag("library_item_${task.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail with Play Button
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceVariant)
            ) {
                AsyncImage(
                    model = task.thumbnailUrl,
                    contentDescription = "Thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = (if (task.type == DownloadType.VIDEO) YouTubeRed else Mp3Purple).copy(alpha = 0.9f),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Media Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.customTitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = task.customArtist,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = (if (task.type == DownloadType.VIDEO) QualityGold else Mp3Purple).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = task.displayQuality,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (task.type == DownloadType.VIDEO) QualityGold else Mp3Purple,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = FpsGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = task.storageLocation.label.split(" ").first(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = FpsGreen,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }

                    Text(
                        text = "• ${task.formattedSize}",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                }
            }

            // Actions (View in Gallery, Share & Delete)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onOpenInGallery) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = "Open in Gallery / Photos",
                        tint = FpsGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onShare) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = YouTubeBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MediaPreviewDialog(
    task: DownloadTask,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onOpenInGallery: () -> Unit,
    onShare: () -> Unit,
    onDismiss: () -> Unit
) {
    var internalPlaying by remember { mutableStateOf(isPlaying) }
    var currentSpeed by remember { mutableFloatStateOf(1.0f) }
    var sleepTimerMinutes by remember { mutableIntStateOf(0) } // 0 = off
    var remainingSleepSeconds by remember { mutableLongStateOf(0L) }
    var isSleepMenuOpen by remember { mutableStateOf(false) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var mediaPlayerRef by remember { mutableStateOf<MediaPlayer?>(null) }
    var currentPositionMs by remember { mutableIntStateOf(0) }
    var durationMs by remember { mutableIntStateOf((task.durationSeconds * 1000).toInt().coerceAtLeast(1000)) }

    // Sleep timer countdown effect
    LaunchedEffect(sleepTimerMinutes) {
        if (sleepTimerMinutes > 0) {
            remainingSleepSeconds = sleepTimerMinutes * 60L
            while (remainingSleepSeconds > 0 && internalPlaying) {
                delay(1000L)
                remainingSleepSeconds -= 1L
            }
            if (remainingSleepSeconds <= 0L) {
                internalPlaying = false
                videoViewRef?.pause()
                mediaPlayerRef?.pause()
            }
        } else {
            remainingSleepSeconds = 0L
        }
    }

    // Position polling
    LaunchedEffect(internalPlaying) {
        while (internalPlaying) {
            videoViewRef?.let { vv ->
                if (vv.isPlaying) {
                    currentPositionMs = vv.currentPosition
                    if (vv.duration > 0) durationMs = vv.duration
                }
            }
            delay(500L)
        }
    }

    DisposableEffect(task.id) {
        onDispose {
            videoViewRef?.stopPlayback()
            mediaPlayerRef?.release()
        }
    }

    fun applySpeed(speed: Float) {
        currentSpeed = speed
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mediaPlayerRef?.let { mp ->
                    val params = mp.playbackParams
                    params.speed = speed
                    mp.playbackParams = params
                }
            }
        } catch (_: Exception) {}
    }

    AlertDialog(
        onDismissRequest = {
            videoViewRef?.stopPlayback()
            onDismiss()
        },
        containerColor = DarkSurface,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (task.type == DownloadType.VIDEO) "Video Player" else "Audio MP3 Player",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    if (remainingSleepSeconds > 0) {
                        Text(
                            text = "Sleep in ${remainingSleepSeconds / 60}m ${remainingSleepSeconds % 60}s",
                            style = MaterialTheme.typography.labelSmall.copy(color = QualityGold)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = FpsGreen.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = task.storageLocation.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = FpsGreen,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Video Screen Area / Audio Spinner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    val mediaUri: Uri? = remember(task) {
                        task.localFilePath?.let { Uri.parse(it) }
                            ?: task.mediaStoreUri?.let { Uri.parse(it) }
                    }

                    if (task.type == DownloadType.VIDEO && mediaUri != null) {
                        AndroidView(
                            factory = { ctx ->
                                VideoView(ctx).apply {
                                    setVideoURI(mediaUri)
                                    setOnPreparedListener { mp ->
                                        mediaPlayerRef = mp
                                        mp.isLooping = true
                                        applySpeed(currentSpeed)
                                        if (internalPlaying) {
                                            start()
                                        }
                                        if (duration > 0) durationMs = duration
                                    }
                                    setOnCompletionListener {
                                        internalPlaying = false
                                    }
                                    videoViewRef = this
                                }
                            },
                            update = { view ->
                                if (internalPlaying && !view.isPlaying) {
                                    view.start()
                                } else if (!internalPlaying && view.isPlaying) {
                                    view.pause()
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Audio visual representation
                        AsyncImage(
                            model = task.thumbnailUrl,
                            contentDescription = "Artwork",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        val infiniteTransition = rememberInfiniteTransition(label = "rotation")
                        val rotation by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(6000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "spin"
                        )
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.75f))
                                .border(2.dp, Mp3Purple, CircleShape)
                                .rotate(if (internalPlaying) rotation else 0f),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.GraphicEq,
                                contentDescription = null,
                                tint = Mp3Purple,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                // Title and Artist Info
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = task.customTitle,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${task.customArtist} • ${task.displayQuality}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Seekbar & Timing
                Column(modifier = Modifier.fillMaxWidth()) {
                    val progressFraction = (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
                    Slider(
                        value = progressFraction,
                        onValueChange = { frac ->
                            val targetMs = (frac * durationMs).toInt()
                            currentPositionMs = targetMs
                            videoViewRef?.seekTo(targetMs)
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = if (task.type == DownloadType.VIDEO) YouTubeRed else Mp3Purple,
                            activeTrackColor = if (task.type == DownloadType.VIDEO) YouTubeRed else Mp3Purple,
                            inactiveTrackColor = DarkOutline
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val currentSec = currentPositionMs / 1000
                        val totalSec = durationMs / 1000
                        Text(
                            text = String.format("%02d:%02d", currentSec / 60, currentSec % 60),
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                        Text(
                            text = String.format("%02d:%02d", totalSec / 60, totalSec % 60),
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }
                }

                // Playback controls (-10s, Play/Pause, +10s)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    IconButton(
                        onClick = {
                            val newPos = (currentPositionMs - 10000).coerceAtLeast(0)
                            currentPositionMs = newPos
                            videoViewRef?.seekTo(newPos)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FastRewind,
                            contentDescription = "Rewind 10s",
                            tint = TextPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = if (task.type == DownloadType.VIDEO) YouTubeRed else Mp3Purple,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .clickable {
                                internalPlaying = !internalPlaying
                                if (internalPlaying) {
                                    videoViewRef?.start()
                                } else {
                                    videoViewRef?.pause()
                                }
                                onTogglePlay()
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (internalPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (internalPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            val newPos = (currentPositionMs + 10000).coerceAtMost(durationMs)
                            currentPositionMs = newPos
                            videoViewRef?.seekTo(newPos)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FastForward,
                            contentDescription = "Fast Forward 10s",
                            tint = TextPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Customizes: Playback Speed selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Speed,
                            contentDescription = null,
                            tint = QualityGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Speed",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = QualityGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                            val isSelected = currentSpeed == speed
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) YouTubeRed else DarkSurfaceVariant,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { applySpeed(speed) }
                            ) {
                                Text(
                                    text = "${speed}x",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Customizes: Sleep Timer selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bedtime,
                            contentDescription = null,
                            tint = Mp3Purple,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Sleep Timer",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Mp3Purple,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Box {
                        OutlinedButton(
                            onClick = { isSleepMenuOpen = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (sleepTimerMinutes > 0) QualityGold else TextSecondary
                            ),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(
                                text = if (sleepTimerMinutes == 0) "Off" else "${sleepTimerMinutes}m",
                                fontSize = 11.sp
                            )
                        }

                        DropdownMenu(
                            expanded = isSleepMenuOpen,
                            onDismissRequest = { isSleepMenuOpen = false }
                        ) {
                            listOf(0, 15, 30, 45, 60).forEach { mins ->
                                DropdownMenuItem(
                                    text = {
                                        Text(if (mins == 0) "Turn Off" else "$mins minutes")
                                    },
                                    onClick = {
                                        sleepTimerMinutes = mins
                                        isSleepMenuOpen = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        videoViewRef?.stopPlayback()
                        onOpenInGallery()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FpsGreen)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Gallery", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        videoViewRef?.stopPlayback()
                        onShare()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = YouTubeBlue)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontSize = 12.sp)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    videoViewRef?.stopPlayback()
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
            ) {
                Text("Close")
            }
        }
    )
}
