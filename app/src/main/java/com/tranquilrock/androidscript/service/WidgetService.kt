package com.tranquilrock.androidscript.service

import android.app.*
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.TextView
import com.tranquilrock.androidscript.R
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs
import kotlin.math.ceil


class WidgetService : Service() {


    private lateinit var widgetView: View
    private lateinit var collapsedView: View
    private lateinit var expandedView: View

    private lateinit var windowManager: WindowManager
    private lateinit var layoutInflater: LayoutInflater

    private lateinit var statusBulletin: Bulletin
    private lateinit var mediaProjection: MediaProjection

    private val windowSize = Point()

    private var xInitMargin = 0
    private var yInitMargin = 0

    private val physicalWidth: Int
        get() = windowManager.currentWindowMetrics.bounds.width()

    private val physicalHeight: Int
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

    /*  return status bar height on basis of device display metrics  */
    private val statusBarHeight: Int
        get() = ceil((25 * applicationContext.resources.displayMetrics.density).toDouble())
            .toInt()


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        if (intent.action == NOTIFICATION_STOP_WIDGET) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        createNotificationChannel()

        Handler(Looper.getMainLooper()).post {
            mediaProjection =
                getSystemService(MediaProjectionManager::class.java).getMediaProjection(
                    RESULT_OK,
                    intent.getParcelableExtra(MEDIA_PROJECTION_KEY)!!
                )
        }

        return super.onStartCommand(intent, flags, startId)
    }

    /*  Implement Touch Listener to Floating Widget Root View  */

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        windowManager.removeView(widgetView)
    }

    // ============== helper =====================

    private fun createNotificationChannel() {
        val navigate = PendingIntent.getService(
            this,
            0,
            Intent(this, WidgetService::class.java).setAction(NOTIFICATION_STOP_WIDGET),
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = Notification.Builder(applicationContext).apply {
            setContentIntent(navigate)
            setContentTitle(NOTIFICATION_CONTENT_TITLE)
            setContentText(NOTIFICATION_CONTENT_TEXT)
            setSmallIcon(R.drawable.ic_launcher_foreground)
        }
        builder.setChannelId(packageName)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            packageName,
            WidgetService::class.java.simpleName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
        startForeground(42, builder.build())
    }

    /*  Reset position of Floating Widget view on dragging  */
    private fun resetPosition() {
        layoutParams.run {
            x = 0; y = 0;
            windowManager.updateViewLayout(widgetView, this)
        }
    }

    /*  on Floating widget click show expanded view  */
    private fun expandView() {
        collapsedView.visibility = View.GONE //Invisible
        expandedView.visibility = View.VISIBLE
    }

    private fun collapseView() {
        collapsedView.visibility = View.VISIBLE
        expandedView.visibility = View.GONE
    }

    private fun switchView() {
        if (collapsedView.visibility == View.VISIBLE) {
            expandView()
        } else {
            collapseView()
        }
    }

    // TODO not checked yet.
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
                            }
                        }
                        yCordDestination = yInitMargin + yDiff
                        yCordDestination =
                            min(
                                max(yCordDestination, 0),
                                physicalHeight// - (widgetView.height + statusBarHeight)
                            )

                        xCordDestination = xInitMargin + xDiff
                        xCordDestination =
                            min(
                                max(xCordDestination, 0),
                                physicalWidth// - widgetView.width
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
                                physicalHeight// - (widgetView.height + statusBarHeight)
                            )
                        xCordDestination =
                            min(
                                max(xCordDestination, 0),
                                physicalWidth// - widgetView.width
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


    class Bulletin internal constructor(private var board: TextView) {
        fun announce(announcement: String) {
            Handler(Looper.getMainLooper()).post { board.text = announcement }
        }
    }

    companion object {
        private val TAG = WidgetService::class.java.simpleName
        const val MEDIA_PROJECTION_KEY = "MEDIA_PROJECTION"
        private const val folderTAG: String = "ScriptFolderName"
        private const val scriptTAG: String = "ScriptName"
        private const val ARGS_TAG: String = "Args"
        private const val NOTIFICATION_STOP_WIDGET: String = "STOP"
        private const val NOTIFICATION_CONTENT_TITLE = "AndroidScript"
        private const val NOTIFICATION_CONTENT_TEXT = "Widget Running :)"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


//    bindService(
//    Intent(this, ProjectionService::class.java),
//    screenShotConnection,
//    BIND_AUTO_CREATE
//    ) // This will then trigger `screenShotConnect`'s methods.
    //    private lateinit var projectionService: ProjectionService
//    private val screenShotConnection = object : ServiceConnection {
//        override fun onServiceConnected(className: ComponentName, service: IBinder) {
//            val binder: ProjectionService.ScreenShotBinder =
//                service as ProjectionService.ScreenShotBinder
//            projectionService = binder.service
//            projectionService.set(mediaProjection)
//            Log.i(LOG_TAG, "ScreenShot Service connected.")
//        }
//
//        override fun onServiceDisconnected(className: ComponentName) {
//            Log.i(LOG_TAG, "ScreenShot Service disconnected!")
//            this@WidgetService.stopForeground(STOP_FOREGROUND_REMOVE)
//            this@WidgetService.stopSelf()
//        }
//    }
}