/* Gather activities' FILE read write here.
 *
 * */
package com.tranquilrock.androidscript.activity

import android.content.Context
import com.google.gson.Gson
import org.json.JSONObject
import java.io.File

interface UseInternalStorage {

    val META_FILE : String
        get() = "meta.json"

    fun getScriptFolder(context: Context, scriptClass: String): File {
        return context.getDir(scriptClass, Context.MODE_PRIVATE)
    }

    /* Creates script files under scriptClass, return whether the operation succeeded. */
    fun createScriptFile(context: Context, scriptClass: String, fileName: String): Boolean {
        return File(getScriptFolder(context, scriptClass), fileName).createNewFile()
    }

    fun getScriptMetadata(context: Context, scriptClass: String): List<Array<Any>> {
        return Gson().fromJson(
            File(getScriptFolder(context, scriptClass), "meta.json").readText(),
            Array<Array<Any>>::class.java
        ).asList()
    }

    fun getScriptFile(context: Context, scriptClass: String, fileName: String): List<String> {
        return File(getScriptFolder(context, scriptClass), fileName).readLines()
    }

    fun writeScriptFile(
        context: Context,
        scriptClass: String,
        fileName: String,
        lines: List<String>
    ) {
        File(getScriptFolder(context, scriptClass), fileName).bufferedWriter().use { out ->
            lines.forEach {
                out.write(it)
            }
        }
    }

    fun deleteFile(context: Context, scriptClass: String, fileName: String) {
        File(getScriptFolder(context, scriptClass), fileName).delete()
    }
}