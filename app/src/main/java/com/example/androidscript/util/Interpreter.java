package com.example.androidscript.util;

import android.graphics.Bitmap;
import android.widget.TextView;

import com.example.androidscript.FloatingWidget.FloatingWidgetService;

import org.opencv.core.Point;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

public class Interpreter extends Thread {//Every child only need to specify where and how to fetch files, as well as what kind of commands are accepted;

    public static final String StrFormat = "([A-Za-z0-9_-]*)";
    public static final String ImgFormat = "([A-Za-z0-9_-]*).(jpg|png)";
    public static final String SptFormat = "([A-Za-z0-9_-]*).txt";
    public static final String VarFormat = "\\$([A-Za-z0-9_-]*)";
    public static final String IntFormat = "[0-9]*";
    public static final String IntVarFormat = "(" + IntFormat + "||" + VarFormat + ")";
    public static final String ImgVarFormat = "(" + ImgFormat + "||" + VarFormat + ")";
    public static final String AnyFormat = "[a-zA-Z.0-9 $]*";
    public static final String[] SUPPORTED_COMMAND = {
            "Exit",
            "Contain " + ImgVarFormat,
            "JumpTo " + IntVarFormat,
            "Wait " + IntVarFormat,
            "Call " + SptFormat,
            "Tag " + VarFormat,
            "Return " + IntVarFormat,
            "ClickPic " + ImgVarFormat,
            "Click " + IntVarFormat + " " + IntVarFormat,
            "CallArg " + SptFormat + " " + AnyFormat,
            "IfGreater " + IntVarFormat + " " + IntVarFormat,
            "IfSmaller " + IntVarFormat + " " + IntVarFormat,
            "Add " + VarFormat + " " + IntVarFormat,
            "Subtract " + VarFormat + " " + IntVarFormat,
            "Var " + VarFormat + " " + IntVarFormat,
            "Swipe " + IntVarFormat + " " + IntVarFormat + " " + IntVarFormat + " " + IntVarFormat,
            "Compare " + IntVarFormat + " " + IntVarFormat + " " + IntVarFormat + " " + IntVarFormat + " " + ImgVarFormat,
    };

    public boolean running = false;
    protected String ScriptName;
    protected String ScriptFolderName;

    public Interpreter(String _ScriptFolderName, String _FileName) {
        this.ScriptName = _FileName;
        this.ScriptFolderName = _ScriptFolderName;
        this.Interpret(_FileName);
    }

    public Vector<String> ReadCodeFromFile(String FileName) {
        return FileOperation.readFromFileLines(this.ScriptFolderName + FileName);
    }

    public Bitmap ReadImgFromFile(String FileName) {
        return FileOperation.readPicAsBitmap(this.ScriptFolderName + FileName);
    }

    public static class INVALID_CODE_EXCEPTION extends Exception {
        public INVALID_CODE_EXCEPTION(String s) {
            DebugMessage.set(s);
        }
    }

    public static class Code {//Valid code that interpreter can recognize
        public Vector<String[]> codes = new Vector<>();
        public Vector<String> dependency = new Vector<>();

        public Code(String[] SUPPORTED_COMMAND, Vector<String> RawCode) throws INVALID_CODE_EXCEPTION {
            for (String line : RawCode) {
                boolean valid = false;
                for (String format : SUPPORTED_COMMAND) {
                    if (Pattern.matches(format, line)) {
                        valid = true;
                        String[] command = line.split(" ");
                        if (command[0].equals("Call") || command[0].equals("CallArg")) {
                            dependency.add(command[1]);
                        }
                        codes.add(command);
                        break;
                    }
                }
                if (!valid) {
                    throw new INVALID_CODE_EXCEPTION("Invalid code \"" + line + "\"");
                }
            }
        }
    }


    protected Map<String, Interpreter.Code> MyCode = new HashMap<>();

    public void Interpret(String FileName) {
        DebugMessage.set("Interpreting:" + FileName);
        Vector<String> Command = ReadCodeFromFile(FileName);
        try {
            MyCode.put(FileName, new Code(SUPPORTED_COMMAND, Command));
        } catch (Exception e) {
            DebugMessage.set("Bug in " + FileName);
            DebugMessage.printStackTrace(e);
        }
        for (String depend : MyCode.get(FileName).dependency) {
            if (!MyCode.containsKey(depend)) {
                Interpret(depend);
            }
        }
    }

    //==========================================
    private String[] run_arg_argv = null;
    private int run_arg_depth = -1;

    public final void runCode(String[] argv) {
        this.run_arg_argv = argv;
        this.run_arg_depth = 0;
        this.running = true;
        this.start();
    }

    @Override
    public final void run() {
        assert (ScriptName != null);
        assert (run_arg_depth != -1);
        this.run(ScriptName, run_arg_argv, run_arg_depth);
    }

    protected int run(String FileName, String[] argv, int depth) {//Run code that is already read in MyCode
        assert (depth < 5);
        Map<String, String> LocalVar = new HashMap<>();
        parseArguments(LocalVar, argv);
        DebugMessage.set("Running " + FileName);
        int codeLength = MyCode.get(FileName).codes.size();

        for (int commandIndex = 0; commandIndex < codeLength; commandIndex++) {
            String[] command = (MyCode.get(FileName).codes.get(commandIndex));
            if (command[0].equals("Tag")) {
                LocalVar.put(command[1], String.valueOf(commandIndex));
            }
        }

        for (int commandIndex = 0; commandIndex < codeLength; commandIndex++) {
            if (!this.running) {
                return 1;
            }
            String[] command = (MyCode.get(FileName).codes.get(commandIndex));
            String[] Arguments = new String[command.length - 1];
            for (int i = 1; i < command.length; i++) {//Substitution
                if (command[i].charAt(0) == '$' && !(command[0].equals("Var") && i == 1) && !(command[0].equals("Subtract") && (i == 1)) && !(command[0].equals("Add") && (i == 1))) {//There might be Var command, that should replace $V
                    Arguments[i - 1] = LocalVar.get(command[i]);
                } else {
                    Arguments[i - 1] = command[i];
                }
            }
            switch (command[0]) {
                case "Exit":
                    this.running = false;
                    return 1;
                case "Contain":
                    if (ScreenShot.contain(ReadImgFromFile(Arguments[0]))) {
                        LocalVar.put("$R", "0");
                    } else {
                        LocalVar.put("$R", "1");
                    }
                    break;
                case "JumpTo":
                    commandIndex = Integer.parseInt(Arguments[0]) - 1;//One-based
                    break;
                case "Wait":
                    sleep(Integer.parseInt(Arguments[0]));
                    break;
                case "Call":
                    LocalVar.put("$R", String.valueOf(run(Arguments[0], null, depth + 1)));
                    break;
                case "Tag":
                    break;
                case "Return":
                    return Integer.parseInt(Arguments[0]);
                case "ClickPic":
                    Bitmap tmp = ReadImgFromFile(Arguments[0]);
                    DebugMessage.set("ClickPic " + Arguments[0]);
                    Point target = ImageHandler.findLocation(ScreenShot.Shot(),tmp);
                    DebugMessage.set("Clicking Picture:" + target.x + " " + target.y);
                    AutoClick.Click((int)target.x,(int)target.y);
                    break;
                case "Click":
                    delay();
                    AutoClick.Click(Integer.parseInt(Arguments[0]), Integer.parseInt(Arguments[1]));
                    break;
                case "CallArg":
                    String[] nextArgv = new String[command.length - 2];
                    System.arraycopy(Arguments, 1, nextArgv, 0, command.length - 2);
                    LocalVar.put("$R", String.valueOf(run(Arguments[0], nextArgv, depth + 1)));
                    break;
                case "IfGreater":
                    if (Integer.parseInt(Arguments[0]) <= Integer.parseInt(Arguments[1])) {//Failed, skip next line
                        commandIndex++;
                    }
                    break;
                case "IfSmaller":
                    if (Integer.parseInt(Arguments[0]) >= Integer.parseInt(Arguments[1])) {//Failed, skip next line
                        commandIndex++;
                    }
                    break;
                case "Add":
                    LocalVar.put(Arguments[0], String.valueOf(Integer.parseInt(LocalVar.get(Arguments[0])) + Integer.parseInt(Arguments[1])));
                    break;
                case "Subtract":
                    DebugMessage.set(Arguments[0]);
                    LocalVar.put(Arguments[0], String.valueOf(Integer.parseInt(LocalVar.get(Arguments[0])) - Integer.parseInt(Arguments[1])));
                    break;
                case "Var":
                    assert (Arguments[0].charAt(0) == '$');
                    LocalVar.put(Arguments[0], Arguments[1]);
                    break;
                case "Swipe":
                    delay();
                    AutoClick.Swipe(Integer.parseInt(Arguments[0]), Integer.parseInt(Arguments[1]), Integer.parseInt(Arguments[2]), Integer.parseInt(Arguments[3]));
                    break;
                case "Compare":
                    int Similarity = ScreenShot.compare(ReadImgFromFile(Arguments[4]), Integer.parseInt(Arguments[0]), Integer.parseInt(Arguments[1]), Integer.parseInt(Arguments[2]), Integer.parseInt(Arguments[3]));
                    LocalVar.put("$R", String.valueOf(Similarity));
                    break;
                default:
                    throw new RuntimeException("Cannot Recognize " + command[0]);
            }
        }
        return 0;
    }
    //==========Helper=========================================

    protected static void parseArguments(Map<String, String> LocalVar, String[] argv) {
        LocalVar.put("$R", "0");
        int argCount = 1;
        if (argv != null) {
            for (String arg : argv) {
                LocalVar.put("$" + argCount, arg);
                argCount++;
            }
        }
    }

    protected static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            DebugMessage.printStackTrace(e);
        }
    }

    protected static void delay() {
        sleep(500);
    }

}
