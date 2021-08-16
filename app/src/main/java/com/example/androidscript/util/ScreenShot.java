package com.example.androidscript.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Image;
import android.graphics.Bitmap;
import android.media.ImageReader;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.graphics.PixelFormat;
import android.content.res.Resources;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;

import androidx.annotation.Nullable;

import com.example.androidscript.Menu.MenuActivity;

import org.opencv.android.OpenCVLoader;

import java.nio.ByteBuffer;

import static java.lang.Math.max;
import static java.lang.Math.min;

public final class ScreenShot extends Service {
    private static ImageReader imageReader = null;
    private static Intent Permission = null;
    private static VirtualDisplay virtualDisplay = null;
    private static MediaProjection mediaProjection = null;
    private static MediaProjectionManager mediaProjectionManager = null;
    private static int screenHeight = 0;
    private static int screenWidth = 0;
    public static boolean ServiceStart = false;
    public static boolean Transposed = false;

    public static int getHeight() {
        return screenHeight;
    }

    public static int getWidth() {
        return screenWidth;
    }

    public static void setUpScreenDimension(int _height, int _width) {
        screenWidth = _width;
        screenHeight = _height;
    }

    public static void endProjection() {
        if (mediaProjection != null) {
            mediaProjection.stop();
        }
        ServiceStart = false;
    }

    public static void setShotOrientation(boolean transpose) {
        Transposed = transpose;
        int tmp;
        if (transpose) {
            tmp = max(screenHeight, screenWidth);
            screenHeight = min(screenHeight, screenWidth);
        } else {
            tmp = min(screenHeight, screenWidth);
            screenHeight = max(screenHeight, screenWidth);
        }
        screenWidth = tmp;
    }

    @SuppressLint("WrongConstant")
    public static void setUpMediaProjectionManager(Intent intent, MediaProjectionManager mm) {
        if (ScreenShot.mediaProjectionManager == null) {
            ScreenShot.mediaProjectionManager = mm;
        }
        if (ScreenShot.Permission == null) {
            ScreenShot.Permission = intent;
        }

        ScreenShot.imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 1);
    }

    public static Bitmap Shot() {
        StartDisplay();
        if (!ServiceStart) {
            DebugMessage.set("Service unavailable.\n");
            return null;
        }
        for (int z = 0; z < 30; z++) {//Auto restart at most three times.
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Image img = imageReader.acquireLatestImage();
            if (img == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DebugMessage.set(z + "::No Img in Screenshot\n");
                continue;
            }
            //TODO:Clarify following.
            DebugMessage.set("IMAGE " + img.getWidth() + " " + img.getHeight());
            int width;
            int height;
            if (Transposed) {
                width = max(img.getHeight(), img.getWidth());
                height = Math.min(img.getHeight(), img.getWidth());
            } else {
                width = Math.min(img.getHeight(), img.getWidth());
                height = max(img.getHeight(), img.getWidth());
            }
            final Image.Plane plane = img.getPlanes()[0];
            final ByteBuffer buffer = plane.getBuffer();
            buffer.rewind();
            int pixelStride = plane.getPixelStride();//像素間距
            int rowStride = plane.getRowStride();//總間距
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            img.close();
            EndDisplay();
            return bitmap;
        }
        return null;
    }

    private static void StartDisplay() {
        if (ServiceStart) {
            try {
                ScreenShot.virtualDisplay = ScreenShot.mediaProjection.createVirtualDisplay("Screenshot",
                        ScreenShot.screenWidth,
                        ScreenShot.screenHeight,
                        Resources.getSystem().getDisplayMetrics().densityDpi,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        ScreenShot.imageReader.getSurface(), null, null);
            } catch (SecurityException e) {
                ServiceStart = false;
                DebugMessage.printStackTrace(e);
            }
        }
    }

    private static void EndDisplay() {
        if (ScreenShot.virtualDisplay != null) {
            ScreenShot.virtualDisplay.release();
            ScreenShot.virtualDisplay = null;
        }
    }

    private void createNotificationChannel() {
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MenuActivity.class).putExtra("Message", "Reset"), 0))
                .setContentTitle("AndroidScript啟動中")
                .setContentText("AndroidScript正在擷取螢幕")
                .setWhen(System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        startForeground(13, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!OpenCVLoader.initDebug()) {
            throw new AssertionError("OpenCV unavailable!");
        }
        createNotificationChannel();
        ScreenShot.mediaProjection = ScreenShot.mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, ScreenShot.Permission);
        ScreenShot.ServiceStart = true;
        DebugMessage.set("Start Screen Casting on (" + screenHeight + "," + screenWidth + ") device\n");
        return 0;
    }

    public static int compare(Bitmap Target, int x1, int y1, int x2, int y2) {
        return ImageHandler.matchPicture(Bitmap.createBitmap(Shot(), x1, y1, (x2 - x1), (y2 - y1)), Target);
    }
}