package com.example.androidscript.UserInterface;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.R;

import java.util.ArrayList;
import java.util.Vector;

public abstract class UIActivity extends AppCompatActivity implements Editor{

    protected abstract ArrayList<String> getBlockData();

    protected abstract ArrayList<String> getButtonData();

    protected RecyclerView BlockView;
    protected RecyclerView ButtonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_uiactivity);
        this.setRecycleBlock(getBlockData());
        this.setRecycleButton(getButtonData());
    }

    protected void setRecycleButton(ArrayList<String> data){
        ButtonView = findViewById(R.id.buttongrid);
        ButtonView.setLayoutManager(new GridLayoutManager(this, 5));
        ButtonView.setAdapter(new ButtonAdapter(data));
    }

    protected void setRecycleBlock(ArrayList<String> data){
        BlockView = findViewById(R.id.recycleview);
        BlockView.setLayoutManager(new LinearLayoutManager(this));
        BlockView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        BlockView.setAdapter(new BlockAdapter(data));
    }
}