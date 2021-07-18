package com.example.androidscript.util;

import android.graphics.Path;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;

public class AutoClick extends AccessibilityService {

    public static AutoClick mService;

    public static boolean isStart() {
        return AutoClick.mService != null;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AutoClick.mService = this;
        System.out.println("onServiceConnected\n");
    }

    @RequiresApi(24)
    public void Click(int x, int y) {
        this.Swipe(x - 1, y - 1, x + 1, y + 1);
    }

    @RequiresApi(24)
    public void Swipe(int x1, int y1, int x2, int y2) {
        Path path = new Path();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription
                (path, 0, 100)).build(), null, null);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        System.out.println("onAccessibilityEvent\n");
    }

    @Override
    public void onInterrupt() {
        System.out.println("AutoClick Interrupted\n");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mService = null;
    }
}