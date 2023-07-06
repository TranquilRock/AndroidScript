package com.tranquilrock.androidscript.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tranquilrock.androidscript.App.Companion.SCRIPT_TYPE_KEY
import com.tranquilrock.androidscript.R
import com.tranquilrock.androidscript.service.WidgetService
import com.tranquilrock.androidscript.feature.InternalStorageUser

/**
 * App Menu, provide download and read available script types then create buttons.
 */
class Menu : AppCompatActivity(), InternalStorageUser {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findViewById<ListView>(R.id.menu_entry_buttons).run {
            val listAdapter: ListAdapter = ArrayAdapter<Any?>(
                this@Menu, R.layout.menu_button, getScriptTypeList(this@Menu)
            )
            adapter = listAdapter
            onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                val scriptType = parent.getItemAtPosition(position) as String
                val toSelectIntent = Intent(this@Menu, SelectActivity::class.java)
                toSelectIntent.putExtra(SCRIPT_TYPE_KEY, scriptType)
                startActivity(toSelectIntent)
            }
        }

        findViewById<View>(R.id.menu_download).setOnClickListener {
            // TODO add download button
            Toast.makeText(this, "NotReadyYet", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Avoid multiple service instance, stop service on resume.
     */
    override fun onResume() {
        super.onResume()
        stopService(Intent(this, WidgetService::class.java))
    }
}