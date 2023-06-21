/** App Menu
 * The display shall be updated, this is the placeholder version.
 */
package com.tranquilrock.androidscript.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tranquilrock.androidscript.R
import org.opencv.android.OpenCVLoader

class Menu : AppCompatActivity() {
    companion object {
        const val TAG = "Menu"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!OpenCVLoader.initDebug()) {
            throw AssertionError("OpenCV unavailable!")
        }

        setContentView(R.layout.activity_menu)

        findViewById<View>(R.id.menu_enter).setOnClickListener {
            Log.d(TAG, "Button Clicked!")
        }
    }
}