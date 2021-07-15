//package com.example.androidscript.Test;
//
//import android.accessibilityservice.AccessibilityService;
//import android.accessibilityservice.GestureDescription;
//import android.annotation.TargetApi;
//import android.content.Intent;
//import android.graphics.Path;
//import android.graphics.Point;
//import android.os.Build;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.util.Log;
//import android.view.accessibility.AccessibilityEvent;
//
//import androidx.annotation.RequiresApi;
//
///**
// * 模拟点击的服务类
// *
// * @author zhang
// * @since 2019-12-14
// */
//public class SimulatedClickService extends AccessibilityService {
//    private int x=616, y=1901;
//    private Point point = new Point(x, y);
//
//
//
//    private Handler handler = new Handler(Looper.getMainLooper()) {
//        @TargetApi(Build.VERSION_CODES.N)
//        @Override
//        public void handleMessage(Message msg) {
//            dispatchGestureClick(160, 60);
//        }
//    };
//
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public void dispatchGestureClick(int x, int y) {
//        Path path = new Path();
//        path.moveTo(x, y);
//        boolean click = dispatchGesture(new GestureDescription
//                .Builder()
//                .addStroke(new GestureDescription.StrokeDescription(path, 0, 100)).build(),null, null);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    @Override
//    public void onAccessibilityEvent(AccessibilityEvent event) {
//
//    }
//
//    @Override
//    public void onInterrupt() {
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    @Override
//    public void onCreate(){
//        Log.v("pp", "CREATE");
//        for(int i=0; i<10; i++){
//            click(point);
//        }
//    }
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    @Override
//    protected void onServiceConnected() {
//        super.onServiceConnected();
//
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public void click(Point point) {
//        Log.v("pp", "CLICK");
//        GestureDescription.Builder builder = new GestureDescription.Builder();
//        Path path = new Path();
//        path.moveTo((float) point.x, (float) point.y);
//        path.lineTo((float) point.x, (float) point.y);
//
//        builder.addStroke(new GestureDescription.StrokeDescription(path, 1, 10));
//        final GestureDescription build = builder.build();
//
//        dispatchGesture(build, new GestureResultCallback() {
//            public void onCancelled(GestureDescription gestureDescription) {
//                super.onCancelled(gestureDescription);
//            }
//            public void onCompleted(GestureDescription gestureDescription) {
//                super.onCompleted(gestureDescription);
//            }
//        }, null);
//    }
//}