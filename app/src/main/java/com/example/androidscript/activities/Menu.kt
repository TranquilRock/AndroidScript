package com.example.androidscript.activities

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Build
import com.example.androidscript.util.FileOperation
import android.os.Bundle
import com.example.androidscript.R
import com.example.androidscript.floatingwidget.FloatingWidgetService
import com.example.androidscript.floatingwidget.ScreenShotService
import android.util.DisplayMetrics
import com.example.androidscript.activities.arknights.ArkKnightsEditor
import org.opencv.android.OpenCVLoader
import android.view.*
import java.lang.AssertionError

/** App entry
 *
 * There are 4 options:
 *  Arknights(Landscape)
 *  FGO(Portrait)
 *  Portrait
 *  Landscape
 *
 * Since screen casting will need to decide the shape, we split Basic to two button here.
 *
 * TODO Consider dynamically reset cast orientation as a new feature.
 */
class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!OpenCVLoader.initDebug()) {
            throw AssertionError("OpenCV unavailable!")
        }

        // Pre-setup
        FileOperation.root = (externalMediaDirs[0].absolutePath + "/")

        windowManager.run {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                DisplayMetrics().also { defaultDisplay.getRealMetrics(it) }.run {
                    ScreenShotService.setScreenDim(heightPixels, widthPixels)
                }
            } else currentWindowMetrics.bounds.run {
                ScreenShotService.setScreenDim(height(), width())
            }
        }

        // View-setup
        setContentView(R.layout.activity_menu)

        findViewById<View>(R.id.button_to_ArkUI).setOnClickListener {
            ScreenShotService.setShotOrientation(isLandscape = true)
            startActivity(Intent(this, ArkKnightsEditor::class.java))
        }

        findViewById<View>(R.id.button_to_FGO).setOnClickListener {
            ScreenShotService.setShotOrientation(isLandscape = true)
            val intent = Intent(this, SelectFileActivity::class.java)
            intent.putExtra(
                "next_destination",
                "com.example.androidscript.Activities.FGO.FGOEditor"
            )
            startActivity(intent)
        }

        findViewById<View>(R.id.button_to_basic_landscape).setOnClickListener {
            ScreenShotService.setShotOrientation(isLandscape = true)
            val intent = Intent(this, SelectFileActivity::class.java)
            intent.putExtra(
                "next_destination",
                "com.example.androidscript.Activities.Basic.BasicEditor"
            )
            startActivity(intent)
        }

        findViewById<View>(R.id.button_to_basic_portrait).setOnClickListener {
            ScreenShotService.setShotOrientation(isLandscape = false)
            val intent = Intent(this, SelectFileActivity::class.java)
            intent.putExtra(
                "next_destination",
                "com.example.androidscript.Activities.Basic.BasicEditor"
            )
            startActivity(intent)
        }

        findViewById<View>(R.id.Exit).setOnClickListener { exit() }
    }

    override fun onResume() {
        super.onResume()
        intent.getStringExtra("Message")?.also {
            if (it == "Reset") {
                exit()
            }
        }
    }

    private fun exit() {
        stopService(Intent(this, ScreenShotService::class.java))
        stopService(Intent(this, FloatingWidgetService::class.java))
        finishAffinity()
    }
}