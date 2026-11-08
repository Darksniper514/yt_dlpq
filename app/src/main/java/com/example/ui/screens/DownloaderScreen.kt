package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AudioBitrate
import com.example.data.model.AudioFormat
import com.example.data.model.DownloadType
import com.example.data.model.FrameRate
import com.example.data.model.StorageLocation
import com.example.data.model.VideoCodec
import com.example.data.model.VideoInfo
import com.example.data.model.VideoResolution
import com.example.ui.DownloaderUiState
import com.example.ui.components.SelectableOptionCard
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DownloaderScreen(
    state: DownloaderUiState,
    sampleVideos: List<VideoInfo>,
    onUrlChange: (String) -> Unit,
    onPasteClick: () -> Unit,
    onAnalyzeClick: () -> Unit,
    onSampleSelect: (VideoInfo) -> Unit,
    onDownloadTypeChange: (DownloadType) -> Unit,
    onResolutionChange: (VideoResolution) -> Unit,
    onFrameRateChange: (FrameRate) -> Unit,
    onVideoCodecChange: (VideoCodec) -> Unit,
    onAudioFormatChange: (AudioFormat) -> Unit,
    onAudioBitrateChange: (AudioBitrate) -> Unit,
    onStorageLocationChange: (StorageLocation) -> Unit,
    onToggleTagEditor: () -> Unit,
    onTagTitleChange: (String) -> Unit,
    onTagArtistChange: (String) -> Unit,
    onTagAlbumChange: (String) -> Unit,
    onStartDownload: (DownloadType?) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Search & Paste URL Input
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "YouTube Downloader",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = FpsGreen.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FpsGreen.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PhotoCamera,
                                    contentDescription = null,
                                    tint = FpsGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "DCIM/Camera Active",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = FpsGreen,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = state.inputUrl,
                        onValueChange = onUrlChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("url_input_field"),
                        placeholder = {
                            Text(
                                "Paste YouTube link or share from YouTube…",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = YouTubeRed
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (state.inputUrl.isNotEmpty()) {
                                    IconButton(onClick = { onUrlChange("") }) {
                                        Icon(
                                            imageVector = Icons.Filled.Clear,
                                            contentDescription = "Clear",
                                            tint = TextSecondary
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = onPasteClick,
                                    modifier = Modifier.testTag("paste_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ContentPaste,
                                        contentDescription = "Paste",
                                        tint = YouTubeBlue
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            onAnalyzeClick()
                        }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = YouTubeRed,
                            unfocusedBorderColor = DarkOutline,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            onAnalyzeClick()
                        },
                        enabled = state.inputUrl.isNotBlank() && !state.isAnalyzing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("analyze_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = YouTubeRed,
                            disabledContainerColor = DarkSurfaceVariant
                        )
                    ) {
                        if (state.isAnalyzing) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyzing Video Streams…", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Filled.HighQuality,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Analyze Video & Formats", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Error banner
                    AnimatedVisibility(visible = state.analysisError != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ErrorRed.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Error,
                                    contentDescription = "Error",
                                    tint = ErrorRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = state.analysisError ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(color = ErrorRed)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Shared from YouTube Special Banner
        if (state.isSharedFromYouTube) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2838)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, YouTubeBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = null,
                                tint = YouTubeBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Received from YouTube Share",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        Text(
                            text = "Link received and analyzed! Choose any video or audio option below, or tap Instant Download to save directly to DCIM/Camera.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB0C4DE))
                        )

                        Button(
                            onClick = { onStartDownload(DownloadType.VIDEO) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Bolt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "⚡ Instant Auto-Download (1080p 60FPS ➔ DCIM/Camera)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Section: Instant Test Videos Carousel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Test Links",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    )
                    Text(
                        text = "Tap to load",
                        style = MaterialTheme.typography.labelSmall.copy(color = YouTubeBlue)
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(sampleVideos) { sample ->
                        val isSelected = state.analyzedVideo?.id == sample.id
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) YouTubeRed.copy(alpha = 0.2f) else DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) YouTubeRed else DarkOutline
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onSampleSelect(sample) }
                                .testTag("sample_chip_${sample.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (sample.title.contains("MP3", ignoreCase = true)) {
                                        Icons.Filled.Audiotrack
                                    } else {
                                        Icons.Filled.Videocam
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) YouTubeRed else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Column {
                                    Text(
                                        text = sample.title.take(24) + "…",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) TextPrimary else TextSecondary
                                        )
                                    )
                                    Text(
                                        text = sample.formattedDuration,
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Analyzed Video Preview Card
        if (state.analyzedVideo != null) {
            val video = state.analyzedVideo
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Thumbnail with Duration & Camera Destination Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceVariant)
                        ) {
                            AsyncImage(
                                model = video.thumbnailUrl,
                                contentDescription = "Video Thumbnail",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Duration Pill
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color.Black.copy(alpha = 0.85f),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = video.formattedDuration,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            // Save Target: DCIM / Camera Badge
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color.Black.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PhotoCamera,
                                        contentDescription = null,
                                        tint = FpsGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = state.selectedStorageLocation.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = FpsGreen,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = video.channelTitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = YouTubeBlue
                                )
                            )
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Verified",
                                tint = YouTubeBlue,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                            Text(
                                text = video.viewsCount,
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "👇 Scroll down to check and select Videos (with FPS) & Audio (MP3)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = YouTubeBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            // ==========================================
            // SECTION 1: VIDEOS (QUALITY, RESOLUTION & FPS)
            // ==========================================
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Section Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Videocam,
                                    contentDescription = null,
                                    tint = YouTubeRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "1. Video Formats & Quality",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = "Install directly into DCIM/Camera",
                                        style = MaterialTheme.typography.labelSmall.copy(color = FpsGreen)
                                    )
                                }
                            }

                            Button(
                                onClick = { onStartDownload(DownloadType.VIDEO) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Download,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Get Video", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Quality / Resolution Chips
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Resolution (Select what you want):",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                VideoResolution.values().forEach { res ->
                                    val isSelected = state.selectedResolution == res && state.downloadType == DownloadType.VIDEO
                                    val estSizeMb = res.estimatedSizeBytes(
                                        video.durationSeconds,
                                        state.selectedFrameRate
                                    ) / (1024 * 1024)

                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { onResolutionChange(res) },
                                        label = {
                                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                                Text(
                                                    text = "${res.badge} (${res.label})",
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    fontSize = 12.sp
                                                )
                                                Text(
                                                    text = "~$estSizeMb MB",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextSecondary,
                                                        fontSize = 10.sp
                                                    )
                                                )
                                            }
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = YouTubeRed,
                                            selectedLabelColor = Color.White,
                                            containerColor = DarkSurfaceVariant,
                                            labelColor = TextPrimary
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = DarkOutline,
                                            selectedBorderColor = YouTubeRed
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("res_chip_${res.badge}")
                                    )
                                }
                            }
                        }

                        // Frame Rate (FPS) Chips
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Frames Per Second (FPS):",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSecondary
                                    )
                                )
                                Text(
                                    text = "Motion Smoothness",
                                    style = MaterialTheme.typography.labelSmall.copy(color = FpsGreen)
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                FrameRate.values().forEach { fps ->
                                    val isSelected = state.selectedFrameRate == fps
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) FpsGreen.copy(alpha = 0.15f) else DarkSurfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) FpsGreen else DarkOutline
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { onFrameRateChange(fps) }
                                            .testTag("fps_chip_${fps.value}")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = fps.label,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) FpsGreen else TextPrimary
                                                )
                                            )
                                            Text(
                                                text = if (fps.value >= 60) "Ultra-Smooth" else if (fps.value == 30) "Standard" else if (fps.value == 24) "Cinema" else "PAL",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (isSelected) FpsGreen.copy(alpha = 0.8f) else TextSecondary,
                                                    fontSize = 9.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Codec
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Video Codec:",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                VideoCodec.values().forEach { codec ->
                                    val isSelected = state.selectedVideoCodec == codec
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { onVideoCodecChange(codec) },
                                        label = {
                                            Text(
                                                text = codec.label,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = YouTubeRed,
                                            selectedLabelColor = Color.White,
                                            containerColor = DarkSurfaceVariant,
                                            labelColor = TextPrimary
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = DarkOutline,
                                            selectedBorderColor = YouTubeRed
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // SECTION 2: AUDIO & MP3 EXTRACTION
            // ==========================================
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Section Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.GraphicEq,
                                    contentDescription = null,
                                    tint = Mp3Purple,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "2. Audio & MP3 Extraction",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = "Studio Master Bitrates",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Mp3Purple)
                                    )
                                }
                            }

                            Button(
                                onClick = { onStartDownload(DownloadType.AUDIO) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Mp3Purple),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Audiotrack,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Get MP3", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Audio Format Chips (MP3, M4A, FLAC, WAV)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Audio Format:",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AudioFormat.values().forEach { format ->
                                    val isSelected = state.selectedAudioFormat == format && state.downloadType == DownloadType.AUDIO
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) Mp3Purple.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) Mp3Purple else DarkOutline
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { onAudioFormatChange(format) }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = format.label.split(" ").first(),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Mp3Purple else TextPrimary
                                                )
                                            )
                                            Text(
                                                text = ".${format.extension}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = TextSecondary,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Audio Bitrates (320kbps, 256kbps, 192kbps, 128kbps, 64kbps)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Audio Bitrate (Sound Fidelity):",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                AudioBitrate.values().forEach { bitrate ->
                                    val isSelected = state.selectedAudioBitrate == bitrate && state.downloadType == DownloadType.AUDIO
                                    val estMb = String.format(
                                        "%.1f",
                                        bitrate.estimatedSizeBytes(video.durationSeconds).toDouble() / (1024 * 1024)
                                    )

                                    SelectableOptionCard(
                                        title = bitrate.label,
                                        subtitle = "${bitrate.description} • ~$estMb MB",
                                        badgeText = if (bitrate == AudioBitrate.KBPS_320) "Studio Master" else if (bitrate == AudioBitrate.KBPS_192) "Recommended" else null,
                                        badgeColor = if (bitrate == AudioBitrate.KBPS_320) QualityGold else Mp3Purple,
                                        isSelected = isSelected,
                                        onClick = { onAudioBitrateChange(bitrate) }
                                    )
                                }
                            }
                        }

                        // ID3 Music Tag Editor (Collapsible)
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onToggleTagEditor() },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = null,
                                            tint = YouTubeBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "ID3 Music Tag Editor (Title, Artist, Album)",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        )
                                    }
                                    Icon(
                                        imageVector = if (state.isTagEditorExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                        contentDescription = "Expand",
                                        tint = TextSecondary
                                    )
                                }

                                AnimatedVisibility(
                                    visible = state.isTagEditorExpanded,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(top = 10.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = state.tagTitle,
                                            onValueChange = onTagTitleChange,
                                            label = { Text("Track Title") },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = YouTubeBlue,
                                                unfocusedBorderColor = DarkOutline,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary
                                            )
                                        )
                                        OutlinedTextField(
                                            value = state.tagArtist,
                                            onValueChange = onTagArtistChange,
                                            label = { Text("Artist / Channel") },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = YouTubeBlue,
                                                unfocusedBorderColor = DarkOutline,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary
                                            )
                                        )
                                        OutlinedTextField(
                                            value = state.tagAlbum,
                                            onValueChange = onTagAlbumChange,
                                            label = { Text("Album Name") },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = YouTubeBlue,
                                                unfocusedBorderColor = DarkOutline,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // SECTION 3: STORAGE DESTINATION SELECTOR
            // ==========================================
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PhotoCamera,
                                contentDescription = null,
                                tint = FpsGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "3. Storage Destination (Install Location)",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = "Choose where files are placed on your device",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            StorageLocation.values().forEach { location ->
                                val isSelected = state.selectedStorageLocation == location
                                SelectableOptionCard(
                                    title = location.label,
                                    subtitle = location.description,
                                    badgeText = if (location.isDcimCamera) "Camera Roll & Gallery" else null,
                                    badgeColor = FpsGreen,
                                    isSelected = isSelected,
                                    onClick = { onStorageLocationChange(location) }
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // SECTION 4: PRIMARY ACTION DOWNLOAD SUMMARY
            // ==========================================
            item {
                val isVideo = state.downloadType == DownloadType.VIDEO
                val summaryPill = if (isVideo) {
                    val estMb = state.selectedResolution.estimatedSizeBytes(
                        video.durationSeconds,
                        state.selectedFrameRate
                    ) / (1024 * 1024)
                    "${state.selectedResolution.badge} • ${state.selectedFrameRate.label} • ${state.selectedVideoCodec.label.split(" ").first()} • ~$estMb MB ➔ ${state.selectedStorageLocation.label}"
                } else {
                    val estMb = String.format(
                        "%.1f",
                        state.selectedAudioBitrate.estimatedSizeBytes(video.durationSeconds).toDouble() / (1024 * 1024)
                    )
                    "${state.selectedAudioFormat.label} • ${state.selectedAudioBitrate.label} • ~$estMb MB ➔ ${state.selectedStorageLocation.label}"
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Selected Output",
                                style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = (if (isVideo) YouTubeRed else Mp3Purple).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = summaryPill,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isVideo) YouTubeRed else Mp3Purple
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Button(
                            onClick = { onStartDownload(null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("download_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isVideo) YouTubeRed else Mp3Purple
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = "Download",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isVideo) {
                                    "Download Video to ${state.selectedStorageLocation.label.split(" ").first()}"
                                } else {
                                    "Extract Audio to ${state.selectedStorageLocation.label.split(" ").first()}"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
