package com.example.androidscript.uitemplate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.androidscript.R
import androidx.recyclerview.widget.RecyclerView
import com.example.androidscript.floatingwidget.FloatingWidgetService
import java.util.*

abstract class UIEditor : Editor() {
    protected lateinit var blockView: RecyclerView
    protected lateinit var buttonView: RecyclerView
    protected lateinit var blockData: Vector<Vector<String>>
    protected lateinit var buttonData: Vector<String>
    protected lateinit var fileName: String
    protected abstract val folderName: String
    protected lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        fileName = intent.getStringExtra("FileName")!!
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_uiactivity)
        buttonView = findViewById(R.id.buttongrid)
        blockView = findViewById(R.id.recycleview)
        blockData = Vector()
        buttonData = Vector()
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    this.startService(
                        Intent(this, FloatingWidgetService::class.java)
                            .putExtra(FloatingWidgetService.folderTAG, this.folderName)
                            .putExtra(FloatingWidgetService.scriptTAG, "Run.txt")
                    )
                    this.finishAffinity()
                }
            }
    }
}