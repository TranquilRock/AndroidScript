package com.example.androidscript.uitemplate

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.androidscript.util.FileOperation
import com.example.androidscript.util.MyLog
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

abstract class Editor : AppCompatActivity() {
    protected lateinit var startServiceLauncher: ActivityResultLauncher<Intent>
    protected lateinit var startWidgetLauncher: ActivityResultLauncher<Intent>
    protected abstract fun resourceInitialize()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resourceInitialize()
        startServiceLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    startWidget()
                }
            }
    }

    private fun startWidget() {
        val mpm: MediaProjectionManager = getSystemService(MediaProjectionManager::class.java)
        startWidgetLauncher.launch(mpm.createScreenCaptureIntent())
    }

    /** Write data from asset to storage when file not found. */
    protected fun getResource(folderName: String, fileName: String) {
        if (!FileOperation.findFile(folderName, fileName)) {
            when (fileName.substring(fileName.length - 4)) {
                ".txt",
                ".blc" -> {
                    val reader =
                        BufferedReader(InputStreamReader(assets.open(folderName + fileName)))
                    val buffer = Vector<String>()
                    while (true) {
                        reader.readLine()?.run {
                            MyLog.set(this)
                            buffer.add(this)
                        } ?: break
                    }
                    FileOperation.writeLines(folderName + fileName, buffer)
                }
                ".png" ->
                    FileOperation.saveBitmapAsPNG(
                        BitmapFactory.decodeStream(assets.open(folderName + fileName)),
                        folderName + fileName
                    )
                ".jpg" ->
                    FileOperation.saveBitmapAsJPG(
                        BitmapFactory.decodeStream(assets.open(folderName + fileName)),
                        folderName + fileName
                    )
            }
        }
    }
}