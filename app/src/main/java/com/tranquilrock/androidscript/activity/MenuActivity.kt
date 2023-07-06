package com.tranquilrock.androidscript.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.ListView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.tranquilrock.androidscript.App.Companion.SCRIPT_TYPE_KEY
import com.tranquilrock.androidscript.R
import com.tranquilrock.androidscript.feature.InternalStorageUser
import com.tranquilrock.androidscript.service.WidgetService
import java.util.zip.ZipInputStream


/**
 * App Menu, provide download and read available script types then create buttons.
 */
class MenuActivity : AppCompatActivity(), InternalStorageUser {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findViewById<ListView>(R.id.menu_entry_buttons).run {
            val listAdapter: ListAdapter = ArrayAdapter<Any?>(
                this@MenuActivity, R.layout.menu_button, getScriptTypeList(this@MenuActivity)
            )
            adapter = listAdapter
            onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                val scriptType = parent.getItemAtPosition(position) as String
                val toSelectIntent = Intent(this@MenuActivity, SelectActivity::class.java)
                toSelectIntent.putExtra(SCRIPT_TYPE_KEY, scriptType)
                startActivity(toSelectIntent)
            }
        }

        findViewById<View>(R.id.menu_download).setOnClickListener {
            Intent().run {
                type = SCRIPT_UPLOAD_TYPE
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
    }

    private val uploadZip = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null) {
                data.data?.run {
                    unzip(
                        this@MenuActivity, ZipInputStream(contentResolver.openInputStream(this))
                    )
                }
            }
        }
    }

    companion object {
        const val SCRIPT_UPLOAD_TYPE = "application/zip"
    }
}