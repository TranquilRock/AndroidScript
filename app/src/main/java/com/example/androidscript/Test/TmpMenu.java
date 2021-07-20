package com.example.androidscript.Test;

import com.example.androidscript.FloatingWidget.FloatingWidgetService;
import com.example.androidscript.util.BtnMaker;
import com.example.androidscript.Menu.MenuActivity;

import android.Manifest;
import android.app.Activity;
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

import com.example.androidscript.R;
import com.example.androidscript.util.*;

import java.util.ArrayList;
import java.util.List;

public class TmpMenu extends AppCompatActivity {

    /*  Permission request code to draw over other apps  */
    private static final int DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222;
    private Button btnToMenu;
    private Button btnToTest;
    private Button btnSetScreenshot;
    private Button btnDoScreenshot;
    public static final int PROJECTION_REQUEST_CODE = 123;
    private MediaProjectionManager mm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPermission();
        setContentView(R.layout.activity_tmp_menu);
        FileOperation.setUpFileOperation(getFilesDir().getAbsolutePath() + "/");


        btnToMenu = BtnMaker.jump(R.id.button_to_menu, this, MenuActivity.class);
        btnToTest = BtnMaker.jump(R.id.button_to_test, this, TestActivity.class);
        btnSetScreenshot = BtnMaker.registerOnClick(R.id.button_set_screenshot,this,(v -> {
            mm = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult((mm).createScreenCaptureIntent(), PROJECTION_REQUEST_CODE);
        }));
        btnDoScreenshot = BtnMaker.registerOnClick(R.id.button_tmp,this,(v -> FileOperation.instance.saveBitmapAsJPG(ScreenShot.Shot(),"image.jpg")));
    }

    public void setPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            List<String> requestedPermissions = new ArrayList<>();
            requestedPermissions.add(Manifest.permission.FOREGROUND_SERVICE);
            String[] requests = new String[requestedPermissions.size()];
            requestPermissions(requests, 100);
        }
    }

    public void createFloatingWidget(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) { //Ask for the permission.(Default for API<23)
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE);
        } else {
            startFloatingWidgetService();
        }
    }

    private void startFloatingWidgetService() {
        this.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        startService(new Intent(TmpMenu.this, FloatingWidgetService.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROJECTION_REQUEST_CODE) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ScreenShot.pass((Intent)data.clone(),mm);
                    startService(new Intent(getApplicationContext(),ScreenShot.class));
                }
            }, 1);
        } else if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE && resultCode == RESULT_OK) {
            startFloatingWidgetService();
        }
    }
}
