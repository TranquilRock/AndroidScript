package com.example.androidscript.UserInterface;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidscript.R;
import com.example.androidscript.util.DebugMessage;
import com.example.androidscript.util.FileOperation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class Editor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.resourceInitialize();
    }

    public abstract String getFolderName();

    protected abstract void resourceInitialize();

    protected void getResource(String sourceName, String folderName, String fileName) throws IOException {
        if (sourceName.endsWith(".txt")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(sourceName)));

            // do reading, usually loop until end of file reading
            String line;
            while ((line = reader.readLine()) != null) {
            }
        } else if (sourceName.endsWith(".png")) {
            FileOperation.saveBitmapAsPNG(BitmapFactory.decodeStream(getAssets().open(sourceName)), folderName + fileName);
        } else if (sourceName.endsWith(".jpg")) {
            FileOperation.saveBitmapAsJPG(BitmapFactory.decodeStream(getAssets().open(sourceName)), folderName + fileName);
        }
    }
}
