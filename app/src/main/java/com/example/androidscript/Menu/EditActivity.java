package com.example.androidscript.Menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.androidscript.R;

public class EditActivity extends AppCompatActivity {
    private TextView output;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        output = (TextView) findViewById(R.id.edit_output);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MenuActivity.EXTRA_MESSAGE);
        output.setText(message);
    }
}