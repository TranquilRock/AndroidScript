package com.example.androidscript.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TextView;

import com.example.androidscript.FloatingWidget.FloatingWidgetService;
import com.example.androidscript.R;
import com.example.androidscript.util.*;
import com.example.androidscript.util.Interpreter.ArkKnightsInterpreter;
import com.example.androidscript.util.Interpreter.Interpreter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

public class MenuActivity extends AppCompatActivity {
    public static final String SUPPORTED_FILE_NAME_PATTERN = "([A-Za-z0-9_-]*).txt";
    private Button btnToCreate;
    private Button btnPickFile;
    private ImageButton btnStartService;
    private MediaProjectionManager mediaProjectionManager;

    private EditText etNewName;
    private TextView output;
    private Spinner selectScript;
    private Vector<String> availableFile = new Vector<>();
    public static final int PROJECTION_REQUEST_CODE = 123;
    private RecyclerView testing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        FileOperation.setUpFileRoot(getFilesDir().getAbsolutePath() + "/");
//        FileOperation.setUpFileRoot(getExternalFilesDir(null).getAbsolutePath() + "/"); This would lead to an invisible dir (from studio)
        browseAvailableFile();
        setupElements();
        SetUpPermissions();
        Interpreter gg = new ArkKnightsInterpreter();
        gg.Interpret("AutoFightEat.txt");
        FloatingWidgetService.setScript(gg);
    }

    private void setupElements() {
        etNewName = findViewById(R.id.et_New_Name);
        output = findViewById(R.id.output);//Show some massage to user
        btnPickFile = BtnMaker.performIntentForResult(R.id.btn_To_Load, this, pickFileIntent(), 111);
        btnToCreate = BtnMaker.registerOnClick(R.id.btn_To_Create, this, (v -> {
            String FileName = etNewName.getText().toString();
            if (!FileName.equals("")) {
                switchToEdit(FileName);
            } else {
                output.setText("必須輸入檔名");
            }
        }));
        selectScript = SpnMaker.fromString(R.id.spinner_Select_Script, this, availableFile);

        //Set up media projection button
        btnStartService = findViewById(R.id.btn_Start_Service);
        btnStartService.setOnClickListener(v -> {
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

    protected void browseAvailableFile() {
        for(String s : FileOperation.readDir("")){
            if(s.contains(".txt")){
                availableFile.add(s);
            }
        }
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
