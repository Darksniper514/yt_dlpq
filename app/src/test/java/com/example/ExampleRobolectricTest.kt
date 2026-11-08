package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AudioBitrate
import com.example.data.model.AudioFormat
import com.example.data.model.FrameRate
import com.example.data.model.VideoResolution
import com.example.data.repository.YouTubeRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("TubeGrab", appName)
    }

    @Test
    fun `extract video ID from various YouTube URL formats`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = YouTubeRepository(context, com.example.data.local.TubeGrabDatabase.getDatabase(context).downloadDao(), kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Unconfined))

        val urls = listOf(
            "https://www.youtube.com/watch?v=dQw4w9WgXcQ" to "dQw4w9WgXcQ",
            "https://youtu.be/dQw4w9WgXcQ" to "dQw4w9WgXcQ",
            "https://youtube.com/shorts/dQw4w9WgXcQ" to "dQw4w9WgXcQ",
            "dQw4w9WgXcQ" to "dQw4w9WgXcQ"
        )

        for ((url, expectedId) in urls) {
            val extracted = repo.extractVideoId(url)
            assertEquals("Failed for $url", expectedId, extracted)
        }
    }

    @Test
    fun `verify video resolution and fps size calculation`() {
        val duration = 120L // 2 minutes
        val size4k60 = VideoResolution.RES_4K.estimatedSizeBytes(duration, FrameRate.FPS_60)
        val size720p30 = VideoResolution.RES_720P.estimatedSizeBytes(duration, FrameRate.FPS_30)

        assertTrue("4K 60fps must be larger than 720p 30fps", size4k60 > size720p30)
    }

    @Test
    fun `verify audio MP3 bitrate size calculation`() {
        val duration = 180L // 3 minutes
        val size320 = AudioBitrate.KBPS_320.estimatedSizeBytes(duration)
        val size128 = AudioBitrate.KBPS_128.estimatedSizeBytes(duration)

        assertTrue("320 kbps MP3 must be larger than 128 kbps", size320 > size128)
        assertEquals("MP3 extension check", "mp3", AudioFormat.MP3.extension)
    }

    @Test
    fun `verify DCIM Camera storage location configuration`() {
        val dcim = com.example.data.model.StorageLocation.DCIM_CAMERA
        assertEquals("DCIM/Camera", dcim.relativePath)
        assertTrue("Must be marked as DCIM Camera", dcim.isDcimCamera)
    }

    @Test
    fun `launch MainActivity with Robolectric`() {
        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
        assertNotNull(controller.get())
    }
}
