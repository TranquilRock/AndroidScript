package com.example.androidscript.Menu.Basic;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.androidscript.Menu.StartService;
import com.example.androidscript.R;
import com.example.androidscript.UserInterface.UIActivity;
import com.example.androidscript.util.BtnMaker;
import com.example.androidscript.util.DebugMessage;
import com.example.androidscript.util.Interpreter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

public class BasicEditor extends UIActivity {

    public static final String FolderName = "Basic/";
    public static Map<String, Vector<String>> Blocks = new HashMap<>();
    public static BasicCompiler compiler;
    static{
        for (String command : Interpreter.SUPPORTED_COMMAND) {
            String[] keys = command.split(" ");
            Vector<String> value = new Vector<>();
            value.add(keys[0]);
            for (int z = 1; z < keys.length; z++) {//+1
                value.add("");
            }
            Blocks.put(keys[0], value);
        }
        compiler = new BasicCompiler();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BtnMaker.registerOnClick(R.id.start_service, this, v -> startActivity(new Intent(this, StartService.class).putExtra("Orientation", "vertical")));
        BtnMaker.registerOnClick(R.id.start_floating, this, v -> StartService.startFloatingWidget(this));
        BtnMaker.registerOnClick(R.id.save_file,this,(v -> {
            compiler.compile(this.BlockData);
        }));
    }

    @Override
    protected Vector<Vector<String>> getBlockData() {
        Vector<Vector<String>> ret = new Vector<>();
        for (Map.Entry<String, Vector<String>> key : Blocks.entrySet()) {
            ret.add(new Vector<>(Blocks.get(key.getKey())));
        }
        return ret;
    }

    @Override
    protected Vector<String> getButtonData() {
        Vector<String> ret = new Vector<>();
        for (Map.Entry<String, Vector<String>> Block : Blocks.entrySet()) {
            ret.add(Block.getKey());
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
        this.ButtonView.setLayoutManager(new GridLayoutManager(this, 2));
        this.ButtonView.setAdapter(new BasicButtonAdapter(BlockData, ButtonData, ((BasicBlockAdapter) Objects.requireNonNull(BlockView.getAdapter())).onOrderChange));
    }

    @Override
    public String getFolderName() {
        return FolderName;
    }
}
