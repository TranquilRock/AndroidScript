package com.example.androidscript.util;

import android.graphics.Bitmap;

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
        public INVALID_CODE_EXCEPTION() {}
    }

    public static class TargetImage {
        public Bitmap source;
        public int x1;//Starting point in whole graph
        public int y1;
        public int x2;
        public int y2;
        public int threshold;
        public TargetImage(Bitmap source, int x1, int y1, int x2, int y2,int threshold) {
            this.source = source;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.threshold = threshold;
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
                        if (command[0].equals("Call")) {
                            dependency.add(command[1]);
                        }
                        codes.add(command);
                        break;
                    }
                }
                if (!valid) {
                    throw new INVALID_CODE_EXCEPTION("Invalid code \"" + line+"\"");
                }
            }
        }
    }

    protected String ScriptName = null;

    protected Map<String, Interpreter.Code> MyCode = new HashMap<>();

    public abstract void Interpret(String FileName);

    protected void Interpret(String[] SUPPORTED_COMMAND, String FileName){
        if(ScriptName == null){
            ScriptName = FileName;
        }
        Vector<String> Command = ReadCodeFromFile(FileName);
        try {
            MyCode.put(FileName, new Code(SUPPORTED_COMMAND, Command));
        } catch (Exception e) {
            DebugMessage.set("Bug in " + FileName);
            DebugMessage.printStackTrace(e);
        }
        for (String depend : MyCode.get(FileName).dependency) {
            if (!MyCode.containsKey(depend)) {
                Interpret(SUPPORTED_COMMAND, depend);
            }
        }
    }

    //==========================================
    private String[] run_arg_argv = null;
    private int run_arg_depth = -1;

    public final void runCode(String[] argv) throws RuntimeException {
        this.run_arg_argv = argv;
        this.run_arg_depth = 0;
        this.start();
    }

    @Override
    public final void run() {
        assert(ScriptName != null);
        assert(run_arg_depth != -1);
        this.run(ScriptName, run_arg_argv, run_arg_depth);
    }

    public abstract int run(String FileName, String[] argv, int depth) throws RuntimeException;

    //==========Helper=========================================

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
