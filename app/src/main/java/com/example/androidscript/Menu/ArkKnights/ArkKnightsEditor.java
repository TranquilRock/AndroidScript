package com.example.androidscript.Menu.ArkKnights;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.androidscript.FloatingWidget.FloatingWidgetService;
import com.example.androidscript.Menu.StartService;
import com.example.androidscript.R;
import com.example.androidscript.UserInterface.Editor;
import com.example.androidscript.util.BtnMaker;
import com.example.androidscript.util.DebugMessage;
import com.example.androidscript.util.FileOperation;
import com.example.androidscript.util.Interpreter;
import com.example.androidscript.util.ScreenShot;

import java.util.ArrayList;
import java.util.Vector;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class ArkKnightsEditor extends Editor {
    SwitchCompat TillEmpty;
    SwitchCompat EatMedicine;
    SwitchCompat EatStone;
    EditText Repeat;
    private boolean isTillEmpty = false;
    private boolean isEatMedicine = false;
    private boolean isEatStone = false;
    private String SelectedScript;
    private int nRepetition;
    public static final String FolderName = "ArkKnights/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ark_knights_editor);
        Repeat = findViewById(R.id.Repetition);
        TillEmpty = findViewById(R.id.tillEmpty);
        TillEmpty.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Repeat.setVisibility(View.GONE);
            } else {
                Repeat.setVisibility(View.VISIBLE);
            }
        });
        EatMedicine = findViewById(R.id.EatMedicine);
        EatStone = findViewById(R.id.EatStone);
        BtnMaker.registerOnClick(R.id.set_service, this, v -> {
            startActivity(new Intent(this, StartService.class).putExtra("Orientation", "Landscape"));
        });
        BtnMaker.registerOnClick(R.id.set_script, this, v -> {
            CheckState();
            GetRepetition();
            if (isEatStone) {
                FloatingWidgetService.setScript(FolderName, "AutoFightEat.txt", new String[]{String.valueOf(nRepetition), "PressRestore.png"});
            } else if (isEatMedicine) {
                FloatingWidgetService.setScript(FolderName, "AutoFightEat.txt", new String[]{String.valueOf(nRepetition), "PressRestoreMedicine.png"});
            } else {
                FloatingWidgetService.setScript(FolderName, "AutoFight.txt", new String[]{String.valueOf(nRepetition)});
            }
            StartService.startFloatingWidget(this);
        });
    }

    private void GetRepetition() {
        if (isTillEmpty) {
            nRepetition = 100000;
        } else {
            try {
                nRepetition = Integer.parseInt(Repeat.getText().toString());
            } catch (NumberFormatException e) {
                nRepetition = 0;
            }
        }
    }

    private void CheckState() {
        isTillEmpty = TillEmpty.isChecked();
        isEatMedicine = EatMedicine.isChecked();
        isEatStone = EatStone.isChecked();
    }

    @Override
    public String getFolderName() {
        return FolderName;
    }

    double resizeRatio;

    @Override
    protected void resourceInitialize() {
        try {
            String[] allFiles = getAssets().list("");//List all file
            for (String file : allFiles) {
                if (file.startsWith("Ark_")) {
                    getResource(file, FolderName, file.substring(4));
                }
            }
        } catch (Exception e) {
            DebugMessage.printStackTrace(e);
        }
        resizeRatio = min(ScreenShot.getHeight(), ScreenShot.getWidth()) / 1152.0;//ArkKnights seems to be height dominate.
        // resizeRatio = ScreenShot.getWidth() / 2432.0;
        writePress();
        writeTryPress();
    }

    private void writePress() {
        Vector<String> buffer = new Vector<>();
        buffer.add("Tag $Start");
        buffer.add("ClickPic $1 " + resizeRatio);
        buffer.add("Wait $2");
        buffer.add("IfGreater $R 0");
        buffer.add("JumpTo $Start");
        FileOperation.writeLines(FolderName + "Press.txt", buffer);
    }

    private void writeTryPress() {
        Vector<String> buffer = new Vector<>();
        buffer.add("ClickPic $1 " + resizeRatio);
        buffer.add("Return $R");
        FileOperation.writeLines(FolderName + "TryPress.txt", buffer);
    }
}