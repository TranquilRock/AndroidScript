package com.example.androidscript.Test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.androidscript.util.AutoClick;
import com.example.androidscript.R;
import com.example.androidscript.util.ScreenShot;

public class TestActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        /*Button scnShot = findViewById(R.id.scnshot);
        scnShot.setOnClickListener(v -> ScreenShot.getScreenShot());*/

    }

    public void ClickTester(View view){
        Toast.makeText(this,"Autoclicksucess", Toast.LENGTH_SHORT).show();
    }

    public void AutoClick_swipe(View view){
        AutoClick.autoClickPos(540, 1310, 545, 1315);
    }

    public void scnShot(View view){
        Activity test = this;
        Bitmap scn = ScreenShot.getScreenShot(test);
        ImageView igShow = findViewById(R.id.imageView);
        igShow.setImageBitmap(scn);
    }
}
