/* Gather activities' FILE read write here.
 *
 * */
package com.tranquilrock.androidscript.activity

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.Vector


interface UseInternalStorage {

    companion object {
        const val SCRIPT_FILE_TYPE = ".blc"
        const val META_FILE = "meta.json"
        private val TAG = UseInternalStorage::class.java.simpleName
    }

    private fun getScriptFolder(context: Context, scriptClass: String): File {
        return context.getDir(scriptClass, Context.MODE_PRIVATE)
    }

    private fun getScriptFile(context: Context, scriptClass: String, fileName: String): File {
        return File(getScriptFolder(context, scriptClass), fileName + SCRIPT_FILE_TYPE)
    }

    /* Creates script files under scriptClass, return whether the operation succeeded. */
    fun createScriptFile(context: Context, scriptClass: String, fileName: String): Boolean {
        return getScriptFile(context, scriptClass, fileName).createNewFile()
    }

    fun deleteScriptFile(context: Context, scriptClass: String, fileName: String) {
        getScriptFile(context, scriptClass, fileName).delete()
    }

    fun getScriptMetadata(context: Context, scriptClass: String): List<Array<Any>> {
        return Gson().fromJson(
            getScriptFile(context, scriptClass, META_FILE).readText(),
            Array<Array<Any>>::class.java
        ).asList()
    }

    fun getScriptList(context: Context, scriptClass: String): List<String> {
        return getScriptFolder(context, scriptClass).list()?.filter { filename ->
            filename.endsWith(SCRIPT_FILE_TYPE)
        }?.map { a -> a.removeSuffix(SCRIPT_FILE_TYPE) } ?: emptyList()
    }

    fun saveScriptFile(
        context: Context,
        scriptClass: String,
        fileName: String,
        data: MutableList<MutableList<String>>
    ) {
        val file = getScriptFile(context, scriptClass, fileName)

        try {
            ObjectOutputStream(FileOutputStream(file)).run {
                writeObject(data)
                close()
            }
        } catch (e: IOException) {
            Log.w(TAG, "saveScriptData Failed!!!!!!")
            e.printStackTrace()
        }
    }

    fun getScriptData(
        context: Context,
        scriptClass: String,
        fileName: String
    ): MutableList<MutableList<String>> {

        val file = getScriptFile(context, scriptClass, fileName)
        if (!file.exists()) throw FileNotFoundException()

        var data: MutableList<MutableList<String>>? = null

        if (file.length() > 0L) {
            try {

                ObjectInputStream(FileInputStream(file)).run {
                    data = readObject() as? MutableList<MutableList<String>> ?: null
                    close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.w(TAG, "getScriptData Failed!!!!!!")
            }
        }

        return data ?: Vector()
    }

    fun writeScriptFile(
        context: Context,
        scriptClass: String,
        fileName: String,
        lines: List<String>
    ) {
        getScriptFile(context, scriptClass, fileName).bufferedWriter().use { out ->
            lines.forEach {
                out.write(it)
            }
        }
    }

    fun testOnlyInitBasic(context: Context) {
        File(getScriptFolder(context, SelectActivity.basicType), META_FILE).delete()
        val data =
            Gson().toJson(
                listOf(
                    listOf(
                        "GGWP",
                        listOf("Spinner", "1", "2", "3"),
                        listOf("EditText", "Placeholder")
                    ),
                    listOf(
                        "Exit"
                    )
                )
            )

        File(getScriptFolder(context, SelectActivity.basicType), META_FILE)
            .bufferedWriter()
            .use { out -> out.write(data) }

    }
}