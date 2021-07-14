package com.example.androidscript.util;

import android.graphics.Bitmap;
import android.view.View;

import android.graphics.Canvas;
import android.app.Activity;


public class ScreenShot {
    public static Bitmap getScreenShot(Activity tmp) {

        View view = tmp.getWindow().getDecorView().getRootView();
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;

    }
}

