/* UI for editing scripts.
 * This will read from scriptClass.meta to get blocks and buttons definition, for example:
 * {
 *      "Exit": [],
 *      "Call": ["file", "arg1"],
 *      "ClickPic": ["Pic"],
 * }
 * {"Exit":"[]","T":"[(Spinner, [1, 2, 3]), (EditText, [Placeholder])]"}
 * */
package com.tranquilrock.androidscript.activity.editor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tranquilrock.androidscript.R
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tranquilrock.androidscript.activity.UseInternalStorage
import com.tranquilrock.androidscript.activity.editor.component.BlockAdapter
import com.tranquilrock.androidscript.activity.editor.component.ButtonAdapter
import java.util.Vector
import android.provider.Settings
import android.widget.Toast
import com.tranquilrock.androidscript.activity.GetPermission

class EditActivity : AppCompatActivity(), UseInternalStorage, GetPermission {
    private lateinit var blockView: RecyclerView
    private lateinit var buttonView: RecyclerView
    private lateinit var blockData: MutableList<MutableList<String>>
    private lateinit var blockMeta: List<Array<*>>
    private lateinit var fileName: String
    private lateinit var scriptClass: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        buttonView = findViewById(R.id.edit_button_grid)
        blockView = findViewById(R.id.edit_code_grid)

        scriptClass = intent.getStringExtra(SCRIPT_TYPE_KEY)!!
        fileName = intent.getStringExtra(SCRIPT_NAME_KEY)!!

        blockMeta = getScriptMetadata(this, scriptClass).also {
            Log.d(TAG, "Metadata: $it")
        }
        blockData = getScriptData(this, scriptClass, fileName)

        findViewById<View>(R.id.start_service).setOnClickListener {
            if (!canDrawOverlays(this)) {
                Log.d(TAG, "Requesting Overlays")
                requestDrawOverlays(this)
            } else if (!accessibilityEnabled(contentResolver)) {
                requestAccessibility(this)
                Log.d(TAG, "Requesting Accessibility")
            } else {
//                this.startService(
//                    Intent(this, FloatingWidgetService::class.java)
//                        .putExtra(FloatingWidgetService.folderTAG, this.folderName)
//                        .putExtra(FloatingWidgetService.scriptTAG, "Run.txt")
//                        .putExtra("MPM", result.data!!)
//                )
                Log.d(TAG, "Start Service")
                // TODO Start Widget
            }
        }

        findViewById<View>(R.id.save_file).setOnClickListener {
            saveScriptFile(this, scriptClass, fileName, blockData)
            Toast.makeText(
                this,
                "Arguments can't be empty",
                Toast.LENGTH_LONG
            ).show()
//            TODO check code valid && store file
        }

        // TODO read file and setup blockData.
        blockView.layoutManager = LinearLayoutManager(this)
        blockView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        blockView.adapter = BlockAdapter(blockMeta, blockData)

        buttonView.layoutManager = GridLayoutManager(this, 2)
        buttonView.adapter = ButtonAdapter(
            blockMeta,
            blockData,
            (blockView.adapter as BlockAdapter).onOrderChange,
        )
    }


    companion object {
        private val TAG = EditActivity::class.java.simpleName
        private const val FOREGROUND_REQUEST_CODE = 111
        const val SCRIPT_TYPE_KEY = "SCRIPT_TYPE"
        const val SCRIPT_NAME_KEY = "SCRIPT_NAME"
    }
}