package com.example.androidscript.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.androidscript.R;
import com.example.androidscript.util.BtnMaker;

import static android.content.ContentValues.TAG;

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
        btnToLoad = BtnMaker.performIntent(R.id.btn_To_Load,this, Intent.ACTION_GET_CONTENT);
        output = (TextView) findViewById(R.id.output);
        btnToCreate = (Button)(this.findViewById(R.id.btn_To_Create));
        btnToCreate.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etNewName.getText() != null && !etNewName.getText().equals("")){
                    switchToEdit(etNewName.getText().toString());
                    //Also Check if exist or invalid
                }
                else{
                    output.setText("必須輸入檔名");
                }
            }
        }));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data.getData()!= null){
            String FileName = data.getDataString();
            output.setText(FileName);
            switchToEdit(FileName);
        }
    }
    protected void switchToEdit(String FileName){
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EXTRA_MESSAGE, FileName);
        startActivity(intent);
    }
}