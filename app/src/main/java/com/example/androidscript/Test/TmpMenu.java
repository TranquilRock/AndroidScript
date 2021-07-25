package com.example.androidscript.Test;

import com.example.androidscript.FloatingWidget.FloatingWidgetService;
import com.example.androidscript.UserInterface.TestRecyclerView;
import com.example.androidscript.util.BtnMaker;
import com.example.androidscript.Menu.MenuActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.androidscript.R;
import com.example.androidscript.util.*;

import org.opencv.android.OpenCVLoader;


import java.util.ArrayList;
import java.util.List;

public class TmpMenu extends AppCompatActivity {

    /*  Permission request code to draw over other apps  */
    private static final int DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222;
    private Button btnToMenu;
    private Button btnToTest;
    private Button btnDoScreenshot;
    private Button btnToRecyclerTest;

    public static final int PROJECTION_REQUEST_CODE = 123;
    private MediaProjectionManager mm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp_menu);
        btnToMenu = BtnMaker.jump(R.id.button_to_menu, this, MenuActivity.class);
        btnToTest = BtnMaker.jump(R.id.button_to_test, this, TestActivity.class);
        btnToRecyclerTest = BtnMaker.jump(R.id.button_to_Recycler, this, TestRecyclerView.class);
        assert(OpenCVLoader.initDebug());
    }

    public void createFloatingWidget(View view) {
        if (Settings.canDrawOverlays(getApplicationContext())) {
            Intent startFloatingWidgetService = new Intent(TmpMenu.this, FloatingWidgetService.class);
            startFloatingWidgetService.putExtra("FileName","Test.txt");
            startService(startFloatingWidgetService);
            finish();
        }else{
            Toast.makeText(getApplicationContext(),"Need permission!",Toast.LENGTH_LONG).show();
        }
    }
}
