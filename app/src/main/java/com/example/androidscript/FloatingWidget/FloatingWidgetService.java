package com.example.androidscript.FloatingWidget;

import android.annotation.SuppressLint;
import android.app.Service;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Gravity;
import android.content.Intent;
import android.graphics.Point;
import android.view.MotionEvent;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.view.LayoutInflater;
import android.graphics.PixelFormat;
import android.content.res.Configuration;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.test.internal.runner.junit4.statement.UiThreadStatement;

import com.example.androidscript.Menu.MenuActivity;
import com.example.androidscript.R;
import com.example.androidscript.util.*;
import com.example.androidscript.util.Interpreter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import android.app.Activity;

public class FloatingWidgetService extends Service implements View.OnClickListener {

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
        DebugMessage.set("Assign Script");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        this.mWindowManager.getDefaultDisplay().getSize(szWindow);
        this.inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        addFloatingWidgetView();
        implementClickListeners();
        implementTouchListenerToFloatingWidgetView();
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
        params.y = 100;

        mWindowManager.addView(mFloatingWidgetView, params);
        collapsedView = mFloatingWidgetView.findViewById(R.id.collapse_view);
        expandedView = mFloatingWidgetView.findViewById(R.id.expanded_container);
        curStatus = new Bulletin(mFloatingWidgetView.findViewById(R.id.stateToast));
    }

    /*  Implement Touch Listener to Floating Widget Root View  */
    private void implementTouchListenerToFloatingWidgetView() {
        mFloatingWidgetView.findViewById(R.id.expanded_container).setOnTouchListener(new View.OnTouchListener() {

            long time_start = 0, time_end = 0;

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
                        //remember the initial position.
                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;
                        return true;
                    case MotionEvent.ACTION_UP:
                        //Get the difference between initial coordinate and current coordinate
                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;
                        //The check for x_diff <5 && y_diff< 5 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5) {
                            time_end = System.currentTimeMillis();
                            //Also check the difference between start time and end time should be less than 300ms
                            if ((time_end - time_start) < 300) {
                                onFloatingWidgetClick();
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
                    default:
                        DebugMessage.set("Unrecognized in " + this.getClass().toString() + " implementTouchListenerToFloatingWidgetView\n");
                }
                return false;
            }
        });
    }

    private void implementClickListeners() {//Set all View's Listener to self
        mFloatingWidgetView.findViewById(R.id.floating_widget_image_view).setOnClickListener(this);
        mFloatingWidgetView.findViewById(R.id.open_activity_button).setOnClickListener(this);
        mFloatingWidgetView.findViewById(R.id.run_script).setOnClickListener(this);
        mFloatingWidgetView.findViewById(R.id.stop_script).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_widget_image_view:
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
                break;
            case R.id.open_activity_button:
                Intent intent = new Intent(FloatingWidgetService.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                stopSelf();
                break;
            case R.id.run_script:
                if (interpreter == null && ScriptName != null && ScriptFolderName != null) {
                    interpreter = new Interpreter(ScriptFolderName, ScriptName, curStatus);
                    interpreter.runCode(Argv);
                }
                break;
            case R.id.stop_script:
                if (interpreter != null) {
                    curStatus.Announce("IDLE");
                    interpreter.running = false;
                    interpreter = null;
                }
                break;
        }
    }

    /*  Reset position of Floating Widget view on dragging  */
    private void resetPosition(int x_cord_now) {
        if (x_cord_now <= szWindow.x / 2) {
            moveToLeft(x_cord_now);
        } else {
            moveToRight(x_cord_now);
        }
    }

    /*  Method to move the Floating widget view to Left  */
    private void moveToLeft(final int current_x_cord) {
        final int x = szWindow.x - current_x_cord;

        new CountDownTimer(500, 5) {
            //get params of Floating Widget view
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) mFloatingWidgetView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;

                mParams.x = -(int) (current_x_cord * current_x_cord * step);

                //Update window manager for Floating Widget
                mWindowManager.updateViewLayout(mFloatingWidgetView, mParams);
            }

            public void onFinish() {
                mParams.x = 0;

                //Update window manager for Floating Widget
                mWindowManager.updateViewLayout(mFloatingWidgetView, mParams);
            }
        }.start();
    }

    /*  Method to move the Floating widget view to Right  */
    private void moveToRight(final int current_x_cord) {

        new CountDownTimer(500, 5) {
            final WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) mFloatingWidgetView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;
                mParams.x = (int) (szWindow.x + (current_x_cord * current_x_cord * step) - mFloatingWidgetView.getWidth());
                mWindowManager.updateViewLayout(mFloatingWidgetView, mParams);
            }

            public void onFinish() {
                mParams.x = szWindow.x - mFloatingWidgetView.getWidth();
                mWindowManager.updateViewLayout(mFloatingWidgetView, mParams);
            }
        }.start();
    }

    /*  Detect if the floating view is collapsed or expanded */
    private boolean isViewCollapsed() {
        return mFloatingWidgetView == null || mFloatingWidgetView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    /*  return status bar height on basis of device display metrics  */
    private int getStatusBarHeight() {
        return (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
    }

    /*  Update Floating Widget view coordinates on Configuration change  */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mWindowManager.getDefaultDisplay().getSize(szWindow);
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mFloatingWidgetView.getLayoutParams();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (layoutParams.y + (mFloatingWidgetView.getHeight() + getStatusBarHeight()) > szWindow.y) {
                layoutParams.y = szWindow.y - (mFloatingWidgetView.getHeight() + getStatusBarHeight());
                mWindowManager.updateViewLayout(mFloatingWidgetView, layoutParams);
            }
            if (layoutParams.x != 0 && layoutParams.x < szWindow.x) {
                resetPosition(szWindow.x);
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (layoutParams.x > szWindow.x) {
                resetPosition(szWindow.x);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*  on Floating widget click show expanded view  */
    private void onFloatingWidgetClick() {
        if (isViewCollapsed()) {
            collapsedView.setVisibility(View.GONE);//Invisible
            expandedView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingWidgetView != null) {
            mWindowManager.removeView(mFloatingWidgetView);
            mFloatingWidgetView = null;
            ScreenShot.endProjection();
        }
    }

    public static class Bulletin {
        TextView board;

        Bulletin(TextView _board) {
            board = _board;
        }

        public void Announce(String announcement){
            try{
                new Handler(Looper.getMainLooper()).post(() -> board.setText(announcement));
            }catch (Throwable e){
                DebugMessage.set("Bulletin GG");
            }
        }
    }
}
