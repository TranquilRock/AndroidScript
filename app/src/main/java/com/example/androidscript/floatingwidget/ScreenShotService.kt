package com.example.androidscript.floatingwidget

import android.app.Service
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Binder
import android.os.IBinder
import com.example.androidscript.util.ImageHandler
import com.example.androidscript.util.MyLog
import kotlin.math.max
import kotlin.math.min

class ScreenShotService : Service() {

    private lateinit var serviceBinder: ScreenShotBinder
    private lateinit var imageReader: ImageReader
    private lateinit var virtualDisplay: VirtualDisplay
    private lateinit var mediaProjection: MediaProjection

    override fun onCreate() {
        super.onCreate()
        serviceBinder = ScreenShotBinder()
    }

    override fun onBind(intent: Intent): IBinder {
        return serviceBinder
    }

    override fun onDestroy() {
        if (this::virtualDisplay.isInitialized) {
            virtualDisplay.release()
        }
        if (this::mediaProjection.isInitialized) {
            mediaProjection.stop()
        }
        super.onDestroy()
    }

    fun set(mediaProjection: MediaProjection) {
        this.mediaProjection = mediaProjection
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 3)
        this.virtualDisplay = mediaProjection.createVirtualDisplay(
            "Screenshot",
            width,
            height,
            Resources.getSystem().displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            this.imageReader.surface, null, null
        )
        MyLog.set("""Start Screen Casting on ($height,$width) device""")
    }

    fun shot(): Bitmap? {
        val img = imageReader.acquireLatestImage()

        // TODO remove this
        MyLog.set("IMAGE " + img.width + " " + img.height)
        val width: Int
        val height: Int

        if (isLandscape) {
            width = max(img.height, img.width)
            height = min(img.height, img.width)
        } else {
            width = min(img.height, img.width)
            height = max(img.height, img.width)
        }
        // =======
        val plane = img.planes[0]
        val buffer = plane.buffer
        buffer.rewind()
        val pixelStride = plane.pixelStride //像素間距
        val rowStride = plane.rowStride //總間距
        val rowPadding = rowStride - pixelStride * width
        var bitmap = Bitmap.createBitmap(
            width + rowPadding / pixelStride, height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height)
        img.close()
        return bitmap
    }

    fun compare(Target: Bitmap?, x1: Int, y1: Int, x2: Int, y2: Int): Int {
        val screen = shot()
        return if (screen == null) {
            0
        } else {
            ImageHandler.matchPicture(
                Bitmap.createBitmap(
                    screen,
                    x1,
                    y1,
                    x2 - x1,
                    y2 - y1
                ), Target
            )
        }
    }

    inner class ScreenShotBinder : Binder() {
        val service: ScreenShotService
            get() = this@ScreenShotService
    }

    companion object {

        var height = 0
            private set
        var width = 0
            private set

        private var isLandscape = false

        fun setScreenDim(_height: Int, _width: Int) {
            width = _width
            height = _height
        }

        fun setShotOrientation(isLandscape: Boolean) {
            this.isLandscape = isLandscape
            if (isLandscape) {
                val tmp = max(height, width)
                height = min(height, width)
                width = tmp
            } else {
                val tmp = min(height, width)
                height = max(height, width)
                width = tmp
            }
        }
    }
}