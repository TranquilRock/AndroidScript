package com.example.androidscript.Menu.ArkKnights;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidscript.R;
import com.example.androidscript.UserInterface.Editor;

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
        Repeat = findViewById(R.id.RepeatNumber);
        TillEmpty = findViewById(R.id.tillEmpty);
        EatMedicine = findViewById(R.id.EatMedicine);
        EatStone = findViewById(R.id.EatStone);
        SetScript = findViewById(R.id.set_script);
        SetScript.setOnClickListener(v -> {
            CheckState();
            if (isTillEmpty){
                if(isEatMedicine && isEatStone){
                    SelectedScript = "AutoFightEatBoth.txt";
                    Toast.makeText(getApplicationContext() ,"EatBoth" , Toast.LENGTH_SHORT).show();
                }else if (isEatMedicine){
                    SelectedScript = "AutoFightEat.txt";
                    Toast.makeText(getApplicationContext(), "EatMedicine", Toast.LENGTH_SHORT).show();
                }else if (isEatStone){
                    SelectedScript = "AutoFightEatStone.txt";
                    Toast.makeText(getApplicationContext(), "EatStone", Toast.LENGTH_SHORT).show();
                }else{
                    SelectedScript = "AutoFight.txt";
                    Toast.makeText(getApplicationContext(), "Do", Toast.LENGTH_SHORT).show();
                }
            }else {//TODO: wait for script for limited times
                GetRepetition();
                SelectedScript = "AutoFight.txt";
                Toast.makeText(getApplicationContext(), String.valueOf(nRepetition), Toast.LENGTH_SHORT).show();
            }
            /*
            Interpreter Do = new ArkKnightsInterpreter();
            Do.Interpret(SelectedScript);
            FloatingWidgetService.setScript(Do);*/
        });
    }

    private void GetRepetition(){
        String tmp = Repeat.getText().toString();
        try {
            nRepetition = Integer.parseInt(tmp);
        }catch (NumberFormatException e){
            nRepetition = 0;
        }
    }

    private void CheckState(){
        isTillEmpty = TillEmpty.isChecked();
        isEatMedicine = EatMedicine.isChecked();
        isEatStone = EatStone.isChecked();
    }

    @Override
    public String getFolderName() {
        return null;
    }
}