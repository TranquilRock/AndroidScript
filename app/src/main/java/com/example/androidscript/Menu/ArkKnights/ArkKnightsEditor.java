package com.example.androidscript.Menu.ArkKnights;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

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
                SelectedScript = "AutoFightEatBoth.txt";
            } else if (isEatMedicine) {
                SelectedScript = "AutoFightEatMedicine.txt";
            } else {
                SelectedScript = "AutoFight.txt";
            }
            FloatingWidgetService.setScript(new Interpreter(FolderName, SelectedScript), new String[]{String.valueOf(nRepetition)});
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
        writeEatMedicine();
        writeEatStone();
        writePressEnd();
        writePressEnter();
        writePressStart();
    }

    private static final int Dev_Width = 3040;
    private static final int Dev_Height = 1440;

    private void writePressStart() {
        Vector<String> buffer = new Vector<>();
        buffer.add("CallArg Check.txt StartOperation.png");
        buffer.add("IfGreater $R 0");
        buffer.add("Return 1");
        buffer.add("ClickPic StartOperation.png");
        buffer.add("Return 0");
        FileOperation.writeLines(FolderName + "PressStart.txt", buffer);
    }

    private void writePressEnter() {
        Vector<String> buffer = new Vector<>();
        buffer.add("CallArg Check.txt EnterOperation.png");
        buffer.add("IfGreater $R 0");
        buffer.add("Return 1");
        buffer.add("ClickPic PressEnterOperation.png" );
        buffer.add("Wait 500");
        buffer.add("Return 0");
        FileOperation.writeLines(FolderName + "PressEnter.txt", buffer);
    }

    private void writePressEnd() {
        Vector<String> buffer = new Vector<>();
        buffer.add("Wait 3000");
        buffer.add("CallArg Check.txt OperationEnd.png");
        buffer.add("IfGreater $R 0");
        buffer.add("Return 1");
        buffer.add("Wait 1000");
        buffer.add("ClickPic OperationEnd.png");
        buffer.add("Return 0");
        FileOperation.writeLines(FolderName + "PressEnd.txt", buffer);
    }

    private void writeEatStone() {
        Vector<String> buffer = new Vector<>();
        buffer.add("CallArg Check.txt RestoreSanityStone.png");
        buffer.add("IfGreater $R 0");
        buffer.add("Return 1");
        buffer.add("ClickPic PressRestore.png");
        buffer.add("Call PressEnter.txt");
        buffer.add("IfGreater $R 0");
        buffer.add("JumpTo 4");
        buffer.add("Return 0");
        FileOperation.writeLines(FolderName + "EatStone.txt", buffer);
    }

    private void writeEatMedicine() {
        Vector<String> buffer = new Vector<>();
        buffer.add("CallArg Check.txt RestoreSanityMedicine.png");
        buffer.add("IfGreater $R 0");
        buffer.add("Return 1");
        buffer.add("ClickPic PressRestore.png");
        buffer.add("Call PressEnter.txt");
        buffer.add("IfGreater $R 0");
        buffer.add("JumpTo 4");
        buffer.add("Return 0");
        FileOperation.writeLines(FolderName + "EatMedicine.txt", buffer);
    }

    private String transform(int x, int y) {
        int w = max(ScreenShot.getWidth(),ScreenShot.getHeight());
        int h = min(ScreenShot.getWidth(),ScreenShot.getHeight());
        return (int) ((double) x / Dev_Width * w) + " " + (int) ((double) y / Dev_Height * h);
    }
}