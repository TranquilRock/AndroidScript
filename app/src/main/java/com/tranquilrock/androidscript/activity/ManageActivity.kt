package com.tranquilrock.androidscript.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tranquilrock.androidscript.App
import com.tranquilrock.androidscript.App.Companion.IMAGE_UPLOAD_EXTENSION
import com.tranquilrock.androidscript.component.manage.ManageAdapter
import com.tranquilrock.androidscript.databinding.ActivityManageBinding
import com.tranquilrock.androidscript.feature.InternalStorageUser
import com.tranquilrock.androidscript.utils.ResourceRemover

class ManageActivity : AppCompatActivity(), InternalStorageUser {

    private lateinit var binding: ActivityManageBinding
    private lateinit var scriptClass: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.manageAdd.setOnClickListener {
            Intent().run {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(IMAGE_UPLOAD_EXTENSION)
                action = Intent.ACTION_GET_CONTENT
                uploadImage.launch(this)
            }
        }

        scriptClass = intent.getStringExtra(App.SCRIPT_TYPE_KEY)!!
    }

    override fun onResume() {
        super.onResume()
        // TODO on item add instead of all from scratch?
        val data = mutableListOf<Pair<String, Bitmap?>>()
        for (script in getScriptList(this, scriptClass)) {
            data.add(Pair(script, null))
        }
        for (imageName in getImageList(this, scriptClass)) {
            data.add(Pair(imageName, getImage(this, scriptClass, imageName)))
        }

        binding.manageResourceGrid.run {
            layoutManager = LinearLayoutManager(this@ManageActivity)
            adapter = ManageAdapter(data, ResourceRemover(this@ManageActivity, scriptClass))
            addItemDecoration(
                DividerItemDecoration(
                    this@ManageActivity, DividerItemDecoration.VERTICAL
                )
            )
        }
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
            Toast.makeText(this, "No File Selected!", Toast.LENGTH_SHORT).show()
        }
    }
}