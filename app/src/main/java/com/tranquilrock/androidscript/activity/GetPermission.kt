package com.tranquilrock.androidscript.activity

import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import com.tranquilrock.androidscript.BuildConfig
import com.tranquilrock.androidscript.service.ClickService

interface GetPermission {

    companion object {
        private const val EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key"
        private const val EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args"
    }

    fun canDrawOverlays(context: Context): Boolean = Settings.canDrawOverlays(context)
    fun accessibilityEnabled(contentResolver: ContentResolver): Boolean = Settings.Secure.getInt(
        contentResolver,
        Settings.Secure.ACCESSIBILITY_ENABLED
    ) != 0

    fun requestDrawOverlays(context: Context) {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            .apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            }

        context.startActivity(intent)
    }

    fun requestAccessibility(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            .apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

                val componentName = ComponentName(
                    BuildConfig.APPLICATION_ID,
                    ClickService::class.java.name
                ).flattenToString()
                val bundle = Bundle().also { it.putString(EXTRA_FRAGMENT_ARG_KEY, componentName) }

                putExtra(EXTRA_FRAGMENT_ARG_KEY, componentName)
                putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle)
            }

        context.startActivity(intent)
    }
}