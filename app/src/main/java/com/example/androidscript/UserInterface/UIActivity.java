package com.example.androidscript.UserInterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.R;
import com.example.androidscript.util.DebugMessage;

import java.util.ArrayList;

public abstract class UIActivity extends AppCompatActivity {

    protected abstract ArrayList<Integer> getBlockData();

    protected abstract ArrayList<String> getButtonData();

    protected RecyclerView mRecyclerView, ButtonRecyclerView;
    protected BlockAdapter mBlockAdapter;
    protected ButtonAdapter mButtonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uiactivity);
        this.setRecycleBlock(getBlockData());
        this.setRecycleButton(getButtonData());
//        mBlockAdapter.notifyItemInserted();
    }

    protected void setRecycleButton(ArrayList<String> data){
        ButtonRecyclerView = findViewById(R.id.buttongrid);
        ButtonRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        mButtonAdapter = new ButtonAdapter(data);
        ButtonRecyclerView.setAdapter(mButtonAdapter);
    }

    protected void setRecycleBlock(ArrayList<Integer> data){
        mRecyclerView = findViewById(R.id.recycleview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mBlockAdapter = new BlockAdapter(data);
        mRecyclerView.setAdapter(mBlockAdapter);
    }
}