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
import com.example.androidscript.util.Interpreter;

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
            startActivity(new Intent(this, StartService.class).putExtra("Orientation","Landscape"));
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
            FloatingWidgetService.setScript(new Interpreter(FolderName,SelectedScript), new String[]{String.valueOf(nRepetition)});
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
        try{
            String[] allFiles = getAssets().list("");
            for(String file : allFiles){
                if(file.startsWith("Ark_")){
                    getResource(file,FolderName,file.substring(4));
                }
            }
            DebugMessage.set("Good");
        }catch (Exception e){
            DebugMessage.printStackTrace(e);
            DebugMessage.set("Dead");
        }
    }
}