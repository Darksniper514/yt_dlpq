package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.ScreenTab
import com.example.ui.TubeGrabViewModel
import com.example.ui.components.TubeGrabBottomNav
import com.example.ui.components.TubeGrabTopBar
import com.example.ui.screens.DownloaderScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.MediaPreviewDialog
import com.example.ui.screens.QueueScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.TubeGrabTheme

class MainActivity : ComponentActivity() {

    private val viewModel: TubeGrabViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle shared URL from YouTube or browser via SEND intent
        handleIncomingIntent(intent)

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            TubeGrabTheme(themeMode = uiState.appThemeMode) {
                TubeGrabApp(
                    viewModel = viewModel,
                    onOpenDirectoryPicker = { launcher ->
                        launcher.launch(null)
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        try {
            if (intent?.action == Intent.ACTION_SEND) {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                    ?: intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
                if (!sharedText.isNullOrBlank()) {
                    viewModel.onUrlInputChanged(sharedText)
                    viewModel.analyzeUrl(sharedText, fromShare = true)
                }
            }
        } catch (e: Exception) {
            // Safe fallback
        }
    }
}

@Composable
fun TubeGrabApp(
    viewModel: TubeGrabViewModel,
    onOpenDirectoryPicker: ((androidx.activity.result.ActivityResultLauncher<Uri?>) -> Unit)? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activeTasks by viewModel.activeTasks.collectAsStateWithLifecycle()
    val completedTasks by viewModel.completedTasks.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // SAF Document Tree Picker Launcher
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)
            } catch (_: Exception) {}

            val pathDecoded = Uri.decode(uri.toString())
            val friendlyName = when {
                pathDecoded.contains(":") -> pathDecoded.substringAfterLast(":")
                pathDecoded.contains("/") -> pathDecoded.substringAfterLast("/")
                else -> uri.lastPathSegment ?: "Custom Folder"
            }
            viewModel.setCustomStorageFolder(uri, friendlyName)
        }
    }

    // Display user feedback snackbars
    LaunchedEffect(uiState.userNotice) {
        uiState.userNotice?.let { notice ->
            snackbarHostState.showSnackbar(notice)
            viewModel.clearNotice()
        }
    }

    // Predictive back handler: navigate to Downloader screen first before closing app
    BackHandler(enabled = uiState.currentTab != ScreenTab.DOWNLOAD) {
        viewModel.setScreenTab(ScreenTab.DOWNLOAD)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TubeGrabTopBar(
                activeDownloadCount = activeTasks.size,
                onTabSelected = { viewModel.setScreenTab(it) }
            )
        },
        bottomBar = {
            TubeGrabBottomNav(
                currentTab = uiState.currentTab,
                activeDownloadCount = activeTasks.size,
                onTabSelected = { viewModel.setScreenTab(it) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (uiState.currentTab) {
                ScreenTab.DOWNLOAD -> {
                    DownloaderScreen(
                        state = uiState,
                        sampleVideos = viewModel.sampleVideos,
                        onUrlChange = { viewModel.onUrlInputChanged(it) },
                        onPasteClick = { viewModel.pasteFromClipboard() },
                        onAnalyzeClick = { viewModel.analyzeCurrentInput() },
                        onSampleSelect = { viewModel.selectSampleVideo(it) },
                        onDownloadTypeChange = { viewModel.setDownloadType(it) },
                        onResolutionChange = { viewModel.setResolution(it) },
                        onFrameRateChange = { viewModel.setFrameRate(it) },
                        onVideoCodecChange = { viewModel.setVideoCodec(it) },
                        onAudioFormatChange = { viewModel.setAudioFormat(it) },
                        onAudioBitrateChange = { viewModel.setAudioBitrate(it) },
                        onStorageLocationChange = { viewModel.setStorageLocation(it) },
                        onToggleTagEditor = { viewModel.toggleTagEditor() },
                        onTagTitleChange = { viewModel.updateTagTitle(it) },
                        onTagArtistChange = { viewModel.updateTagArtist(it) },
                        onTagAlbumChange = { viewModel.updateTagAlbum(it) },
                        onStartDownload = { viewModel.startDownload(it) }
                    )
                }

                ScreenTab.QUEUE -> {
                    QueueScreen(
                        activeTasks = activeTasks,
                        onPauseClick = { viewModel.pauseDownload(it) },
                        onResumeClick = { viewModel.resumeDownload(it) },
                        onCancelClick = { viewModel.cancelDownload(it) },
                        onNavigateToDownloader = { viewModel.setScreenTab(ScreenTab.DOWNLOAD) }
                    )
                }

                ScreenTab.LIBRARY -> {
                    LibraryScreen(
                        tasks = completedTasks,
                        currentFilter = uiState.libraryFilter,
                        searchQuery = uiState.librarySearchQuery,
                        onFilterChange = { viewModel.setLibraryFilter(it) },
                        onSearchChange = { viewModel.setLibrarySearchQuery(it) },
                        onPlayClick = { viewModel.showPreview(it) },
                        onOpenInGallery = { viewModel.openInGallery(it) },
                        onShareClick = { viewModel.shareMedia(it) },
                        onDeleteClick = { viewModel.deleteTask(it) },
                        onClearCompletedClick = { viewModel.clearCompleted() }
                    )
                }

                ScreenTab.SETTINGS -> {
                    SettingsScreen(
                        state = uiState,
                        onThemeChange = { viewModel.setAppTheme(it) },
                        onStorageLocationChange = { viewModel.updateSelectedStorageLocation(it) },
                        onPickCustomFolder = { folderPickerLauncher.launch(null) },
                        onMaxConcurrentChange = { viewModel.updateMaxConcurrent(it) },
                        onToggleTurbo = { viewModel.toggleTurboMode() },
                        onToggleAutoClipboard = { viewModel.toggleAutoClipboard() },
                        onToggleAutoShare = { viewModel.toggleAutoDownloadOnShare() },
                        onClearCache = { viewModel.showNotice("Temporary cache cleared") }
                    )
                }
            }

            // Media Preview Dialog (Video / Audio MP3 Player)
            if (uiState.previewItem != null) {
                MediaPreviewDialog(
                    task = uiState.previewItem!!,
                    isPlaying = uiState.isPlaying,
                    onTogglePlay = { viewModel.togglePlayback() },
                    onOpenInGallery = {
                        val item = uiState.previewItem
                        if (item != null) viewModel.openInGallery(item)
                    },
                    onShare = {
                        val item = uiState.previewItem
                        if (item != null) viewModel.shareMedia(item)
                    },
                    onDismiss = { viewModel.dismissPreview() }
                )
            }
        }
    }
}
