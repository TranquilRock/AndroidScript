package com.example.androidscript.UserInterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.Menu.FGO.FGOBlockAdapter;
import com.example.androidscript.Menu.FGO.FGOButtonAdapter;
import com.example.androidscript.Menu.FGO.FGOEditor;
import com.example.androidscript.Menu.MenuActivity;
import com.example.androidscript.Menu.StartService;
import com.example.androidscript.R;
import com.example.androidscript.util.BtnMaker;
import com.example.androidscript.util.DebugMessage;
import com.example.androidscript.util.FileOperation;
import com.example.androidscript.util.ScreenShot;
import com.example.androidscript.util.SpnMaker;

import java.util.ArrayList;
import java.util.Vector;

public abstract class UIActivity extends AppCompatActivity implements Editor {

    protected abstract Vector<Vector<String>> getBlockData();

    protected abstract Vector<String> getButtonData();

    protected abstract void setRecycleBlock();

    protected abstract void setRecycleButton();

    protected RecyclerView BlockView;
    protected RecyclerView ButtonView;
    protected Vector<Vector<String>> BlockData;
    protected Vector<String> ButtonData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String filename = getIntent().getStringExtra("FileName");
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_uiactivity);
        this.BlockData = getBlockData();
        this.ButtonData = getButtonData();
        this.ButtonView = findViewById(R.id.buttongrid);
        this.BlockView = findViewById(R.id.recycleview);
        this.setRecycleBlock();
        this.setRecycleButton();
        if(BlockView.getAdapter() instanceof FGOBlockAdapter) {
            Button btn = findViewById(R.id.save_file);
            btn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FGOBlockAdapter adp = (FGOBlockAdapter) BlockView.getAdapter();
                    Vector<Vector<String>> data = adp.Data;
                    for(int i=0; i<data.size(); i++){
                        Log.d("kkk", String.valueOf(data.get(i)));
                    }

                    FGOEditor.savetointp(filename, data, ScreenShot.getWidth(), ScreenShot.getHeight());
                }
            });
        }
    }

    public Vector<String> makeVector(String tmp){
        Vector<String> ret = new Vector<>();
        ret.add(tmp);
        return ret;
    }
}