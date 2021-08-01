package com.example.androidscript.Menu.FGO;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.androidscript.Menu.StartService;
import com.example.androidscript.R;
import com.example.androidscript.UserInterface.UIActivity;
import com.example.androidscript.util.BtnMaker;
import com.example.androidscript.util.FileOperation;

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



    public static String linx(int x, int w, int m){
        return Integer.toString((int)w/2+m*(x-960));
    }
    public static String liny(int y, int h, int m){
        return Integer.toString((int)h/2+m*(y-600));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BtnMaker.registerOnClick(R.id.start_service, this, v -> startActivity(new Intent(this, StartService.class).putExtra("Orientation","Landscape")));
        BtnMaker.registerOnClick(R.id.start_floating, this, v -> StartService.startFloatingWidget(this));
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
        ret.add("SelectCard");
        ret.add("CraftSkill");
        ret.add("ServantSkill");
        return ret;
    }

    @Override
    public String getFolderName() {
        return "";
    }

    public static void savetointp(String filename, Vector<Vector<String>> data, int w, int h) {
        if(h > w){
            int tmp = w;
            w = h;
            h = tmp;
        }
        int m = 1;
        if(w/h < 16/9){
            m = w/1920;
        }else{
            m = h/1200;
        }

        Vector<String> save = new Vector<String>();

        for(int i=0; i<data.size(); i++){
            switch (data.get(i).get(0)){
                case"PreStage":
                    save.add("Click "+linx(1400, w, m)+" "+liny(320, h, m));
                    switch (data.get(i).get(1)){
                        case"0":
                            save.add("Click "+linx(960, w, m)+" "+liny(330, h, m));
                            break;
                        case"1":
                            save.add("Click "+linx(960, w, m)+" "+liny(540, h, m));
                            break;
                        case"2":
                            save.add("Click "+linx(960, w, m)+" "+liny(750, h, m));
                            break;
                        case"3":
                            save.add("Swipe "+linx(960, w, m)+" "+liny(786, h, m)+" "+linx(960, w, m)+" "+liny(516, h, m));
                            save.add("Click "+linx(960, w, m)+" "+liny(320, h, m));
                            break;
                    }
                    save.add("Click "+linx(960, w, m)+" "+liny(320, h, m));
                    switch (data.get(i).get(2)){
                        case"0":
                            save.add("Click "+linx(800, w, m)+" "+liny(467, h, m));
                            break;
                        case"1":
                            save.add("Click "+linx(960, w, m)+" "+liny(540, h, m));
                            break;
                        case"2":
                            save.add("Click "+linx(960, w, m)+" "+liny(750, h, m));
                            break;
                        case"3":
                            save.add("Swipe "+linx(960, w, m)+" "+liny(786, h, m)+" "+linx(960, w, m)+" "+liny(516, h, m));
                            save.add("Click "+linx(960, w, m)+" "+liny(320, h, m));
                            break;
                    }
                    break;


            }
        }
        FileOperation.writeToFile(filename, save);
    }
}
