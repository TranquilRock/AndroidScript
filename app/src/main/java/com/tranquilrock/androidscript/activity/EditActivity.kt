package com.tranquilrock.androidscript.activity

import android.content.Intent
import android.media.projection.MediaProjectionManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tranquilrock.androidscript.feature.InternalStorageUser
import com.tranquilrock.androidscript.component.editor.BlockAdapter
import com.tranquilrock.androidscript.component.editor.ButtonAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.tranquilrock.androidscript.App.Companion.BLOCK_DATA_KEY
import com.tranquilrock.androidscript.App.Companion.MEDIA_PROJECTION_KEY
import com.tranquilrock.androidscript.App.Companion.ORIENTATION_KEY
import com.tranquilrock.androidscript.App.Companion.SCRIPT_NAME_KEY
import com.tranquilrock.androidscript.App.Companion.SCRIPT_TYPE_KEY
import com.tranquilrock.androidscript.databinding.ActivityEditBinding
import com.tranquilrock.androidscript.feature.PermissionRequester
import com.tranquilrock.androidscript.service.WidgetService
import java.io.IOException

/**
 * UI for editing scripts.
 * This will read from scriptClass.meta to get blocks and buttons definition, for example:
 * [
 *      Pair("Exit", []),
 *      Pair("Call", ["EditText", "Placeholder"]),
 *      ...
 * ]
 *  will be stored as:
 *  [{"first":"Exit","second":[]},{"first":"Call","second":["EditText", "Placeholder"]}]
 * */
class EditActivity : AppCompatActivity(), InternalStorageUser, PermissionRequester {

    private lateinit var blockData: ArrayList<ArrayList<String>>
    private lateinit var blockMeta: Array<Pair<String, List<List<String>>>>
    private lateinit var fileName: String
    private lateinit var scriptClass: String
    private lateinit var binding: ActivityEditBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scriptClass = intent.getStringExtra(SCRIPT_TYPE_KEY)!!
        fileName = intent.getStringExtra(SCRIPT_NAME_KEY)!!

        try {
            blockMeta = getMetadata(this, scriptClass)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            Log.e(TAG, "Meta file format error!")
        }

        try {
            blockData = getScript(this, scriptClass, fileName)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "Reading block file error!")
        }

        val mediaProjectionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                startWidgetService(result.data!!)
            } else {
                Toast.makeText(
                    this, "Please Enable Media Projection", Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.startService.setOnClickListener {
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

        binding.saveFile.setOnClickListener {
            saveScript(this, scriptClass, fileName, blockData)
            Toast.makeText(
                this, "File Saved", Toast.LENGTH_LONG
            ).show()
        }

        binding.editCodeGrid.run {
            layoutManager = LinearLayoutManager(this@EditActivity)
            adapter = BlockAdapter(blockMeta, blockData)
            addItemDecoration(
                DividerItemDecoration(
                    this@EditActivity, DividerItemDecoration.VERTICAL
                )
            )
        }

        binding.editButtonGrid.run {
            layoutManager = GridLayoutManager(this@EditActivity, 2)
            adapter = ButtonAdapter(
                blockMeta,
                blockData,
                (binding.editCodeGrid.adapter as BlockAdapter).onOrderChange,
            )
        }
    }

    private fun startWidgetService(data: Intent) {
        val blockCopy = Gson().fromJson(Gson().toJson(blockData), Array<Array<String>>::class.java)
        blockCopy.forEach {
            it[0] = blockMeta[it[0].toInt()].first
        }

        val startServiceIntent = Intent(this, WidgetService::class.java).apply {
            putExtra(SCRIPT_TYPE_KEY, scriptClass)
            putExtra(MEDIA_PROJECTION_KEY, data)
            putExtra(BLOCK_DATA_KEY, Gson().toJson(blockCopy))
            putExtra(ORIENTATION_KEY, binding.toggleOrientation.isChecked)
        }

        this.startService(startServiceIntent)
        finishAffinity()
    }

    companion object {
        val TAG: String = EditActivity::class.java.simpleName
    }
}