package com.example.ui

import android.app.Application
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.TubeGrabDatabase
import com.example.data.model.AudioBitrate
import com.example.data.model.AudioFormat
import com.example.data.model.DownloadStatus
import com.example.data.model.DownloadTask
import com.example.data.model.DownloadType
import com.example.data.model.FrameRate
import com.example.data.model.StorageLocation
import com.example.data.model.VideoCodec
import com.example.data.model.VideoInfo
import com.example.data.model.VideoResolution
import com.example.data.repository.YouTubeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

enum class ScreenTab(val title: String) {
    DOWNLOAD("Downloader"),
    QUEUE("Queue"),
    LIBRARY("Library"),
    SETTINGS("Settings")
}

enum class LibraryFilter(val label: String) {
    ALL("All Media"),
    VIDEOS("Videos (DCIM)"),
    AUDIO("Audio (MP3)")
}

data class DownloaderUiState(
    val currentTab: ScreenTab = ScreenTab.DOWNLOAD,
    val inputUrl: String = "",
    val isAnalyzing: Boolean = false,
    val analyzedVideo: VideoInfo? = null,
    val analysisError: String? = null,
    val isSharedFromYouTube: Boolean = false,
    
    // Download Options
    val downloadType: DownloadType = DownloadType.VIDEO,
    val selectedResolution: VideoResolution = VideoResolution.RES_1080P,
    val selectedFrameRate: FrameRate = FrameRate.FPS_60,
    val selectedVideoCodec: VideoCodec = VideoCodec.MP4_H264,
    val selectedAudioFormat: AudioFormat = AudioFormat.MP3,
    val selectedAudioBitrate: AudioBitrate = AudioBitrate.KBPS_320,
    val selectedStorageLocation: StorageLocation = StorageLocation.DCIM_CAMERA,
    
    // Audio Tag Editor
    val tagTitle: String = "",
    val tagArtist: String = "",
    val tagAlbum: String = "YouTube Downloads",
    val isTagEditorExpanded: Boolean = false,

    // Feedback
    val userNotice: String? = null,

    // Media Preview / Playback
    val previewItem: DownloadTask? = null,
    val isPlaying: Boolean = false,

    // Library filter & search
    val libraryFilter: LibraryFilter = LibraryFilter.ALL,
    val librarySearchQuery: String = "",

    // Settings & Personalization
    val appThemeMode: com.example.ui.theme.AppThemeMode = com.example.ui.theme.AppThemeMode.DARK_YOUTUBE,
    val maxConcurrentDownloads: Int = 3,
    val autoClipboardDetection: Boolean = true,
    val highSpeedTurboMode: Boolean = true,
    val autoDownloadOnShare: Boolean = false,
    val customStorageUri: String? = null,
    val customStorageDisplayPath: String? = null
)

class TubeGrabViewModel(application: Application) : AndroidViewModel(application) {
    private val database = TubeGrabDatabase.getDatabase(application)
    private val repository = YouTubeRepository(application, database.downloadDao(), viewModelScope)

    private val _uiState = MutableStateFlow(DownloaderUiState())
    val uiState: StateFlow<DownloaderUiState> = _uiState.asStateFlow()

    val sampleVideos: List<VideoInfo> = repository.sampleVideos

    val allTasks: StateFlow<List<DownloadTask>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeTasks: StateFlow<List<DownloadTask>> = repository.getActiveTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val rawCompletedTasks = repository.getCompletedTasks()

    val completedTasks: StateFlow<List<DownloadTask>> = combine(
        rawCompletedTasks,
        _uiState
    ) { tasks, state ->
        tasks.filter { task ->
            val matchesFilter = when (state.libraryFilter) {
                LibraryFilter.ALL -> true
                LibraryFilter.VIDEOS -> task.type == DownloadType.VIDEO
                LibraryFilter.AUDIO -> task.type == DownloadType.AUDIO
            }
            val matchesSearch = if (state.librarySearchQuery.isBlank()) {
                true
            } else {
                task.customTitle.contains(state.librarySearchQuery, ignoreCase = true) ||
                        task.customArtist.contains(state.librarySearchQuery, ignoreCase = true)
            }
            matchesFilter && matchesSearch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Automatically select the first popular sample video so user lands on a populated preview
        selectSampleVideo(sampleVideos.first())
    }

    fun setScreenTab(tab: ScreenTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun onUrlInputChanged(newUrl: String) {
        _uiState.update { it.copy(inputUrl = newUrl, analysisError = null) }
    }

    fun pasteFromClipboard() {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip() && clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true) {
            val item = clipboard.primaryClip?.getItemAt(0)
            val text = item?.text?.toString() ?: ""
            if (text.isNotBlank()) {
                _uiState.update { it.copy(inputUrl = text) }
                analyzeUrl(text)
            } else {
                showNotice("Clipboard is empty")
            }
        } else {
            showNotice("No text link in clipboard")
        }
    }

    fun analyzeCurrentInput() {
        analyzeUrl(_uiState.value.inputUrl)
    }

    fun analyzeUrl(rawUrl: String, fromShare: Boolean = false) {
        val videoId = repository.extractVideoId(rawUrl)
        if (videoId == null) {
            _uiState.update {
                it.copy(
                    analysisError = "Invalid YouTube link. Please check the URL or ID.",
                    isSharedFromYouTube = false
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAnalyzing = true,
                    analysisError = null,
                    isSharedFromYouTube = fromShare,
                    currentTab = ScreenTab.DOWNLOAD
                )
            }
            val result = repository.fetchVideoDetails(videoId)
            result.onSuccess { info ->
                _uiState.update {
                    it.copy(
                        isAnalyzing = false,
                        analyzedVideo = info,
                        tagTitle = info.title,
                        tagArtist = info.channelTitle,
                        tagAlbum = "YouTube Downloads",
                        analysisError = null
                    )
                }

                // If user enabled auto-download on share, trigger instant download
                if (fromShare && _uiState.value.autoDownloadOnShare) {
                    startDownload()
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isAnalyzing = false,
                        analysisError = error.localizedMessage ?: "Failed to analyze video."
                    )
                }
            }
        }
    }

    fun selectSampleVideo(videoInfo: VideoInfo) {
        _uiState.update {
            it.copy(
                inputUrl = "https://youtu.be/${videoInfo.id}",
                analyzedVideo = videoInfo,
                tagTitle = videoInfo.title,
                tagArtist = videoInfo.channelTitle,
                tagAlbum = "YouTube Downloads",
                analysisError = null,
                isSharedFromYouTube = false
            )
        }
    }

    fun setDownloadType(type: DownloadType) {
        _uiState.update { it.copy(downloadType = type) }
    }

    fun setResolution(res: VideoResolution) {
        _uiState.update { it.copy(selectedResolution = res, downloadType = DownloadType.VIDEO) }
    }

    fun setFrameRate(fps: FrameRate) {
        _uiState.update { it.copy(selectedFrameRate = fps) }
    }

    fun setVideoCodec(codec: VideoCodec) {
        _uiState.update { it.copy(selectedVideoCodec = codec) }
    }

    fun setAudioFormat(format: AudioFormat) {
        _uiState.update { it.copy(selectedAudioFormat = format, downloadType = DownloadType.AUDIO) }
    }

    fun setAudioBitrate(bitrate: AudioBitrate) {
        _uiState.update { it.copy(selectedAudioBitrate = bitrate, downloadType = DownloadType.AUDIO) }
    }

    fun setStorageLocation(location: StorageLocation) {
        _uiState.update { it.copy(selectedStorageLocation = location) }
    }

    fun toggleTagEditor() {
        _uiState.update { it.copy(isTagEditorExpanded = !it.isTagEditorExpanded) }
    }

    fun updateTagTitle(title: String) {
        _uiState.update { it.copy(tagTitle = title) }
    }

    fun updateTagArtist(artist: String) {
        _uiState.update { it.copy(tagArtist = artist) }
    }

    fun updateTagAlbum(album: String) {
        _uiState.update { it.copy(tagAlbum = album) }
    }

    fun startDownload(typeOverride: DownloadType? = null) {
        val state = _uiState.value
        val video = state.analyzedVideo ?: return
        val effectiveType = typeOverride ?: state.downloadType

        viewModelScope.launch {
            repository.createAndStartDownload(
                videoInfo = video,
                type = effectiveType,
                resolution = if (effectiveType == DownloadType.VIDEO) state.selectedResolution else null,
                frameRate = if (effectiveType == DownloadType.VIDEO) state.selectedFrameRate else null,
                codec = if (effectiveType == DownloadType.VIDEO) state.selectedVideoCodec else null,
                audioFormat = state.selectedAudioFormat,
                audioBitrate = state.selectedAudioBitrate,
                storageLocation = state.selectedStorageLocation,
                customTitle = state.tagTitle,
                customArtist = state.tagArtist,
                customAlbum = state.tagAlbum
            )
            _uiState.update {
                it.copy(
                    currentTab = ScreenTab.QUEUE,
                    userNotice = if (state.selectedStorageLocation.isDcimCamera) {
                        "Downloading directly to DCIM/Camera!"
                    } else {
                        "Downloading to ${state.selectedStorageLocation.label}!"
                    }
                )
            }
        }
    }

    fun pauseDownload(taskId: String) {
        repository.pauseDownload(taskId)
    }

    fun resumeDownload(taskId: String) {
        repository.resumeDownload(taskId)
    }

    fun cancelDownload(taskId: String) {
        repository.cancelDownload(taskId)
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            showNotice("File removed")
        }
    }

    fun clearCompleted() {
        viewModelScope.launch {
            repository.clearCompleted()
            showNotice("Completed tasks cleared")
        }
    }

    fun setLibraryFilter(filter: LibraryFilter) {
        _uiState.update { it.copy(libraryFilter = filter) }
    }

    fun setLibrarySearchQuery(query: String) {
        _uiState.update { it.copy(librarySearchQuery = query) }
    }

    fun showPreview(task: DownloadTask) {
        _uiState.update { it.copy(previewItem = task, isPlaying = true) }
    }

    fun dismissPreview() {
        _uiState.update { it.copy(previewItem = null, isPlaying = false) }
    }

    fun togglePlayback() {
        _uiState.update { it.copy(isPlaying = !it.isPlaying) }
    }

    fun openInGallery(task: DownloadTask) {
        val context = getApplication<Application>()
        try {
            val uri = task.mediaStoreUri?.let { Uri.parse(it) } ?: run {
                val path = task.localFilePath ?: return
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(path))
            }
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, if (task.type == DownloadType.VIDEO) "video/*" else "audio/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(viewIntent)
        } catch (e: Exception) {
            shareMedia(task)
        }
    }

    fun shareMedia(task: DownloadTask) {
        val path = task.localFilePath ?: return
        val file = File(path)
        if (!file.exists()) {
            showNotice("File not found on device")
            return
        }

        try {
            val context = getApplication<Application>()
            val uri: Uri = task.mediaStoreUri?.let { Uri.parse(it) } ?: FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val mime = if (task.type == DownloadType.VIDEO) "video/*" else "audio/*"
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mime
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, task.customTitle)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share ${task.customTitle}").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            showNotice("Could not share file: ${e.localizedMessage}")
        }
    }

    fun showNotice(msg: String) {
        _uiState.update { it.copy(userNotice = msg) }
    }

    fun clearNotice() {
        _uiState.update { it.copy(userNotice = null) }
    }

    fun updateMaxConcurrent(count: Int) {
        _uiState.update { it.copy(maxConcurrentDownloads = count) }
    }

    fun toggleTurboMode() {
        _uiState.update { it.copy(highSpeedTurboMode = !it.highSpeedTurboMode) }
    }

    fun toggleAutoClipboard() {
        _uiState.update { it.copy(autoClipboardDetection = !it.autoClipboardDetection) }
    }

    fun setAppTheme(theme: com.example.ui.theme.AppThemeMode) {
        _uiState.update { it.copy(appThemeMode = theme) }
    }

    fun setCustomStorageFolder(uri: Uri, displayPath: String) {
        _uiState.update {
            it.copy(
                customStorageUri = uri.toString(),
                customStorageDisplayPath = displayPath,
                selectedStorageLocation = StorageLocation.CUSTOM
            )
        }
        showNotice("Download folder set to: $displayPath")
    }

    fun updateSelectedStorageLocation(location: StorageLocation) {
        _uiState.update { it.copy(selectedStorageLocation = location) }
    }

    fun toggleAutoDownloadOnShare() {
        _uiState.update { it.copy(autoDownloadOnShare = !it.autoDownloadOnShare) }
    }
}
