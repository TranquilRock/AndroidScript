package com.example.androidscript.activities.basic

import com.example.androidscript.util.FileOperation
import com.example.androidscript.util.ScriptCompiler
import java.util.*

class BasicScriptCompiler : ScriptCompiler() {
    override fun compile(data: Vector<Vector<String>>) {
        FileOperation.writeWords(BasicEditor.folderName + "Run.txt", data)
    }
}