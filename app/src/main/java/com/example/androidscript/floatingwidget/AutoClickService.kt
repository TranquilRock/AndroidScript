package com.example.androidscript.floatingwidget

import com.example.androidscript.util.DebugMessage
import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.content.Intent
import android.graphics.Path

class AutoClickService : AccessibilityService() {

    public override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        DebugMessage.set("AutoClick::onServiceConnected\n")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        DebugMessage.set("AutoClick::onAccessibilityEvent\n")
    }

    override fun onInterrupt() {
        DebugMessage.set("AutoClick::onInterrupt\n")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    companion object {
        private var instance: AutoClickService? = null

        fun running(): Boolean {
            return instance != null
        }

        fun stop() {
            if (instance != null) {
                instance!!.stopSelf()
            }
        }

        fun click(x: Int, y: Int) {
            val path = Path()
            path.moveTo((x - 1).toFloat(), (y - 1).toFloat())
            path.lineTo((x + 1).toFloat(), (y + 1).toFloat())
            instance!!.dispatchGesture(
                GestureDescription.Builder().addStroke(StrokeDescription(path, 0, 100)).build(),
                null,
                null
            )
        }

        fun swipe(x1: Int, y1: Int, x2: Int, y2: Int) {
            val path = Path()
            path.moveTo(x1.toFloat(), y1.toFloat())
            path.lineTo(x2.toFloat(), y2.toFloat())
            instance!!.dispatchGesture(
                GestureDescription.Builder().addStroke(StrokeDescription(path, 0, 1000)).build(),
                null,
                null
            )
        }
    }
}