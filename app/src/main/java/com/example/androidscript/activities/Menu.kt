package com.example.androidscript.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.androidscript.R
import com.example.androidscript.activities.arknights.ArkKnightsEditor
import com.example.androidscript.activities.basic.BasicEditor
import com.example.androidscript.activities.fgo.FGOEditor
import com.example.androidscript.services.ScreenShotService
import com.example.androidscript.util.FileOperation
import org.opencv.android.OpenCVLoader

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
 * TODO Consider dynamically reset casting orientation as a new feature.
 */
class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!OpenCVLoader.initDebug()) {
            throw AssertionError("OpenCV unavailable!")
        }

        // TODO stop service
        // Pre-setup
        // TODO make activity to handle it's own root.
        // A known issue is that when we re-enter this activity, onCreate won't be called
        // and root is not initialized.
        FileOperation.root = (externalMediaDirs[0].absolutePath + "/")

        @Suppress("DEPRECATION") // For old api version
        windowManager.run {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                DisplayMetrics().also {
                    defaultDisplay.getRealMetrics(it)
                }.run {
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

        // TODO combine SelectFileActivity.kt with this, so that no putExtra is needed.
        findViewById<View>(R.id.button_to_FGO).setOnClickListener {
            ScreenShotService.setShotOrientation(isLandscape = true)
            val intent = Intent(this, SelectFileActivity::class.java)
            intent.putExtra(
                "next_destination",
                FGOEditor::class.qualifiedName
            )
            startActivity(intent)
        }

        findViewById<View>(R.id.button_to_basic_landscape).setOnClickListener {
            ScreenShotService.setShotOrientation(isLandscape = true)
            val intent = Intent(this, SelectFileActivity::class.java)
            intent.putExtra(
                "next_destination",
                BasicEditor::class.qualifiedName
            )
            startActivity(intent)
        }

        findViewById<View>(R.id.button_to_basic_portrait).setOnClickListener {
            ScreenShotService.setShotOrientation(isLandscape = false)
            val intent = Intent(this, SelectFileActivity::class.java)
            intent.putExtra(
                "next_destination",
                BasicEditor::class.qualifiedName
            )
            startActivity(intent)
        }

        // TODO Remove this button.
        findViewById<View>(R.id.Exit).setOnClickListener {
            finishAffinity()
        }
    }
}