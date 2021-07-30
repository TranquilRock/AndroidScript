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

public abstract class UIActivity extends AppCompatActivity implements Editor {

    protected abstract Vector<String> getBlockData();

    protected abstract Vector<String> getButtonData();

    protected abstract void setRecycleBlock();

    protected abstract void setRecycleButton();

    protected RecyclerView BlockView;
    protected RecyclerView ButtonView;
    protected Vector<String> BlockData = getBlockData();
    protected Vector<String> ButtonData = getButtonData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_uiactivity);
        this.BlockData = getBlockData();
        this.ButtonData = getButtonData();
        this.ButtonView = findViewById(R.id.buttongrid);
        this.BlockView = findViewById(R.id.recycleview);
        this.setRecycleBlock();
        this.setRecycleButton();
    }

}