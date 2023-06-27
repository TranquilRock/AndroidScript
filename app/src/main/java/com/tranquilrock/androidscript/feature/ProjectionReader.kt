package com.tranquilrock.androidscript.feature


import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.util.Log

interface ProjectionReader {

    var mediaProjection: MediaProjection

    var imageReader: ImageReader
    var virtualDisplay: VirtualDisplay

    val screenWidth: Int
    val screenHeight: Int

    @SuppressLint("WrongConstant")
    fun setupProjection() {
        imageReader = ImageReader.newInstance(
            screenWidth, screenHeight, PixelFormat.RGBA_8888, NUM_MAX_IMAGES
        )
        virtualDisplay = mediaProjection.createVirtualDisplay(
            VIRTUAL_DISPLAY_NAME,
            screenWidth,
            screenHeight,
            Resources.getSystem().displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.surface,
            null,
            null
        )
        Log.d(TAG, "Start Screen Casting on ($screenHeight,$screenWidth) device")
    }

    suspend fun screenShot(): Image? {
        return imageReader.acquireLatestImage() ?: return null
    }


    companion object {
        private val TAG = ProjectionReader::class.java.simpleName
        private const val VIRTUAL_DISPLAY_NAME = "AndroidScriptProjection"
        private const val NUM_MAX_IMAGES = 3
    }
}