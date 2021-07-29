package com.example.androidscript.UserInterface;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.Menu.FGO.FGOBlockAdapter;
import com.example.androidscript.Menu.FGO.FGOButtonAdapter;
import com.example.androidscript.R;

import java.util.ArrayList;
import java.util.Vector;

public abstract class UIActivity extends AppCompatActivity implements Editor{

    protected abstract Vector<String> getBlockData();

    protected abstract Vector<String> getButtonData();

    protected RecyclerView BlockView;
    protected RecyclerView ButtonView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_uiactivity);
        Vector<String> BlockData = getBlockData();
        Vector<String> ButtonData = getButtonData();

        this.setRecycleBlock(BlockData);
        this.setRecycleButton(BlockData, ButtonData);
    }

    protected void setRecycleButton(Vector<String> BlockData,Vector<String> ButtonData){
        ButtonView = findViewById(R.id.buttongrid);
        ButtonView.setLayoutManager(new GridLayoutManager(this, 5));
        ButtonView.setAdapter(new FGOButtonAdapter(BlockData,ButtonData));
    }

    protected void setRecycleBlock(Vector<String> data){
        BlockView = findViewById(R.id.recycleview);
        BlockView.setLayoutManager(new LinearLayoutManager(this));
        BlockView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        BlockView.setAdapter(new FGOBlockAdapter(data));
    }
}