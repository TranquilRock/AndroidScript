package com.example.androidscript.Test;

import com.example.androidscript.FloatingWidget.FloatingWidgetService;
import com.example.androidscript.Menu.FGO.FGOEditor;
import com.example.androidscript.Menu.ArkKnights.ArkKnightEditor;
import com.example.androidscript.util.BtnMaker;
import com.example.androidscript.Menu.MenuActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import com.example.androidscript.R;

import org.opencv.android.OpenCVLoader;

public class TmpMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp_menu);
        BtnMaker.jump(R.id.button_to_menu, this, MenuActivity.class);
        BtnMaker.jump(R.id.button_to_test, this, TestActivity.class);
        BtnMaker.jump(R.id.button_to_Recycler, this, FGOEditor.class);
        BtnMaker.jump(R.id.button_to_ArkUI, this, ArkKnightEditor.class);
        if (!OpenCVLoader.initDebug()){ throw new AssertionError("OpenCV unavailable!");}
    }

    public void createFloatingWidget(View view) {
        if (Settings.canDrawOverlays(getApplicationContext())) {
            startService(new Intent(TmpMenu.this, FloatingWidgetService.class));
            finish();
        }else{
            Toast.makeText(getApplicationContext(),"Need permission!",Toast.LENGTH_LONG).show();
        }
    }
}
