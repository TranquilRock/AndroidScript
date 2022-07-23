package com.example.androidscript.activities.basic

import com.example.androidscript.util.DebugMessage
import com.example.androidscript.util.FileOperation
import com.example.androidscript.uitemplate.UIEditor
import android.os.Bundle
import com.example.androidscript.R
import android.widget.Toast
import android.view.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidscript.floatingwidget.Interpreter
import java.lang.RuntimeException
import java.util.*

class BasicEditor : UIEditor() {
    override val folderName: String
        get() = BasicEditor.folderName
    companion object {
        const val folderName = "Basic/"
        var Blocks: MutableMap<String, Vector<String>> = HashMap()
        var compiler = BasicScriptCompiler()

        private fun getCommandLength(Command: String): Int {
            when (Command) {
                "Exit" -> return 0
                "Log", "JumpTo", "Wait", "Call", "Tag", "Return" -> return 1
                "ClickPic", "Click", "CallArg", "IfGreater", "IfSmaller", "Add", "Subtract", "Var" -> return 2
                "Check" -> return 3
                "Swipe" -> return 4
                "Compare" -> return 5
            }
            throw RuntimeException("Unrecognized command!")
        }

        init {
            for (command in Interpreter.SUPPORTED_COMMAND) {
                val key: String = command.split(" ".toRegex()).toTypedArray()[0]
                val value = Vector<String>()
                value.add(key)
                for (z in 0 until getCommandLength(key)) value.add("")
                Blocks[key] = value
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val orientation = intent.getStringExtra("Orientation")!!
        findViewById<View>(R.id.start_service).setOnClickListener{
            startServiceHandler(orientation)
        }
        findViewById<View>(R.id.save_file).setOnClickListener{
            var syntaxFlag = true
            for (Line in blockData) {
                if (Line!!.contains("")) {
                    syntaxFlag = false
                    break
                }
            }
            if (syntaxFlag) {
                FileOperation.writeWords(folderName + fileName, blockData)
                compiler.compile(blockData)
                Toast.makeText(this.applicationContext, "Successful!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this.applicationContext,
                    "Arguments can't be empty",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        //SetBlockData
        if (FileOperation.findFile(folderName, fileName)) {
            blockData = FileOperation.readFromFileWords(folderName + fileName)
        } else {
            DebugMessage.set("No such file")
            blockData.add(Vector(Blocks["Return"]!!))
        }
        for (blc in blockData) {
            DebugMessage.set(blc.joinToString(":"))
        }
        blockView.layoutManager = LinearLayoutManager(this)
        blockView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        blockView.adapter = BasicBlockAdapter(blockData)
        //SetButtonData
        for (key in Blocks.keys) {
            buttonData.add(key)
        }
        buttonView.layoutManager = GridLayoutManager(this, 2)
        buttonView.adapter =
            BasicButtonAdapter(
                blockData,
                buttonData,
                (blockView.adapter as BasicBlockAdapter).onOrderChange,
            )
    }

    override fun resourceInitialize() {
        FileOperation.readDir(folderName)
        val allFiles = assets.list(folderName) //List all file
        for (file in allFiles!!) {
            getResource(folderName, file!!)
        }
    }
}