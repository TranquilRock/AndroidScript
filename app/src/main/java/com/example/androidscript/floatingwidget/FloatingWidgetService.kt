package com.example.androidscript.floatingwidget

import android.annotation.SuppressLint
import android.app.*
import android.app.Activity.RESULT_OK
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.PixelFormat
import android.graphics.Point
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.DisplayMetrics
import android.view.*
import android.view.View.OnTouchListener
import android.widget.TextView
import com.example.androidscript.R
import com.example.androidscript.activities.Menu
import com.example.androidscript.util.MyLog
import kotlin.math.abs
import kotlin.math.ceil


class FloatingWidgetService : Service() {

    private lateinit var mWindowManager: WindowManager
    private lateinit var mWidget: View
    private lateinit var collapsedView: View
    private lateinit var expandedView: View
    private val szWindow = Point()
    private var width = 0
    private var height = 0
    private lateinit var inflater: LayoutInflater
    private var xInitCord = 0
    private var yInitCord = 0
    private var xInitMargin = 0
    private var yInitMargin = 0
    private lateinit var statusBulletin: Bulletin
    private var args: ArrayList<String>? = null
    private lateinit var interpreter: Interpreter
    private lateinit var mMediaProjectionManager: MediaProjectionManager
    private lateinit var mMediaProjection: MediaProjection
    private var mThread: Thread? = null


    @Suppress("DEPRECATION") // Allow lower api version.
    private val physicalWidth: Int
        get() = mWindowManager
            .run {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) DisplayMetrics().also {
                    defaultDisplay.getRealMetrics(it)
                }.widthPixels
                else currentWindowMetrics.bounds.width()
            }

    @Suppress("DEPRECATION") // Allow lower api version.
    private val physicalHeight: Int
        get() = mWindowManager
            .run {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) DisplayMetrics().also {
                    defaultDisplay.getRealMetrics(it)
                }.heightPixels
                else currentWindowMetrics.bounds.height()
            }

    // =========================================================================

    private lateinit var screenShotService: ScreenShotService
    private var screenShotConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder: ScreenShotService.ScreenShotBinder =
                service as ScreenShotService.ScreenShotBinder
            screenShotService = binder.service
            screenShotService.set(mMediaProjection)
            MyLog.set("Connected")
        }

        override fun onServiceDisconnected(className: ComponentName) {
            MyLog.set("onServiceDisconnected")
        }
    }

    /*  return status bar height on basis of device display metrics  */
    private val statusBarHeight: Int
        get() = ceil((25 * applicationContext.resources.displayMetrics.density).toDouble())
            .toInt()

    override fun onCreate() {
        super.onCreate()

        bindService(
            Intent(this, ScreenShotService::class.java),
            screenShotConnection,
            BIND_AUTO_CREATE
        )

        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mWidget = inflater.inflate(R.layout.floating_widget_layout, null)

        //Add the view to the window.
        @Suppress("DEPRECATION") // Allow lower api version.
        val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            (if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_PHONE
            else
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        //Specify the view position
        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.x = 0
        layoutParams.y = 0

        mWindowManager.addView(mWidget, layoutParams)
        collapsedView = mWidget.findViewById(R.id.collapse_view)
        expandedView = mWidget.findViewById(R.id.expanded_container)
        statusBulletin = Bulletin(mWidget.findViewById(R.id.stateToast))

        mWidget.run {
            findViewById<View>(R.id.open_activity_button)
                .setOnClickListener {
                    val intent = Intent(this@FloatingWidgetService, Menu::class.java).putExtra(
                        "Message",
                        "Reset"
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    stopSelf()
                }
            findViewById<View>(R.id.run_script).setOnClickListener {
                mThread ?: run {
                    interpreter.setup(args, screenShotService)
                    mThread = Thread(interpreter)
                    mThread!!.start()
                    resetPosition()
                    collapseView()
                }
            }
            findViewById<View>(R.id.stop_script).setOnClickListener {
                statusBulletin.announce("Stopping...")
                interpreter.runningFlag = false
                mThread?.join()
                mThread = null
            }
            findViewById<View>(R.id.root_container).setOnTouchListener(
                floatingWidgetViewTouchListener
            )
            findViewById<View>(R.id.floating_widget_image_view).setOnTouchListener(
                floatingWidgetViewTouchListener
            )
        }

        mMediaProjectionManager = getSystemService(MediaProjectionManager::class.java)
        updateScreenBound()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        createNotificationChannel()

        Handler(Looper.getMainLooper()).post {
            mMediaProjection =
                mMediaProjectionManager.getMediaProjection(
                    RESULT_OK,
                    intent.getParcelableExtra("MPM")!!
                )
        }

        val scriptFolderName = intent.getStringExtra(folderTAG)!!
        val scriptName = intent.getStringExtra(scriptTAG)!!
        args = intent.getStringArrayListExtra(argsTAG)

        interpreter = Interpreter(scriptFolderName, scriptName, statusBulletin)
        return super.onStartCommand(intent, flags, startId)
    }

    /*  Implement Touch Listener to Floating Widget Root View  */
    private val floatingWidgetViewTouchListener: OnTouchListener
        get() {
            val value = object : OnTouchListener {
                var time_start: Long = 0

                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    val layoutParams = mWidget.layoutParams as WindowManager.LayoutParams
                    val xCord = event.rawX.toInt()
                    val yCord = event.rawY.toInt()
                    val xCordDestination: Int
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
                            //The check for x_diff <5 && y_diff< 5 because sometime elements moves a little while clicking.
                            //So that is click event.
                            if (abs(xDiff) < 5 && abs(yDiff) < 5) {
                                if (System.currentTimeMillis() - time_start < 800) {
                                    switchView()
                                }
                            }
                            yCordDestination = yInitMargin + yDiff
                            val barHeight = statusBarHeight
                            if (yCordDestination < 0) {
                                yCordDestination = 0
                            } else if (yCordDestination + (mWidget.height + barHeight) > szWindow.y) {
                                yCordDestination =
                                    szWindow.y - (mWidget.height + barHeight)
                            }
                            layoutParams.y = yCordDestination
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val xDiffMove = xCord - xInitCord
                            val yDiffMove = yCord - yInitCord
                            xCordDestination = xInitMargin + xDiffMove
                            yCordDestination = yInitMargin + yDiffMove
                            layoutParams.x = xCordDestination
                            layoutParams.y = yCordDestination
                            mWindowManager.updateViewLayout(mWidget, layoutParams)
                            return true
                        }
                    }
                    return false
                }
            }
            return value
        }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(screenShotConnection)
        mWindowManager.removeView(mWidget)
        interpreter.runningFlag = false
        mThread?.join()
        mThread = null
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    // ============== helper =====================

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

    private fun updateScreenBound() {
        height = physicalHeight
        width = physicalWidth
    }

    /*  Reset position of Floating Widget view on dragging  */
    private fun resetPosition() {
        updateScreenBound()
        val pos = mWidget.layoutParams as WindowManager.LayoutParams
        pos.x = 0
        pos.y = 0
        mWindowManager.updateViewLayout(mWidget, pos)
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
        if (mWidget.findViewById<View>(R.id.collapse_view).visibility == View.VISIBLE) {
            expandView()
        } else {
            collapseView()
        }
    }


    class Bulletin internal constructor(private var board: TextView) {
        fun announce(announcement: String) {
            Handler(Looper.getMainLooper()).post { board.text = announcement }
        }
    }

    companion object {
        var folderTAG: String = "ScriptFolderName"
        var scriptTAG: String = "ScriptName"
        var argsTAG: String = "Args"
    }
}