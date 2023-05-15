package com.example.androidscript.activities.basic

import com.example.androidscript.util.FileOperation
import com.example.androidscript.activities.template.UICompiler
import java.util.*

class BasicUICompiler : UICompiler() {
    override fun compile(data: Vector<Vector<String>>) {
        FileOperation.writeWords(BasicEditor.folderName + "Run.txt", data)
    }
}