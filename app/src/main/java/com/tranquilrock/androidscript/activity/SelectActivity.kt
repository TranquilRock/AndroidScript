/** Activity to select compiler, block editor, ..so on.
 * The files will not be reachable by users directly.
 * /data/user/0/com.tranquilrock.androidscript/app_BASIC
 * */
package com.tranquilrock.androidscript.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.tranquilrock.androidscript.R
import com.tranquilrock.androidscript.activity.editor.EditActivity
import java.util.regex.Pattern


open class SelectActivity : AppCompatActivity(), UseInternalStorage {

    private lateinit var editTextNewName: EditText
    private lateinit var textViewDialogBox: TextView
    private lateinit var spinnerFileList: Spinner
    private lateinit var buttonLoad: View
    private lateinit var buttonCreate: View

    private lateinit var scriptType: String
    private lateinit var availableFile: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)
        editTextNewName = findViewById(R.id.select_new_name)
        textViewDialogBox = findViewById(R.id.select_dialog_box)
        spinnerFileList = findViewById(R.id.select_file_list)
        buttonLoad = findViewById(R.id.select_load)
        buttonCreate = findViewById(R.id.select_create)

        buttonLoad.setOnClickListener {
            openFile(spinnerFileList.selectedItem?.toString())
        }
        buttonCreate.setOnClickListener {
            openFile(editTextNewName.text.toString())
        }

        scriptType = intent.extras?.getString(TYPE_KEY) ?: basicType


        deleteFile(this, basicType, META_FILE) // For test only
        if (createScriptFile(this, basicType, META_FILE)) {
            /* Basic not initialized yet*/
            val basicMeta = Gson().toJson(
                listOf(
                    listOf(
                        "GGWP",
                        listOf("Spinner", "1", "2", "3"),
                        listOf("EditText", "Placeholder")
                    ),
                    listOf(
                        "Exit"
                    )
                )
            )
            writeScriptFile(
                this,
                scriptType,
                "meta.json",
                listOf(basicMeta.toString())
            )
        }

    }

    override fun onResume() {
        super.onResume()
        availableFile = getScriptFolder(this, scriptType).list()?.filter { filename ->
            filename.endsWith(FILE_TYPE)
        }?.map { a -> a.removeSuffix(FILE_TYPE) } ?: emptyList()
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
            if (!createScriptFile(this, scriptType, fileName + FILE_TYPE)) {
                textViewDialogBox.text = getString(R.string.select_activity__file_exists)
            }
            val goToEditIndent = Intent(this, EditActivity::class.java)
            goToEditIndent.putExtra(EditActivity.SCRIPT_TYPE_KEY, scriptType)
            goToEditIndent.putExtra(EditActivity.SCRIPT_NAME_KEY, fileName)
            startActivity(goToEditIndent)
        }
    }

    companion object {
        private val TAG = SelectActivity::class.java.simpleName
        const val FILE_TYPE = ".txt"
        private const val VALID_FILENAME_PATTERN = "([A-Za-z0-9_-]*)"
        private const val basicType = "BASIC"
        const val TYPE_KEY = "TYPE"

        fun isValidFileName(FileName: String): Boolean {
            return Pattern.matches(
                VALID_FILENAME_PATTERN, FileName
            ) && FileName.isNotEmpty()
        }
    }
}