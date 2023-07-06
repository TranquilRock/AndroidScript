package com.tranquilrock.androidscript.utils

import android.content.Context
import android.graphics.Bitmap
import com.tranquilrock.androidscript.feature.InternalStorageUser

/**
 * Read-only instance for interpreter to access code and image files.
 *
 * @param context the application context that can access app storage.
 * @param scriptType the type of script to be run, e.g. BASIC.
 */
class ResourceReader(private val context: Context, val scriptType: String) : InternalStorageUser {

    private val imageCache: HashMap<String, Bitmap> = HashMap()

    fun getCode(fileName: String): List<String> = getCode(context, scriptType, fileName)
    fun getImage(fileName: String): Bitmap {
        if (imageCache.containsKey(fileName)) {
            return imageCache[fileName]!!
        }
        val image = getImage(context, scriptType, fileName)
        imageCache[fileName] = image
        if (imageCache.size < CACHE_SIZE) {
            imageCache.remove(imageCache.keys.random())
        }
        return image
    }

    companion object {
        const val CACHE_SIZE = 20
    }
}