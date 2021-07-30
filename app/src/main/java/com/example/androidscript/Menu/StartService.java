package com.example.androidscript.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidscript.FloatingWidget.FloatingWidgetService;
import com.example.androidscript.Menu.ArkKnights.ArkKnightsInterpreter;
import com.example.androidscript.R;
import com.example.androidscript.util.BtnMaker;
import com.example.androidscript.util.DebugMessage;
import com.example.androidscript.util.FileOperation;
import com.example.androidscript.util.Interpreter;
import com.example.androidscript.util.ScreenShot;
import com.example.androidscript.util.SpnMaker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

public class StartService extends AppCompatActivity {
    public static final int START_SERVICE_CODE = 321;
    public static final int PROJECTION_REQUEST_CODE = 123;
    private MediaProjectionManager mediaProjectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_service);

        if (!Settings.canDrawOverlays(getApplicationContext())) {//Floating Widget
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
        }
        //AutoClick
        try {
            if (Settings.Secure.getInt(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED) == 0) {
                this.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));//Get permission
            }
        } catch (Settings.SettingNotFoundException e) {
            DebugMessage.printStackTrace(e);
        }
        //ScreenShot, need to be foreground.(The rest parts are inside its class and onActivityResult.)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            List<String> requestedPermissions = new ArrayList<>();
            requestedPermissions.add(Manifest.permission.FOREGROUND_SERVICE);//Stub to add more permissions.
            String[] requests = new String[requestedPermissions.size()];
            requestPermissions(requests, 100);
        }

        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult((mediaProjectionManager).createScreenCaptureIntent(), PROJECTION_REQUEST_CODE);

        setTmpScript();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROJECTION_REQUEST_CODE && resultCode == RESULT_OK) {
            new Handler().postDelayed(() -> {
                ScreenShot.setUpMediaProjectionManager(data, mediaProjectionManager, true);
                startService(new Intent(getApplicationContext(), ScreenShot.class));
            }, 1);
            try {//Check all satisfied
                if (Settings.canDrawOverlays(getApplicationContext()) && Settings.Secure.getInt(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED) > 0) {
                    startService(new Intent(this, FloatingWidgetService.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Need permission!", Toast.LENGTH_LONG).show();
                }
            } catch (Settings.SettingNotFoundException e) {
                DebugMessage.printStackTrace(e);
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Need permission!", Toast.LENGTH_LONG).show();
        }
        finish();//Exit anyway, since nothing to show;
    }
    @Deprecated
    private void setTmpScript(){
        Interpreter gg = new ArkKnightsInterpreter();
        gg.Interpret("AutoFightEat.txt");
        FloatingWidgetService.setScript(gg);
    }
}
