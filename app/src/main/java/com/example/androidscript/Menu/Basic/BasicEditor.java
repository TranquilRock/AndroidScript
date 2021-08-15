package com.example.androidscript.Menu.Basic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.androidscript.FloatingWidget.FloatingWidgetService;
import com.example.androidscript.Menu.FGO.FGOEditor;
import com.example.androidscript.Menu.StartService;
import com.example.androidscript.R;
import com.example.androidscript.UserInterface.UIActivity;
import com.example.androidscript.util.BtnMaker;
import com.example.androidscript.util.DebugMessage;
import com.example.androidscript.util.FileOperation;
import com.example.androidscript.util.Interpreter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

public class BasicEditor extends UIActivity {

    public static final String FolderName = "Basic/";
    public static Map<String, Vector<String>> Blocks = new HashMap<>();
    public static BasicScriptCompiler compiler = new BasicScriptCompiler();

    static {
        for (String command : Interpreter.SUPPORTED_COMMAND) {
            String[] keys = command.split(" ");
            Vector<String> value = new Vector<>();
            value.add(keys[0]);
            for (int z = 0; z < getCommandLength(keys[0]); z++) {//+1
                value.add("");
            }
            Blocks.put(keys[0], value);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BtnMaker.registerOnClick(R.id.start_service, this, v -> startActivity(new Intent(this, StartService.class).putExtra("Orientation", "Landscape")));
        BtnMaker.registerOnClick(R.id.start_floating, this, v -> StartService.startFloatingWidget(this));
        BtnMaker.registerOnClick(R.id.save_file, this, (v -> {
            boolean flag = true;
            for (Vector<String> Line : this.BlockData) {
                if (Line.contains("")) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                FileOperation.writeWords(BasicEditor.FolderName + this.filename, this.BlockData);
                compiler.compile(this.BlockData);
                FloatingWidgetService.setScript(BasicEditor.FolderName, "Run.txt", null);
                Toast.makeText(this.getApplicationContext(), "Successful!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.getApplicationContext(), "Arguments can't be empty", Toast.LENGTH_LONG).show();
            }
        }));

        //SetBlockData

        if (FileOperation.findFile(BasicEditor.FolderName, this.filename)) {
            this.BlockData = FileOperation.readFromFileWords(BasicEditor.FolderName + this.filename);
        } else {
            DebugMessage.set("No such file");
            this.BlockData.add(new Vector<>(Blocks.get("Return")));
        }
        this.BlockView.setLayoutManager(new LinearLayoutManager(this));
        this.BlockView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        this.BlockView.setAdapter(new BasicBlockAdapter(BlockData));
        //SetButtonData
        for (Map.Entry<String, Vector<String>> Block : Blocks.entrySet()) {
            this.ButtonData.add(Block.getKey());
        }
        this.ButtonView.setLayoutManager(new GridLayoutManager(this, 2));
        this.ButtonView.setAdapter(new BasicButtonAdapter(BlockData, ButtonData, ((BasicBlockAdapter) Objects.requireNonNull(BlockView.getAdapter())).onOrderChange));

    }

    private static int getCommandLength(String Command) {
        switch (Command) {
            case "Exit":
                return 0;
            case "JumpTo":
            case "Wait":
            case "Call":
            case "Tag":
            case "Return":
                return 1;
            case "ClickPic":
            case "Click":
            case "CallArg":
            case "IfGreater":
            case "IfSmaller":
            case "Add":
            case "Subtract":
            case "Var":
                return 2;
            case "Swipe":
                return 4;
            case "Compare":
                return 5;
        }
        return 0;
    }

    @Override
    public String getFolderName() {
        return FolderName;
    }

    @Override
    protected void resourceInitialize() {}
}
