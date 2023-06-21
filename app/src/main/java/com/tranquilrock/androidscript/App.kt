/*
 * https://developer.android.com/reference/android/app/Application
 * Holds the global application state.
 */
package com.tranquilrock.androidscript

import android.app.Application
import android.util.Log

class App : Application() {
    companion object {
        const val TAG = "App"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }
}