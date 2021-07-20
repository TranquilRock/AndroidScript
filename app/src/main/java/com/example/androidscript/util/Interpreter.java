package com.example.androidscript.util;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

public class Interpreter {//Only Support Integer Var
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

    public void Interpret(String FileName) throws INVALID_CODE_EXCEPTION {//Construct MyCode Object
        String Command = ReadCodeFromFile(FileName);
        MyCode.put(FileName, new Code(Command));
        for (String depend : MyCode.get(FileName).dependency) {
            if (!MyCode.containsKey(depend)) {
                Interpret(depend);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Run(String FileName, String[] argv, int depth) throws RuntimeException {//Run code that is already read in MyCode
        if (depth > 3) {
            throw new RuntimeException("Too deep, panic to avoid stackoverflow!\n");
        }
        Map<String, String> LocalVar = new HashMap<>();
        LocalVar.put("$R", "0");
        int argCount = 1;
        for (String arg : argv) {
            LocalVar.put("$" + String.valueOf(argCount), arg);
            argCount++;
        }
        int codeLength = MyCode.get(FileName).codes.size();
        for (int commandIndex = 0; commandIndex < codeLength; commandIndex++) {
            String[] command = MyCode.get(FileName).codes.get(commandIndex);
            for (int i = 1; i < command.length; i++) {//Substitude
                if (command[i].charAt(0) == '$' && LocalVar.containsKey(command[i])) {
                    command[i] = LocalVar.get(i);
                }
            }
            switch (command[0]) {
                case "Click":
//                    AutoClick.mService.Click(Integer.valueOf(command[1]), Integer.valueOf(command[2]));
                    LocalVar.put("$R", "0");
                    break;
                case "Compare":
                    System.out.println("Not Done Yet!\n");
                    LocalVar.put("$R", "0");
                    break;
                case "JumpToLine":
                    commandIndex = Integer.valueOf(command[1]);
                    LocalVar.put("$R", "0");
                    break;
                case "Wait":
                    try {
                        Thread.sleep(Integer.valueOf(command[1]));
                        LocalVar.put("$R", "0");
                    } catch (InterruptedException e) {
                        System.out.println("Waked up unexpected!\n");
                        LocalVar.put("$R", "1");
                    }
                    break;
                case "Call":
                    if (MyCode.containsKey(command[1])) {
                        String[] nextArgv = new String[command.length - 2];
                        for (int j = 2; j < command.length; j++) {
                            nextArgv[j - 2] = command[j];
                        }
                        Run(command[1], nextArgv, depth + 1);
                        LocalVar.put("$R", "0");
                    } else {
                        LocalVar.put("$R", "1");
                    }
                    break;
                case "IfGreater":
                    if (Integer.valueOf(command[1]) <= Integer.valueOf(command[2])) {//Failed, skip next line
                        commandIndex++;
                        LocalVar.put("$R", "1");
                    } else {
                        LocalVar.put("$R", "0");
                    }
                    break;
                case "IfSmaller":
                    if (Integer.valueOf(command[1]) >= Integer.valueOf(command[2])) {//Failed, skip next line
                        commandIndex++;
                        LocalVar.put("$R", "1");
                    } else {
                        LocalVar.put("$R", "0");
                    }
                    break;
                case "Var":
                    assert (command[1].charAt(0) == '$');
                    LocalVar.put(command[1], command[2]);
                    LocalVar.put("$R", "0");
                    break;
                default:
                    throw new RuntimeException("Cannot Recognize " + command[0]);
            }
        }
    }
}
