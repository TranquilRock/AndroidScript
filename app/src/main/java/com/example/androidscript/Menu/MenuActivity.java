package com.example.androidscript.Menu;

import com.example.androidscript.FloatingWidget.FloatingWidgetService;
import com.example.androidscript.Menu.Basic.BasicEditor;
import com.example.androidscript.Menu.FGO.FGOEditor;
import com.example.androidscript.Menu.ArkKnights.ArkKnightsEditor;
import com.example.androidscript.util.BtnMaker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.androidscript.R;
import com.example.androidscript.util.DebugMessage;
import com.example.androidscript.util.FileOperation;

import org.opencv.android.OpenCVLoader;

import com.example.androidscript.util.ScreenShot;

import java.io.File;
import java.util.Vector;

public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileOperation.fileRootInit(getExternalMediaDirs()[0].getAbsolutePath() + "/");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        ScreenShot.setUpScreenDimension(displayMetrics.heightPixels,displayMetrics.widthPixels);
        setContentView(R.layout.activity_menu);
        BtnMaker.jump(R.id.button_to_ArkUI, this, ArkKnightsEditor.class);
        BtnMaker.jumpWithMessage(R.id.button_to_FGO, this, SelectFile.class, "next_destination", "com.example.androidscript.Menu.FGO.FGOEditor");
        BtnMaker.jumpWithMessage(R.id.button_to_basic, this, SelectFile.class, "next_destination", "com.example.androidscript.Menu.Basic.BasicEditor");
        if (!OpenCVLoader.initDebug()) {
            throw new AssertionError("OpenCV unavailable!");
        }
    }
}
