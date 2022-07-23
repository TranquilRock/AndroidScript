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
import android.os.Binder
import com.example.androidscript.activities.Menu
import com.example.androidscript.util.*
import kotlin.math.max
import kotlin.math.min

class ScreenShotService : Service() {

    private lateinit var serviceBinder: ScreenShotBinder
    private lateinit var imageReader: ImageReader

    private val virtualDisplay: VirtualDisplay
        get() = serviceBinder.virtualDisplay

    private val mediaProjection
        get() = serviceBinder.mediaProjection

    override fun onCreate() {
        super.onCreate()
        serviceBinder = ScreenShotBinder()
    }

    override fun onBind(intent: Intent): IBinder {
        return serviceBinder
    }

    inner class ScreenShotBinder : Binder() {
        lateinit var mediaProjection: MediaProjection
        lateinit var virtualDisplay: VirtualDisplay
        fun set(mediaProjection: MediaProjection){
            this.mediaProjection = mediaProjection
            this.virtualDisplay = mediaProjection.createVirtualDisplay(
                "Screenshot",
                width,
                height,
                Resources.getSystem().displayMetrics.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                this@ScreenShotService.imageReader.surface, null, null
            )
        }
        fun end(){
            if(this::virtualDisplay.isInitialized){
                virtualDisplay.release()
            }
            if(this::mediaProjection.isInitialized){
                mediaProjection.stop()
            }
        }
        val service: ScreenShotService
            get() = this@ScreenShotService
    }

    private fun createNotificationChannel() {
        val navigate = PendingIntent.getActivity(
            this,
            0,
            Intent(this, Menu::class.java).putExtra("Message", "Reset"),
            PendingIntent.FLAG_IMMUTABLE
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


    @SuppressLint("WrongConstant")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        createNotificationChannel()

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 3)

        DebugMessage.set("""Start Screen Casting on ($height,$width) device""")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        serviceBinder.end()
        super.onDestroy()
    }

    fun shot(): Bitmap? {
        val img = imageReader.acquireLatestImage()

        // TODO remove this
        DebugMessage.set("IMAGE " + img.width + " " + img.height)
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