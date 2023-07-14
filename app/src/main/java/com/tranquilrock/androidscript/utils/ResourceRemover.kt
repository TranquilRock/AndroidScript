package com.tranquilrock.androidscript.utils

import android.content.Context
import com.tranquilrock.androidscript.feature.InternalStorageUser

class ResourceRemover(private val context: Context, private val scriptType: String) :
    InternalStorageUser {
    fun removeImage(fileName: String) {
        deleteImage(context, scriptType, fileName)
    }

    fun removeScript(fileName: String) {
        deleteScript(context, scriptType, fileName)
    }
}