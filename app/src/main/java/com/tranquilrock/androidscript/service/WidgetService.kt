package com.tranquilrock.androidscript.service

import android.annotation.SuppressLint
import android.app.*
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.TextView
import com.tranquilrock.androidscript.R
import com.tranquilrock.androidscript.activity.Menu
import com.tranquilrock.androidscript.utils.ProjectionReader
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs


class WidgetService : Service(), ProjectionReader {

    private lateinit var widgetView: View
    private lateinit var collapsedView: View
    private lateinit var expandedView: View

    private lateinit var windowManager: WindowManager
    private lateinit var layoutInflater: LayoutInflater

    private lateinit var statusBulletin: Bulletin

    private var xInitMargin = 0
    private var yInitMargin = 0

    override lateinit var imageReader: ImageReader
    override lateinit var virtualDisplay: VirtualDisplay
    override lateinit var mediaProjection: MediaProjection
    override val screenWidth: Int
        get() = windowManager.currentWindowMetrics.bounds.width()
    override val screenHeight: Int
        get() = windowManager.currentWindowMetrics.bounds.height()

    private val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
    ).also {
        it.gravity = Gravity.TOP or Gravity.START
        it.x = 0
        it.y = 0
    }

    // =========================================================================
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        layoutInflater = getSystemService(LayoutInflater::class.java)
        @SuppressLint("InflateParams")
        widgetView = layoutInflater.inflate(R.layout.floating_widget_layout, null)
        collapsedView = widgetView.findViewById(R.id.collapse_view)
        expandedView = widgetView.findViewById(R.id.expanded_container)
        statusBulletin = Bulletin(widgetView.findViewById(R.id.stateToast))

        windowManager = getSystemService(WindowManager::class.java)
        windowManager.addView(widgetView, layoutParams)


        widgetView.run {
            findViewById<View>(R.id.open_activity_button)
                .setOnClickListener {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            findViewById<View>(R.id.run_script).setOnClickListener {
                resetPosition()
                collapseView()
                // TODO start script
            }
            findViewById<View>(R.id.stop_script).setOnClickListener {
                statusBulletin.announce("Stopping...")
                // TODO stop the script
                statusBulletin.announce("IDLE")
            }
            findViewById<View>(R.id.root_container).setOnTouchListener(
                floatingWidgetViewTouchListener
            )
            findViewById<View>(R.id.floating_widget_image_view).setOnTouchListener(
                floatingWidgetViewTouchListener
            )
        }
    }

    /**
     * Acquires media projection on service start.
     * */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")

        createNotificationChannel()
        Handler(Looper.getMainLooper()).post {
            @Suppress("DEPRECATION")
            mediaProjection =
                getSystemService(MediaProjectionManager::class.java).getMediaProjection(
                    RESULT_OK,
                    intent.getParcelableExtra(MEDIA_PROJECTION_KEY)!!
                )
            setupProjection()
        }

        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        if (::windowManager.isInitialized) windowManager.removeView(widgetView)
        if (::mediaProjection.isInitialized) mediaProjection.stop()
        if (::virtualDisplay.isInitialized) virtualDisplay.release()
        super.onDestroy()
    }

    /**
     * Create a notification to become a foreground service.
     * */
    private fun createNotificationChannel() {
        val navigateIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, Menu::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val notificationBuilder = Notification.Builder(applicationContext, CHANNEL_ID).apply {
            setContentIntent(navigateIntent)
            setContentTitle(NOTIFICATION_CONTENT_TITLE)
            setContentText(NOTIFICATION_CONTENT_TEXT)
            setSmallIcon(R.drawable.ic_launcher_foreground)
        }
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            WidgetService::class.java.simpleName,
            NotificationManager.IMPORTANCE_HIGH
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            notificationChannel
        )
        startForeground(CHANNEL_ID.toInt(), notificationBuilder.build())
    }

    /**
     * Resets position of Widget view on script start.
     * */
    private fun resetPosition() {
        layoutParams.run {
            x = 0; y = 0
            windowManager.updateViewLayout(widgetView, this)
        }
    }

    private fun expandView() {
        expandedView.visibility = View.VISIBLE
        collapsedView.visibility = View.GONE
    }

    private fun collapseView() {
        collapsedView.visibility = View.VISIBLE
        expandedView.visibility = View.GONE
    }

    /**
     * Triggered on Floating widget click or script starts/ends.
     * */
    private fun switchView() {
        if (collapsedView.visibility == View.VISIBLE) {
            expandView()
        } else {
            collapseView()
        }
    }

    /**
     * Handles widget's movement.
     * A minor known bug is that on switching views the originally hidden one will flash.
     * */
    private val floatingWidgetViewTouchListener: OnTouchListener
        get() = object : OnTouchListener {
            var xInitCord = 0
            var yInitCord = 0
            var time_start: Long = 0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val layoutParams = widgetView.layoutParams as WindowManager.LayoutParams
                val xCord = event.rawX.toInt()
                val yCord = event.rawY.toInt()
                var xCordDestination: Int
                var yCordDestination: Int
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        time_start = System.currentTimeMillis()
                        xInitCord = xCord
                        yInitCord = yCord
                        xInitMargin = layoutParams.x
                        yInitMargin = layoutParams.y
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        val xDiff = xCord - xInitCord
                        val yDiff = yCord - yInitCord
                        // Checks for x_diff < 5 && y_diff < 5
                        // because sometime elements moves a little while clicking.
                        // So that should be a click event.
                        if (abs(xDiff) < 5 && abs(yDiff) < 5) {
                            if (System.currentTimeMillis() - time_start < 800) {
                                switchView()
                                return true
                            }
                        }
                        yCordDestination = yInitMargin + yDiff
                        yCordDestination =
                            min(
                                max(yCordDestination, 0),
                                screenHeight// - (widgetView.height + statusBarHeight)
                            )

                        xCordDestination = xInitMargin + xDiff
                        xCordDestination =
                            min(
                                max(xCordDestination, 0),
                                screenWidth// - widgetView.width
                            )

                        layoutParams.y = yCordDestination
                        layoutParams.x = xCordDestination
                        windowManager.updateViewLayout(widgetView, layoutParams)
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val xDiffMove = xCord - xInitCord
                        val yDiffMove = yCord - yInitCord
                        xCordDestination = xInitMargin + xDiffMove
                        yCordDestination = yInitMargin + yDiffMove
                        yCordDestination =
                            min(
                                max(yCordDestination, 0),
                                screenHeight// - (widgetView.height + statusBarHeight)
                            )
                        xCordDestination =
                            min(
                                max(xCordDestination, 0),
                                screenWidth// - widgetView.width
                            )

                        layoutParams.x = xCordDestination
                        layoutParams.y = yCordDestination
                        windowManager.updateViewLayout(widgetView, layoutParams)
                        return true
                    }
                }
                v.performClick()
                return false
            }
        }

    /**
     * Encapsulates widget text view update.
     *
     * This class is used for the script to show messages.
     *
     * @property board the TextView to put messages on.
     * */
    class Bulletin internal constructor(private var board: TextView) {
        /**
         * Display a short message [announcement] to users.
         */
        fun announce(announcement: String) {
            Handler(Looper.getMainLooper()).post { board.text = announcement }
        }
    }

    companion object {
        private val TAG = WidgetService::class.java.simpleName
        const val MEDIA_PROJECTION_KEY = "MEDIA_PROJECTION"
        private const val CHANNEL_ID = "8763"
        private const val NOTIFICATION_CONTENT_TITLE = "AndroidScript"
        private const val NOTIFICATION_CONTENT_TEXT = "Widget Running :)"
    }

    /**
     * By design, this widget service is unbounded.
     * */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}