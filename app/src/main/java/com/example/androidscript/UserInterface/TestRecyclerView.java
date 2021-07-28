package com.example.androidscript.UserInterface;

import android.os.Bundle;

import androidx.cardview.widget.CardView;

import com.example.androidscript.R;

import java.util.ArrayList;
import java.util.Vector;

public class TestRecyclerView extends UIActivity{
    @Override
    public ArrayList<Vector<String>> getBlockData() {
        ArrayList<Vector<String>> ret = new ArrayList<Vector<String>>();
        for (int i = 0; i < 10; i++) {
            Vector<String> tmp = new Vector<>();
            tmp.add("Skill");

            tmp.add("PreStage");
            ret.add(tmp);
        }
        return ret;
    }

    @Override
    public ArrayList<String> getButtonData() {
        ArrayList<String> ret  = new ArrayList<>();
        ret.add("click");
        return ret;
    }
}
