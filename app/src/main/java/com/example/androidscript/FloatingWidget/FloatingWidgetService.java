package com.example.androidscript.FloatingWidget;

import android.annotation.SuppressLint;
import android.app.Service;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.view.Gravity;
import android.content.Intent;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.LayoutInflater;
import android.graphics.PixelFormat;
import android.content.res.Configuration;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.androidscript.Menu.MenuActivity;
import com.example.androidscript.R;
import com.example.androidscript.util.*;
import com.example.androidscript.util.Interpreter;

public class FloatingWidgetService extends Service {
    private WindowManager mWindowManager = null;
    private View mFloatingWidgetView = null, collapsedView = null, expandedView = null;
    private final Point szWindow = new Point();
    private LayoutInflater inflater = null;
    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private static Interpreter interpreter;
    private static String ScriptFolderName;
    private static String ScriptName;
    private static String[] Argv;
    private Bulletin curStatus;

    public static void setScript(String _ScriptFolderName, String _ScriptName, String[] _Argv) {
        ScriptFolderName = _ScriptFolderName;
        ScriptName = _ScriptName;
        Argv = _Argv;
        interpreter = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        this.mWindowManager.getDefaultDisplay().getSize(szWindow);
        this.inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        addFloatingWidgetView();
        setUpListener();
    }

    private void setUpListener() {
        this.mFloatingWidgetView.findViewById(R.id.open_activity_button).setOnClickListener(v -> {
            Intent intent = new Intent(FloatingWidgetService.this, MenuActivity.class).putExtra("Message", "Reset");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            stopSelf();
        });

        this.mFloatingWidgetView.findViewById(R.id.run_script).setOnClickListener(v -> {
            if (interpreter == null && ScriptName != null && ScriptFolderName != null) {
                interpreter = new Interpreter(ScriptFolderName, ScriptName, curStatus);
                interpreter.runCode(Argv);
                resetPosition();
                collapseView();
            }
        });

        this.mFloatingWidgetView.findViewById(R.id.stop_script).setOnClickListener(v -> {
            if (interpreter != null) {
                interpreter.running = false;
                if(interpreter.isAlive()){
                    interpreter.interrupt();
                }
                interpreter = null;
            }
        });

        this.mFloatingWidgetView.findViewById(R.id.root_container).setOnTouchListener(getFloatingWidgetViewTouchListener());
        this.mFloatingWidgetView.findViewById(R.id.floating_widget_image_view).setOnTouchListener(getFloatingWidgetViewTouchListener());
    }

    /*  Add Floating Widget View to Window Manager  */
    private void addFloatingWidgetView() {
        //Inflate the floating view layout we created
        mFloatingWidgetView = this.inflater.inflate(R.layout.floating_widget_layout, null);

        //Add the view to the window.
        WindowManager.LayoutParams params;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.START;

        params.x = 0;
        params.y = 0;

        mWindowManager.addView(mFloatingWidgetView, params);
        collapsedView = mFloatingWidgetView.findViewById(R.id.collapse_view);
        expandedView = mFloatingWidgetView.findViewById(R.id.expanded_container);
        curStatus = new Bulletin(mFloatingWidgetView.findViewById(R.id.stateToast));
    }

    /*  Implement Touch Listener to Floating Widget Root View  */
    private View.OnTouchListener getFloatingWidgetViewTouchListener() {
        return (new View.OnTouchListener() {
            long time_start = 0;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mFloatingWidgetView.getLayoutParams();

                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();

                int x_cord_Destination, y_cord_Destination;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time_start = System.currentTimeMillis();
                        x_init_cord = x_cord;
                        y_init_cord = y_cord;
                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;
                        return true;
                    case MotionEvent.ACTION_UP:
                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;
                        //The check for x_diff <5 && y_diff< 5 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5) {
                            if ((System.currentTimeMillis() - time_start) < 800) {
                                switchView();
                            }
                        }
                        y_cord_Destination = y_init_margin + y_diff;
                        int barHeight = getStatusBarHeight();
                        if (y_cord_Destination < 0) {
                            y_cord_Destination = 0;
                        } else if (y_cord_Destination + (mFloatingWidgetView.getHeight() + barHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (mFloatingWidgetView.getHeight() + barHeight);
                        }
                        layoutParams.y = y_cord_Destination;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int x_diff_move = x_cord - x_init_cord;
                        int y_diff_move = y_cord - y_init_cord;
                        x_cord_Destination = x_init_margin + x_diff_move;
                        y_cord_Destination = y_init_margin + y_diff_move;
                        layoutParams.x = x_cord_Destination;
                        layoutParams.y = y_cord_Destination;
                        mWindowManager.updateViewLayout(mFloatingWidgetView, layoutParams);
                        return true;
                }
                return false;
            }
        });
    }

    /*  Reset position of Floating Widget view on dragging  */
    private void resetPosition() {
        mWindowManager.getDefaultDisplay().getSize(szWindow);
        WindowManager.LayoutParams pos = (WindowManager.LayoutParams) mFloatingWidgetView.getLayoutParams();
        pos.x = 0;
        pos.y = 0;
//        if (pos.x <= szWindow.x / 2) {
//            if(pos.y <= szWindow.y/2){
//                pos.x = 0;
//                pos.y = 0;
//            }else{
//                pos.x = 0;
//                pos.y = szWindow.y;
//            }
//        } else {
//            if(pos.y <= szWindow.y/2){
//                pos.x = szWindow.x;
//                pos.y = 0;
//            }else{
//                pos.x = szWindow.x;
//                pos.y = szWindow.y;
//            }
//        }
        mWindowManager.updateViewLayout(mFloatingWidgetView, pos);
    }

    /*  return status bar height on basis of device display metrics  */
    private int getStatusBarHeight() {
        return (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*  on Floating widget click show expanded view  */
    private void expandView() {
        if (mFloatingWidgetView != null) {
            collapsedView.setVisibility(View.GONE);//Invisible
            expandedView.setVisibility(View.VISIBLE);
        }
    }

    private void collapseView() {
        if (mFloatingWidgetView != null) {
            collapsedView.setVisibility(View.VISIBLE);
            expandedView.setVisibility(View.GONE);
        }
    }

    private void switchView() {
        if (mFloatingWidgetView != null && mFloatingWidgetView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE) {
            expandView();
        } else {
            collapseView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingWidgetView != null) {
            mWindowManager.removeView(mFloatingWidgetView);
            mFloatingWidgetView = null;
        }
        if (interpreter != null) {
            interpreter.running = false;
        }
    }

    public static class Bulletin {
        TextView board;

        Bulletin(TextView _board) {
            board = _board;
        }

        public void Announce(String announcement) {
            try {
                new Handler(Looper.getMainLooper()).post(() -> board.setText(announcement));
            } catch (Throwable e) {
                DebugMessage.set("Bulletin GG");
            }
        }
    }
}
