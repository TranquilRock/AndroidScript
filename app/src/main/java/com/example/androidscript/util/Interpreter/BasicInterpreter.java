package com.example.androidscript.util.Interpreter;

import android.graphics.Bitmap;

import com.example.androidscript.util.DebugMessage;
import com.example.androidscript.util.FileOperation;

import java.util.Vector;


public final class BasicInterpreter extends Interpreter {
    public static final String ImgFormat = "([A-Za-z0-9_-]*).(jpg|png)";
    public static final String SptFormat = "([A-Za-z0-9_-]*).txt";
    public static final String VarFormat = "\\$([A-Za-z0-9_-]*)";
    public static final String IntFormat = "[0-9]*";
    public static final String IntVarFormat = "(" + IntFormat + "||" + VarFormat + ")";
    public static final String ImgVarFormat = "(" + ImgFormat + "||" + VarFormat + ")";
    public static final String FileRoot = "basic/";
    public static final String[] SUPPORTED_COMMAND = {
            "Click " + IntVarFormat + " " + IntVarFormat,
            "Compare " + IntVarFormat + " " + IntVarFormat + " " + IntVarFormat + " " + IntVarFormat + " " + ImgVarFormat,
            "JumpToLine " + IntVarFormat,
            "Wait " + IntVarFormat,
            "Call " + SptFormat + " *",//Allow passing arguments, but only crafted dependency
            "IfGreater " + IntVarFormat + " " + IntVarFormat,
            "IfSmaller " + IntVarFormat + " " + IntVarFormat,
            "Var " + VarFormat + " " + IntFormat,//Declare Initial Value of Variable
            "Return " + IntVarFormat,
            "Exit",
    };


    @Override
    public Vector<String> ReadCodeFromFile(String FileName) {
        return FileOperation.instance.readFromFileLines(FileRoot + FileName);
    }

    @Override
    public Bitmap ReadImgFromFile(String FileName) {
        return FileOperation.instance.readPicAsBitmap(FileRoot + FileName);
    }

    @Override
    public void Interpret(String FileName) {
        try {
            super.Interpret(SUPPORTED_COMMAND, FileName);
        } catch (Exception e) {
            DebugMessage.printStackTrace(e);
        }
    }

}
