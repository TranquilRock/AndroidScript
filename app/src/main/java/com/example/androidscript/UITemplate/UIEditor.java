package com.example.androidscript.UITemplate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.FloatingWidget.FloatingWidgetService;
import com.example.androidscript.Activities.StartService;
import com.example.androidscript.R;

import java.util.Vector;

public abstract class UIEditor extends Editor {

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

    protected void startServiceHandler(String orientation){
        if(!FloatingWidgetService.ScriptSet()){
            Toast.makeText(this.getApplicationContext(), "No script loaded!", Toast.LENGTH_LONG).show();
        }
        else if(!StartService.IsAuthorized(this)){
            this.startActivity(new Intent(this, StartService.class).putExtra("Orientation", orientation));
        }else{
            StartService.StartFloatingWidget(this);
        }
    }
}