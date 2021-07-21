package com.example.androidscript.util.Interpreter;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Bitmap;

import com.example.androidscript.util.AutoClick;
import com.example.androidscript.util.DebugMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

public abstract class Interpreter extends Thread {//Every child only need to specify where and how to fetch files, as well as what kind of commands are accepted;

    protected abstract String ReadCodeFromFile(String FileName);

    protected abstract Bitmap ReadImgFromFile(String FileName);

    public static class INVALID_CODE_EXCEPTION extends Exception {
    }

    public class Code {//Every instance would be a valid code that interpreter can recognize
        public Vector<String[]> codes = new Vector<>();
        public Vector<String> dependency = new Vector<>();

        public Code(String[] SUPPORTED_COMMAND, String RawCode) throws INVALID_CODE_EXCEPTION {
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


    protected Map<String, Interpreter.Code> MyCode = new HashMap<>();

    public void Interpret(String[] SUPPORTED_COMMAND, String FileName) throws Interpreter.INVALID_CODE_EXCEPTION {
        String Command = ReadCodeFromFile(FileName);
        MyCode.put(FileName, new Interpreter.Code(SUPPORTED_COMMAND, Command));
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
        run_arg_FileName = "";
        run_arg_argv = null;
        run_arg_depth = 0;
        this.accessibilityService = null;
    }

    @Override
    public final void run() {
        if (this.run_arg_FileName.equals("")) {
            throw new RuntimeException("No code to run");
        }
        this.run(run_arg_FileName, run_arg_argv, run_arg_depth);
    }

    public void run(String FileName, String[] argv, int depth) throws RuntimeException {//Run code that is already read in MyCode
        if (depth > 3 || !MyCode.containsKey(FileName)) {
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
            for (int i = 1; i < command.length; i++) {//Substitution
                if (command[i].charAt(0) == '$' && LocalVar.containsKey(command[i])) {
                    command[i] = LocalVar.get(i);
                }
            }
            switch (command[0]) {
                case "Click":
                    AutoClick.Click(accessibilityService,Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                    LocalVar.put("$R", "0");
                    break;
                case "Compare":
                    DebugMessage.set("Interpreter Screenshot not done yet!\n");
                    LocalVar.put("$R", "0");
                    break;
                case "JumpToLine":
                    commandIndex = Integer.parseInt(command[1]);
                    LocalVar.put("$R", "0");
                    break;
                case "Wait":
                    try {
                        Thread.sleep(Integer.parseInt(command[1]));
                        LocalVar.put("$R", "0");
                    } catch (InterruptedException e) {
                        DebugMessage.set("Waked up unexpected!\n");
                        LocalVar.put("$R", "1");
                    }
                    break;
                case "Call":
                    if (MyCode.containsKey(command[1])) {
                        String[] nextArgv = new String[command.length - 2];
                        for (int j = 2; j < command.length; j++) {
                            nextArgv[j - 2] = command[j];
                        }
                        run(command[1], nextArgv, depth + 1);
                        LocalVar.put("$R", "0");
                    } else {
                        LocalVar.put("$R", "1");
                    }
                    break;
                case "IfGreater":
                    if (Integer.parseInt(command[1]) <= Integer.parseInt(command[2])) {//Failed, skip next line
                        commandIndex++;
                        LocalVar.put("$R", "1");
                    } else {
                        LocalVar.put("$R", "0");
                    }
                    break;
                case "IfSmaller":
                    if (Integer.parseInt(command[1]) >= Integer.parseInt(command[2])) {//Failed, skip next line
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
