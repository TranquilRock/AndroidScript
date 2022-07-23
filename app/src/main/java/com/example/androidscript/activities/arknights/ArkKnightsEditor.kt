package com.example.androidscript.activities.arknights

import android.content.Intent
import com.example.androidscript.util.DebugMessage
import com.example.androidscript.util.FileOperation
import android.os.Bundle
import com.example.androidscript.R
import com.example.androidscript.floatingwidget.FloatingWidgetService
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import android.widget.CompoundButton
import com.example.androidscript.floatingwidget.ScreenShotService
import com.example.androidscript.activities.StartServiceActivity
import android.view.*
import com.example.androidscript.uitemplate.Editor
import java.lang.Exception
import java.lang.Integer.min
import java.util.*

class ArkKnightsEditor : Editor() {
    private lateinit var tillEmpty: SwitchCompat
    private lateinit var eatMedicine: SwitchCompat
    private lateinit var eatStone: SwitchCompat
    private lateinit var repeat: EditText
    private val isTillEmpty: Boolean
        get() = tillEmpty.isChecked
    private val isEatMedicine: Boolean
        get() = eatMedicine.isChecked
    private val isEatStone: Boolean
        get() = eatStone.isChecked
    private val nRepetition: Int
        get() {
            return when (isTillEmpty) {
                true -> 10000
                false -> repeat.text.toString().toInt()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ark_knights_editor)

        repeat = findViewById(R.id.Repetition)

        tillEmpty = findViewById(R.id.tillEmpty)
        tillEmpty.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                repeat.visibility = View.GONE
            } else {
                repeat.visibility = View.VISIBLE
            }
        }

        eatMedicine = findViewById(R.id.EatMedicine)
        eatStone = findViewById(R.id.EatStone)

        findViewById<View>(R.id.set_script).setOnClickListener{
            if (!StartServiceActivity.isAuthorized(this)) {
                this.startActivity(
                    Intent(this, StartServiceActivity::class.java).putExtra(
                        "Orientation",
                        "Landscape"
                    )
                )
            } else {
                lateinit var scriptName: String
                lateinit var arguments: ArrayList<String>
                if (isEatStone) {
                    scriptName = "AutoFightEat.txt"
                    arguments = mutableListOf(
                        nRepetition.toString(),
                        "PressRestore.png"
                    ) as ArrayList<String>
                } else if (isEatMedicine) {
                    scriptName = "AutoFightEat.txt"
                    arguments = mutableListOf(
                        nRepetition.toString(),
                        "PressRestoreMedicine.png"
                    ) as ArrayList<String>
                } else {
                    scriptName = "AutoFight.txt"; arguments =
                        mutableListOf(nRepetition.toString()) as ArrayList<String>
                }
                this.startService(
                    Intent(this, FloatingWidgetService::class.java)
                        .putExtra(FloatingWidgetService.folderTAG, folderName)
                        .putExtra(FloatingWidgetService.scriptTAG, scriptName)
                        .putExtra(FloatingWidgetService.argsTAG, arguments)
                )
                this.finishAffinity()
            }
        }
    }


    private var resizeRatio = 0.0
    override fun resourceInitialize() {
        try {
            FileOperation.readDir(folderName)
            for (file in assets.list(folderName)!!) {
                DebugMessage.set(file)
                getResource(folderName, file!!)
            }
        } catch (e: Exception) {
            DebugMessage.printStackTrace(e)
        }
        resizeRatio = min(
            ScreenShotService.height,
            ScreenShotService.width
        ) / 1152.0 //ArkKnights seems to be height dominate.
        // resizeRatio = ScreenShot.getWidth() / 2432.0;
        writePress()
        writeTryPress()
    }

    private fun writePress() {
        val buffer = Vector<String>()
        buffer.add("Tag \$Start")
        buffer.add("ClickPic $1 $resizeRatio")
        buffer.add("Wait $2")
        buffer.add("IfGreater \$R 0")
        buffer.add("JumpTo \$Start")
        FileOperation.writeLines(folderName + "Press.txt", buffer)
    }

    private fun writeTryPress() {
        val buffer = Vector<String>()
        buffer.add("ClickPic $1 $resizeRatio")
        buffer.add("Return \$R")
        FileOperation.writeLines(folderName + "TryPress.txt", buffer)
    }

    companion object {
        const val folderName = "ArkKnights/"
    }
}