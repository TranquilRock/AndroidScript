/**
 * Activity to select or create script files(.blc).
 * The files will not be reachable from users directly.
 * Data will be stored in `/data/user/0/com.tranquilrock.androidscript/app_BASIC`
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
import com.tranquilrock.androidscript.App.Companion.BASIC_SCRIPT_TYPE
import com.tranquilrock.androidscript.App.Companion.SCRIPT_TYPE_KEY
import com.tranquilrock.androidscript.App.Companion.SCRIPT_NAME_KEY
import com.tranquilrock.androidscript.R
import com.tranquilrock.androidscript.feature.InternalStorageUser


open class SelectActivity : AppCompatActivity(), InternalStorageUser {

    private lateinit var editTextNewName: EditText
    private lateinit var textViewDialogBox: TextView
    private lateinit var spinnerFileList: Spinner
    private lateinit var buttonLoad: View
    private lateinit var buttonCreate: View
    private lateinit var buttonManage: View

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
        buttonManage = findViewById(R.id.select_manage)

        buttonLoad.setOnClickListener {
            openFile(spinnerFileList.selectedItem?.toString())
        }
        buttonCreate.setOnClickListener {
            openFile(editTextNewName.text.toString())
        }
        buttonManage.setOnClickListener {
            startActivity(Intent(this, ManageActivity::class.java))
        }

        scriptType = intent.extras?.getString(SCRIPT_TYPE_KEY) ?: BASIC_SCRIPT_TYPE
    }

    override fun onResume() {
        super.onResume()
        availableFile = getScriptList(this, scriptType)
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
            if (!createScript(this, scriptType, fileName)) {
                textViewDialogBox.text = getString(R.string.select_activity__file_exists)
            }

            val goToEditIndent = Intent(this, EditActivity::class.java).apply {
                putExtra(SCRIPT_TYPE_KEY, scriptType)
                putExtra(SCRIPT_NAME_KEY, fileName)
            }
            startActivity(goToEditIndent)
        }
    }
}