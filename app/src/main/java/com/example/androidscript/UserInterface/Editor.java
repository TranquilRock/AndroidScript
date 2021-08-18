package com.example.androidscript.UserInterface;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidscript.R;
import com.example.androidscript.util.DebugMessage;
import com.example.androidscript.util.FileOperation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public abstract class Editor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.resourceInitialize();
    }

    public abstract String getFolderName();

    protected abstract void resourceInitialize();

    protected void getResource(String folderName, String fileName){
        if(!FileOperation.findFile(folderName,fileName)){
            try{
                if (fileName.endsWith(".txt")) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(folderName + fileName)));
                    Vector<String> buffer = new Vector<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        DebugMessage.set(line);
                        buffer.add(line);
                    }
                    FileOperation.writeLines(folderName + fileName,buffer);
                } else if (fileName.endsWith(".png")) {
                    FileOperation.saveBitmapAsPNG(BitmapFactory.decodeStream(getAssets().open(folderName + fileName)), folderName + fileName);
                } else if (fileName.endsWith(".jpg")) {
                    FileOperation.saveBitmapAsJPG(BitmapFactory.decodeStream(getAssets().open(folderName + fileName)), folderName + fileName);
                }
            }catch (IOException e){
                DebugMessage.printStackTrace(e);
            }
        }
    }
}
