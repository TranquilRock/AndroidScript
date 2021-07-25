package com.example.androidscript.util.Interpreter;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Bitmap;

import com.example.androidscript.util.AutoClick;
import com.example.androidscript.util.DebugMessage;
import com.example.androidscript.util.FileOperation;
import com.example.androidscript.util.ScreenShot;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

public abstract class Interpreter extends Thread {//Every child only need to specify where and how to fetch files, as well as what kind of commands are accepted;

    protected abstract Vector<String> ReadCodeFromFile(String FileName);

    protected abstract Bitmap ReadImgFromFile(String FileName);

    public static class INVALID_CODE_EXCEPTION extends Exception {
        public INVALID_CODE_EXCEPTION(String s) {
            DebugMessage.set(s);
        }

        public INVALID_CODE_EXCEPTION() {
        }
    }

    public static class TargetImage {
        public Bitmap source;
        public int x1;//Starting point in whole graph
        public int y1;
        public int x2;
        public int y2;

        public TargetImage(Bitmap source, int x1, int y1, int x2, int y2) {
            this.source = source;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }

    public static class Code {//Every instance would be a valid code that interpreter can recognize
        public Vector<String[]> codes = new Vector<>();
        public Vector<String> dependency = new Vector<>();

        public Code(String[] SUPPORTED_COMMAND, Vector<String> RawCode) throws INVALID_CODE_EXCEPTION {
            for (String line : RawCode) {
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

                    throw new INVALID_CODE_EXCEPTION("Invalid " + line);
                }
            }
        }
    }


    protected Map<String, Interpreter.Code> MyCode = new HashMap<>();

    public abstract void Interpret(String FileName);

    protected void Interpret(String[] SUPPORTED_COMMAND, String FileName) throws Interpreter.INVALID_CODE_EXCEPTION {
        Vector<String> Command = ReadCodeFromFile(FileName);
        MyCode.put(FileName, new Code(SUPPORTED_COMMAND, Command));
        for (String depend : MyCode.get(FileName).dependency) {
            if (!MyCode.containsKey(depend)) {
                Interpret(SUPPORTED_COMMAND, depend);
            }
        }
    }

    //==========================================
    protected String run_arg_FileName = "";
    protected String[] run_arg_argv = null;
    protected int run_arg_depth = 0;
    protected AccessibilityService accessibilityService;

    public final void run(AccessibilityService accessibilityService, String FileName, String[] argv) throws RuntimeException {
        this.run_arg_FileName = FileName;
        this.run_arg_argv = argv;
        this.run_arg_depth = 0;
        this.accessibilityService = accessibilityService;
        this.start();
    }

    @Override
    public final void run() {
        if (this.run_arg_FileName.equals("")) {
            throw new RuntimeException("No code to run");
        }
        DebugMessage.set("RUN " + run_arg_FileName);
        this.run(run_arg_FileName, run_arg_argv, run_arg_depth);
    }

    protected void parseArguments(Map<String, String> LocalVar, String[] argv) {
        LocalVar.put("$R", "0");
        int argCount = 1;
        if (argv != null) {
            for (String arg : argv) {
                LocalVar.put("$" + argCount, arg);
                argCount++;
            }
        }
    }

    public int run(String FileName, String[] argv, int depth) throws RuntimeException {//Run code that is already read in MyCode
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
                    Arguments[i - 1] = LocalVar.get(command[i]);
                } else {
                    Arguments[i - 1] = command[i];
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
                        int result = run(Arguments[0], nextArgv, depth + 1);
                        LocalVar.put("$R", String.valueOf(result));
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
                case "Exit":
                    Thread.currentThread().interrupt();
                    break;
                case "Return":
                    return Integer.parseInt(Arguments[0]);
                default:
                    throw new RuntimeException("Cannot Recognize " + command[0]);
            }

        }
        return 0;
    }

    protected void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            DebugMessage.printStackTrace(e);
        }
    }

    protected void delay(){
        sleep(500);
    }

}
