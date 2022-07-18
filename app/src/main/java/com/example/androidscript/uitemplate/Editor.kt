package com.example.androidscript.uitemplate

import androidx.appcompat.app.AppCompatActivity
import com.example.androidscript.util.DebugMessage
import com.example.androidscript.util.FileOperation
import android.graphics.BitmapFactory
import android.os.Bundle
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

abstract class Editor : AppCompatActivity() {

    protected abstract fun resourceInitialize()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resourceInitialize()
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
                    var line: String
                    while (reader.readLine().also { line = it } != null) {
                        DebugMessage.set(line)
                        buffer.add(line)
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