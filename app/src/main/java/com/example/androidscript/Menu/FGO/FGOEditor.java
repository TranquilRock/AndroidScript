package com.example.androidscript.Menu.FGO;

import android.os.Bundle;

import androidx.cardview.widget.CardView;

import com.example.androidscript.R;
import com.example.androidscript.UserInterface.UIActivity;

import java.util.ArrayList;
import java.util.Vector;

public class FGOEditor extends UIActivity {
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
        ret.add("click");
        return ret;
    }

    @Override
    public String getFolderName() {
        return "";
    }
}
