package com.example.androidscript.Menu.Basic;

import com.example.androidscript.util.Compiler;
import com.example.androidscript.util.FileOperation;

import java.util.Vector;

public class BasicCompiler extends Compiler {
    @Override
    public void compile(Vector<Vector<String>> Data) {
        Vector<String> buffer = new Vector<>();
        for (Vector<String> command : Data) {
            StringBuilder tmp = new StringBuilder();
            for (int z = 0; z < command.size() - 1; z++) {
                tmp.append(command.get(z));
                tmp.append(" ");
            }
            tmp.append(command.get(command.size() - 1));
            buffer.add(tmp.toString());
        }
        FileOperation.writeToFile("Run.txt", buffer);
    }
}
