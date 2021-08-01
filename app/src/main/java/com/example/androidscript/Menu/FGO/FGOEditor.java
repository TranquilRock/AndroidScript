package com.example.androidscript.Menu.FGO;

import android.os.Bundle;
import android.util.Log;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.androidscript.R;
import com.example.androidscript.UserInterface.UIActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

public class FGOEditor extends UIActivity {

    public static final String[] PreStageBlock = {"PreStage", "0", "0", "0", ""};
    public static final String[] SkillBlock = {"Skill", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
    public static final String[] CraftSkillBlock = {"CraftSkill", "0", "0", "0", "0"};
    public static final String[] NoblePhantasmsBlock = {"NoblePhantasms", "0", "0", "0", "0"};
    public static final String[] EndBlock = {"End"};

    public static void savetointp(String filename, Vector<Vector<String>> data) {
        Vector<String> save=null;
        int w0 = 0;
        int h0 = 0;
        for(int i=0; i<data.size(); i++){
            switch(data.get(i).get(0)){
                case"PreStage":
                    save.add("Click "+linx(1400, w0)+" ");
                    break;


            }
        }
    }

    public static String linx(int x, int w0){
        return Integer.toString((int)w0+(w0/1920)*(x-1920));
    }
    public static String liny(int y, int h0){
        return Integer.toString((int)h0+(h0/1200)*(y-1200));
    }

    @Override
    protected void setRecycleButton() {
        this.ButtonView.setLayoutManager(new GridLayoutManager(this, 4));
        this.ButtonView.setAdapter(new FGOButtonAdapter(BlockData, ButtonData, ((FGOBlockAdapter) Objects.requireNonNull(BlockView.getAdapter())).onOrderChange));
    }

    @Override
    protected void setRecycleBlock() {
        this.BlockView.setLayoutManager(new LinearLayoutManager(this));
        this.BlockView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        this.BlockView.setAdapter(new FGOBlockAdapter(BlockData));
    }

    @Override
    public Vector<Vector<String>> getBlockData() {
        Vector<Vector<String>> ret = new Vector<>();
        ret.add(new Vector<>(Arrays.asList(PreStageBlock)));
        for (int i = 0; i <= 2; i++) {
            switch (i % 3) {
                case 2:
                    ret.add(new Vector<>(Arrays.asList(SkillBlock)));
                    break;
                case 1:
                    ret.add(new Vector<>(Arrays.asList(NoblePhantasmsBlock)));
                    break;
                case 0:
                    ret.add(new Vector<>(Arrays.asList(CraftSkillBlock)));
                    break;
            }
        }
        ret.add(new Vector<>(Arrays.asList(EndBlock)));

        return ret;
    }

    @Override
    public Vector<String> getButtonData() {
        Vector<String> ret = new Vector<>();
        ret.add("自動選卡");
        ret.add("御主技能");
        ret.add("從者技能");
        return ret;
    }

    @Override
    public String getFolderName() {
        return "";
    }
}
