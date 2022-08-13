package com.example.androidscript.floatingwidget

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class AutoClickService : AccessibilityService() {

    public override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.i(LOG_TAG, "onServiceConnected\n")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.i(LOG_TAG, "onAccessibilityEvent\n")
    }

    override fun onInterrupt() {
        instance = null
        Log.i(LOG_TAG, "onInterrupt\n")
    }

    companion object {
        private val LOG_TAG = AutoClickService::class.java.simpleName
        private var instance: AutoClickService? = null

        fun running(): Boolean {
            return (instance != null)
        }

        fun click(x: Int, y: Int) {
            val path = Path()
            path.moveTo((x - 1).toFloat(), (y - 1).toFloat())
            path.lineTo((x + 1).toFloat(), (y + 1).toFloat())
            instance?.dispatchGesture(
                GestureDescription.Builder().addStroke(StrokeDescription(path, 0, 100)).build(),
                null,
                null
            ) ?: throw AccessibilityServiceOffException()
        }

        fun swipe(x1: Int, y1: Int, x2: Int, y2: Int) {
            val path = Path()
            path.moveTo(x1.toFloat(), y1.toFloat())
            path.lineTo(x2.toFloat(), y2.toFloat())
            instance?.dispatchGesture(
                GestureDescription.Builder().addStroke(StrokeDescription(path, 0, 1000)).build(),
                null,
                null
            ) ?: throw AccessibilityServiceOffException()
        }
    }

    class AccessibilityServiceOffException : Exception()
}