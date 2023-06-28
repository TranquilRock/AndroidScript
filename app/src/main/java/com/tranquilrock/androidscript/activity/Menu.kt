/** App Menu
 * The display shall be updated, this is the placeholder version.
 */
package com.tranquilrock.androidscript.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import org.opencv.android.OpenCVLoader


class Menu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(this, "OpenCV Not Loaded!!!", Toast.LENGTH_LONG).show()
            // This app should not run without OpenCV.
            finishAffinity()
        }

        val listView = findViewById<ListView>(R.id.menu_entry_buttons)
        val values = arrayOf("BASIC", "FGO") // TODO replace with script type list?
        val listAdapter: ListAdapter = ArrayAdapter<Any?>(this, R.layout.menu_btn_format, values)
        listView.adapter = listAdapter
        listView.onItemClickListener = OnItemClickListener { parent, _, position, id ->
            val scriptType = parent.getItemAtPosition(position) as String
            val toSelectIntent = Intent(this, SelectActivity::class.java)
            toSelectIntent.putExtra(SCRIPT_TYPE_KEY, scriptType)
            startActivity(toSelectIntent)
        }

        findViewById<View>(R.id.menu_download).setOnClickListener {
            Log.d(TAG, "Not implement yet!")
            // TODO add download button
        }
    }

    override fun onResume() {
        super.onResume()
        stopService(Intent(this, WidgetService::class.java))
    }

    companion object {
        private val TAG = Menu::class.java.simpleName
    }
}