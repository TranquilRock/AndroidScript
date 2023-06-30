/**
 * UI for editing scripts.
 * This will read from scriptClass.meta to get blocks and buttons definition, for example:
 * [
 *      ["Exit"],
 *      ["Call", ["EditText", "Placeholder"]],
 *      ...
 * ]
 * */
package com.tranquilrock.androidscript.activity.editor

import android.content.Intent
import android.media.projection.MediaProjectionManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tranquilrock.androidscript.R
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tranquilrock.androidscript.feature.InternalStorageUser
import com.tranquilrock.androidscript.activity.editor.component.BlockAdapter
import com.tranquilrock.androidscript.activity.editor.component.ButtonAdapter
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.result.contract.ActivityResultContracts
import com.tranquilrock.androidscript.App.Companion.BLOCK_DATA_KEY
import com.tranquilrock.androidscript.App.Companion.BLOCK_META_KEY
import com.tranquilrock.androidscript.App.Companion.MEDIA_PROJECTION_KEY
import com.tranquilrock.androidscript.App.Companion.ORIENTATION_KEY
import com.tranquilrock.androidscript.App.Companion.SCRIPT_NAME_KEY
import com.tranquilrock.androidscript.App.Companion.SCRIPT_TYPE_KEY
import com.tranquilrock.androidscript.feature.PermissionRequester
import com.tranquilrock.androidscript.service.WidgetService

class EditActivity : AppCompatActivity(), InternalStorageUser, PermissionRequester {
    private lateinit var toggleOrientation: ToggleButton
    private lateinit var blockView: RecyclerView
    private lateinit var buttonView: RecyclerView
    private lateinit var blockData: ArrayList<ArrayList<String>>
    private lateinit var blockMeta: Array<Array<Any>>
    private lateinit var fileName: String
    private lateinit var scriptClass: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        toggleOrientation = findViewById(R.id.toggle_orientation)

        buttonView = findViewById(R.id.edit_button_grid)
        blockView = findViewById(R.id.edit_code_grid)

        scriptClass = intent.getStringExtra(SCRIPT_TYPE_KEY)!!
        fileName = intent.getStringExtra(SCRIPT_NAME_KEY)!!

        blockMeta = getMetadata(this, scriptClass)
        blockData = getScript(this, scriptClass, fileName)

        val mediaProjectionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                startWidgetService(result.data!!)
            } else {
                Toast.makeText(
                    this, "Please Enable Media Projection", Toast.LENGTH_SHORT
                ).show()
            }
        }

        findViewById<View>(R.id.start_service).setOnClickListener {
            for (block in blockData) {
                if (block.contains("")) {
                    Toast.makeText(
                        this, "Block not filled.", Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }

            if (!canDrawOverlays(this)) {
                requestDrawOverlays(this)
            } else if (!accessibilityEnabled(contentResolver)) {
                requestAccessibility(this)
            } else {
                mediaProjectionLauncher.launch(
                    getSystemService(MediaProjectionManager::class.java).createScreenCaptureIntent()
                )
            }
        }

        findViewById<View>(R.id.save_file).setOnClickListener {
            saveScript(this, scriptClass, fileName, blockData)
            Toast.makeText(
                this, "File Saved", Toast.LENGTH_LONG
            ).show()
        }

        blockView.run {
            layoutManager = LinearLayoutManager(this@EditActivity)
            addItemDecoration(
                DividerItemDecoration(
                    this@EditActivity, DividerItemDecoration.VERTICAL
                )
            )
            adapter = BlockAdapter(blockMeta, blockData)
        }

        buttonView.run {
            layoutManager = GridLayoutManager(this@EditActivity, 2)
            buttonView.adapter = ButtonAdapter(
                blockMeta,
                blockData,
                (blockView.adapter as BlockAdapter).onOrderChange,
            )
        }
    }

    private fun startWidgetService(data: Intent) {
        val startServiceIntent = Intent(this, WidgetService::class.java).apply {
            putExtra(SCRIPT_TYPE_KEY, scriptClass)
            putExtra(MEDIA_PROJECTION_KEY, data)
            putExtra(BLOCK_DATA_KEY, blockData)
            putExtra(BLOCK_META_KEY, blockMeta)
            putExtra(ORIENTATION_KEY, toggleOrientation.isChecked)
        }

        this.startService(startServiceIntent)
        finishAffinity()
    }
}