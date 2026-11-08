package com.example.data.model

import java.io.File

enum class DownloadType(val label: String) {
    VIDEO("Video"),
    AUDIO("Audio (MP3)")
}

enum class StorageLocation(
    val label: String,
    val description: String,
    val relativePath: String,
    val isDcimCamera: Boolean = false
) {
    DCIM_CAMERA("DCIM / Camera", "Saves directly to Camera Roll (Appears in Gallery & Google Photos)", "DCIM/Camera", true),
    DCIM_TUBEGRAB("DCIM / TubeGrab", "Saves to DCIM/TubeGrab folder in Gallery", "DCIM/TubeGrab"),
    MOVIES("Movies / TubeGrab", "Standard Movies directory", "Movies/TubeGrab"),
    MUSIC("Music / TubeGrab", "Standard Music directory (Best for MP3s)", "Music/TubeGrab"),
    DOWNLOADS("Download / TubeGrab", "Standard public Downloads folder", "Download/TubeGrab"),
    CUSTOM("Custom Folder", "User selected folder via Android file explorer", "Custom")
}

enum class VideoResolution(
    val label: String,
    val height: Int,
    val width: Int,
    val badge: String,
    val approxBitrateMbps: Double
) {
    RES_4K("4K UHD", 2160, 3840, "2160p", 25.0),
    RES_1440P("2K QHD", 1440, 2560, "1440p", 12.0),
    RES_1080P("Full HD", 1080, 1920, "1080p", 6.0),
    RES_720P("HD", 720, 1280, "720p", 3.0),
    RES_480P("SD", 480, 854, "480p", 1.5),
    RES_360P("Standard", 360, 640, "360p", 0.8),
    RES_240P("Low", 240, 426, "240p", 0.4);

    fun estimatedSizeBytes(durationSeconds: Long, fps: FrameRate): Long {
        val fpsMultiplier = if (fps.value >= 60) 1.35 else 1.0
        val bits = (approxBitrateMbps * 1_000_000 * fpsMultiplier * durationSeconds)
        return (bits / 8).toLong().coerceAtLeast(500_000)
    }
}

enum class FrameRate(val value: Int, val label: String, val description: String) {
    FPS_60(60, "60 FPS", "Ultra-smooth high frame rate"),
    FPS_50(50, "50 FPS", "High frame rate"),
    FPS_30(30, "30 FPS", "Standard smooth playback"),
    FPS_24(24, "24 FPS", "Cinematic motion")
}

enum class VideoCodec(val label: String, val extension: String, val description: String) {
    MP4_H264("MP4 (H.264)", "mp4", "Universal device playback"),
    WEBM_VP9("WEBM (VP9)", "webm", "High compression efficiency"),
    AV1("MP4 (AV1)", "mp4", "Modern high efficiency codec")
}

enum class AudioFormat(val label: String, val extension: String, val mimeType: String) {
    MP3("MP3", "mp3", "audio/mpeg"),
    M4A("M4A (AAC)", "m4a", "audio/mp4"),
    FLAC("FLAC (Lossless)", "flac", "audio/flac"),
    WAV("WAV", "wav", "audio/wav")
}

enum class AudioBitrate(val kbps: Int, val label: String, val description: String) {
    KBPS_320(320, "320 kbps", "Studio Master quality"),
    KBPS_256(256, "256 kbps", "Very high fidelity"),
    KBPS_192(192, "192 kbps", "High quality (Recommended)"),
    KBPS_128(128, "128 kbps", "Standard streaming"),
    KBPS_64(64, "64 kbps", "Lightweight / Speech");

    fun estimatedSizeBytes(durationSeconds: Long): Long {
        val bits = kbps * 1000L * durationSeconds
        return (bits / 8).coerceAtLeast(100_000)
    }
}

enum class DownloadStatus(val label: String) {
    QUEUED("Queued"),
    FETCHING("Fetching stream…"),
    DOWNLOADING("Downloading"),
    SAVING_MEDIASTORE("Saving to DCIM/Camera…"),
    CONVERTING("Extracting audio…"),
    COMPLETED("Saved to DCIM/Camera"),
    PAUSED("Paused"),
    FAILED("Failed")
}

data class VideoInfo(
    val id: String,
    val title: String,
    val channelTitle: String,
    val durationSeconds: Long,
    val thumbnailUrl: String,
    val viewsCount: String,
    val uploadDate: String,
    val description: String,
    val isLiveStream: Boolean = false,
    val streamSampleUrl: String? = null
) {
    val formattedDuration: String
        get() {
            val hours = durationSeconds / 3600
            val minutes = (durationSeconds % 3600) / 60
            val seconds = durationSeconds % 60
            return if (hours > 0) {
                String.format("%d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%d:%02d", minutes, seconds)
            }
        }
}

data class DownloadTask(
    val id: String,
    val videoId: String,
    val title: String,
    val channel: String,
    val thumbnailUrl: String,
    val durationSeconds: Long,
    val type: DownloadType,
    val resolution: VideoResolution?,
    val frameRate: FrameRate?,
    val videoCodec: VideoCodec?,
    val audioFormat: AudioFormat,
    val audioBitrate: AudioBitrate,
    val storageLocation: StorageLocation = StorageLocation.DCIM_CAMERA,
    val totalBytes: Long,
    val downloadedBytes: Long,
    val progress: Float, // 0.0 to 1.0
    val speedBps: Long, // bytes per second
    val status: DownloadStatus,
    val localFilePath: String?,
    val mediaStoreUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val errorMessage: String? = null,
    // Tag editor metadata
    val customTitle: String = title,
    val customArtist: String = channel,
    val customAlbum: String = "YouTube Downloads"
) {
    val fileExtension: String
        get() = if (type == DownloadType.VIDEO) {
            videoCodec?.extension ?: "mp4"
        } else {
            audioFormat.extension
        }

    val displayQuality: String
        get() = if (type == DownloadType.VIDEO) {
            "${resolution?.badge ?: "1080p"} • ${frameRate?.label ?: "60 FPS"}"
        } else {
            "${audioFormat.label} • ${audioBitrate.label}"
        }

    val formattedSpeed: String
        get() {
            val mbps = speedBps.toDouble() / (1024 * 1024)
            return if (mbps >= 1.0) {
                String.format("%.1f MB/s", mbps)
            } else {
                val kbps = speedBps.toDouble() / 1024
                String.format("%.0f KB/s", kbps)
            }
        }

    val formattedSize: String
        get() {
            val currentMb = downloadedBytes.toDouble() / (1024 * 1024)
            val totalMb = totalBytes.toDouble() / (1024 * 1024)
            return if (status == DownloadStatus.COMPLETED) {
                String.format("%.1f MB", totalMb)
            } else {
                String.format("%.1f / %.1f MB", currentMb, totalMb)
            }
        }

    val etaSeconds: Long
        get() {
            if (speedBps <= 0 || status != DownloadStatus.DOWNLOADING) return 0
            val remainingBytes = (totalBytes - downloadedBytes).coerceAtLeast(0)
            return remainingBytes / speedBps
        }

    val fileExists: Boolean
        get() = localFilePath != null && File(localFilePath).exists()
}
