package com.example.androidscript.Menu.FGO;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.androidscript.FloatingWidget.FloatingWidgetService;
import com.example.androidscript.Menu.Basic.BasicEditor;
import com.example.androidscript.Menu.StartService;
import com.example.androidscript.R;
import com.example.androidscript.UserInterface.UIActivity;
import com.example.androidscript.util.BtnMaker;
import com.example.androidscript.util.DebugMessage;
import com.example.androidscript.util.FileOperation;
import com.example.androidscript.util.Interpreter;
import com.example.androidscript.util.ScriptCompiler;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

public class FGOEditor extends UIActivity {

    public static final String FolderName = "FGO/";
    public static final String[] PreStageBlock = {"PreStage", "0", "0", "0", "", ""};
    public static final String[] SkillBlock = {"Skill", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
    public static final String[] CraftSkillBlock = {"CraftSkill", "0", "0", "0", "0"};
    public static final String[] NoblePhantasmsBlock = {"NoblePhantasms", "0", "0", "0", "0"};
    public static final String[] EndBlock = {"End"};
    public static final ScriptCompiler compiler = new FGOScriptCompiler();

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
                FileOperation.writeWords(FGOEditor.FolderName + this.filename, this.BlockData);


                for(Vector<String> tt : this.BlockData){
                    StringBuilder gg = new StringBuilder();
                    for(String t:tt){
                        gg.append(t).append(" ");
                    }
                    DebugMessage.set(gg.toString());
                }


                compiler.compile(this.BlockData);
                FloatingWidgetService.setScript(FGOEditor.FolderName, "Run.txt", null);
                Toast.makeText(this.getApplicationContext(), "Successful!!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.getApplicationContext(), "Arguments can't be empty", Toast.LENGTH_LONG).show();
            }
        }
        ));
        //SetBlockData

        if (FileOperation.findFile(FGOEditor.FolderName, this.filename)) {
            this.BlockData = FileOperation.readFromFileWords(FGOEditor.FolderName + this.filename);

        } else {
            this.BlockData.add(new Vector<>(Arrays.asList(PreStageBlock)));
            this.BlockData.add(new Vector<>(Arrays.asList(SkillBlock)));
            this.BlockData.add(new Vector<>(Arrays.asList(NoblePhantasmsBlock)));
            this.BlockData.add(new Vector<>(Arrays.asList(CraftSkillBlock)));
            this.BlockData.add(new Vector<>(Arrays.asList(EndBlock)));
        }

        this.BlockView.setLayoutManager(new LinearLayoutManager(this));
        this.BlockView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        this.BlockView.setAdapter(new FGOBlockAdapter(BlockData));
        //SetButtonData
        this.ButtonData.add("SelectCard");
        this.ButtonData.add("CraftSkill");
        this.ButtonData.add("ServantSkill");

        this.ButtonView.setLayoutManager(new GridLayoutManager(this, 2));
        this.ButtonView.setAdapter(new FGOButtonAdapter(BlockData, ButtonData, ((FGOBlockAdapter) Objects.requireNonNull(BlockView.getAdapter())).onOrderChange));
    }

    @Override
    public String getFolderName() {
        return FolderName;
    }

    @Override
    protected void resourceInitialize() {
        try {
            String[] allFiles = getAssets().list("");//List all file
            FileOperation.readDir(FolderName);//Since we access depth 2 folder later, make sure depth 1 is built;
            for (String file : allFiles) {
                if (file.startsWith("FGO_")) {
                    if(file.startsWith("FGO_Friend_")){
                        getResource(file, FolderName  + "Friend/", file.substring(11));
                    }
                    else if(file.startsWith("FGO_Craft_")){
                        getResource(file, FolderName + "Craft/", file.substring(10));
                    }else{
                        getResource(file, FolderName, file.substring(4));
                    }
                }
            }
        } catch (Exception e) {
            DebugMessage.printStackTrace(e);
        }
    }

}
