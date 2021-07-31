package com.example.androidscript.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.content.Intent;
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
    public static final String SUPPORTED_FILE_NAME_PATTERN = "([A-Za-z0-9_-]*).txt";
    private EditText etNewName;
    private TextView output;
    private Spinner select;
    private Vector<String> availableFile;
//    public static final int SELECT_FILE_CODE  = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);
        this.availableFile = FileOperation.browseAvailableFile("ArkKnights/",".txt");
        setupElements();
    }

    private void setupElements() {
        etNewName = findViewById(R.id.et_New_Name);
        output = findViewById(R.id.output);//Show some massage to user
        select = SpnMaker.fromString(R.id.spinner_Select_Script, this, availableFile);

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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
////        if (requestCode == SELECT_FILE_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
////            String FileName = data.getDataString();
////            File g = new File(FileName);
////            if(g.canRead()){
////                DebugMessage.set("Can read file.");
////            }
////            output.setText(FileName);
////            switchToEdit(FileName);
////        }
//    }

    protected void switchToEdit(String FileName){
        String[] tmp = FileName.split("/");
        FileName = tmp[tmp.length - 1];
        output.setText(FileName);
        if (checkFilename(FileName)) {
            try{
                Intent intent = new Intent(this,  Class.forName(getIntent().getStringExtra("next_destination")));
                intent.putExtra("FileName", FileName);
                startActivity(intent);
            }
            catch (ClassNotFoundException e){
                DebugMessage.printStackTrace(e);
            }
        }
        else{
            output.setText("僅能包含英文字母、數字與底線\n且為txt檔案格式\n例如:a_1-B.txt");
        }
    }

//    private static Intent pickFileIntent() {
//        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
//        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
//        chooseFile.setType("file/*");
//        return Intent.createChooser(chooseFile, "Choose a file");
//    }

    protected boolean checkFilename(String FileName) {
        return Pattern.matches(SUPPORTED_FILE_NAME_PATTERN, FileName);
    }
}
