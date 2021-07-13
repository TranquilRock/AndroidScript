package com.example.androidscript.Test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.androidscript.R;

import com.example.androidscript.util.BtnMaker;
import com.example.androidscript.Menu.MenuActivity;
public class TmpMenu extends AppCompatActivity {
    private Button btnToMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp_menu);
        btnToMenu = BtnMaker.jump(R.id.button_to_menu,this,MenuActivity.class);
    }
}