package com.example.androidscript.UserInterface;

import android.os.Bundle;

import androidx.cardview.widget.CardView;

import com.example.androidscript.R;

import java.util.ArrayList;

public class TestRecyclerView extends UIActivity{
    @Override
    public ArrayList<Integer> getBlockData() {
        ArrayList<Integer> ret = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ret.add(0);
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
