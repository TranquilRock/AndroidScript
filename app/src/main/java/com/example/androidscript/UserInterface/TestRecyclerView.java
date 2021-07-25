package com.example.androidscript.UserInterface;

import android.os.Bundle;

import java.util.ArrayList;

public class TestRecyclerView extends UIActivity {
    @Override
    public ArrayList<String> BlockData() {
        ArrayList<String> BlockInfo = new ArrayList<>();
        String count;
        for (int i = 0;i < 100;i++){
            count = String.valueOf(i+1);
            BlockInfo.add("第" + count + "個Block");
        }
        return BlockInfo;
    }

    @Override
    public ArrayList<String> ButtonData() {
        ArrayList<String> ButtonInfo = new ArrayList<>();
        for (int i = 0;i < 11;i++){
            ButtonInfo.add("點擊");
        }
        return ButtonInfo;
    }
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }

}
