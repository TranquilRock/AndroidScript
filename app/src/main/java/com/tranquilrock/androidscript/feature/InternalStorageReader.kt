/* Gather activities' FILE read write here.
 * /data/user/0/com.tranquilrock.androidscript/files
 * */
package com.tranquilrock.androidscript.feature

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.gson.Gson
import com.tranquilrock.androidscript.activity.SelectActivity
import com.tranquilrock.androidscript.core.Command
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream


interface InternalStorageReader {

    companion object {
        const val CODE_FILE_TYPE = ".txt"
        const val SCRIPT_FILE_TYPE = ".blc"
        const val IMAGE_FILE_TYPE = ".jpg"
        const val META_FILE = "meta.json"
        private val TAG = InternalStorageReader::class.java.simpleName
    }

    private fun getScriptFolder(context: Context, scriptType: String): File {
        return context.getDir(scriptType, Context.MODE_PRIVATE)
    }

    private fun getScriptFile(context: Context, scriptType: String, fileName: String): File {
        return File(getScriptFolder(context, scriptType), fileName + SCRIPT_FILE_TYPE)
    }

    private fun getCodeFile(context: Context, scriptType: String, fileName: String): File {
        return File(getScriptFolder(context, scriptType), fileName + CODE_FILE_TYPE)
    }

    private fun getImageFile(context: Context, scriptType: String, fileName: String): File {
        return File(getScriptFolder(context, scriptType), fileName + IMAGE_FILE_TYPE)
    }


    fun getMetadata(context: Context, scriptType: String): Array<Array<Any>> {
        return Gson().fromJson(
            File(getScriptFolder(context, scriptType), META_FILE).readText(),
            Array<Array<Any>>::class.java
        )
    }

    /**
     * Creates script files under folder, return whether the operation succeeded.
     * */
    fun createScript(context: Context, scriptType: String, fileName: String): Boolean {
        return getScriptFile(context, scriptType, fileName).createNewFile()
    }

    /**
     * Delete a script file under folder, return whether the operation succeeded.
     * */
    fun deleteScript(context: Context, scriptType: String, fileName: String): Boolean {
        return getScriptFile(context, scriptType, fileName).delete()
    }

    /**
     * List all script files inside folder.
     * */
    fun getScriptList(context: Context, scriptType: String): List<String> {
        return getScriptFolder(context, scriptType).list()?.filter { filename ->
            filename.endsWith(SCRIPT_FILE_TYPE)
        }?.map { a -> a.removeSuffix(SCRIPT_FILE_TYPE) } ?: emptyList()
    }

    /**
     * Serialize the blockData object and save.
     * */
    fun saveScript(
        context: Context, scriptType: String, fileName: String, data: ArrayList<ArrayList<String>>
    ) {
        val file = getScriptFile(context, scriptType, fileName)

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

    /**
     * Serialize back the blockData object.
     * */
    fun getScript(
        context: Context, scriptType: String, fileName: String
    ): ArrayList<ArrayList<String>> {

        val file = getScriptFile(context, scriptType, fileName)
        if (!file.exists()) throw FileNotFoundException()

        var data: ArrayList<ArrayList<String>>? = null

        if (file.length() > 0L) {
            try {
                ObjectInputStream(FileInputStream(file)).run {
                    data = readObject() as? ArrayList<ArrayList<String>>
                    close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.w(TAG, "getScriptData Failed!!!!!!")
            }
        }

        return data ?: ArrayList()
    }

    fun getCode(
        context: Context, scriptType: String, fileName: String
    ): List<String> {
        val file = getCodeFile(context, scriptType, fileName)
        if (!file.exists()) throw FileNotFoundException()
        return file.readLines()
    }


    fun saveImage(context: Context, scriptType: String, fileName: String, bitmap: Bitmap) {
        val file = getImageFile(context, scriptType, fileName)
        FileOutputStream(file).run {
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, this)
            flush()
            close()
        }
    }

    fun getImage(context: Context, scriptType: String, fileName: String): Bitmap {
        return BitmapFactory.decodeStream(getImageFile(context, scriptType, fileName).inputStream())
    }

    fun testOnlyInitBasic(context: Context) {
        File(getScriptFolder(context, SelectActivity.basicType), META_FILE).delete()
        val data = Gson().toJson(
            Command.BASIC_META
        )

        File(getScriptFolder(context, SelectActivity.basicType), META_FILE).bufferedWriter()
            .use { out -> out.write(data) }
    }
}