package com.example.androidscript.util

import android.util.Log
import java.lang.Exception

object DebugMessage {
    //Turn these off when publish
    fun set(output: String) {
        Log.d("My log", output)
    }

    fun printStackTrace(e: Exception) {
        e.printStackTrace()
    }
}