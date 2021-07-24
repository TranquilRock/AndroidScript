package com.example.androidscript.util.Interpreter;

import android.graphics.Bitmap;

import com.example.androidscript.util.AutoClick;
import com.example.androidscript.util.DebugMessage;
import com.example.androidscript.util.FileOperation;
import com.example.androidscript.util.ScreenShot;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class ArkKnightsInterpreter extends Interpreter {
    public static final String StrFormat = "([A-Za-z0-9_-]*)";
    public static final String ImgFormat = "([A-Za-z0-9_-]*).(jpg|png)";
    public static final String SptFormat = "([A-Za-z0-9_-]*).txt";
    public static final String VarFormat = "\\$([A-Za-z0-9_-]*)";
    public static final String IntFormat = "[0-9]*";
    public static final String IntVarFormat = "(" + IntFormat + "||" + VarFormat + ")";
    public static final String ImgVarFormat = "(" + ImgFormat + "||" + VarFormat + ")";
    public static final String AnyFormat = "[a-zA-Z0-9 $]*";
    public static final String FileRoot = "ArkKnights/";
    public static final String[] SUPPORTED_COMMAND = {
            "Click " + IntVarFormat + " " + IntVarFormat,
            "Compare " + IntVarFormat + " " + IntVarFormat + " " + IntVarFormat + " " + IntVarFormat + " " + ImgVarFormat,
            "Check " + StrFormat,
            "JumpToLine " + IntVarFormat,
            "Wait " + IntVarFormat,
            "Call " + SptFormat + " " + AnyFormat,//Allow passing arguments, but only crafted dependency
            "Call " + SptFormat,
            "IfGreater " + IntVarFormat + " " + IntVarFormat,
            "IfSmaller " + IntVarFormat + " " + IntVarFormat,
            "Var " + VarFormat + " " + IntFormat,//Declare Initial Value of Variable
    };
    public Map<String, TargetImage> ArkKnights = new HashMap<>();

    public ArkKnightsInterpreter() {
        this.ArkKnights.put("EnterOperation", new TargetImage(this.ReadImgFromFile("EnterOperation.png"), 2510, 1120, 2960, 1400));
        this.ArkKnights.put("StartOperation", new TargetImage(this.ReadImgFromFile("StartOperation.png"), 2300, 720, 2600, 1260));
        this.ArkKnights.put("Operating", new TargetImage(this.ReadImgFromFile("Operating.png"), 1130, 1230, 1480, 1380));
        this.ArkKnights.put("OperationEnd", new TargetImage(this.ReadImgFromFile("OperationEnd.png"), 90, 1130, 800, 1360));
    }

    @Override
    public void Interpret(String FileName) {
        try {
            super.Interpret(SUPPORTED_COMMAND, FileName);

        } catch (Exception e) {
            DebugMessage.printStackTrace(e);
        }
    }

    @Override
    public Vector<String> ReadCodeFromFile(String FileName) {
        return FileOperation.instance.readFromFileLines(FileRoot + FileName);
    }

    @Override
    public Bitmap ReadImgFromFile(String FileName) {
        return FileOperation.instance.readPicAsBitmap(FileRoot + FileName);
    }

    @Override
    public void run(String FileName, String[] argv, int depth) throws RuntimeException {//Run code that is already read in MyCode
        assert (depth < 5);
        assert (MyCode.containsKey(FileName));
        Map<String, String> LocalVar = new HashMap<>();
        parseArguments(LocalVar, argv);

        int codeLength = MyCode.get(FileName).codes.size();
        for (int commandIndex = 0; commandIndex < codeLength; commandIndex++) {
            String[] command = (MyCode.get(FileName).codes.get(commandIndex));
            String[] Arguments = new String[command.length - 1];
            for (int i = 1; i < command.length; i++) {//Substitution
                if (command[i].charAt(0) == '$' && LocalVar.containsKey(command[i])) {//There might be Var command, that should replace $V
                    Arguments[i -1] = LocalVar.get(command[i]);
                }
                else{
                    Arguments[i -1] = command[i];
                }
            }
            sleep(300);
            switch (command[0]) {
                case "Click":
                    AutoClick.Click(accessibilityService, Integer.parseInt(Arguments[0]), Integer.parseInt(Arguments[1]));
                    LocalVar.put("$R", "0");
                    break;
                case "Compare":
                    if (ScreenShot.compare(FileOperation.instance.readPicAsBitmap(Arguments[4]), Integer.parseInt(Arguments[0]), Integer.parseInt(Arguments[1]), Integer.parseInt(Arguments[2]), Integer.parseInt(Arguments[3]), true)) {
                        LocalVar.put("$R", "0");
                    } else {
                        LocalVar.put("$R", "1");
                    }
                    break;
                case "Check":
                    if (ArkKnights.containsKey(command[1])) {
                        if (ScreenShot.compare(ArkKnights.get(Arguments[0]), true)) {
                            LocalVar.put("$R", "0");
                        } else {
                            LocalVar.put("$R", "1");
                        }
                    } else {
                        throw new RuntimeException("Check failed " + Arguments[0]);
                    }
                    break;
                case "JumpToLine":
                    commandIndex = Integer.parseInt(Arguments[0]) - 1;//One-based
                    LocalVar.put("$R", "0");
                    break;
                case "Wait":
                    sleep(Integer.parseInt(Arguments[0]));
                    LocalVar.put("$R", "0");
                    break;
                case "Call":
                    if (MyCode.containsKey(Arguments[0])) {
                        String[] nextArgv = new String[command.length - 2];
                        System.arraycopy(Arguments, 1, nextArgv, 0, command.length - 2);
                        run(Arguments[0], nextArgv, depth + 1);
                        LocalVar.put("$R", "0");
                    } else {
                        LocalVar.put("$R", "1");
                    }
                    break;
                case "IfGreater":
                    DebugMessage.set(Arguments[0] + ":" + Integer.parseInt(Arguments[0]) + ">" + Arguments[1] + ":" + Integer.parseInt(Arguments[1]));
                    if (Integer.parseInt(Arguments[0]) <= Integer.parseInt(Arguments[1])) {//Failed, skip next line
                        commandIndex++;
                        LocalVar.put("$R", "1");
                    } else {
                        LocalVar.put("$R", "0");
                    }
                    break;
                case "IfSmaller":
                    if (Integer.parseInt(Arguments[0]) >= Integer.parseInt(Arguments[1])) {//Failed, skip next line
                        commandIndex++;
                        LocalVar.put("$R", "1");
                    } else {
                        LocalVar.put("$R", "0");
                    }
                    break;
                case "Var":
                    assert (Arguments[0].charAt(0) == '$');
                    LocalVar.put(Arguments[0], Arguments[1]);
                    LocalVar.put("$R", "0");
                    break;
                default:
                    throw new RuntimeException("Cannot Recognize " + command[0]);
            }
        }
    }

}