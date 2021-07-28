package com.example.androidscript.UserInterface;

import android.os.Bundle;

import androidx.cardview.widget.CardView;

import com.example.androidscript.R;

import java.util.ArrayList;
import java.util.Vector;

public class TestRecyclerView extends UIActivity {
    @Override
    public ArrayList<Vector<String>> getBlockData() {
        ArrayList<Vector<String>> ret = new ArrayList<Vector<String>>();
        for (int i = 1; i <= 5; i++) {
            Vector<String> tmp = new Vector<>();
            switch (i) {
                case 2:
                    tmp.add("Skill");
                    break;
                case 1:
                    tmp.add("PreStage");
                    break;
                case 3:
                    tmp.add("NoblePhantasms");
                    break;
                case 4:
                    tmp.add("CraftSkill");
                    break;
                case 5:
                    tmp.add("EndStage");
                    break;
            }
            ret.add(tmp);
        }
        return ret;
    }

    @Override
    public ArrayList<String> getButtonData() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add("click");
        return ret;
    }
}
