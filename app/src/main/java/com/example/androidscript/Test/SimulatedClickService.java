package com.example.androidscript.Test;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;

public class SimulatedClickService extends AccessibilityService {

    public static SimulatedClickService mService;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mService = this;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {
        mService = null;
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
    

}