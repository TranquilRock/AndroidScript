package com.example.androidscript.util;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;

public class AutoClick extends AccessibilityService {

    public static AutoClick mService;
    static{
        AutoClick.mService = new AutoClick();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        System.out.println("onServiceConnected\n");
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

    public static boolean isStart() {
        return mService != null;
    }

    @RequiresApi(24)
    public void Click(int x, int y) {
        Path path = new Path();
        path.moveTo(x - 1, y - 1);
        path.lineTo(x + 1, y + 1);
        dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription
                (path, 0, 100)).build(), null, null);
    }

    @RequiresApi(24)
    public void Swipe(int x1, int y1,int x2,int y2) {
        Path path = new Path();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription
                (path, 0, 100)).build(), null, null);
    }
}