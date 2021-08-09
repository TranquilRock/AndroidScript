package com.example.androidscript.UserInterface;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.Menu.FGO.FGOBlockAdapter;
import com.example.androidscript.Menu.FGO.FGOEditor;
import com.example.androidscript.R;
import com.example.androidscript.util.ScreenShot;

import java.util.Vector;

public abstract class UIActivity extends Editor {

    protected RecyclerView BlockView;
    protected RecyclerView ButtonView;
    protected Vector<Vector<String>> BlockData;
    protected Vector<String> ButtonData;
    protected String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.filename = getIntent().getStringExtra("FileName");
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_uiactivity);
        this.ButtonView = findViewById(R.id.buttongrid);
        this.BlockView = findViewById(R.id.recycleview);
        this.BlockData = new Vector<>();
        this.ButtonData= new Vector<>();
    }
}