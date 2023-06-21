/** Activity to select compiler, block editor, ..so on.
 *
 * Notes:
 *  1. `getExternalFilesDir(null).getAbsolutePath()`
 *      - Will lead to an invisible dir (from studio)
 * */
package com.tranquilrock.androidscript.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tranquilrock.androidscript.R
import java.io.File
import java.util.regex.Pattern

open class SelectActivity : AppCompatActivity() {
    private lateinit var editTextNewName: EditText
    private lateinit var textViewDialogBox: TextView
    private lateinit var spinnerFileList: Spinner
    private lateinit var availableFile: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        editTextNewName = findViewById(R.id.select_new_name)
        textViewDialogBox = findViewById(R.id.select_dialog_box)
        spinnerFileList = findViewById(R.id.select_file_list)
        findViewById<View>(R.id.select_load).setOnClickListener {
            openFile(spinnerFileList.selectedItem?.toString())
        }

        findViewById<View>(R.id.select_create).setOnClickListener {
            openFile(editTextNewName.text.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, externalMediaDirs[0].absolutePath)
        availableFile = readDir(externalMediaDirs[0].absolutePath).filter { filename ->
            filename.endsWith(FILE_TYPE)
        }
        spinnerFileList.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, availableFile)
    }

    private fun openFile(fileName: String?) {
        if (fileName.isNullOrBlank() || fileName.isEmpty()) {
            textViewDialogBox.text = getString(R.string.select_activity__name_empty)
        } else if (checkFilename(fileName)) {
            textViewDialogBox.text = "TMP GOOD"
            Log.d(TAG, fileName)
        } else {
            textViewDialogBox.text = getString(R.string.select_activity__invalid_name)
        }
    }

    companion object {
        const val TAG = "SELECT_ACTIVITY"
        const val FILE_TYPE = "txt"
        private const val VALID_FILENAME_PATTERN = "([A-Za-z0-9_-]*).$FILE_TYPE"

        fun readDir(path: String): List<String> {
            val dir = File(path)
            dir.mkdir()
            return dir.list()?.toList() ?: emptyList()
        }

        fun checkFilename(FileName: String): Boolean {
            return Pattern.matches(
                VALID_FILENAME_PATTERN, FileName
            ) && FileName.length > (FILE_TYPE.length + 1)
        }
    }
}