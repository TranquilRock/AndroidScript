package com.example.androidscript.util;

import android.graphics.Path;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import androidx.annotation.NonNull;

public class AutoClick{
    public static void Click(@NonNull AccessibilityService service, int x, int y) {
        AutoClick.Swipe(service,x - 1, y - 1, x + 1, y + 1);
    }

    public static void Swipe(@NonNull AccessibilityService service,int x1, int y1, int x2, int y2) {
        Path path = new Path();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        service.dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription
                (path, 0, 100)).build(), null, null);
    }
}