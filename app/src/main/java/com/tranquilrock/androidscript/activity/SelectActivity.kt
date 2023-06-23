/** Activity to select compiler, block editor, ..so on.
 * The files will not be reachable by users directly.
 * /data/user/0/com.tranquilrock.androidscript/app_BASIC
 * */
package com.tranquilrock.androidscript.activity

import android.content.Context
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

    private val editTextNewName: EditText
        get() = findViewById(R.id.select_new_name)
    private val textViewDialogBox: TextView
        get() = findViewById(R.id.select_dialog_box)
    private val spinnerFileList: Spinner
        get() = findViewById(R.id.select_file_list)
    private val buttonLoad: View
        get() = findViewById(R.id.select_load)
    private val buttonCreate: View
        get() = findViewById(R.id.select_create)

    private lateinit var scriptType: String
    private lateinit var availableFile: List<String>

    private val scriptFolder: File
        get() = this.getDir(scriptType, Context.MODE_PRIVATE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        buttonLoad.setOnClickListener {
            openFile(spinnerFileList.selectedItem?.toString())
        }

        buttonCreate.setOnClickListener {
            openFile(editTextNewName.text.toString())
        }

        scriptType = intent.extras?.getString("TYPE") ?: "BASIC"
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, scriptFolder.absolutePath)
        for (c in scriptFolder.list()!!){
            Log.d(TAG, c)
        }
        availableFile = scriptFolder.list()?.filter { filename ->
            filename.endsWith(FILE_TYPE)
        } ?: emptyList()
        spinnerFileList.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, availableFile)
    }

    private fun openFile(fileName: String?) {
        if (fileName.isNullOrBlank() || fileName.isEmpty()) {
            textViewDialogBox.text = getString(R.string.select_activity__name_empty)
        } else if (!isValidFileName(fileName)) {
            textViewDialogBox.text = getString(R.string.select_activity__invalid_name)
        } else {
            textViewDialogBox.text = ""
            if (!File(scriptFolder, fileName + FILE_TYPE).createNewFile()) {
                textViewDialogBox.text = getString(R.string.select_activity__file_exists)
            }
        }
    }

    companion object {
        const val TAG = "SELECT_ACTIVITY"
        const val FILE_TYPE = ".txt"
        private const val VALID_FILENAME_PATTERN = "([A-Za-z0-9_-]*)"


        fun isValidFileName(FileName: String): Boolean {
            return Pattern.matches(
                VALID_FILENAME_PATTERN, FileName
            ) && FileName.isNotEmpty()
        }
    }
}