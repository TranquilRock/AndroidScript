package com.example.androidscript.activities.template

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.example.androidscript.R
import com.example.androidscript.services.WidgetService
import java.util.*

abstract class UIEditor : Editor() {
    protected lateinit var blockView: RecyclerView
    protected lateinit var buttonView: RecyclerView
    protected lateinit var blockData: Vector<Vector<String>>
    protected lateinit var buttonData: Vector<String>
    protected lateinit var fileName: String
    protected abstract val folderName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        fileName = intent.getStringExtra("FileName")!!
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_uiactivity)
        buttonView = findViewById(R.id.buttongrid)
        blockView = findViewById(R.id.recycleview)
        blockData = Vector()
        buttonData = Vector()

        startWidgetLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    this.startService(
                        Intent(this, WidgetService::class.java)
                            .putExtra(WidgetService.folderTAG, this.folderName)
                            .putExtra(WidgetService.scriptTAG, "Run.txt")
                            .putExtra("MPM", result.data!!)
                    )
                    this.finishAffinity()
                }
            }
    }
}