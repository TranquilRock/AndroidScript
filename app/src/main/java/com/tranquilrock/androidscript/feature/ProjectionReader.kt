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
import java.lang.Integer.max
import java.lang.Integer.min

interface ProjectionReader {


    var isLandscape: Boolean
    var mediaProjection: MediaProjection

    var imageReader: ImageReader
    var virtualDisplay: VirtualDisplay

    val screenWidth: Int
    val screenHeight: Int

    @SuppressLint("WrongConstant")
    fun setupProjection() {
        val w = if (isLandscape) max(screenHeight, screenWidth) else min(screenHeight, screenWidth)
        val h = if (isLandscape) min(screenHeight, screenWidth) else max(screenHeight, screenWidth)
        imageReader = ImageReader.newInstance(
            w, h, PixelFormat.RGBA_8888, NUM_MAX_IMAGES,
        )
        virtualDisplay = mediaProjection.createVirtualDisplay(
            VIRTUAL_DISPLAY_NAME,
            w,
            h,
            Resources.getSystem().displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.surface,
            null,
            null
        )

        Log.d(TAG, "Start Screen Casting on ($w, $h) device")
    }

    suspend fun screenShot(): Image? = imageReader.acquireLatestImage()


    companion object {
        private val TAG = ProjectionReader::class.java.simpleName
        private const val VIRTUAL_DISPLAY_NAME = "AndroidScriptProjection"
        private const val NUM_MAX_IMAGES = 3
    }
}