package com.example.androidscript.activities.arknights

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import com.example.androidscript.R
import com.example.androidscript.activities.StartServiceActivity
import com.example.androidscript.floatingwidget.FloatingWidgetService
import com.example.androidscript.floatingwidget.ScreenShotService
import com.example.androidscript.uitemplate.Editor
import com.example.androidscript.util.FileOperation
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
    private val nRepetition: String
        get() {
            return when (isTillEmpty) {
                true -> "10000"
                false -> repeat.text.toString()
            }
        }
    private var resizeRatio = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ark_knights_editor)

        repeat = findViewById(R.id.Repetition)

        tillEmpty = findViewById(R.id.tillEmpty)
        tillEmpty.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            repeat.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        eatMedicine = findViewById(R.id.EatMedicine)
        eatStone = findViewById(R.id.EatStone)

        startWidgetLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    lateinit var scriptName: String
                    lateinit var arguments: ArrayList<String>
                    if (isEatStone) {
                        scriptName = "AutoFightEat.txt"
                        arguments = mutableListOf(
                            nRepetition,
                            "PressRestore.png"
                        ) as ArrayList<String>
                    } else if (isEatMedicine) {
                        scriptName = "AutoFightEat.txt"
                        arguments = mutableListOf(
                            nRepetition,
                            "PressRestoreMedicine.png"
                        ) as ArrayList<String>
                    } else {
                        scriptName = "AutoFight.txt"; arguments =
                            mutableListOf(nRepetition) as ArrayList<String>
                    }
                    this.startService(
                        Intent(this, FloatingWidgetService::class.java)
                            .putExtra(FloatingWidgetService.folderTAG, folderName)
                            .putExtra(FloatingWidgetService.scriptTAG, scriptName)
                            .putExtra(FloatingWidgetService.argsTAG, arguments)
                            .putExtra("MPM", result.data!!)
                    )
                    this.finishAffinity()
                }
            }


        findViewById<View>(R.id.set_script).setOnClickListener {
            startServiceLauncher.launch(Intent(this, StartServiceActivity::class.java))
        }
    }

    override fun resourceInitialize() {
        FileOperation.readDir(folderName)
        for (file in assets.list(folderName)!!) {
            Log.i(LOG_TAG, file)
            getResource(folderName, file!!)
        }
        resizeRatio = min(
            ScreenShotService.height,
            ScreenShotService.width
        ) / 1152.0
        // ArkKnights is height dominant.
        // If becomes width dominant use this:
        //  resizeRatio = ScreenShot.width / 2432.0
        writePress()
        writeTryPress()
    }

    private fun writePress() {
        Vector<String>().run {
            add("Tag \$Start")
            add("ClickPic $1 $resizeRatio")
            add("Wait $2")
            add("IfGreater \$R 0")
            add("JumpTo \$Start")
            FileOperation.writeLines(folderName + "Press.txt", this)
        }
    }

    private fun writeTryPress() {
        Vector<String>().run {
            add("ClickPic $1 $resizeRatio")
            add("Return \$R")
            FileOperation.writeLines(folderName + "TryPress.txt", this)
        }
    }

    companion object {
        const val folderName = "ArkKnights/"
        private val LOG_TAG = ArkKnightsEditor::class.java.simpleName
    }
}