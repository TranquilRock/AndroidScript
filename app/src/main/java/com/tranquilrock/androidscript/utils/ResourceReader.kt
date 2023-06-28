package com.tranquilrock.androidscript.utils

import android.content.Context
import android.graphics.Bitmap
import com.tranquilrock.androidscript.feature.InternalStorageReader

class ResourceReader(private val context: Context, private val scriptType: String) :
    InternalStorageReader {
    fun getCode(fileName: String): List<String> = getCode(context, scriptType, fileName)
    fun getImage(fileName: String): Bitmap = getImage(context, scriptType, fileName)
}