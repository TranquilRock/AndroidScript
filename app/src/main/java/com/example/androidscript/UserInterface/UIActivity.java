package com.example.androidscript.UserInterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.Menu.FGO.FGOBlockAdapter;
import com.example.androidscript.Menu.FGO.FGOButtonAdapter;
import com.example.androidscript.Menu.StartService;
import com.example.androidscript.R;
import com.example.androidscript.util.BtnMaker;
import com.example.androidscript.util.DebugMessage;

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
        BtnMaker.registerOnClick(R.id.start_service, this, v -> startActivity(new Intent(this, StartService.class)));
        BtnMaker.registerOnClick(R.id.start_floating, this, v -> StartService.startFloatingWidget(this));

        this.setRecycleBlock();
        this.setRecycleButton();
        DebugMessage.set("Still Alive");
    }

}