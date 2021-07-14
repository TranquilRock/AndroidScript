package com.example.androidscript.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
public class ScreenShot {
    public static Bitmap getScreenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}

