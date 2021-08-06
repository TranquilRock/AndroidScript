package com.example.androidscript.Menu.Basic;

import com.example.androidscript.util.ScriptCompiler;
import com.example.androidscript.util.DebugMessage;
import com.example.androidscript.util.FileOperation;

import java.util.Vector;

public class BasicScriptCompiler extends ScriptCompiler {
    @Override
    public void compile(Vector<Vector<String>> Data) {
        FileOperation.writeWords(BasicEditor.FolderName + "Run.txt", Data);
    }
}
