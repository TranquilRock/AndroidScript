package com.example.androidscript.UserInterface;

import android.os.Bundle;

import com.example.androidscript.R;

import java.util.ArrayList;

public class TestRecyclerView extends UIActivity {
    @Override
    public ArrayList<Integer> BlockData() {
        ArrayList<Integer> BlockInfo = new ArrayList<>();
        for (int i = 0;i < 10;i++){
            BlockInfo.add(i);
        }
        return BlockInfo;
    }

    @Override
    public ArrayList<String> ButtonData() {
        ArrayList<String> ButtonInfo = new ArrayList<>();
        ButtonInfo.add("click");
        return ButtonInfo;
    }


    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }

}
