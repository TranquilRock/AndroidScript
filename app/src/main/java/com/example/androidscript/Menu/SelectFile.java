package com.example.androidscript.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.androidscript.R;
import com.example.androidscript.util.*;

import java.io.File;
import java.util.Vector;
import java.util.regex.Pattern;
// {getExternalFilesDir(null).getAbsolutePath() + "/}This would lead to an invisible dir (from studio)

public class SelectFile extends AppCompatActivity {
    public static final String SUPPORTED_FILE_NAME_PATTERN = "([A-Za-z0-9_-]*).blc";
    private EditText etNewName;
    private TextView output;
    private Spinner select;
    private Vector<String> availableFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);
        String[] classPath = getIntent().getStringExtra("next_destination").split("\\.");
        DebugMessage.set(classPath[classPath.length - 2] + "/");
        try {
            this.availableFile = FileOperation.browseAvailableFile(classPath[classPath.length - 2] + "/", ".blc");
        } catch (Exception e) {
            DebugMessage.printStackTrace(e);
            this.availableFile = new Vector<>();
            this.availableFile.add("Empty");
        }
        setupElements();
    }

    private void setupElements() {
        etNewName = findViewById(R.id.et_New_Name);
        output = findViewById(R.id.output);//Show some massage to user
        select = SpnMaker.fromStringWithActivity(R.id.spinner_Select_Script, this, availableFile);

        BtnMaker.registerOnClick(R.id.btn_To_Load, this, (v -> {
            String FileName = select.getSelectedItem().toString();
            if (!FileName.equals("")) {
                switchToEdit(FileName);
            } else {
                output.setText("必須輸入檔名");
            }
        }));

        BtnMaker.registerOnClick(R.id.btn_To_Create, this, (v -> {
            String FileName = etNewName.getText().toString();
            if (!FileName.equals("")) {
                switchToEdit(FileName);
            } else {
                output.setText("必須輸入檔名");
            }
        }));
    }

    protected void switchToEdit(String FileName) {
        String[] tmp = FileName.split("/");
        FileName = tmp[tmp.length - 1];
        output.setText(FileName);
        if (checkFilename(FileName)) {
            try {
                Intent intent = new Intent(this, Class.forName(getIntent().getStringExtra("next_destination")));
                intent.putExtra("FileName", FileName);
                String orientation = getIntent().getStringExtra("Orientation");
                if(orientation != null){
                    intent.putExtra("Orientation",orientation);
                }
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                DebugMessage.printStackTrace(e);
            }
        } else {
            output.setText("僅能包含英文字母、數字與底線\n且為txt檔案格式\n例如:a_1-B.blc");
        }
    }
    protected boolean checkFilename(String FileName) {
        return Pattern.matches(SUPPORTED_FILE_NAME_PATTERN, FileName) && (FileName.length() > 4);
    }
}
