package com.example.androidscript.Test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.androidscript.R;
import com.example.androidscript.util.ScreenShot;

import java.io.File;
import java.io.FileOutputStream;

public class TestActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        /*Button scnShot = findViewById(R.id.scnshot);
        scnShot.setOnClickListener(v -> ScreenShot.getScreenShot());*/
//        setContentView(R.layout.scm);

    }

    public void ClickTester(View view) {
        Toast.makeText(this, "Autoclicksucess", Toast.LENGTH_SHORT).show();
    }

    public void AutoClick_swipe(View view) {

    }

    public void scnShot(View view) {
        Activity test = this;
//        Bitmap scn;
//        ImageView igShow = findViewById(R.id.imageView);
//        igShow.setImageBitmap(scn);
    }

}
