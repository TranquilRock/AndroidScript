package com.tranquilrock.androidscript.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.tranquilrock.androidscript.R
import com.tranquilrock.androidscript.feature.InternalStorageUser
import java.io.FileNotFoundException
import java.io.IOException

// TODO
class ManageActivity : AppCompatActivity(), InternalStorageUser {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage)
        findViewById<Button>(R.id.manage_add).setOnClickListener {
            imageChooser()
        }
    }

    private fun imageChooser() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        launchSomeActivity.launch(i)
    }

    private var launchSomeActivity = registerForActivityResult (
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null && data.data != null) {
                val selectedImageUri = data.data
                saveImage(this, "FGO", "a", getImage(selectedImageUri!!))
            }
        }
    }

    @Throws(FileNotFoundException::class, IOException::class)
    fun getImage(uri: Uri): Bitmap {
        val source = ImageDecoder.createSource(this.contentResolver, uri)
        return ImageDecoder.decodeBitmap(source)
    }
}