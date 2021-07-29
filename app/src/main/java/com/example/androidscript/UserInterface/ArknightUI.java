package com.example.androidscript.UserInterface;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidscript.FloatingWidget.FloatingWidgetService;
import com.example.androidscript.R;
import com.example.androidscript.util.Interpreter.ArkKnightsInterpreter;
import com.example.androidscript.util.Interpreter.Interpreter;

public class ArknightUI extends AppCompatActivity {
    SwitchCompat TillEmpty;
    SwitchCompat EatMedicine;
    SwitchCompat EatStone;
    Button SetScript;
    EditText Repeat;
    private boolean isTillEmpty = false;
    private boolean isEatMedicine = false;
    private boolean isEatStone = false;
    private String SelectedScript;
    private int RepeatNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arknight_ui);
        Init();
        SetScript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    GetRepeatNumber();
                    SelectedScript = "AutoFight.txt";
                    Toast.makeText(getApplicationContext(), String.valueOf(RepeatNumber), Toast.LENGTH_SHORT).show();
                }
                /*
                Interpreter Do = new ArkKnightsInterpreter();
                Do.Interpret(SelectedScript);
                FloatingWidgetService.setScript(Do);*/
            }
        });



    }

    private void Init(){
        Repeat = (EditText) findViewById(R.id.RepeatNumber);
        TillEmpty = (SwitchCompat) findViewById(R.id.tillEmpty);
        EatMedicine = (SwitchCompat) findViewById(R.id.EatMedicine);
        EatStone = (SwitchCompat) findViewById(R.id.EatStone);
        SetScript = (Button) findViewById(R.id.set_script);
    }

    private void GetRepeatNumber(){
        String tmp = Repeat.getText().toString();
        try {
            RepeatNumber = Integer.parseInt(tmp);
        }catch (NumberFormatException e){
            RepeatNumber = 0;
        }
    }

    private void CheckState(){
        isTillEmpty = TillEmpty.isChecked();
        isEatMedicine = EatMedicine.isChecked();
        isEatStone = EatStone.isChecked();
    }


}