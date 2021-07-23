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
import android.graphics.Point;
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

import com.example.androidscript.Test.TmpMenu;

import java.nio.ByteBuffer;

public class ScreenShot extends Service {
    private static ImageReader imageReader = null;
    private static int TargetHeight = 0;
    private static int TargetWidth = 0;
    private static Intent Permission = null;
    private static Point TargetOffset = null;//Todo make screenshot range start from offset
    private static VirtualDisplay virtualDisplay = null;
    private static MediaProjection mediaProjection = null;
    private static MediaProjectionManager mediaProjectionManager = null;
    private static int screenHeight = 0;
    private static int screenWidth = 0;
    public static boolean ServiceStart = false;

    static {
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    }

    public static void pass(Intent intent, MediaProjectionManager mm) {
        if (ScreenShot.mediaProjectionManager == null) {
            ScreenShot.Permission = intent;
            ScreenShot.mediaProjectionManager = mm;
        }
    }

    public static Bitmap Shot() {
        StartDisplay();
        if (!ServiceStart) {
            DebugMessage.set("Service unavailable.\n");
            return null;
        }
        for (int z = 0; z < 3; z++) {//Auto restart at most three times.
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Image img = imageReader.acquireLatestImage();
            if (img == null) {
                DebugMessage.set(z + "::No Img in Screenshot\n");
                continue;
            }
            //TODO:Clarify following.
            int width = img.getWidth();
            int height = img.getHeight();
            final Image.Plane[] planes = img.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();//像素間距
            int rowStride = planes[0].getRowStride();//總間距
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
                        ScreenShot.TargetWidth,
                        ScreenShot.TargetHeight,
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

    public static int getScreenWidth() {
        return ScreenShot.screenWidth;
    }

    public static int getScreenHeight() {
        return ScreenShot.screenHeight;
    }


    private void createNotificationChannel() {
        Notification.Builder builder = new Notification.Builder(getApplicationContext()); //获取一个Notification构造器
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, TmpMenu.class), 0))
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
        createNotificationChannel();
        ScreenShot.mediaProjection = ScreenShot.mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, ScreenShot.Permission);
        ScreenShot.imageReader = ImageReader.newInstance(getScreenWidth(), getScreenHeight(), PixelFormat.RGBA_8888, 10);
        ScreenShot.TargetOffset = new Point(0, 0);
        ScreenShot.TargetHeight = getScreenHeight();
        ScreenShot.TargetWidth = getScreenWidth();
        ScreenShot.ServiceStart = true;
        DebugMessage.set("Start Screen Casting on (" + screenHeight + "," + screenWidth + ") device\n");
        return 0;
    }

}