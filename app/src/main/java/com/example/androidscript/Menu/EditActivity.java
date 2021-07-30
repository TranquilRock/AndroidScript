package com.example.androidscript.Menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.androidscript.R;
import com.example.androidscript.util.DebugMessage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
@Deprecated
public class EditActivity extends AppCompatActivity {
    private TextView output;
    protected String FileName;
    protected String folder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.folder = "AndroidScript/";

        setContentView(R.layout.activity_edit);
        output = (TextView) findViewById(R.id.edit_output);

        Intent intent = getIntent();//Get string passing by intent
        String FileName = intent.getStringExtra("FileName");
        this.FileName = folder + FileName;
        setOutput(this.FileName);
        writeScript();
        readScript();
    }

    protected void readScript() {
        try {
            File scriptFile = new File(FileName);
            if (scriptFile.exists()) {
                Scanner myReader = new Scanner(scriptFile);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    DebugMessage.set(data);
                }
                myReader.close();
            }
        } catch (IOException e) {
            DebugMessage.printStackTrace(e);
        }
    }

    protected void writeScript() {
        try {
            File scriptFile = new File(FileName);
            DebugMessage.set(FileName);
            if (!scriptFile.exists() || Objects.requireNonNull(scriptFile.getParentFile()).mkdir()) {
                scriptFile.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(this.FileName, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("Test Write Message");
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            DebugMessage.printStackTrace(e);
        }
    }

    private void setOutput(String out) {
        output.setText(out);
    }
}