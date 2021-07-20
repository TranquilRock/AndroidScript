package com.example.androidscript.util;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Vector;

public class SpnMaker {
    public static Spinner fromString(int id, AppCompatActivity activity, Vector<String> content){
        Spinner spn = activity.findViewById(id);
        ArrayAdapter<String> adp = new ArrayAdapter<String> (activity,android.R.layout.simple_spinner_dropdown_item,content);
        spn.setAdapter(adp);
        return spn;
    }
}
