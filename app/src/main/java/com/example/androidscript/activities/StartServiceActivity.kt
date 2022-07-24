package com.example.androidscript.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity

class StartServiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Settings.canDrawOverlays(applicationContext)) { //Floating Widget
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                requestPermissions(arrayOfNulls<String>(1), FOREGROUND_REQUEST_CODE)
            }
        } else if (Settings.Secure.getInt(
                contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            ) == 0
        ) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) //Get permission
        } else {
            setResult(RESULT_OK)
        }
        finish()
    }

    companion object {
        const val FOREGROUND_REQUEST_CODE = 111
    }
}