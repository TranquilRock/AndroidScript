package com.example.androidscript.util;

import android.graphics.Bitmap;
import android.util.Log;
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
    public static final String ImgFormat = "([A-Za-z0-9_/-]*).(jpg|png)";
    public static final String SptFormat = "([A-Za-z0-9_-]*).txt";
    public static final String VarFormat = "\\$([A-Za-z0-9_-]*)";
    public static final String IntFormat = "[0-9]*";
    public static final String FloatFormat = "[0-9.]*";
    public static final String IntVarFormat = "(" + IntFormat + "||" + VarFormat + ")";
    public static final String ImgVarFormat = "(" + ImgFormat + "||" + VarFormat + ")";
    public static final String AnyFormat = "[a-zA-Z.0-9 $]*";
    public static final String[] SUPPORTED_COMMAND = {
            "Exit",
            "JumpTo " + IntVarFormat,
            "Wait " + IntVarFormat,
            "Call " + SptFormat,
            "Tag " + VarFormat,
            "Return " + IntVarFormat,
            "ClickPic " + ImgVarFormat + " " + FloatFormat,
            "Click " + IntVarFormat + " " + IntVarFormat,
            "CallArg " + SptFormat + " " + AnyFormat,
            "IfGreater " + IntVarFormat + " " + IntVarFormat,
            "IfSmaller " + IntVarFormat + " " + IntVarFormat,
            "Add " + VarFormat + " " + IntVarFormat,
            "Subtract " + VarFormat + " " + IntVarFormat,
            "Var " + VarFormat + " " + IntVarFormat,
            "Check " + VarFormat + " " + VarFormat + " " + IntVarFormat,
            "Swipe " + IntVarFormat + " " + IntVarFormat + " " + IntVarFormat + " " + IntVarFormat,
            "Compare " + IntVarFormat + " " + IntVarFormat + " " + IntVarFormat + " " + IntVarFormat + " " + ImgVarFormat,
    };

    public boolean running = false;
    private final String ScriptName;
    private final String ScriptFolderName;
    private final FloatingWidgetService.Bulletin board;

    public Interpreter(String _ScriptFolderName, String _FileName, FloatingWidgetService.Bulletin _board) {
        this.ScriptName = _FileName;
        this.ScriptFolderName = _ScriptFolderName;
        this.board = _board;
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
        board.Announce("IDLE");
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

        for (int commandIndex = 0; commandIndex < codeLength && this.running; commandIndex++) {
            String[] Command = (MyCode.get(FileName).codes.get(commandIndex));
            String[] Arguments = new String[Command.length - 1];
            System.arraycopy(Command, 1, Arguments, 0, Command.length - 1);
            varsSubstitution(LocalVar, Command[0], Arguments);
            board.Announce(Command[0]);
            //=====================================================
            StringBuilder tt = new StringBuilder();
            tt.append(Command[0] + " ");
            for (String z : Arguments) {
                tt.append(z + " ");
            }
            DebugMessage.set(commandIndex + "  " + tt.toString());
            //=====================================================
            switch (Command[0]) {
                case "Exit":
                    this.running = false;
                    return 1;
                case "JumpTo":
                    commandIndex = Integer.parseInt(Arguments[0]) - 1;//One-based
                    break;
                case "Wait":
                    sleep(Integer.parseInt(Arguments[0]));
                    break;
                case "Call":
                    LocalVar.put("$R", String.valueOf(run(Arguments[0], null, depth + 1)));
                    board.Announce(depth + "  " + FileName);
                    break;
                case "Tag":
                    break;
                case "Return":
                    return Integer.parseInt(Arguments[0]);
                case "ClickPic":
                    Bitmap tmp = ReadImgFromFile(Arguments[0]);
                    Point target = ImageHandler.findLocation(ScreenShot.Shot(), tmp, Double.valueOf(Arguments[1]));
                    if (target != null) {
                        delay();
                        DebugMessage.set("Clicking Picture:" + target.x + " " + target.y);
                        AutoClick.Click((int) target.x, (int) target.y);
                        LocalVar.put("$R", "0");
                    } else {
                        LocalVar.put("$R", "1");
                    }
                    break;
                case "Click":
                    delay();
                    AutoClick.Click(Integer.parseInt(Arguments[0]), Integer.parseInt(Arguments[1]));
                    break;
                case "CallArg":
                    String[] nextArgv = new String[Command.length - 2];
                    System.arraycopy(Arguments, 1, nextArgv, 0, Command.length - 2);
                    LocalVar.put("$R", String.valueOf(run(Arguments[0], nextArgv, depth + 1)));
                    board.Announce(depth + "  " + FileName);
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
                    LocalVar.put(Arguments[0], String.valueOf(Integer.parseInt(LocalVar.get(Arguments[0])) - Integer.parseInt(Arguments[1])));
                    break;
                case "Var":
                    LocalVar.put(Arguments[0], Arguments[1]);
                    break;
                case "Check":
                    if(ImageHandler.checkColor(ScreenShot.Shot(),Integer.parseInt(Arguments[0]),Integer.parseInt(Arguments[1]), Integer.decode(Arguments[2]))){
                        LocalVar.put("$R", "0");
                    } else {
                        LocalVar.put("$R", "1");
                    }
                    break;
                case "Swipe":
                    delay();
                    AutoClick.Swipe(Integer.parseInt(Arguments[0]), Integer.parseInt(Arguments[1]), Integer.parseInt(Arguments[2]), Integer.parseInt(Arguments[3]));
                    break;
                case "Compare":
                    ScreenShot.Shot();//Empty shot to make sure Image be the newest.
                    int Similarity = ScreenShot.compare(ReadImgFromFile(Arguments[4]), Integer.parseInt(Arguments[0]), Integer.parseInt(Arguments[1]), Integer.parseInt(Arguments[2]), Integer.parseInt(Arguments[3]));
                    LocalVar.put("$R", String.valueOf(Similarity));
                    break;
                default:
                    throw new RuntimeException("Cannot Recognize " + Command[0]);
            }
        }
        return 0;
    }
    //==========Helper=========================================

    protected static void varsSubstitution(Map<String, String> LocalVar, String command, String[] Arguments) {
        if (Arguments[0].charAt(0) == '$'
                && !command.equals("Var")
                && !command.equals("Subtract")
                && !command.equals("Add")) {
            Arguments[0] = LocalVar.get(Arguments[0]);
        }
        for (int z = 1; z < Arguments.length; z++) {
            if (Arguments[z].charAt(0) == '$') {//There might be Var command, that should replace $V
                Arguments[z] = LocalVar.get(Arguments[z]);
            }
        }
    }

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
