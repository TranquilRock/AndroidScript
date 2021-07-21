package com.example.androidscript.util.Interpreter;

import android.graphics.Bitmap;


public class BasicInterpreter extends Interpreter {
    public static final String ImgFormat = "([A-Za-z0-9_-]*).(jpg|png)";
    public static final String SptFormat = "([A-Za-z0-9_-]*).txt";
    public static final String VarFormat = "\\$([A-Za-z0-9_-]*)";
    public static final String IntFormat = "[0-9]*";
    public static final String IntVarFormat = "(" + IntFormat + "||" + VarFormat + ")";
    public static final String ImgVarFormat = "(" + ImgFormat + "||" + VarFormat + ")";

    public static final String[] SUPPORTED_COMMAND = {
            "Click " + IntVarFormat + " " + IntVarFormat,
            "Compare " + IntVarFormat + " " + IntVarFormat + " " + IntVarFormat + " " + IntVarFormat + " " + ImgVarFormat,
            "JumpToLine " + IntVarFormat,
            "Wait " + IntVarFormat,
            "Call " + SptFormat + " *",//Allow passing arguments, but only crafted dependency
            "IfGreater " + IntVarFormat + " " + IntVarFormat,
            "IfSmaller " + IntVarFormat + " " + IntVarFormat,
            "Var " + VarFormat + " " + IntFormat,//Declare Initial Value of Variable
    };

    @Override
    public String ReadCodeFromFile(String FileName) {
        //TODO read code under "Environment.getExternalStorageDirectory() + /Target/"
        return "";
    }

    @Override
    public Bitmap ReadImgFromFile(String FileName) {
        //TODO read image under "Environment.getExternalStorageDirectory() + /Target/"
        return null;
    }

}
