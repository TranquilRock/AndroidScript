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
import java.util.Objects;
import java.util.Vector;

public class FGOEditor extends UIActivity {
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
    public Vector<String> getBlockData() {
        Vector<String> ret = new Vector<>();
        ret.add("PreStage");
        for (int i = 0; i <= 2; i++) {
            switch (i % 3) {
                case 2:
                    ret.add("Skill");
                    break;
                case 1:
                    ret.add("NoblePhantasms");
                    break;
                case 0:
                    ret.add("CraftSkill");
                    break;
            }
        }
        ret.add("End");

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
