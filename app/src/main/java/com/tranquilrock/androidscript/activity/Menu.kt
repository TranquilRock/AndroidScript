/**
 * App Menu, read available script types and create buttons.
 */
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
import com.google.gson.Gson
import com.tranquilrock.androidscript.App.Companion.BASIC_SCRIPT_TYPE
import com.tranquilrock.androidscript.App.Companion.SCRIPT_TYPE_KEY
import com.tranquilrock.androidscript.R
import com.tranquilrock.androidscript.core.Command
import com.tranquilrock.androidscript.service.WidgetService
import com.tranquilrock.androidscript.feature.InternalStorageUser
import org.opencv.android.OpenCVLoader

class Menu : AppCompatActivity(), InternalStorageUser {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(this, "OpenCV Not Loaded!!!", Toast.LENGTH_LONG).show()
            finishAffinity()
        }

        getMetaFile(this, BASIC_SCRIPT_TYPE).run {
            /* Initialize BASIC */
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
            Toast.makeText(this, "NotReadyYet", Toast.LENGTH_SHORT).show()
            // TODO add download button
        }
    }

    override fun onResume() {
        /* Stop service to avoid duplicate service coexists. */
        super.onResume()
        stopService(Intent(this, WidgetService::class.java))
    }
}