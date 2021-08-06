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

public abstract class UIActivity extends AppCompatActivity implements Editor {

    protected abstract Vector<Vector<String>> getBlockData();

    protected abstract Vector<String> getButtonData();

    protected abstract void setRecycleBlock();

    protected abstract void setRecycleButton();

    protected RecyclerView BlockView;
    protected RecyclerView ButtonView;
    protected Vector<Vector<String>> BlockData = new Vector<>();
    protected Vector<String> ButtonData= new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String filename = getIntent().getStringExtra("FileName");
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_uiactivity);
        this.ButtonView = findViewById(R.id.buttongrid);
        this.BlockView = findViewById(R.id.recycleview);

        this.BlockData = getBlockData();
        this.ButtonData = getButtonData();
        this.setRecycleBlock();
        this.setRecycleButton();
        if(BlockView.getAdapter() instanceof FGOBlockAdapter) {
            Button btn = findViewById(R.id.save_file);
            btn.setOnClickListener(v -> {
                FGOBlockAdapter adp = (FGOBlockAdapter) BlockView.getAdapter();
                Vector<Vector<String>> data = adp.Data;
                for(int i=0; i<data.size(); i++){
                    Log.d("kkk", String.valueOf(data.get(i)));
                }
                FGOEditor.compile(filename, data, ScreenShot.getWidth(), ScreenShot.getHeight());
            });
        }
    }
}