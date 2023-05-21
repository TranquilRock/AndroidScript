package com.example.androidscript.activities.fgo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidscript.R
import com.example.androidscript.activities.StartServiceActivity
import com.example.androidscript.activities.fgo.FGOBlockAdapter.Companion.CRAFT_SKILL_BLOCK
import com.example.androidscript.activities.fgo.FGOBlockAdapter.Companion.END_BLOCK
import com.example.androidscript.activities.fgo.FGOBlockAdapter.Companion.NOBLE_PHANTASM_BLOCK
import com.example.androidscript.activities.fgo.FGOBlockAdapter.Companion.PRESTAGE_BLOCK
import com.example.androidscript.activities.fgo.FGOBlockAdapter.Companion.SKILL_BLOCK
import com.example.androidscript.activities.template.UIEditor
import com.example.androidscript.util.FileOperation
import com.example.androidscript.activities.template.UICompiler
import java.util.*

class FGOEditor : UIEditor() {
    override val folderName: String
        get() = FGOEditor.folderName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<View>(R.id.start_service).setOnClickListener {
            startServiceLauncher.launch(
                Intent(this, StartServiceActivity::class.java)
            )
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
                Toast.makeText(this.applicationContext, "Successful!!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this.applicationContext,
                    "Arguments can't be empty",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        //Set blocks' data
        if (FileOperation.findFile(folderName, fileName)) {
            blockData = FileOperation.readFromFileWords(folderName + fileName)
        } else {
            blockData.add(Vector(listOf(*preStageBlockData)))
            blockData.add(Vector(listOf(*skillBlockData)))
            blockData.add(Vector(listOf(*noblePhantasmBlockData)))
            blockData.add(Vector(listOf(*craftSkillBlockData)))
            blockData.add(Vector(listOf(*endBlockData)))
        }
        blockView.layoutManager = LinearLayoutManager(this)
        blockView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        blockView.adapter = FGOBlockAdapter(blockData)
        //SetButtonData
        buttonData.add("SelectCard")
        buttonData.add("CraftSkill")
        buttonData.add("ServantSkill")
        buttonView.layoutManager = GridLayoutManager(this, 2)
        buttonView.adapter = FGOButtonAdapter(
            blockData, buttonData, (Objects.requireNonNull(
                blockView.adapter
            ) as FGOBlockAdapter).onOrderChange
        )
    }

    override fun resourceInitialize() {
        FileOperation.readDir(folderName) //Since we access depth 2 folder later, make sure depth 1 is built;
        FileOperation.readDir(folderName + "Friend/")
        FileOperation.readDir(folderName + "Craft/")
        for (folder in arrayOf(
            folderName,
            folderName + "Friend/",
            folderName + "Craft/"
        )) {
            for (file in assets.list(folder)!!) {
                getResource(folder, file!!)
            }
        }
    }

    companion object {
        const val folderName = "FGO/"
        val preStageBlockData = arrayOf(PRESTAGE_BLOCK, "0", "0", "0", "1", "0", "0", "0", "0", "0")
        val skillBlockData = arrayOf(SKILL_BLOCK, "0", "0", "0", "0", "0", "0", "0", "0", "0")
        val craftSkillBlockData = arrayOf(CRAFT_SKILL_BLOCK, "0", "0", "0", "0")
        val noblePhantasmBlockData = arrayOf(NOBLE_PHANTASM_BLOCK, "0", "0", "0", "0")
        val endBlockData = arrayOf(END_BLOCK)
        val compiler: UICompiler = FGOUICompiler()
    }
}