package com.example.androidscript.util;

import android.graphics.Path;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.view.accessibility.AccessibilityEvent;
public final class AutoClick extends AccessibilityService{
    private static AutoClick instance;
    public static void Click(int x, int y) {
        Path path = new Path();
        path.moveTo(x - 1, y - 1);
        path.lineTo(x + 1, y + 1);
        instance.dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription
                (path, 0, 100)).build(), null, null);
    }

    public static void Swipe(int x1, int y1, int x2, int y2) {
        Path path = new Path();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        instance.dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription
                (path, 0, 500)).build(), null, null);
    }


    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        DebugMessage.set("AutoClick::onServiceConnected\n");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        DebugMessage.set("AutoClick::onAccessibilityEvent\n");
    }

    @Override
    public void onInterrupt() {
        DebugMessage.set("AutoClick::onInterrupt\n");
    }
}