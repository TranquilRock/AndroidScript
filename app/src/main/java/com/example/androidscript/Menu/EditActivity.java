package com.example.androidscript.Menu;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.androidscript.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class EditActivity extends AppCompatActivity {
    private TextView output;
    protected String FileName;
    protected String folderPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.folderPath = getFilesDir().getAbsolutePath() + "/AndroidScript/";//Environment.getExternalStorageDirectory()
//        this.folderPath = "/sdcard/Android/media/AndroidScript/";//Environment.getExternalStorageDirectory()

        setContentView(R.layout.activity_edit);
        output = (TextView) findViewById(R.id.edit_output);

        Intent intent = getIntent();
        String FileName = intent.getStringExtra(MenuActivity.EXTRA_MESSAGE);
        this.FileName = folderPath + FileName;
        setOutput(this.FileName);
        setPermission();
        setDir();
        writeScript();
        readScript();
    }

    public void setPermission() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.FOREGROUND_SERVICE
        };
        requestPermissions(permissions, 100);
    }

    public void setDir() {
        File folder = new File(folderPath);
        System.out.println("SetDir\n");
        if (!folder.exists()) {
            folder.mkdir();
            System.out.println("SetDirSuccess\n");
        }
    }

    protected void readScript() {
        try {
            File scriptFile = new File(FileName);
            if (scriptFile.exists()) {
                Scanner myReader = new Scanner(scriptFile);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    System.out.println(data);
                }
                myReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void writeScript() {
        try {
            File scriptFile = new File(FileName);
            if (!scriptFile.exists()) {
//                scriptFile.getParentFile().mkdirs();
                System.out.println(FileName);
                scriptFile.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(this.FileName, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("Test Write Message");
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setOutput(String out) {
        output.setText(out);
    }
}