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

import com.example.androidscript.Activities.Menu;
import com.example.androidscript.R;

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
        if (ScreenShot.virtualDisplay != null) {
            ScreenShot.virtualDisplay.release();
            ScreenShot.virtualDisplay = null;
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
        ScreenShot.imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 3);
    }

    public static Bitmap Shot() {
        if (!ServiceStart) {
            DebugMessage.set("Service unavailable.\n");
            return null;
        }

        Image img = imageReader.acquireLatestImage();
        if (img == null) {
            DebugMessage.set("No Img in Screenshot");
            return null;
        }

        DebugMessage.set("IMAGE " + img.getWidth() + " " + img.getHeight());
        int width;
        int height;
        if (Transposed) {
            width = max(img.getHeight(), img.getWidth());
            height = min(img.getHeight(), img.getWidth());
        } else {
            width = min(img.getHeight(), img.getWidth());
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
        return bitmap;
    }

    private void createNotificationChannel() {
        PendingIntent navigate = PendingIntent.getActivity(this, 0, new Intent(this, Menu.class).putExtra("Message", "Reset"), 0);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentIntent(navigate)
                .setContentTitle("AndroidScript")
                .setContentText("Capturing Screen")
                .setSmallIcon(R.drawable.ic_launcher_foreground) //Necessary
                .setPriority(Notification.PRIORITY_HIGH).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        startForeground(13, builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        ScreenShot.mediaProjection = ScreenShot.mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, ScreenShot.Permission);
        ScreenShot.ServiceStart = true;
        ScreenShot.virtualDisplay = ScreenShot.mediaProjection.createVirtualDisplay("Screenshot",
                ScreenShot.screenWidth,
                ScreenShot.screenHeight,
                Resources.getSystem().getDisplayMetrics().densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                ScreenShot.imageReader.getSurface(), null, null);
        DebugMessage.set("Start Screen Casting on (" + screenHeight + "," + screenWidth + ") device\n");
        return 0;
    }

    public static int compare(Bitmap Target, int x1, int y1, int x2, int y2) {
        Bitmap screen = Shot();
        if (screen == null) {
            return 0;
        } else {
            return ImageHandler.matchPicture(Bitmap.createBitmap(screen, x1, y1, (x2 - x1), (y2 - y1)), Target);
        }
    }
}