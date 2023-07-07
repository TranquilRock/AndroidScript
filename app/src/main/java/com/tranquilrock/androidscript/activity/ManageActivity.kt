package com.tranquilrock.androidscript.activity

import android.content.Intent
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.tranquilrock.androidscript.App
import com.tranquilrock.androidscript.databinding.ActivityManageBinding
import com.tranquilrock.androidscript.feature.InternalStorageUser
import com.tranquilrock.androidscript.feature.InternalStorageUser.Companion.IMAGE_FILE_TYPE

// TODO recycler view to list images with remove button
class ManageActivity : AppCompatActivity(), InternalStorageUser {

    private lateinit var binding: ActivityManageBinding
    private lateinit var scriptClass: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.manageAdd.setOnClickListener {
            Intent().run {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(IMAGE_FILE_TYPE)
                action = Intent.ACTION_GET_CONTENT
                uploadImage.launch(this)
            }
        }

        scriptClass = intent.getStringExtra(App.SCRIPT_TYPE_KEY)!!
    }

    private var uploadImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.run {
                saveImage(
                    this@ManageActivity, scriptClass, "a", getImage(this@ManageActivity, this)
                )
                Toast.makeText(this@ManageActivity, "File Uploaded!", Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(this@ManageActivity, "File Failed!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@ManageActivity, "No File Selected!", Toast.LENGTH_SHORT).show()
        }
    }
}