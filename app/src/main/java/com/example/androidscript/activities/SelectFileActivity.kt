package com.example.androidscript.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidscript.R
import com.example.androidscript.util.FileOperation
import java.util.*
import java.util.regex.Pattern

open class SelectFileActivity : AppCompatActivity() {
    private lateinit var etNewName: EditText
    private lateinit var output: TextView
    private lateinit var select: Spinner
    private lateinit var availableFile: Vector<String>
    private lateinit var classPath: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_file)
        classPath = intent.getStringExtra("next_destination")!!
            .split("\\.".toRegex()).toTypedArray()

        setupElements()
    }

    override fun onResume() {
        availableFile =
            FileOperation.browseAvailableFile(classPath[classPath.size - 2] + "/", ".blc")
                ?: Vector<String>()
        if (availableFile.size == 0) {
            availableFile.add("Empty")
        }
        select.also {
            it.adapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, availableFile)
        }

        super.onResume()
    }

    private fun setupElements() {
        etNewName = findViewById(R.id.et_New_Name)
        output = findViewById(R.id.output) //Show some massage to user
        select = findViewById(R.id.spinner_Select_Script)
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

    companion object {
        const val SUPPORTED_FILE_NAME_PATTERN = "([A-Za-z0-9_-]*).blc"
    }

    /** Dev note
     * {getExternalFilesDir(null).getAbsolutePath() + "/}
     * This would lead to an invisible dir (from studio)
     * */
}