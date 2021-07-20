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
import android.util.DisplayMetrics;
import android.graphics.PixelFormat;
import android.content.res.Resources;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;

import androidx.annotation.Nullable;
import com.example.androidscript.Test.TmpMenu;

import java.nio.ByteBuffer;

public class ScreenShot extends Service {
    private static boolean initialized = false;
    private static ImageReader imageReader = null;
    private static int TargetHeight = 0;
    private static int TargetWidth = 0;
    private static Intent Permission = null;
    private static Point TargetOffset = null;//Todo make screenshot range start from offset
    private static VirtualDisplay virtualDisplay = null;
    private static MediaProjection mediaProjection = null;
    private static MediaProjectionManager mediaProjectionManager = null;

    public static void pass(Intent shit, MediaProjectionManager mm) {
        if (ScreenShot.mediaProjectionManager == null) {
            ScreenShot.Permission = (Intent) shit.clone();
            ScreenShot.mediaProjectionManager = mm;
        }
    }

    public static Bitmap Shot() {
        StartDisplay();
        Image img = imageReader.acquireLatestImage();
        if (img == null) {
            System.out.println("No Img in Screenshot\n");
            return null;
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

    private static void StartDisplay() {
        if (ScreenShot.virtualDisplay == null) {
            ScreenShot.virtualDisplay = ScreenShot.mediaProjection.createVirtualDisplay("Screenshot",
                    ScreenShot.TargetWidth,
                    ScreenShot.TargetHeight,
                    Resources.getSystem().getDisplayMetrics().densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    ScreenShot.imageReader.getSurface(), null, null);
        }
    }

    private static void EndDisplay() {
        if (ScreenShot.virtualDisplay != null) {
            ScreenShot.virtualDisplay.release();
            ScreenShot.virtualDisplay = null;
        }
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    private void createNotificationChannel() {
        Notification.Builder builder = new Notification.Builder(getApplicationContext()); //获取一个Notification构造器
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, TmpMenu.class),0))
                .setContentTitle("AndroidScript啟動中")
                .setContentText("AndroidScript正在擷取螢幕")
                .setWhen(System.currentTimeMillis())
//                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
//                .setSmallIcon(R.mipmap.ic_launcher)
        ;

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
        System.out.println("Before\n");
        createNotificationChannel();
        System.out.println("After\n");
        ScreenShot.mediaProjection = ScreenShot.mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, ScreenShot.Permission);
        System.out.println("A\n");
        ScreenShot.imageReader = ImageReader.newInstance(getScreenWidth(), getScreenHeight(), PixelFormat.RGBA_8888, 10);
        System.out.println("B\n");
        ScreenShot.TargetOffset = new Point(0, 0);
        ScreenShot.TargetHeight = getScreenHeight();
        ScreenShot.TargetWidth = getScreenWidth();
        return 0;
    }
//    public static Double ScreenRatio() {
//        DisplayMetrics dm = new DisplayMetrics();
//        dm = Resources.getSystem().getDisplayMetrics();
//        int screenWidth = dm.widthPixels;
//        int screenHeight = dm.heightPixels;
//        return Math.max(screenWidth / (double) screenHeight, screenHeight / (double) screenWidth);
//    }
}