/*
 * https://developer.android.com/reference/android/app/Application
 * Holds the global application state.
 */
package com.tranquilrock.androidscript

import android.app.Application
import android.util.Log

class App : Application() {
    companion object {
        const val BLOCK_DATA_KEY = "BLOCK_DATA_KEY"
        const val BLOCK_META_KEY = "BLOCK_META_KEY"
        const val MEDIA_PROJECTION_KEY = "MEDIA_PROJECTION"
        const val BASIC_SCRIPT_TYPE = "BASIC"
        const val SCRIPT_TYPE_KEY = "SCRIPT_TYPE"
        const val SCRIPT_NAME_KEY = "SCRIPT_NAME"

    }

    override fun onCreate() {
        super.onCreate()
        Log.d(packageName, "onCreate")
    }
}