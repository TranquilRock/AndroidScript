package com.tranquilrock.androidscript.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.content.Intent
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ClickService : AccessibilityService() {

    public override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "onServiceConnected")

        clicker = this
        Toast.makeText(this, "ClickService On", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")

        clicker = null

        Toast.makeText(this, "ClickService Down", Toast.LENGTH_SHORT).show()
        stopService(Intent(this, WidgetService::class.java))
        super.onDestroy()
    }

    fun click(x: Int, y: Int) {
        val path = Path().apply {
            moveTo((x - 1).toFloat(), (y - 1).toFloat())
            lineTo((x + 1).toFloat(), (y + 1).toFloat())
        }
        susDispatch(
            GestureDescription.Builder().addStroke(StrokeDescription(path, 0, 1000)).build()
        )
    }

    fun swipe(x1: Int, y1: Int, x2: Int, y2: Int) {
        val path = Path().apply {
            moveTo(x1.toFloat(), y1.toFloat())
            lineTo(x2.toFloat(), y2.toFloat())
        }
        susDispatch(
            GestureDescription.Builder().addStroke(StrokeDescription(path, 0, 1000)).build()
        )
    }

    private fun susDispatch(gestureDescription: GestureDescription) {
        clicker?.run {
            dispatchGesture(
                gestureDescription,
                object : GestureResultCallback() {
                    override fun onCompleted(gestureDescription: GestureDescription?) {
                        Log.d(TAG, "Completed: $gestureDescription")
                    }

                    override fun onCancelled(gestureDescription: GestureDescription?) {
                        Log.d(TAG, "Cancelled: $gestureDescription")
                    }
                },
                null,
            )
        } ?: Log.e(TAG, "NOT ONNNNNNN!!!!")// throw OffException()
    }

    companion object {
        private val TAG = ClickService::class.java.simpleName

        var clicker: ClickService? = null
            private set
    }

    class OffException : Exception("AccessibilityService Not On!")

    /**
     * AccessibilityEvent Callback, as all events got filtered, will not be called.
     * */
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d(TAG, "onAccessibilityEvent")
    }

    /**
     * Android Developers `Callback for interrupting the accessibility feedback.`
     * Unused in this service
     * */
    override fun onInterrupt() {
        Log.d(TAG, "onInterrupt")
    }
}