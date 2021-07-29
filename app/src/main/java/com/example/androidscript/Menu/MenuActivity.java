package com.example.androidscript.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import com.example.androidscript.FloatingWidget.FloatingWidgetService;
import com.example.androidscript.R;
import com.example.androidscript.util.*;
import com.example.androidscript.Menu.ArkKnights.ArkKnightsInterpreter;
import com.example.androidscript.util.Interpreter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;
// {getExternalFilesDir(null).getAbsolutePath() + "/}This would lead to an invisible dir (from studio)

public class MenuActivity extends AppCompatActivity {
    public static final String SUPPORTED_FILE_NAME_PATTERN = "([A-Za-z0-9_-]*).txt";
    private MediaProjectionManager mediaProjectionManager;

    private EditText etNewName;
    private TextView output;
    private Vector<String> availableFile;
    public static final int PROJECTION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        FileOperation.setUpFileRoot(getFilesDir().getAbsolutePath() + "/");
        this.availableFile = FileOperation.browseAvailableFile("ArkKnights/",".txt");
        setupElements();
        SetUpPermissions();
        Interpreter gg = new ArkKnightsInterpreter();
        gg.Interpret("AutoFightEat.txt");
        FloatingWidgetService.setScript(gg);
    }

    private void setupElements() {
        etNewName = findViewById(R.id.et_New_Name);
        output = findViewById(R.id.output);//Show some massage to user
        BtnMaker.performIntentForResult(R.id.btn_To_Load, this, pickFileIntent(), 111);
        BtnMaker.registerOnClick(R.id.btn_To_Create, this, (v -> {
            String FileName = etNewName.getText().toString();
            if (!FileName.equals("")) {
                switchToEdit(FileName);
            } else {
                output.setText("必須輸入檔名");
            }
        }));
        SpnMaker.fromString(R.id.spinner_Select_Script, this, availableFile);

        //Set up media projection button
        findViewById(R.id.btn_Start_Service).setOnClickListener(v -> {
            mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult((mediaProjectionManager).createScreenCaptureIntent(), PROJECTION_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            String FileName = data.getDataString();
            File g = new File(FileName);
            if(g.canRead()){
                DebugMessage.set("Good");
            }
            output.setText(FileName);
            switchToEdit(FileName);
        } else if (requestCode == PROJECTION_REQUEST_CODE) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ScreenShot.setUpMediaProjectionManager((Intent) data, mediaProjectionManager, true);
                    startService(new Intent(getApplicationContext(), ScreenShot.class));
                }
            }, 1);
        }
    }

    protected void switchToEdit(String FileName) {
        String[] tmp = FileName.split("/");
        FileName = tmp[tmp.length - 1];
        output.setText(FileName);
        if (parseFile(FileName)) {
            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra("FileName", FileName);
            startActivity(intent);
        }
        output.setText("僅能包含英文字母、數字與底線\n且為txt檔案格式\n例如:a_1-B.txt");
    }

    private static Intent pickFileIntent() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("file/*");
        return Intent.createChooser(chooseFile, "Choose a file");
    }

    protected boolean parseFile(String FileName) {
        return Pattern.matches(SUPPORTED_FILE_NAME_PATTERN, FileName);
    }


    public void SetUpPermissions() {
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
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            List<String> requestedPermissions = new ArrayList<>();
            requestedPermissions.add(Manifest.permission.FOREGROUND_SERVICE);//Stub to add more permissions.
            String[] requests = new String[requestedPermissions.size()];
            requestPermissions(requests, 100);
        }
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult((mediaProjectionManager).createScreenCaptureIntent(), PROJECTION_REQUEST_CODE);
    }
}
