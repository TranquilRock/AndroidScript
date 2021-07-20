package com.example.androidscript.util;

import android.view.View;
import android.widget.Button;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;


public class BtnMaker {
    public static Button jump(int id, AppCompatActivity from, Class to) {
        Button btn = from.findViewById(id);
        btn.setOnClickListener((v -> {
            Intent intent = new Intent(from, to);
            from.startActivity(intent);
        }));
        return btn;
    }

    public static Button performIntentForResult(int id, AppCompatActivity from, Intent intent,int requestCode) {
        Button btn = from.findViewById(id);
        btn.setOnClickListener((v -> {
            from.startActivityForResult(intent, requestCode);
        }));
        return btn;
    }

    public static Button registerOnClick(int id, AppCompatActivity from,View.OnClickListener listener) {
        Button btn = from.findViewById(id);
        btn.setOnClickListener(listener);
        return btn;
    }
}
