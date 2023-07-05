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
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.TextView
import com.tranquilrock.androidscript.App.Companion.BLOCK_DATA_KEY
import com.tranquilrock.androidscript.App.Companion.BLOCK_META_KEY
import com.tranquilrock.androidscript.App.Companion.MEDIA_PROJECTION_KEY
import com.tranquilrock.androidscript.App.Companion.ORIENTATION_KEY
import com.tranquilrock.androidscript.App.Companion.SCRIPT_TYPE_KEY
import com.tranquilrock.androidscript.R
import com.tranquilrock.androidscript.activity.Menu
import com.tranquilrock.androidscript.core.Interpreter
import com.tranquilrock.androidscript.feature.ProjectionReader
import com.tranquilrock.androidscript.service.ClickService.Companion.clicker
import com.tranquilrock.androidscript.utils.ResourceReader
import com.tranquilrock.androidscript.utils.ImageParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs


class WidgetService : Service(), ProjectionReader {

    private lateinit var widgetView: View
    private lateinit var collapsedView: View
    private lateinit var expandedView: View

    private lateinit var windowManager: WindowManager
    private lateinit var layoutInflater: LayoutInflater

    private lateinit var interpreter: Interpreter
    private lateinit var statusBulletin: Bulletin

    override var isLandscape: Boolean = false
    private lateinit var scriptType: String

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + Job())

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
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
    ).also {
        it.gravity = Gravity.TOP or Gravity.START
        it.x = 0
        it.y = 0
    }

    // =========================================================================
    @SuppressLint("InflateParams")
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        layoutInflater = getSystemService(LayoutInflater::class.java)
        widgetView = layoutInflater.inflate(R.layout.floating_widget, null)
        collapsedView = widgetView.findViewById(R.id.collapse_view)
        expandedView = widgetView.findViewById(R.id.expanded_container)
        statusBulletin = Bulletin(widgetView.findViewById(R.id.stateToast))

        windowManager = getSystemService(WindowManager::class.java)
        windowManager.addView(widgetView, layoutParams)


        widgetView.run {
            findViewById<View>(R.id.open_activity_button).setOnClickListener {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            findViewById<View>(R.id.run_script).setOnClickListener {
                if (job == null || job!!.isCompleted) {
                    resetPosition()
                    job = scope.launch {
                        withContext(Dispatchers.IO) {
                            interpreter.run()
                            withContext(Dispatchers.Main) {
                                statusBulletin.announce("IDLE")
                                expandView()
                            }
                        }
                    }
                    collapseView()
                }

            }
            findViewById<View>(R.id.stop_script).setOnClickListener {
                job?.run {
                    statusBulletin.announce("Stopping...")
                    cancel()
                    statusBulletin.announce("IDLE")
                }
                job = null
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
    @Suppress("DEPRECATION")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")

        createNotificationChannel()

        isLandscape = intent.getBooleanExtra(ORIENTATION_KEY, false)
        scriptType = intent.getStringExtra(SCRIPT_TYPE_KEY)!!
        val projectionIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(MEDIA_PROJECTION_KEY, Intent::class.java)!!
        } else {
            intent.getParcelableExtra(MEDIA_PROJECTION_KEY)!!
        }
        Handler(Looper.getMainLooper()).post {
            mediaProjection =
                getSystemService(MediaProjectionManager::class.java).getMediaProjection(
                    RESULT_OK, projectionIntent
                )
            setupProjection()
        }

        // Uncheck for serialization
        @Suppress("UNCHECKED_CAST") val blockData: ArrayList<ArrayList<String>> =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra(
                    BLOCK_DATA_KEY, ArrayList<ArrayList<String>>()::class.java
                )!!
            } else ({
                intent.getSerializableExtra(BLOCK_DATA_KEY)!!
            }) as ArrayList<ArrayList<String>>

        // Uncheck for serialization
        @Suppress("UNCHECKED_CAST") val blockMeta: Array<Array<Any>> =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra(BLOCK_META_KEY, Array<Array<Any>>::class.java)!!
            } else ({
                intent.getSerializableExtra(BLOCK_META_KEY)!!
            }) as Array<Array<Any>>
        val imageParser = ImageParser(this)
        val resourceReader = ResourceReader(applicationContext, scriptType)
        interpreter = Interpreter(
            blockData, blockMeta, resourceReader, clicker, imageParser, statusBulletin
        )

        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        job?.cancel()
        job = null
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
            this, 0, Intent(this, Menu::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        val notificationBuilder =
            Notification.Builder(applicationContext, NOTIFICATION_CHANNEL_ID).apply {
                setContentIntent(navigateIntent)
                setContentTitle(NOTIFICATION_CONTENT_TITLE)
                setContentText(NOTIFICATION_CONTENT_TEXT)
                setSmallIcon(R.drawable.ic_launcher_foreground)
            }
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            WidgetService::class.java.simpleName,
            NotificationManager.IMPORTANCE_HIGH
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            notificationChannel
        )
        startForeground(NOTIFICATION_CHANNEL_ID.toInt(), notificationBuilder.build())
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
                        yCordDestination = min(
                            max(yCordDestination, 0),
                            screenHeight// - (widgetView.height + statusBarHeight)
                        )

                        xCordDestination = xInitMargin + xDiff
                        xCordDestination = min(
                            max(xCordDestination, 0), screenWidth// - widgetView.width
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
                        yCordDestination = min(
                            max(yCordDestination, 0),
                            screenHeight// - (widgetView.height + statusBarHeight)
                        )
                        xCordDestination = min(
                            max(xCordDestination, 0), screenWidth// - widgetView.width
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
        private const val NOTIFICATION_CHANNEL_ID = "8763"
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