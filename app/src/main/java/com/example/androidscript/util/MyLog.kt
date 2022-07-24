package com.example.androidscript.util

import android.util.Log

object MyLog {
    //Turn these off when publish
    fun set(output: String) {
        Log.d("My log", output)
    }
}