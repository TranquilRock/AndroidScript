/** App Menu
 * The display shall be updated, this is the placeholder version.
 */
package com.tranquilrock.androidscript.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tranquilrock.androidscript.R
import com.tranquilrock.androidscript.service.WidgetService
import org.opencv.android.OpenCVLoader

class Menu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!OpenCVLoader.initDebug()) {
            throw AssertionError("OpenCV unavailable!")
        }

        setContentView(R.layout.activity_menu)

        // TODO add download button
        // TODO add extra to select Activity

        findViewById<View>(R.id.menu_enter).setOnClickListener {
            Log.d(TAG, "Button Clicked!")
            val toSelectIntent = Intent(this, SelectActivity::class.java)
            toSelectIntent.putExtra(SelectActivity.TYPE_KEY, "BASIC")
            startActivity(toSelectIntent)
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