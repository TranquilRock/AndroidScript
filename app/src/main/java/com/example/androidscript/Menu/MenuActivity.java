package com.example.androidscript.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import com.example.androidscript.R;
import com.example.androidscript.util.BtnMaker;
import com.example.androidscript.util.ScreenShot;
import com.example.androidscript.util.SaveImg;

import java.util.regex.Pattern;

public class MenuActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.androidscript.Menu";
    private Button btnToCreate;
    private Button btnToLoad;
    private EditText etNewName;
    private TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        etNewName = (EditText) findViewById(R.id.et_New_Name);
        btnToLoad = BtnMaker.performIntent(R.id.btn_To_Load, this, Intent.ACTION_GET_CONTENT);
        output = (TextView) findViewById(R.id.output);

        btnToCreate = (Button) (this.findViewById(R.id.btn_To_Create));
        btnToCreate.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FileName = etNewName.getText().toString();
                if (FileName != null && !FileName.equals("")) {
                    switchToEdit(FileName);
                } else {
                    output.setText("必須輸入檔名");
                }
            }
        }));
        SaveImg.bitmap(ScreenShot.instance.Shot(),"Image");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data.getData() != null) {
            String FileName = data.getDataString();
            output.setText(FileName);
            switchToEdit(FileName);
        }
    }

    protected boolean switchToEdit(String FileName) {
        String[] tmp = FileName.split("/");
        FileName = tmp[tmp.length - 1];
        output.setText(FileName);
        if (parseFile(FileName)) {
            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra(EXTRA_MESSAGE, FileName);
            startActivity(intent);
            return true;
        }
        output.setText("僅能包含英文字母、數字與底線\n        且為txt檔案格式\n       例如:a_1-B.txt");
        return false;
    }

    public static final String SUPPORTED_FILE_NAME_PATTERN = "([A-Za-z0-9_-]*).txt";

    protected boolean parseFile(String path) {
        return Pattern.matches(SUPPORTED_FILE_NAME_PATTERN, path);
    }
}