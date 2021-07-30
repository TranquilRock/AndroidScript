package com.example.androidscript.Menu.ArkKnights;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidscript.FloatingWidget.FloatingWidgetService;
import com.example.androidscript.Menu.StartService;
import com.example.androidscript.R;
import com.example.androidscript.UserInterface.Editor;
import com.example.androidscript.util.DebugMessage;
import com.example.androidscript.util.Interpreter;

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
            GetRepetition();
            if(isEatStone){
                SelectedScript = "AutoFightEatBoth.txt";
                Toast.makeText(getApplicationContext() ,"EatBoth" , Toast.LENGTH_SHORT).show();
            }else if (isEatMedicine){
                SelectedScript = "AutoFightEatMedicine.txt";
                Toast.makeText(getApplicationContext(), "EatMedicine", Toast.LENGTH_SHORT).show();
            }else{
                SelectedScript = "AutoFight.txt";
                Toast.makeText(getApplicationContext(), "Do", Toast.LENGTH_SHORT).show();
            }
            String[] Argv = {String.valueOf(nRepetition)};
            Interpreter Script = new ArkKnightsInterpreter();
            Script.Interpret(SelectedScript);
            FloatingWidgetService.setScript(Script,Argv);
            startActivity(new Intent(this, StartService.class));
        });
    }

    private void GetRepetition(){
        if(isTillEmpty){
            nRepetition = 100000;
        }else{
            try {
                nRepetition = Integer.parseInt(Repeat.getText().toString());
            }catch (NumberFormatException e){

                nRepetition = 0;
            }
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