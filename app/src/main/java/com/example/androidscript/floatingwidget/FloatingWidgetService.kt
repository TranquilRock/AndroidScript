package com.example.androidscript.floatingwidget

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.*
import android.util.DisplayMetrics
import android.view.*
import android.view.View.OnTouchListener
import android.widget.TextView
import com.example.androidscript.R
import com.example.androidscript.activities.Menu
import kotlin.collections.ArrayList
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
    private lateinit var screenShotBinder: ScreenShotService.ScreenShotBinder

    private val physicalWidth: Int
        get() = mWindowManager
            .run {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) DisplayMetrics().also {
                    defaultDisplay.getRealMetrics(it)
                }.widthPixels
                else currentWindowMetrics.bounds.width()
            }

    private val physicalHeight: Int
        get() = mWindowManager
            .run {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) DisplayMetrics().also {
                    defaultDisplay.getRealMetrics(it)
                }.heightPixels
                else currentWindowMetrics.bounds.height()
            }

    // =========================================================================

    // TODO bind screenshot service


    override fun onCreate() {
        super.onCreate()
        //TODO create serviceConnection
        bindService(
            Intent(this, ScreenShotService::class.java),
            null,
            BIND_AUTO_CREATE
        )

        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        updateScreenBound()

        inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mWidget = inflater.inflate(R.layout.floating_widget_layout, null)

        //Add the view to the window.
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
                interpreter.runCode(args, screenShotBinder = screenShotBinder)
                resetPosition()
                collapseView()
            }
            findViewById<View>(R.id.stop_script).setOnClickListener {
                statusBulletin.announce("Stopping...")
                interpreter.runningFlag = false
                interpreter.join()
            }
            findViewById<View>(R.id.root_container).setOnTouchListener(
                floatingWidgetViewTouchListener
            )
            findViewById<View>(R.id.floating_widget_image_view).setOnTouchListener(
                floatingWidgetViewTouchListener
            )
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
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

    /*  return status bar height on basis of device display metrics  */
    private val statusBarHeight: Int
        get() = ceil((25 * applicationContext.resources.displayMetrics.density).toDouble())
            .toInt()

    override fun onBind(intent: Intent): IBinder? {
        return null
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

    override fun onDestroy() {
        super.onDestroy()
        mWindowManager.removeView(mWidget)
        interpreter.runningFlag = false
        interpreter.join()
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