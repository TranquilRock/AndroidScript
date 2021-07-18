package com.example.androidscript.util;

import android.annotation.SuppressLint;

import android.media.Image;
import android.graphics.Point;
import android.graphics.Bitmap;
import android.media.ImageReader;
import android.util.DisplayMetrics;
import android.graphics.PixelFormat;
import android.content.res.Resources;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;

import java.nio.ByteBuffer;

public class ScreenShot {
    public static ScreenShot instance = null;

    public static ScreenShot Instance(int width, int height, Point Offset, MediaProjection mediaProjection) {
        if (ScreenShot.instance == null) {
            ScreenShot.instance = new ScreenShot(width, height, Offset, mediaProjection);
            System.out.println("Screen Shot Init Succeeded.\n");
        }
        return ScreenShot.instance;
    }

    private MediaProjection mediaProjection;
    private ImageReader imageReader;
    private int TargetHeight;
    private int TargetWidth;
    private Point TargetOffset;
    private VirtualDisplay virtualDisplay;

    @SuppressLint("WrongConstant")
    private ScreenShot(int width, int height, Point Offset, MediaProjection mediaProjection) {
        this.mediaProjection = mediaProjection;
        this.imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 1);
        this.TargetOffset = Offset;
        this.TargetHeight = height;
        this.TargetWidth = width;
    }

    public Bitmap Shot() {
        StartDisplay();

        Image img = imageReader.acquireLatestImage();
        if (img == null) {
            System.out.println("GG\n");
            return null;
        }
        //TODO:Clarify following.
        int width = img.getWidth();
        int height = img.getHeight();
        final Image.Plane[] planes = img.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();//每个像素的间距
        int rowStride = planes[0].getRowStride();//总的间距
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                Bitmap.Config.ARGB_8888);//虽然这个色彩比较费内存但是 兼容性更好
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        img.close();

        EndDisplay();
        return bitmap;
    }

    private void StartDisplay() {
        this.virtualDisplay = mediaProjection.createVirtualDisplay("screen-mirror",
                this.TargetWidth,
                this.TargetHeight,
                Resources.getSystem().getDisplayMetrics().densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(), null, null);
    }

    private void EndDisplay() {
        if (this.virtualDisplay != null) {
            this.virtualDisplay.release();
            this.virtualDisplay = null;
        }
    }

    public int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public Double ScreenRatio() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = Resources.getSystem().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        return Math.max(screenWidth / (double) screenHeight, screenHeight / (double) screenWidth);
    }
}