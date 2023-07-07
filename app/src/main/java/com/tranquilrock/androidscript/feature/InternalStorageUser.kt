package com.tranquilrock.androidscript.feature

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.lang.NullPointerException
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * Interface to declare that the class will access internal storage for this application.
 * The path is:
 *  1. /data/user/0/com.tranquilrock.androidscript/app_*
 *  2. /data/data/com.tranquilrock.androidscript/app_*
 */
interface InternalStorageUser {

    companion object {
        const val CODE_FILE_TYPE = ".txt"
        const val SCRIPT_FILE_TYPE = ".blc"
        const val IMAGE_FILE_TYPE = ".png"
        const val META_FILE = "meta.json"
        private const val SCRIPT_FOLDER_PREFIX = "app_"
        private const val CODE_COMMENT_PREFIX = "//"
        private const val VALID_FILENAME_PATTERN = "([A-Za-z0-9_-]*)"
        private val TAG = InternalStorageUser::class.java.simpleName
    }

    fun isValidFileName(FileName: String): Boolean {
        return Pattern.matches(
            VALID_FILENAME_PATTERN, FileName
        ) && FileName.isNotEmpty()
    }

    /**
     * Unzip file into APP's dataDir
     */
    @Throws(IOException::class)
    fun unzip(context: Context, zipInputStream: ZipInputStream) {
        var zipEntry: ZipEntry
        var readLen: Int
        val readBuffer = ByteArray(8192)
        while (true) {
            zipEntry = zipInputStream.nextEntry ?: return
            val file = File(context.dataDir, zipEntry.name)
            val dir = if (zipEntry.isDirectory) file else file.parentFile
            if (!dir.isDirectory && !dir.mkdirs()) throw FileNotFoundException(
                "Failed to ensure directory: " + dir.absolutePath
            )
            if (zipEntry.isDirectory) continue
            if (!(zipEntry.name.let { it.endsWith(CODE_FILE_TYPE) || it.endsWith(IMAGE_FILE_TYPE) })) continue
            FileOutputStream(file).use { out ->
                while (zipInputStream.read(readBuffer).also { readLen = it } != -1) {
                    out.write(readBuffer, 0, readLen)
                }
            }
        }
    }

    // =============================================================================================
    /**
     * Read meta data then add resource list to spinner.
     */
    @Throws(JsonSyntaxException::class)
    fun getMetadata(
        context: Context, scriptType: String
    ): Array<Pair<String, List<List<String>>>> {
        val data = try {
            Gson().fromJson(
                File(getScriptFolder(context, scriptType), META_FILE).readText(),
                arrayOf(Pair("", mutableListOf(listOf<String>())))::class.java
            )
        } catch (e: NullPointerException) {
            e.printStackTrace()
            emptyArray()
        }

        for ((_, block) in data) {
            for (i in block.indices) {
                if (block[i][0] == "Spinner") {
                    block[i] = block[i].plus(
                        when (block[i][1]) {
                            "Image" -> getImageList(context, scriptType)
                            else -> emptyList()
                        }
                    )
                    if (block[i].size < 3) {
                        Log.e(TAG, "Spinner no options!!")
                    }
                }
            }
        }

        return arrayOf(*data)
    }

    // =============================================================================================

    /**
     * List all image files inside script type folder.
     * */
    fun getImageList(context: Context, scriptType: String): List<String> {
        return getScriptFolder(context, scriptType).list()?.filter { filename ->
            filename.endsWith(IMAGE_FILE_TYPE)
        }?.map { a -> a.removeSuffix(IMAGE_FILE_TYPE) } ?: emptyList()
    }

    /**
     * List all script types.
     * */
    fun getScriptTypeList(context: Context): List<String> {
        return context.dataDir?.list()?.filter { dirName ->
            dirName.startsWith(SCRIPT_FOLDER_PREFIX)
        }?.map { a -> a.removePrefix(SCRIPT_FOLDER_PREFIX) } ?: emptyList()
    }

    /**
     * List all script files inside folder.
     * */
    fun getScriptList(context: Context, scriptType: String): List<String> {
        return getScriptFolder(context, scriptType).list()?.filter { filename ->
            filename.endsWith(SCRIPT_FILE_TYPE)
        }?.map { a -> a.removeSuffix(SCRIPT_FILE_TYPE) } ?: emptyList()
    }

    // =============================================================================================

    /**
     * Creates script files under folder, return whether the operation succeeded.
     * */
    fun createScript(context: Context, scriptType: String, fileName: String): Boolean {
        return getScriptFile(context, scriptType, fileName).createNewFile()
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
            e.printStackTrace()
            Log.e(TAG, "saveScript:: writeObject error")
        }
    }

    /**
     * Delete a script file under folder, return whether the operation succeeded.
     * */
    @Suppress("unused") // Will be used in MANAGE
    fun deleteScript(context: Context, scriptType: String, fileName: String): Boolean {
        return getScriptFile(context, scriptType, fileName).delete()
    }


    /**
     * Serialize back the blockData object.
     * */
    @Suppress("UNCHECKED_CAST") // Uncheck for serialization
    fun getScript(
        context: Context, scriptType: String, fileName: String
    ): ArrayList<ArrayList<String>> {

        val file = getScriptFile(context, scriptType, fileName)
        if (!file.exists()) {
            Log.e(TAG, "getScript:: No such file!")
            return ArrayList()
        }

        var data: ArrayList<ArrayList<String>>? = null

        if (file.length() > 0L) { // File is empty on new create.
            try {
                ObjectInputStream(FileInputStream(file)).run {
                    data = readObject() as ArrayList<ArrayList<String>>
                    close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "getScript:: readObject error")
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
                Log.e(TAG, "getScript:: Wrong script format!!")
            }
        }

        return data ?: ArrayList()
    }


    // =============================================================================================

    fun getCode(
        context: Context, scriptType: String, fileName: String
    ): List<String> {
        val file = getCodeFile(context, scriptType, fileName)
        return if (file.exists()) {
            file.readLines().filter { it.isNotEmpty() && !it.startsWith(CODE_COMMENT_PREFIX) }
        } else {
            Log.e(TAG, "getCode:: No such code file!")
            emptyList()
        }
    }

    // =============================================================================================

    fun saveImage(context: Context, scriptType: String, fileName: String, bitmap: Bitmap) {
        val file = getImageFile(context, scriptType, fileName)
        file.createNewFile()
        try {
            FileOutputStream(file).run {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, this)
                flush()
                close()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.e(TAG, "saveImage:: FileNot found!")
        }
    }

    fun getImage(context: Context, scriptType: String, fileName: String): Bitmap {
        val fileInputStream = getImageFile(context, scriptType, fileName).inputStream()
        val image = BitmapFactory.decodeStream(fileInputStream)
        fileInputStream.close()
        return image
    }

    // =============================================================================================

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
}