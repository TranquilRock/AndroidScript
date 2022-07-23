package com.example.androidscript.activities

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Spinner
import com.example.androidscript.util.DebugMessage
import com.example.androidscript.util.FileOperation
import android.os.Bundle
import android.view.View
import com.example.androidscript.R
import android.widget.EditText
import com.example.androidscript.util.SpnMaker
import android.widget.TextView
import java.lang.Exception
import java.util.*
import java.util.regex.Pattern

// {getExternalFilesDir(null).getAbsolutePath() + "/}This would lead to an invisible dir (from studio)
open class SelectFileActivity : AppCompatActivity() {
    private lateinit var etNewName: EditText
    private lateinit var output: TextView
    private lateinit var select: Spinner
    private lateinit var availableFile: Vector<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_file)
        val classPath = intent.getStringExtra("next_destination")!!
            .split("\\.".toRegex()).toTypedArray()
        DebugMessage.set(classPath[classPath.size - 2] + "/")
        try {
            availableFile =
                FileOperation.browseAvailableFile(classPath[classPath.size - 2] + "/", ".blc")
        } catch (e: Exception) {
            DebugMessage.printStackTrace(e)
            availableFile = Vector()
            availableFile.add("Empty")
        }
        setupElements()
    }

    private fun setupElements() {
        etNewName = findViewById(R.id.et_New_Name)
        output = findViewById(R.id.output) //Show some massage to user
        select = SpnMaker.fromStringWithActivity(R.id.spinner_Select_Script, this, availableFile)

        findViewById<View>(R.id.btn_To_Load).setOnClickListener {
            val fileName = select.selectedItem.toString()
            if (fileName != "") {
                switchToEdit(fileName)
            } else {
                output.text = "必須輸入檔名"
            }
        }
        findViewById<View>(R.id.btn_To_Create).setOnClickListener {
            val fileName = etNewName.text.toString()
            if (fileName != "") {
                switchToEdit(fileName)
            } else {
                output.text = "必須輸入檔名"
            }
        }
    }

    private fun switchToEdit(filePath: String) {
        val fileName = filePath.split("/".toRegex()).last()
        output.text = fileName
        if (checkFilename(fileName)) {
            val intent = Intent(this,
                intent.getStringExtra("next_destination")?.let { Class.forName(it) })
            intent.putExtra("FileName", fileName)
            val orientation = getIntent().getStringExtra("Orientation")
            if (orientation != null) {
                intent.putExtra("Orientation", orientation)
            }
            startActivity(intent)
        } else {
            output.text = "僅能包含英文字母、數字與底線\n且為txt檔案格式\n例如:a_1-B.blc"
        }
    }

    private fun checkFilename(FileName: String): Boolean {
        return Pattern.matches(SUPPORTED_FILE_NAME_PATTERN, FileName) && FileName.length > 4
    }

    // TODO refresh files onResume

    companion object {
        const val SUPPORTED_FILE_NAME_PATTERN = "([A-Za-z0-9_-]*).blc"
    }
}