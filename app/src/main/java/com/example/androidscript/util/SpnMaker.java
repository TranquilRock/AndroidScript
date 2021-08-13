package com.example.androidscript.util;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Vector;

public final class SpnMaker {
    public static Spinner fromStringWithView(int id, View view, Vector<String> content){
        Spinner spn = view.findViewById(id);
        ArrayAdapter<String> adp = new ArrayAdapter<String> (view.getContext(),android.R.layout.simple_spinner_dropdown_item,content);
        spn.setAdapter(adp);
        return spn;
    }
    public static Spinner fromStringWithActivity(int id, AppCompatActivity activity, Vector<String> content){
        Spinner spn = activity.findViewById(id);
        ArrayAdapter<String> adp = new ArrayAdapter<String> (activity,android.R.layout.simple_spinner_dropdown_item,content);
        spn.setAdapter(adp);
        return spn;
    }
}
