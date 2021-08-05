package com.example.androidscript.Menu.Basic;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.androidscript.Menu.FGO.FGOBlockAdapter;
import com.example.androidscript.Menu.FGO.FGOButtonAdapter;
import com.example.androidscript.Menu.StartService;
import com.example.androidscript.R;
import com.example.androidscript.UserInterface.UIActivity;
import com.example.androidscript.util.BtnMaker;
import com.example.androidscript.util.Interpreter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

public class BasicEditor extends UIActivity {

    public static final String FolderName = "Basic/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BtnMaker.registerOnClick(R.id.start_service, this, v -> startActivity(new Intent(this, StartService.class).putExtra("Orientation","vertical")));
        BtnMaker.registerOnClick(R.id.start_floating, this, v -> StartService.startFloatingWidget(this));
    }

    @Override
    protected Vector<Vector<String>> getBlockData() {
        Vector<Vector<String>> ret = new Vector<>();
        for(String command : Interpreter.SUPPORTED_COMMAND){
            ret.add(new Vector<>(Arrays.asList(command.split(" "))));
        }
        return ret;
    }

    @Override
    protected Vector<String> getButtonData() {
        Vector<String> ret = new Vector<>();
        for(String command : Interpreter.SUPPORTED_COMMAND){
            ret.add(command.split(" ")[0]);
        }
        return ret;
    }

    @Override
    protected void setRecycleBlock() {
        this.BlockView.setLayoutManager(new LinearLayoutManager(this));
        this.BlockView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        this.BlockView.setAdapter(new BasicBlockAdapter(BlockData));
    }

    @Override
    protected void setRecycleButton() {
        this.ButtonView.setLayoutManager(new GridLayoutManager(this, 4));
        this.ButtonView.setAdapter(new BasicButtonAdapter(BlockData, ButtonData, ((BasicBlockAdapter) Objects.requireNonNull(BlockView.getAdapter())).onOrderChange));
    }

    @Override
    public String getFolderName() {
        return FolderName;
    }
}
