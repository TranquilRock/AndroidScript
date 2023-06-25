package com.tranquilrock.androidscript.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClickService : AccessibilityService() {

    public override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d(LOG_TAG, "onServiceConnected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d(LOG_TAG, "onAccessibilityEvent")
    }

    override fun onInterrupt() {
        instance = null
        Log.d(LOG_TAG, "onInterrupt")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LOG_TAG, "onDestroy")
    }

    companion object {
        private val LOG_TAG = ClickService::class.java.simpleName
        private var instance: ClickService? = null

        fun running(): Boolean {
            return (instance != null)
        }

        suspend fun click(x: Int, y: Int) {
            val path = Path()
            path.moveTo((x - 1).toFloat(), (y - 1).toFloat())
            path.lineTo((x + 1).toFloat(), (y + 1).toFloat())
            instance?.run {
                withContext(Dispatchers.Default) {
                    dispatchGesture(
                        GestureDescription.Builder().addStroke(StrokeDescription(path, 0, 100))
                            .build(),
                        null,
                        null
                    )
                }
            } ?: throw OffException()
        }

        suspend fun swipe(x1: Int, y1: Int, x2: Int, y2: Int) {
            val path = Path()
            path.moveTo(x1.toFloat(), y1.toFloat())
            path.lineTo(x2.toFloat(), y2.toFloat())
            instance?.run {
                withContext(Dispatchers.Default) {
                    dispatchGesture(
                        GestureDescription.Builder().addStroke(StrokeDescription(path, 0, 1000))
                            .build(),
                        null,
                        null
                    )
                }
            } ?: throw OffException()

        }
    }

    class OffException : Exception()
}