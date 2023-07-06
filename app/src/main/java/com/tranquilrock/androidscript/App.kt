package com.tranquilrock.androidscript

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.tranquilrock.androidscript.core.Command
import org.opencv.android.OpenCVLoader
import java.io.File
import java.lang.IllegalStateException
import kotlin.system.exitProcess

/**
 * Application's global states.
 *
 * Handles OpenCV check and setup the default exception handler.
 */
class App : Application() {
    companion object {
        const val BLOCK_DATA_KEY = "BLOCK_DATA_KEY"
        const val MEDIA_PROJECTION_KEY = "MEDIA_PROJECTION"
        const val BASIC_SCRIPT_TYPE = "BASIC"
        const val SCRIPT_TYPE_KEY = "SCRIPT_TYPE"
        const val SCRIPT_NAME_KEY = "SCRIPT_NAME"
        const val ORIENTATION_KEY = "PROJECTION_ORIENTATION"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(packageName, "onCreate")

        /**
         * Assert OpenCV Loaded.
         */
        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(this, "OpenCV Not Loaded!!!", Toast.LENGTH_LONG).show()
            Log.e(packageName, "OpenCV Failed to Load.")
            throw IllegalStateException("OpenCV Missing")
        }

        /**
         * Initialize BASIC's meta If NOT EXIST.
         */
        File(getDir(BASIC_SCRIPT_TYPE, MODE_PRIVATE), "meta.json").run {
            if (createNewFile()) {
                val data = Gson().toJson(
                    Command.BASIC_META
                )
                bufferedWriter().run {
                    use { out -> out.write(data) }
                    close()
                }
            }
        }
    }
}