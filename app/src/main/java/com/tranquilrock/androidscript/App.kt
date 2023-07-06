package com.tranquilrock.androidscript

import android.app.Application
import android.util.Log
import android.widget.Toast
import org.opencv.android.OpenCVLoader
import kotlin.system.exitProcess

/**
 * Application's global states.
 *
 * Handles OpenCV check and setup the default exception handler.
 */
class App : Application() {
    companion object {
        const val BLOCK_DATA_KEY = "BLOCK_DATA_KEY"
        const val BLOCK_META_KEY = "BLOCK_META_KEY"
        const val MEDIA_PROJECTION_KEY = "MEDIA_PROJECTION"
        const val BASIC_SCRIPT_TYPE = "BASIC"
        const val SCRIPT_TYPE_KEY = "SCRIPT_TYPE"
        const val SCRIPT_NAME_KEY = "SCRIPT_NAME"
        const val ORIENTATION_KEY = "PROJECTION_ORIENTATION"
    }

    override fun onCreate() {
        super.onCreate()

        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(this, "OpenCV Not Loaded!!!", Toast.LENGTH_LONG).show()
            Log.e(packageName, "OpenCV Failed to Load.")
            exitProcess(1)
        }

        Log.d(packageName, "onCreate")
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            e.printStackTrace()
            Log.e(packageName, "Unhandled Error Occurred.")
            exitProcess(2)
        }
    }
}