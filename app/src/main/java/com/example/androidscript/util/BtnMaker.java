package com.example.androidscript.util;


import android.view.View;
import android.widget.Button;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidscript.Menu.MenuActivity;


public final class BtnMaker {
    public static void jump(int id, AppCompatActivity from, Class<?> to) {
        Button btn = from.findViewById(id);
        btn.setOnClickListener((v -> {
            Intent intent = new Intent(from, to);
            from.startActivity(intent);
            intent.putExtra("a", MenuActivity.class);
        }));
    }

    public static void jumpWithMessage(int id, AppCompatActivity from, Class<?> to,String name,String message) {
        Button btn = from.findViewById(id);
        btn.setOnClickListener((v -> {
            Intent intent = new Intent(from, to);
            intent.putExtra(name, message);
            from.startActivity(intent);
        }));
    }

    public static void performIntentForResult(int id, AppCompatActivity from, Intent intent,int requestCode) {
        Button btn = from.findViewById(id);
        btn.setOnClickListener((v -> {
            from.startActivityForResult(intent, requestCode);
        }));
    }

    public static void registerOnClick(int id, AppCompatActivity from,View.OnClickListener listener) {
        Button btn = from.findViewById(id);
        btn.setOnClickListener(listener);
    }
}
