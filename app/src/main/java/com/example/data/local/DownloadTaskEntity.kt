package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AudioBitrate
import com.example.data.model.AudioFormat
import com.example.data.model.DownloadStatus
import com.example.data.model.DownloadTask
import com.example.data.model.DownloadType
import com.example.data.model.FrameRate
import com.example.data.model.StorageLocation
import com.example.data.model.VideoCodec
import com.example.data.model.VideoResolution

@Entity(tableName = "download_tasks")
data class DownloadTaskEntity(
    @PrimaryKey
    val id: String,
    val videoId: String,
    val title: String,
    val channel: String,
    val thumbnailUrl: String,
    val durationSeconds: Long,
    val type: String, // DownloadType name
    val resolution: String?, // VideoResolution name
    val frameRate: String?, // FrameRate name
    val videoCodec: String?, // VideoCodec name
    val audioFormat: String, // AudioFormat name
    val audioBitrate: String, // AudioBitrate name
    val storageLocation: String = StorageLocation.DCIM_CAMERA.name,
    val totalBytes: Long,
    val downloadedBytes: Long,
    val progress: Float,
    val status: String, // DownloadStatus name
    val localFilePath: String?,
    val mediaStoreUri: String? = null,
    val createdAt: Long,
    val errorMessage: String?,
    val customTitle: String,
    val customArtist: String,
    val customAlbum: String
) {
    fun toTask(speedBps: Long = 0L): DownloadTask {
        return DownloadTask(
            id = id,
            videoId = videoId,
            title = title,
            channel = channel,
            thumbnailUrl = thumbnailUrl,
            durationSeconds = durationSeconds,
            type = runCatching { DownloadType.valueOf(type) }.getOrDefault(DownloadType.VIDEO),
            resolution = resolution?.let { runCatching { VideoResolution.valueOf(it) }.getOrNull() },
            frameRate = frameRate?.let { runCatching { FrameRate.valueOf(it) }.getOrNull() },
            videoCodec = videoCodec?.let { runCatching { VideoCodec.valueOf(it) }.getOrNull() },
            audioFormat = runCatching { AudioFormat.valueOf(audioFormat) }.getOrDefault(AudioFormat.MP3),
            audioBitrate = runCatching { AudioBitrate.valueOf(audioBitrate) }.getOrDefault(AudioBitrate.KBPS_320),
            storageLocation = runCatching { StorageLocation.valueOf(storageLocation) }.getOrDefault(StorageLocation.DCIM_CAMERA),
            totalBytes = totalBytes,
            downloadedBytes = downloadedBytes,
            progress = progress,
            speedBps = speedBps,
            status = runCatching { DownloadStatus.valueOf(status) }.getOrDefault(DownloadStatus.QUEUED),
            localFilePath = localFilePath,
            mediaStoreUri = mediaStoreUri,
            createdAt = createdAt,
            errorMessage = errorMessage,
            customTitle = customTitle,
            customArtist = customArtist,
            customAlbum = customAlbum
        )
    }

    companion object {
        fun fromTask(task: DownloadTask): DownloadTaskEntity {
            return DownloadTaskEntity(
                id = task.id,
                videoId = task.videoId,
                title = task.title,
                channel = task.channel,
                thumbnailUrl = task.thumbnailUrl,
                durationSeconds = task.durationSeconds,
                type = task.type.name,
                resolution = task.resolution?.name,
                frameRate = task.frameRate?.name,
                videoCodec = task.videoCodec?.name,
                audioFormat = task.audioFormat.name,
                audioBitrate = task.audioBitrate.name,
                storageLocation = task.storageLocation.name,
                totalBytes = task.totalBytes,
                downloadedBytes = task.downloadedBytes,
                progress = task.progress,
                status = task.status.name,
                localFilePath = task.localFilePath,
                mediaStoreUri = task.mediaStoreUri,
                createdAt = task.createdAt,
                errorMessage = task.errorMessage,
                customTitle = task.customTitle,
                customArtist = task.customArtist,
                customAlbum = task.customAlbum
            )
        }
    }
}
