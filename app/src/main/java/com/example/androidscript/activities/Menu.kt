package com.example.androidscript.activities

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.example.androidscript.util.FileOperation
import android.os.Bundle
import com.example.androidscript.util.BtnMaker
import com.example.androidscript.R
import com.example.androidscript.floatingwidget.FloatingWidgetService
import com.example.androidscript.floatingwidget.ScreenShotService
import android.util.DisplayMetrics
import com.example.androidscript.activities.arknights.ArkKnightsEditor
import org.opencv.android.OpenCVLoader
import com.example.androidscript.floatingwidget.AutoClickService
import android.view.*
import java.lang.AssertionError

class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FileOperation.root = (externalMediaDirs[0].absolutePath + "/")
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        ScreenShotService.setUpScreenDimension(
            displayMetrics.heightPixels,
            displayMetrics.widthPixels
        )
        setContentView(R.layout.activity_menu)
        BtnMaker.startActivity(R.id.button_to_ArkUI, this, ArkKnightsEditor::class.java)
        BtnMaker.startActivityWithMessage(
            R.id.button_to_FGO,
            this,
            SelectFileActivity::class.java,
            "next_destination",
            "com.example.androidscript.Activities.FGO.FGOEditor"
        )
        findViewById<View>(R.id.button_to_basic_landscape).setOnClickListener {
            val intent = Intent(this, SelectFileActivity::class.java)
            intent.putExtra(
                "next_destination",
                "com.example.androidscript.Activities.Basic.BasicEditor"
            )
            intent.putExtra("Orientation", "Landscape")
            startActivity(intent)
        }
        findViewById<View>(R.id.button_to_basic_portrait).setOnClickListener {
            val intent = Intent(this, SelectFileActivity::class.java)
            intent.putExtra(
                "next_destination",
                "com.example.androidscript.Activities.Basic.BasicEditor"
            )
            intent.putExtra("Orientation", "Portrait")
            startActivity(intent)
        }
        BtnMaker.registerOnClick(R.id.Exit, this) { endService(true) }
        if (!OpenCVLoader.initDebug()) {
            throw AssertionError("OpenCV unavailable!")
        }
    }

    override fun onResume() {
        super.onResume()
        if (intent.getStringExtra("Message") != null && intent.getStringExtra("Message") == "Reset") {
            endService(false)
        }
    }

    private fun endService(stop: Boolean) {
        AutoClickService.stop()
        ScreenShotService.endProjection()
        stopService(Intent(this, FloatingWidgetService::class.java))
        stopService(Intent(this, ScreenShotService::class.java))
        if (stop) {
            finish()
        }
    }
}