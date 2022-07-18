package com.example.androidscript.floatingwidget

import android.content.Intent
import android.graphics.Bitmap
import com.example.androidscript.R
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.annotation.SuppressLint
import android.app.*
import android.content.res.Resources
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.graphics.PixelFormat
import android.media.ImageReader
import com.example.androidscript.activities.Menu
import com.example.androidscript.util.*
import kotlin.math.max
import kotlin.math.min

class ScreenShotService : Service() {
    private fun createNotificationChannel() {
        val navigate = PendingIntent.getActivity(
            this,
            0,
            Intent(this, Menu::class.java).putExtra("Message", "Reset"),
            0
        )
        val builder = Notification.Builder(applicationContext, "com.example.androidscript")
        builder.setContentIntent(navigate)
            .setContentTitle("AndroidScript")
            .setContentText("Capturing Screen")
            .setSmallIcon(R.drawable.ic_launcher_foreground) //Necessary
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id")
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                "notification_id",
                "notification_name",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        startForeground(13, builder.build())
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("WrongConstant")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        createNotificationChannel()
        mediaProjection =
            mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, Permission)
        ServiceStart = true
        virtualDisplay = mediaProjection.createVirtualDisplay(
            "Screenshot",
            width,
            height,
            Resources.getSystem().displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.surface, null, null
        )
        DebugMessage.set(
            """
    Start Screen Casting on ($height,$width) device
    
    """.trimIndent()
        )
        return 0
    }

    companion object {
        private lateinit var imageReader: ImageReader
        private lateinit var Permission: Intent
        private lateinit var virtualDisplay: VirtualDisplay
        private lateinit var mediaProjection: MediaProjection
        private lateinit var mediaProjectionManager: MediaProjectionManager
        var height = 0
            private set
        var width = 0
            private set
        var ServiceStart = false
        private var transposed = false
        fun setUpScreenDimension(_height: Int, _width: Int) {
            width = _width
            height = _height
        }

        fun endProjection() {
            mediaProjection.stop()
            virtualDisplay.release()
            ServiceStart = false
        }

        fun setShotOrientation(transpose: Boolean) {
            transposed = transpose
            if (transpose) {
                width = max(height, width)
                height = min(height, width)
            } else {
                width = min(height, width)
                height = max(height, width)
            }
        }

        @SuppressLint("WrongConstant")
        fun setUpMediaProjectionManager(intent: Intent, mpm: MediaProjectionManager) {
            mediaProjectionManager = mpm
            Permission = intent
            imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 3)
        }

        fun shot(): Bitmap? {
            if (!ServiceStart) {
                DebugMessage.set("Service unavailable.\n")
                return null
            }
            val img = imageReader.acquireLatestImage()
            if (img == null) {
                DebugMessage.set("No Img in Screenshot")
                return null
            }
            DebugMessage.set("IMAGE " + img.width + " " + img.height)
            val width: Int
            val height: Int
            if (transposed) {
                width = max(img.height, img.width)
                height = min(img.height, img.width)
            } else {
                width = min(img.height, img.width)
                height = max(img.height, img.width)
            }
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
    }
}