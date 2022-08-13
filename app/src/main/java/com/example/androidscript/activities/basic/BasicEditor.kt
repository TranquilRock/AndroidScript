package com.example.androidscript.activities.basic

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidscript.R
import com.example.androidscript.activities.StartServiceActivity
import com.example.androidscript.floatingwidget.Interpreter
import com.example.androidscript.uitemplate.UIEditor
import com.example.androidscript.util.Commands.getCommandLength
import com.example.androidscript.util.FileOperation
import java.util.*

class BasicEditor : UIEditor() {

    override val folderName: String
        get() = BasicEditor.folderName

    override fun onCreate(savedInstanceState: Bundle?) {
        findViewById<View>(R.id.start_service).setOnClickListener {
            startServiceLauncher.launch(Intent(this, StartServiceActivity::class.java))
        }

        findViewById<View>(R.id.save_file).setOnClickListener {
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
            Log.i(LOG_TAG, "No such file")
            blockData.add(Vector(Blocks["Return"]!!))
        }
        for (blc in blockData) {
            Log.i(LOG_TAG, blc.joinToString(":"))
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
                (blockView.adapter as BasicBlockAdapter).onOrderChange,
                buttonData,
            )
        super.onCreate(savedInstanceState)
    }

    override fun resourceInitialize() {
        FileOperation.readDir(folderName)
        val allFiles = assets.list(folderName) //List all file
        for (file in allFiles!!) {
            getResource(folderName, file!!)
        }
    }

    companion object {
        private val LOG_TAG = BasicEditor::class.java.simpleName

        const val folderName = "Basic/"
        var Blocks: MutableMap<String, Vector<String>> = HashMap()
        var compiler = BasicScriptCompiler()


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
}