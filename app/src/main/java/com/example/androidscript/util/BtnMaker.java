package com.example.androidscript.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import java.util.function.Function;

public class BtnMaker {
    public static Button jump(int id, AppCompatActivity from, Class to){
        Button btn = (Button)(from.findViewById(id));
        btn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoClick.autoClickPos(620, 1400, 620, 500);
                Intent intent = new Intent(from, to);
                from.startActivity(intent);
            }
        }));
        return btn;
    }
    public static Button performIntent(int id,AppCompatActivity from ,String command){
        Button btn = (Button)(from.findViewById(id));
        btn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(command);
                from.startActivityForResult(intent,0);
            }
        }));
        return btn;
    }
    public static Button doAction(int id, AppCompatActivity from){
        Button btn = (Button)(from.findViewById(id));
        btn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }));
        return btn;
    }
}
