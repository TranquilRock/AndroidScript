package com.tranquilrock.androidscript.activity.editor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tranquilrock.androidscript.R
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tranquilrock.androidscript.activity.editor.component.BlockAdapter
import com.tranquilrock.androidscript.activity.editor.component.ButtonAdapter
import com.tranquilrock.androidscript.core.Command
import java.util.Vector

class EditActivity : AppCompatActivity() {
    lateinit var blockView: RecyclerView
    lateinit var buttonView: RecyclerView
    lateinit var blockData: MutableList<MutableList<String>>
    lateinit var buttonData: MutableList<String>
    lateinit var fileName: String
    lateinit var folderName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

//        fileName = intent.getStringE("FileName", "a")!!
        buttonView = findViewById(R.id.edit_button_grid)
        buttonData = Vector()
        blockView = findViewById(R.id.edit_code_grid)
        blockData = Vector()

        findViewById<View>(R.id.start_service).setOnClickListener {
//            if (!Settings.canDrawOverlays(applicationContext)) { //Floating Widget
//                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    requestPermissions(arrayOfNulls<String>(1), FOREGROUND_REQUEST_CODE)
//                }
//            } else if (Settings.Secure.getInt(
//                    contentResolver,
//                    Settings.Secure.ACCESSIBILITY_ENABLED
//                ) == 0
//            ) {
//                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) //Get permission
//            } else {
//                this.startService(
//                    Intent(this, FloatingWidgetService::class.java)
//                        .putExtra(FloatingWidgetService.folderTAG, this.folderName)
//                        .putExtra(FloatingWidgetService.scriptTAG, "Run.txt")
//                        .putExtra("MPM", result.data!!)
//                )
                // TODO check permission and start services
//            }
        }

        findViewById<View>(R.id.save_file).setOnClickListener {
//            if (Code.isValid(blockData.toList())) {
//                FileOperation.writeWords(folderName + fileName, blockData)
//                Toast.makeText(this.applicationContext, "Successful!", Toast.LENGTH_LONG).show()
//            } else {
//                Toast.makeText(
//                    this.applicationContext,
//                    "Arguments can't be empty",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
            // TODO check code valid && store file
        }

        // TODO read file and setup blockData.

        blockView.layoutManager = LinearLayoutManager(this)
        blockView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        blockView.adapter = BlockAdapter(blockData)
        //SetButtonData
        for (key in Command.COMMAND_LIST) {
            buttonData.add(key)
        }
        buttonView.layoutManager = GridLayoutManager(this, 2)
        buttonView.adapter =
            ButtonAdapter(
                blockData,
                (blockView.adapter as BlockAdapter).onOrderChange,
                buttonData,
            )


    }

    companion object {
        private val TAG = EditActivity::class.java.simpleName
        private const val FOREGROUND_REQUEST_CODE = 111
    }
}