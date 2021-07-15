package com.example.androidscript.Menu;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

public class Interpreter {//Only Support Integer Var
    public static final String ImgFormat = "([A-Za-z0-9_-]*).(jpg|png)";
    public static final String SptFormat = "([A-Za-z0-9_-]*).txt";
    public static final String VarFormat = "\\$([A-Za-z0-9_-]*)";
    public static final String IntFormat = "[0-9]*";
    public static final String[] SUPPORTED_COMMAND = {
            "Click " + IntFormat + " " + IntFormat,
            "Compare " + IntFormat + " " + IntFormat + " " + IntFormat + " " + IntFormat + " " + ImgFormat,
            "JumpToLine " + IntFormat,
            "Wait " + IntFormat,
            "Call " + SptFormat,
            "If " + VarFormat + " (>|<) " + IntFormat,
            "Var " + VarFormat + " (+|-|=) " + IntFormat,
    };

    public static class INVALID_CODE_EXCEPTION extends Exception {
    }

    public class Code {
        public Vector<String[]> codes = new Vector<String[]>();
        public Vector<String> dependency = new Vector<String>();

        public Code(String RawCode) throws INVALID_CODE_EXCEPTION {
            String[] UncheckedCode = RawCode.split("\n");
            for (String line : UncheckedCode) {
                boolean valid = false;
                for (String format : SUPPORTED_COMMAND) {
                    if (Pattern.matches(format, line)) {
                        valid = true;
                        String[] command = line.split(" ");
                        if (command[0].equals("Call")) {
                            dependency.add(command[1]);
                        }
                        codes.add(command);
                        break;
                    }
                }
                if (!valid) {
                    throw new INVALID_CODE_EXCEPTION();
                }
            }
        }
    }

    public static String ReadCodeFromFile(String FileName) {
        //TODO read code under "Environment.getExternalStorageDirectory() + /Target/"
        return "";
    }

    public static Bitmap ReadImgFromFile(String FileName) {
        //TODO read image under "Environment.getExternalStorageDirectory() + /Target/"
        return null;
    }

    private Map<String, Code> MyCode = new HashMap<>();

    public void Interpret(String FileName) throws INVALID_CODE_EXCEPTION {
        String Command = ReadCodeFromFile(FileName);
        MyCode.put(FileName, new Code(Command));
        for (String depend : MyCode.get(FileName).dependency) {
            if (!MyCode.containsKey(depend)) {
                Interpret(depend);
            }
        }
    }
    private Map<String, Integer> MyVar = new HashMap<>();
    public void Run(String FileName) throws RuntimeException {
        for(String[] command : MyCode.get(FileName).codes){
            switch (command[0]){
                case "Click":
                    break;
                case "Compare":
                    break;
                case "JumpToLine":
                    break;
                case "Wait":
                    break;
                case "Call":
                    break;
                case "If":
                    break;
                case "Var":
                    break;
                default:
                    throw new RuntimeException("Cannot Recognize " + command[0]);
            }
        }
    }
}
