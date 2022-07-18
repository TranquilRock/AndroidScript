package com.example.androidscript.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.androidscript.R
import com.example.androidscript.floatingwidget.AutoClickService
import com.example.androidscript.floatingwidget.FloatingWidgetService
import com.example.androidscript.floatingwidget.ScreenShotService
import com.example.androidscript.util.DebugMessage

class StartServiceActivity : AppCompatActivity() {

    private lateinit var mediaProjectionManager: MediaProjectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_service)
        ScreenShotService.setShotOrientation(intent.getStringExtra("Orientation") == "Landscape")
        if (!Settings.canDrawOverlays(applicationContext)) { //Floating Widget
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
        }
        //AutoClick
        try {
            if (Settings.Secure.getInt(
                    contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED
                ) == 0
            ) {
                this.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) //Get permission
            }
        } catch (e: SettingNotFoundException) {
            DebugMessage.printStackTrace(e)
        }

        //ScreenShot, need to be foreground.(The rest parts are inside its class and onActivityResult.)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val requestedPermissions: MutableList<String> = ArrayList()
            requestedPermissions.add(Manifest.permission.FOREGROUND_SERVICE) //Stub to add more permissions.
            val requests = arrayOfNulls<String>(requestedPermissions.size)
            requestPermissions(requests, FOREGROUND_REQUEST_CODE)
        }
        if (!ScreenShotService.ServiceStart) {
            mediaProjectionManager =
                getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

            val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    Handler(Looper.getMainLooper()).postDelayed({
                        ScreenShotService.setUpMediaProjectionManager(intent!!, mediaProjectionManager)
                        startService(Intent(applicationContext, ScreenShotService::class.java))
                    }, 1)
                }
            }

            startForResult.launch(mediaProjectionManager.createScreenCaptureIntent())

        } else {
            finish()
        }
    }


    companion object {
        const val FOREGROUND_REQUEST_CODE = 111
        private var SERVICE_STARTED = false

        fun startFloatingWidget(appCompatActivity: AppCompatActivity) {
            //2022_01_21 move checking to isAuthorized
            if (SERVICE_STARTED) {
                appCompatActivity.startService(
                    Intent(
                        appCompatActivity,
                        FloatingWidgetService::class.java
                    )
                )
                appCompatActivity.finishAffinity()
            }
        }

        fun isAuthorized(appCompatActivity: AppCompatActivity): Boolean {
            Toast.makeText(
                appCompatActivity.applicationContext,
                "Please Authorize.",
                Toast.LENGTH_SHORT
            ).show()
            val debug = true // TODO Remove this
            try {
                if (Settings.canDrawOverlays(appCompatActivity.applicationContext)) {
                    if (ScreenShotService.ServiceStart) {
                        if (Settings.Secure.getInt(
                                appCompatActivity.contentResolver,
                                Settings.Secure.ACCESSIBILITY_ENABLED
                            ) > 0 && AutoClickService.running()
                        ) {
                            SERVICE_STARTED = true
                            Toast.makeText(
                                appCompatActivity.applicationContext,
                                "Authorized",
                                Toast.LENGTH_LONG
                            ).show()
                            return true
                        } else if (debug) {
                            Toast.makeText(
                                appCompatActivity.applicationContext,
                                "Need Accessibility Enabled!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else if (debug) {
                        Toast.makeText(
                            appCompatActivity.applicationContext,
                            "Can't Capture Screen!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else if (debug) {
                    Toast.makeText(
                        appCompatActivity.applicationContext,
                        "Can't Draw Over Layers!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: SettingNotFoundException) {
                DebugMessage.printStackTrace(e)
            }
            SERVICE_STARTED = false
            return false
        }
    }
}