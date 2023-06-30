package com.tranquilrock.androidscript.utils

import android.content.Context
import android.graphics.Bitmap
import com.tranquilrock.androidscript.feature.InternalStorageUser

class ResourceReader(private val context: Context, val scriptType: String) : InternalStorageUser {

    private val imageCache: HashMap<String, Bitmap> = HashMap()

    fun getCode(fileName: String): List<String> = getCode(context, scriptType, fileName)
    fun getImage(fileName: String): Bitmap {
        if (imageCache.containsKey(fileName)) {
            return imageCache[fileName]!!
        }
        val image = getImage(context, scriptType, fileName)
        imageCache[fileName] = image
        if (imageCache.size < 20) {
            imageCache.remove(imageCache.keys.random())
        }
        return image
    }
}