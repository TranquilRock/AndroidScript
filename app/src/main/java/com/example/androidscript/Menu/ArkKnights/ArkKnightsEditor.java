package com.example.androidscript.Menu.ArkKnights;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
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

public class ArkKnightsEditor extends AppCompatActivity implements Editor {
    SwitchCompat TillEmpty;
    SwitchCompat EatMedicine;
    SwitchCompat EatStone;
    Button SetScript;
    EditText Repeat;
    private boolean isTillEmpty = false;
    private boolean isEatMedicine = false;
    private boolean isEatStone = false;
    private String SelectedScript;
    private int nRepetition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ark_knights_editor);
        Repeat = findViewById(R.id.Repetition);
        TillEmpty = findViewById(R.id.tillEmpty);
        TillEmpty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Repeat.setVisibility(View.GONE);
                } else {
                    Repeat.setVisibility(View.VISIBLE);
                }
            }
        });
        EatMedicine = findViewById(R.id.EatMedicine);
        EatStone = findViewById(R.id.EatStone);
        BtnMaker.registerOnClick(R.id.set_service, this, v -> {
            startActivity(new Intent(this, StartService.class));
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
            FloatingWidgetService.setScript(new ArkKnightsInterpreter(SelectedScript), new String[]{String.valueOf(nRepetition)});
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
        return null;
    }
}