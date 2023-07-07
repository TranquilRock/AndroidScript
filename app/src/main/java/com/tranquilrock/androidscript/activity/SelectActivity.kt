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
import com.tranquilrock.androidscript.databinding.ActivityMenuBinding
import com.tranquilrock.androidscript.databinding.ActivitySelectBinding
import com.tranquilrock.androidscript.feature.InternalStorageUser

/**
 * Activity for selecting script files or to enter file manager.
 * */
open class SelectActivity : AppCompatActivity(), InternalStorageUser {

    private lateinit var binding: ActivitySelectBinding


    private lateinit var scriptType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectLoad.setOnClickListener {
            openFile(binding.selectFileList.selectedItem?.toString())
        }
        binding.selectCreate.setOnClickListener {
            openFile(binding.selectNewName.text.toString())
        }
        binding.selectManage.setOnClickListener {
            startActivity(Intent(this, ManageActivity::class.java))
        }

        scriptType = intent.extras?.getString(SCRIPT_TYPE_KEY) ?: BASIC_SCRIPT_TYPE
    }

    override fun onResume() {
        super.onResume()
        binding.selectFileList.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, getScriptList(this, scriptType)
        )
    }

    private fun openFile(fileName: String?) {
        if (fileName.isNullOrBlank() || fileName.isEmpty()) {
            binding.selectDialogBox.text = getString(R.string.select_activity__name_empty)
        } else if (!isValidFileName(fileName)) {
            binding.selectDialogBox.text = getString(R.string.select_activity__invalid_name)
        } else {
            binding.selectDialogBox.text = ""
            if (!createScript(this, scriptType, fileName)) {
                binding.selectDialogBox.text = getString(R.string.select_activity__file_exists)
            }

            val goToEditIntent = Intent(this, EditActivity::class.java).apply {
                putExtra(SCRIPT_TYPE_KEY, scriptType)
                putExtra(SCRIPT_NAME_KEY, fileName)
            }
            startActivity(goToEditIntent)
        }
    }
}