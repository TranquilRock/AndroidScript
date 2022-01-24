package com.example.androidscript.Activities;

import com.example.androidscript.FloatingWidget.FloatingWidgetService;
import com.example.androidscript.Activities.ArkKnights.ArkKnightsEditor;
import com.example.androidscript.util.AutoClick;
import com.example.androidscript.util.BtnMaker;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.widget.Button;

import com.example.androidscript.R;
import com.example.androidscript.util.FileOperation;

import org.opencv.android.OpenCVLoader;

import com.example.androidscript.util.ScreenShot;

public class Menu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FileOperation.fileRootInit(getExternalMediaDirs()[0].getAbsolutePath() + "/");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        ScreenShot.setUpScreenDimension(displayMetrics.heightPixels, displayMetrics.widthPixels);
        setContentView(R.layout.activity_menu);
        BtnMaker.Jump(R.id.button_to_ArkUI, this, ArkKnightsEditor.class);
        BtnMaker.JumpWithMessage(R.id.button_to_FGO, this, SelectFile.class, "next_destination", "com.example.androidscript.Activities.FGO.FGOEditor");

        Button btn = findViewById(R.id.button_to_basic_landscape);
        btn.setOnClickListener((v -> {
            Intent intent = new Intent(this,SelectFile.class );
            intent.putExtra("next_destination", "com.example.androidscript.Activities.Basic.BasicEditor");
            intent.putExtra("Orientation","Landscape");
            startActivity(intent);
        }));

        btn = findViewById(R.id.button_to_basic_portrait);
        btn.setOnClickListener((v -> {
            Intent intent = new Intent(this,SelectFile.class );
            intent.putExtra("next_destination", "com.example.androidscript.Activities.Basic.BasicEditor");
            intent.putExtra("Orientation","Portrait");
            startActivity(intent);
        }));

        BtnMaker.registerOnClick(R.id.Exit, this, v -> endService(true));
        if (!OpenCVLoader.initDebug()) {
            throw new AssertionError("OpenCV unavailable!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getStringExtra("Message") != null && getIntent().getStringExtra("Message").equals("Reset")) {
            endService(false);
        }
    }
    public void endService(boolean stop){
        AutoClick.stop();
        ScreenShot.endProjection();
        stopService(new Intent(this,FloatingWidgetService.class));
        stopService(new Intent(this, ScreenShot.class));
        if(stop){
            this.finish();
        }
    }
}
