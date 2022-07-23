package com.example.androidscript.activities

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
import com.example.androidscript.floatingwidget.AutoClickService
import com.example.androidscript.floatingwidget.FloatingWidgetService
import com.example.androidscript.floatingwidget.ScreenShotService
import com.example.androidscript.util.DebugMessage

class StartServiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_start_service) Not needed?

        if (!Settings.canDrawOverlays(applicationContext)) { //Floating Widget
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
        } else if (Settings.Secure.getInt(
                contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            ) == 0
        ) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) //Get permission
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            requestPermissions(arrayOfNulls<String>(1), FOREGROUND_REQUEST_CODE)
        } else if (!ScreenShotService.mpmSet) {

            val mpm =
                getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

            val resultLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        Handler(Looper.getMainLooper()).post {

                            val mediaProjection =
                                mpm.getMediaProjection(Activity.RESULT_OK, result.data!!)

                            // TODO pass projection to floating widget
                        }
                    }
                }

            resultLauncher.launch(mpm.createScreenCaptureIntent())
        } else {
            startService(
                Intent(
                    this,
                    FloatingWidgetService::class.java
                )
            )
            finishAffinity()
            return
        }
        finish()
    }


    companion object {
        const val FOREGROUND_REQUEST_CODE = 111

        @Deprecated("Should not use this to check")
        var SERVICE_STARTED = false

        fun startFloatingWidget(appCompatActivity: AppCompatActivity) {
            //2022_01_21 move checking to isAuthorized
            if (SERVICE_STARTED) {

            }
        }
    }
}