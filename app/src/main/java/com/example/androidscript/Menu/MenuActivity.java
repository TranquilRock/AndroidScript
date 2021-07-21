package com.example.androidscript.Menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.DataSetObserver;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TextView;

import com.example.androidscript.R;
import com.example.androidscript.util.*;

import org.jetbrains.annotations.NotNull;

import java.util.Vector;
import java.util.regex.Pattern;

public class MenuActivity extends AppCompatActivity {
    public static final String SUPPORTED_FILE_NAME_PATTERN = "([A-Za-z0-9_-]*).txt";
    public static final String EXTRA_MESSAGE = "com.example.androidscript.Menu";
    private String root;
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
        root = getFilesDir().getAbsolutePath();
        FileOperation.setUpFileRoot(root);
        browseAvailableFile();
//        testing = findViewById(R.id.testing);
//        ArrayAdapter<String> gg = (new ArrayAdapter<String>(this, android.R.layout.list_content,availableFile);
//        testing.setAdapter(gg);
        setupElements();
    }

    private void setupElements() {
        //
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
            output.setText(FileName);
            switchToEdit(FileName);
        } else if (requestCode == PROJECTION_REQUEST_CODE) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ScreenShot.pass((Intent) data, mediaProjectionManager);
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
            intent.putExtra(EXTRA_MESSAGE, FileName);
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
        //TODO complete here
        this.availableFile.add("GG1.txt");
        this.availableFile.add("GG2.txt");
        this.availableFile.add("GG3.txt");
    }
}