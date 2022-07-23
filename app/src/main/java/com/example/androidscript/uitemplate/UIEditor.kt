package com.example.androidscript.uitemplate

import android.content.Intent
import android.os.Bundle
import com.example.androidscript.R
import com.example.androidscript.activities.StartServiceActivity
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
    override fun onCreate(savedInstanceState: Bundle?) {
        fileName = intent.getStringExtra("FileName")!!
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_uiactivity)
        buttonView = findViewById(R.id.buttongrid)
        blockView = findViewById(R.id.recycleview)
        blockData = Vector()
        buttonData = Vector()
    }

    protected fun startServiceHandler(orientation: String) {
        if (!StartServiceActivity.isAuthorized(this)) {
            this.startActivity(
                Intent(this, StartServiceActivity::class.java).putExtra(
                    "Orientation",
                    orientation
                )
            )
        } else {
            this.startService(
                Intent(this,FloatingWidgetService::class.java)
                    .putExtra(FloatingWidgetService.folderTAG, this.folderName)
                    .putExtra(FloatingWidgetService.scriptTAG, "Run.txt")
            )
            this.finishAffinity()
            StartServiceActivity.startFloatingWidget(this)
        }
    }
}