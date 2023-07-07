package com.tranquilrock.androidscript.activity

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.tranquilrock.androidscript.App.Companion.SCRIPT_TYPE_KEY
import com.tranquilrock.androidscript.R
import com.tranquilrock.androidscript.databinding.ActivityMenuBinding
import com.tranquilrock.androidscript.feature.InternalStorageUser
import com.tranquilrock.androidscript.service.WidgetService
import java.util.zip.ZipInputStream


/**
 * App Menu, provide download and read available script types then create buttons.
 */
class MenuActivity : AppCompatActivity(), InternalStorageUser {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.menuDownload.setOnClickListener {
            Intent().run {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(SCRIPT_UPLOAD_TYPE)
                action = Intent.ACTION_GET_CONTENT
                uploadZip.launch(this)
            }
        }
    }

    /**
     * Avoid multiple service instance, stop service on resume.
     */
    override fun onResume() {
        super.onResume()
        stopService(Intent(this, WidgetService::class.java))

        // Update the ListView onResume for download update.
        val listAdapter: ListAdapter =
            ArrayAdapter(this, R.layout.menu_button, getScriptTypeList(this))
        binding.menuEntryButtons.run {
            adapter = listAdapter
            onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                val scriptType = parent.getItemAtPosition(position) as String
                val toSelectIntent = Intent(this@MenuActivity, SelectActivity::class.java)
                toSelectIntent.putExtra(SCRIPT_TYPE_KEY, scriptType)
                startActivity(toSelectIntent)
            }
        }
    }

    private val uploadZip = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.run {
                // Not needed: Check filename, removed as content provider restrict the types.
//                contentResolver.query(this, null, null, null, null)?.run {
//                    val nameIndex = getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                    moveToFirst()
//                    val fileName = getString(nameIndex)
//                    close()
//                    if (!fileName.endsWith(SCRIPT_UPLOAD_TYPE)) return@registerForActivityResult
//                } ?: return@registerForActivityResult

                unzip(
                    this@MenuActivity, ZipInputStream(contentResolver.openInputStream(this))
                )
                Toast.makeText(this@MenuActivity, "File Uploaded!", Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(this@MenuActivity, "File Failed!", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val SCRIPT_UPLOAD_TYPE = "zip"
    }
}