package com.example.androidscript.util

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.view.*
import com.example.androidscript.activities.Menu

/** Helper class to simplify button construction.  */
object BtnMaker {
    fun startActivity(id: Int, from: AppCompatActivity, to: Class<*>) {
        from.findViewById<View>(id).setOnClickListener {
            val intent = Intent(from, to)
            from.startActivity(intent)
            intent.putExtra("a", Menu::class.java)
        }
    }

    fun startActivityWithMessage(
        id: Int,
        from: AppCompatActivity,
        to: Class<*>,
        name: String,
        message: String,
    ) {
        from.findViewById<View>(id).setOnClickListener {
            val intent = Intent(from, to)
            intent.putExtra(name, message)
            from.startActivity(intent)
        }
    }

    fun registerOnClick(id: Int, from: AppCompatActivity, listener: View.OnClickListener) {
        from.findViewById<View>(id).setOnClickListener(listener)
    }
}